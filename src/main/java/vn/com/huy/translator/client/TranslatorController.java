package vn.com.huy.translator.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import vn.com.huy.translator.decorator.DefaultTextDecorator;
import vn.com.huy.translator.decorator.TextDecorator;
import vn.com.huy.translator.processor.TranslatorProcessor;

/**
 *
 * @author xchd
 */
public class TranslatorController implements Initializable {
    Logger LOG = LoggerFactory.getLogger(TranslatorController.class);
    @FXML
    ListView<TextArea> lvSource;
    @FXML
    ListView<TextArea> lvDest;
    @FXML
    Button btnBrowser;
    @FXML
    Button btnSave;
    @FXML
    Label lblPath;
    ObservableList<TextArea> sourceItems = FXCollections.observableArrayList();
    ObservableList<TextArea> destItems = FXCollections.observableArrayList();
    FileChooser.ExtensionFilter EXT_ALL_FILES = new FileChooser.ExtensionFilter("Docx", "*.docx");
    TranslatorProcessor.Out doc;

    TextDecorator textDecorator = new DefaultTextDecorator();

    Path path;

    @FXML
    protected void handledBrowserAction(ActionEvent event) {
        List<FileChooser.ExtensionFilter> extensionFiltersList = new ArrayList<>();

        extensionFiltersList.add(EXT_ALL_FILES);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select doc file");
        if (path != null) {
            fileChooser.setInitialDirectory(path.getParent().toFile());
        }
        fileChooser.getExtensionFilters().addAll(extensionFiltersList);
        File file = fileChooser.showOpenDialog(lblPath.getScene().getWindow());
        if (file == null) {
            return;
        }
        path = file.toPath();
        lblPath.setText(file.getAbsolutePath());
        sourceItems.clear();
        destItems.clear();

        lvSource.setItems(sourceItems);
        lvDest.setItems(destItems);

        try (InputStream in = Files.newInputStream(path);
             XWPFDocument docIn = new XWPFDocument(in)) {
            TranslatorProcessor translatorProcessor = new TranslatorProcessor();
            doc = translatorProcessor.processFile(docIn, "fr", "en");
            List<Pair<String, List<XWPFParagraph>>> result = doc.getResult();
            result.forEach(item -> {

               final TextArea sourceTextArea = new TextArea();

                sourceTextArea.setStyle("-fx-margin:20");
                sourceTextArea.setText(item.getLeft());
                sourceTextArea.focusedProperty().addListener((observable, oldValue, newValue)->{
                    if(newValue != null && newValue) {
                        lvSource.getSelectionModel().select(sourceTextArea);
                    }
                });
                TextArea desTextArea = new TextArea();
                desTextArea.setStyle("-fx-margin:10");
                sourceItems.add(sourceTextArea);
                destItems.add(desTextArea);

            });
            lvDest.scrollTo(0);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);

        }

    }

    @FXML
    protected void handledSaveAction(ActionEvent event) throws IOException {
        List<String> keys = new ArrayList<>();
        List<String> values = new ArrayList<>();
        AtomicInteger count = new AtomicInteger();
        String originalFullName = path.toFile().getName();
        String originalBaseName = FilenameUtils.getBaseName(originalFullName);
        String targetLang = "en";
        String translatedFileName = StringUtils.replace(originalBaseName, "_" + "fr", "_" + targetLang) + ".docx";
        Path destFolder = Paths.get(path.getParent().toString(), targetLang);
        destFolder.toFile().mkdirs();
        try (InputStream in = Files.newInputStream(path);
             OutputStream out = Files.newOutputStream(Paths.get(destFolder.toString(), translatedFileName));
             XWPFDocument docIn = new XWPFDocument(in)) {

            TranslatorProcessor translatorProcessor = new TranslatorProcessor();
            doc = translatorProcessor.processFile(docIn, "fr", "en");
            destItems.forEach(dest -> {
                String translatedText = dest.getText();
                translatedText = StringUtils.replaceEach(translatedText, keys.toArray(new String[]{}), values.toArray(new String[]{}));
                String[] translateTexts = translatedText.split("\n");

                List<XWPFParagraph> entry = doc.getResult().get(count.getAndIncrement()).getRight();

                for (int i = 0; i < entry.size(); i++) {
                    try {
                        XWPFRun run = entry.get(i).createRun();
                        run.addBreak(BreakType.TEXT_WRAPPING);
                        textDecorator.decorate(run, translateTexts[i]);
                    } catch (Exception e) {
                        LOG.error(e.getMessage(), e);
                        break;
                    }
                }
            });
            doc.getDoc().write(out);

        } catch (IOException e) {
            LOG.error(e.getMessage(), e);

        }
    }

    @Override
    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        this.lvSource.getSelectionModel().selectedItemProperty().addListener((obv, oldValue, newValue) -> {
            int selectedIndex = this.lvSource.getSelectionModel().getSelectedIndex();
            this.lvDest.getSelectionModel().select(selectedIndex);
            if (selectedIndex - 1 > 0) {
                this.lvDest.scrollTo(selectedIndex - 1);
            }
        });

    }
}
