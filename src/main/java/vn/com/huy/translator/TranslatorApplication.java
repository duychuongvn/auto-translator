package vn.com.huy.translator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import vn.com.huy.translator.processor.TranslatorProcessor;

public class TranslatorApplication {

    private static final Logger LOG = LoggerFactory.getLogger(TranslatorApplication.class);

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            LOG.error("Please specify 3 params as below:");
            LOG.error("1st: path to folder that contains document");
            LOG.error("2nd: language of document e.g. fr, de");
            LOG.error("3rd: language that you want to translate to e.g. en, it");
            return;
        }

        // Params:
        // 1st: path to folder
        String path = args[0];
        // 2nd: document language
        String sourceLanguage = args[1];
        // 3rd: translated language
        String targetLanguage = args[2];

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TranslatorApplicationConfig.class);
        TranslatorProcessor translatorProcessor = context.getBean(TranslatorProcessor.class);
        translatorProcessor.prepare();

        // TODO parallel to boost the speed?
        try (Stream<Path> stream = Files.list(Paths.get(path))) {
            stream
                    .filter(file -> !file.toFile().isHidden())
                    .filter(file -> file.getFileName().toString().endsWith(translatorProcessor.getSupportedExtension()))
                    .forEach(file -> translatorProcessor.processFile(file, sourceLanguage, targetLanguage));
        }
        LOG.info("DONE");
    }

}
