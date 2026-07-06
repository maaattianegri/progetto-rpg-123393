package it.unicam.cs.mpgc.rpg123393;

import it.unicam.cs.mpgc.rpg123393.controller.AchievementToastManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Inizializza il toast manager prima di mostrare qualsiasi scena
        AchievementToastManager.getInstance().init(stage);

        FXMLLoader fxmlLoader = new FXMLLoader(
                HelloApplication.class.getResource("view/main-menu-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Arena Master");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public static void main(String[] args) { launch(); }
}
