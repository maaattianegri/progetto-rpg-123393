package it.unicam.cs.mpgc.rpg123393.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Utility per la navigazione tra schermate.
 *
 * Usa un StackPane root fisso (impostato in HelloApplication) e swappa
 * solo il primo figlio (il contenuto della scena), lasciando intatto
 * l'overlay toast in cima allo stack.
 *
 * In questo modo il fullscreen non viene mai interrotto e il
 * AchievementToastManager mantiene sempre il suo riferimento all'overlay.
 */
public final class SceneNavigator {

    private SceneNavigator() {}

    public static FXMLLoader navigateTo(Stage stage, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                SceneNavigator.class.getResource(fxmlPath));
        Parent newContent = loader.load();

        // Il root e' sempre lo StackPane fisso creato in HelloApplication
        if (stage.getScene().getRoot() instanceof StackPane rootPane) {
            // Sostituisce solo il primo figlio (il contenuto), non l'overlay toast
            if (!rootPane.getChildren().isEmpty()) {
                rootPane.getChildren().set(0, newContent);
            } else {
                rootPane.getChildren().add(newContent);
            }
        } else {
            // Fallback di sicurezza (non dovrebbe mai succedere)
            stage.getScene().setRoot(newContent);
        }

        return loader;
    }
}
