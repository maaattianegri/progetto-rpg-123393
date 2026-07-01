package it.unicam.cs.mpgc.rpg123393.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Utility per la navigazione tra schermate.
 * Preserva le dimensioni e lo stato fullscreen dello Stage corrente.
 * Tutti i controller devono usare questo metodo invece di
 * "stage.setScene(new Scene(root, W, H))" con dimensioni hardcoded.
 */
public final class SceneNavigator {

    private SceneNavigator() {}

    /**
     * Cambia scena sullo stage preservando fullscreen e dimensioni.
     *
     * @param stage   stage corrente
     * @param fxmlPath path assoluto FXML (es. "/it/unicam/.../view/hello-view.fxml")
     * @return il FXMLLoader già caricato, così il chiamante può fare getController()
     * @throws IOException se l'FXML non viene trovato
     */
    public static FXMLLoader navigateTo(Stage stage, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                SceneNavigator.class.getResource(fxmlPath));
        Parent root = loader.load();

        boolean wasFullScreen = stage.isFullScreen();
        double  width         = stage.getWidth();
        double  height        = stage.getHeight();

        Scene scene = new Scene(root, width, height);
        stage.setScene(scene);

        if (wasFullScreen) {
            stage.setFullScreen(true);
        }

        return loader;
    }
}
