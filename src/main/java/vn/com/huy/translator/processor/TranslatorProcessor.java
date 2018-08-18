package vn.com.huy.translator.processor;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import vn.com.huy.translator.decorator.TextDecorator;
import vn.com.huy.translator.encoder.Encoder;
import vn.com.huy.translator.progress.ProgressManager;
import vn.com.huy.translator.provider.TranslationProvider;

@Component
@Getter
public class TranslatorProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(TranslatorProcessor.class);
    public static final String BREAK_LINE = "<<< BRL >>>";

    private Map<String, String> glossaryMap = new HashMap<>();

    @Value("${translator.blacklist.equal}")
    private List<String> blacklistEqual;
    @Value("${translator.blacklist.contain}")
    private List<String> blacklistContain;
    @Value("${translator.glossary.path}")
    private String glossaryPath;
    @Value("${translator.file.overwrite}")
    private boolean overwrite;
    @Value("${translator.provider.maxsize")
    private Integer maxSize;
    @Autowired
    private Encoder encoder;
    @Autowired
    private TextDecorator textDecorator;
    @Autowired
    private TranslationProvider translationProvider;

    @PostConstruct
    public void initialize() {
        LOG.info("Using translation provider    = {}", translationProvider.getClass().getSimpleName());
        LOG.info("Using text decorator          = {}", textDecorator.getClass().getSimpleName());
        LOG.info("Using glossary encoder        = {}", encoder.getClass().getSimpleName());
        LOG.info("Using glossary path           = {}", glossaryPath);
        LOG.info("Using equal-blacklist         = {}", blacklistEqual);
        LOG.info("Using contain-blacklist       = {}", blacklistContain);
        LOG.info("Using overwrite               = {}", overwrite);
    }

    public void prepare() {
        try {
            File glossariesFile = Paths.get(glossaryPath).toFile();
            if (glossariesFile.exists() && glossariesFile.canRead()) {
                addGlossaries(FileUtils.readLines(glossariesFile, Charset.forName("UTF-8")));
            }
        } catch (Exception e) {
            LOG.error("Error while processing configurations", e);
        }
    }

    public String getSupportedExtension() {
        return ".docx";
    }

    public final void processFile(Path path, String srcLang, String targetLang) {
        try {
            String originalFullName = path.toFile().getName();
            String originalBaseName = FilenameUtils.getBaseName(originalFullName);
            LOG.info("Translating file: {}", originalFullName);

            String translatedFileName = StringUtils.replace(originalBaseName, "_" + srcLang, "_" + targetLang) + getSupportedExtension();

            Path destFolder = Paths.get(path.getParent().toString(), targetLang);
            // Create folder if needed
            destFolder.toFile().mkdirs();

            if (Paths.get(destFolder.toString(), translatedFileName).toFile().exists() && !overwrite) {
                LOG.info("File {} was already translated ({})", originalBaseName, translatedFileName);
                return;
            }

            try (InputStream in = Files.newInputStream(path);
                 OutputStream out = Files.newOutputStream(Paths.get(destFolder.toString(), translatedFileName));
                 XWPFDocument doc = new XWPFDocument(in)) {
                List<XWPFParagraph> paragraphs = doc.getParagraphs();
                List<XWPFParagraph> paragraphsInTables = doc.getTables().stream()
                        .flatMap(table -> table.getRows().stream())
                        .flatMap(row -> row.getTableCells().stream())
                        .flatMap(cell -> cell.getParagraphs().stream())
                        .collect(Collectors.toList());

                List<List<XWPFParagraph>> paragraphGroups = createXWPFParagraphGroups(paragraphs);
                List<List<XWPFParagraph>> tableParagraphGroups = createXWPFParagraphGroups(paragraphsInTables);
                ProgressManager progressManager = new ProgressManager(paragraphGroups.size() + tableParagraphGroups.size());
                processTranslation(progressManager, paragraphGroups, srcLang, targetLang);
                processTranslation(progressManager, tableParagraphGroups, srcLang, targetLang);
                doc.write(out);
            }
        } catch (Exception ex) {
            LOG.error("Error when processing file: " + path, ex);
        }
    }

    private synchronized void processTranslation(ProgressManager progressManager, List<List<XWPFParagraph>> groupParagraphs, String sourceLanguage, String targetLanguage) {

        groupParagraphs.forEach(entry -> {
            String text = entry.stream().map(x -> x.getParagraphText()).collect(Collectors.joining(BREAK_LINE));
            List<String> keys = new ArrayList<>();
            List<String> values = new ArrayList<>();
            glossaryMap.entrySet().stream().forEach(e -> {
                keys.add(e.getKey());
                values.add(e.getValue());
            });

            text = StringUtils.replaceEach(text, values.toArray(new String[]{}), keys.toArray(new String[]{}));
            String translatedText = translationProvider.translate(text, sourceLanguage, targetLanguage);
            translatedText = StringUtils.replaceEach(translatedText, keys.toArray(new String[]{}), values.toArray(new String[]{}));
            String[] translateTexts = translatedText.split(BREAK_LINE);

            for (int i = 0; i < entry.size(); i++) {
                XWPFRun run = entry.get(i).createRun();
                run.addBreak(BreakType.TEXT_WRAPPING);
                textDecorator.decorate(run, translateTexts[i]);
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // Do no thing
            }
            progressManager.step();
        });
    }

    private List<List<XWPFParagraph>> createXWPFParagraphGroups(List<XWPFParagraph> paragraphs) {
        List<XWPFParagraph> xwpfParagraphs = paragraphs.stream().filter(paragraph -> (normalize(paragraph.getText()).isEmpty()
                || StringUtils.containsAny(normalize(paragraph.getText()), blacklistContain.toArray(new String[blacklistContain.size()]))
                || StringUtils.equalsAny(normalize(paragraph.getText()), blacklistEqual.toArray(new String[blacklistEqual.size()])))).collect(Collectors.toList());
        List<List<XWPFParagraph>> groupParagraphs = new ArrayList<>();
        String text = "";
        List<XWPFParagraph> groupXWPFParagraph = new ArrayList<>();
        groupParagraphs.add(groupXWPFParagraph);
        for (int i = 0; i < xwpfParagraphs.size(); i++) {
            XWPFParagraph xwpfParagraph = xwpfParagraphs.get(i);
            if (StringUtils.isNoneBlank(xwpfParagraph.getParagraphText())) {
                text += xwpfParagraph.getParagraphText();
                if (text.length() > maxSize) {
                    groupXWPFParagraph = new ArrayList<>();
                    groupParagraphs.add(groupXWPFParagraph);
                    text = "";
                }
                groupXWPFParagraph.add(xwpfParagraph);
            }
        }
        return groupParagraphs;
    }

    private void addGlossaries(Collection<String> glossaries) {
        glossaries
                .stream()
                .map(StringUtils::trimToEmpty)
                        // TODO case-sensitive?
                .forEach(g -> glossaryMap.put(encoder.encode(g), g));
    }

    private String normalize(String text) {
        return StringUtils.trimToEmpty(text);
    }

}
