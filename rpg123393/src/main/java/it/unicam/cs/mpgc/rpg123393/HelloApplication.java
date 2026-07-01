package it.unicam.cs.mpgc.rpg123393;

import it.unicam.cs.mpgc.rpg123393.controller.HelloController;
import it.unicam.cs.mpgc.rpg123393.persistence.GameState;
import it.unicam.cs.mpgc.rpg123393.persistence.JsonSaveRepository;
import it.unicam.cs.mpgc.rpg123393.service.GameService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        JsonSaveRepository saveRepo = new JsonSaveRepository();

        if (saveRepo.saveExists()) {
            GameState saved = saveRepo.load();
            if (saved != null && offerContinue(stage, saved)) {
                launchFromSave(stage, saved);
                return;
            }
        }

        // Nessun salvataggio o utente ha scelto "Nuova partita"
        launchCreation(stage);
    }

    /**
     * Mostra un Alert che chiede se continuare la partita salvata.
     *
     * @return true se l'utente sceglie "Continua"
     */
    private boolean offerContinue(Stage stage, GameState saved) {
        ButtonType continua   = new ButtonType("Continua",    ButtonData.OK_DONE);
        ButtonType nuovaPartita = new ButtonType("Nuova partita", ButtonData.CANCEL_CLOSE);

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Partita salvata");
        alert.setHeaderText("Trovata una partita salvata");
        alert.setContentText(
                "Personaggio: " + saved.getPlayerName() + "\n" +
                "Classe: "      + saved.getClassName()  + "\n" +
                "Livello: "     + saved.getPlayerLevel() + "\n" +
                "Salvata il: "  + saved.getSaveDate()
        );
        alert.getButtonTypes().setAll(continua, nuovaPartita);
        alert.initOwner(stage);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == continua;
    }

    /** Ripristina la partita dal salvataggio e avvia direttamente la battaglia. */
    private void launchFromSave(Stage stage, GameState saved) throws IOException {
        GameService gameService = new GameService();
        gameService.restoreFromState(saved);
        gameService.startBattle();

        FXMLLoader loader = new FXMLLoader(
                HelloApplication.class.getResource("view/hello-view.fxml"));
        Scene scene = new Scene(loader.load(), 800, 700);

        HelloController ctrl = loader.getController();
        ctrl.initData(
                saved.getPlayerName(),
                gameService.getVigore(),
                gameService.getArcano(),
                saved.getImagePath(),
                gameService
        );

        stage.setTitle("Arena Master - La Forgia");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    /** Avvia il flusso normale dalla schermata di creazione. */
    private void launchCreation(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                HelloApplication.class.getResource("view/creation-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Arena Master - La Forgia");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
