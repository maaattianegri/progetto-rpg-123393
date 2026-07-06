package it.unicam.cs.mpgc.rpg123393;

import it.unicam.cs.mpgc.rpg123393.controller.AchievementToastManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                HelloApplication.class.getResource("view/main-menu-view.fxml"));
        Parent initialRoot = fxmlLoader.load();

        // Root fisso: uno StackPane che contiene la scena corrente + l'overlay toast sopra
        StackPane rootPane = new StackPane(initialRoot);
        rootPane.setStyle("-fx-background-color: black;");

        Scene scene = new Scene(rootPane, 800, 600);
        stage.setTitle("Arena Master");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();

        // Inizializza il toast manager DOPO che lo stage ha una scena
        AchievementToastManager.getInstance().init(stage, rootPane);
    }

    public static void main(String[] args) { launch(); }
}
