package it.unicam.cs.mpgc.rpg123393.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Utility per la navigazione tra schermate.
 *
 * Usa scene.setRoot() invece di stage.setScene() per evitare il flash visivo
 * (il millisecondo in cui la finestra torna piccola) che si verifica quando
 * si crea una nuova Scene su uno Stage in fullscreen.
 *
 * In questo modo il fullscreen non viene mai interrotto.
 */
public final class SceneNavigator {

    private SceneNavigator() {}

    /**
     * Cambia il contenuto della scena corrente senza toccare Stage o fullscreen.
     *
     * @param stage    stage corrente (usato solo per accedere alla Scene)
     * @param fxmlPath path assoluto FXML
     * @return il FXMLLoader già caricato per fare getController()
     * @throws IOException se l'FXML non viene trovato
     */
    public static FXMLLoader navigateTo(Stage stage, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                SceneNavigator.class.getResource(fxmlPath));
        Parent newRoot = loader.load();

        // Sostituisce solo il root della scena esistente — fullscreen intatto
        stage.getScene().setRoot(newRoot);

        return loader;
    }
}
