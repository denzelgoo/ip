package bro.ui;

import java.io.IOException;

import bro.Bro;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Bro.
 */
public class Main extends Application {
    private Bro bro = new Bro();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setTitle("Bro");
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setBro(bro);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
