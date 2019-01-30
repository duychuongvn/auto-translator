package vn.com.huy.translator.client;/**
 *
 * @author xchd
 */

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(this.getClass().getClassLoader().getResource("translator.fxml"));
        primaryStage.setTitle("Ipension Document Translator");
        primaryStage.setScene(new Scene(root, 1024, 800));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

}
