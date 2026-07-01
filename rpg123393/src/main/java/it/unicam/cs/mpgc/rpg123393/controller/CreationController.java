package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.persistence.GameState;
import it.unicam.cs.mpgc.rpg123393.persistence.JsonSaveRepository;
import it.unicam.cs.mpgc.rpg123393.service.GameService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class CreationController {

    @FXML private TextField nameField;
    @FXML private VBox      continueSection;
    @FXML private Label     continueInfoLabel;

    private final JsonSaveRepository saveRepo = new JsonSaveRepository();
    private GameState loadedState = null;

    /**
     * Chiamato automaticamente da JavaFX dopo il caricamento dell'FXML.
     * Controlla se esiste un salvataggio e mostra la sezione "Continua partita".
     */
    @FXML
    public void initialize() {
        if (saveRepo.saveExists()) {
            try {
                loadedState = saveRepo.load();
                if (loadedState != null) {
                    continueSection.setVisible(true);
                    continueSection.setManaged(true);
                    continueInfoLabel.setText(
                            loadedState.getPlayerName()
                            + "  ·  " + loadedState.getClassName()
                            + "  ·  Livello " + loadedState.getPlayerLevel()
                            + "  ·  Salvato: " + loadedState.getSaveDate()
                    );
                }
            } catch (IOException e) {
                System.err.println("[WARN] Impossibile leggere il salvataggio: " + e.getMessage());
            }
        } else {
            continueSection.setVisible(false);
            continueSection.setManaged(false);
        }
    }

    // -------------------------------------------------------
    // Continua partita dal salvataggio
    // -------------------------------------------------------

    @FXML
    private void onContinue(ActionEvent event) {
        if (loadedState == null) return;
        try {
            GameService gameService = new GameService();
            gameService.restoreFromState(loadedState);
            gameService.startBattle();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/it/unicam/cs/mpgc/rpg123393/view/hello-view.fxml"));
            Parent root = loader.load();

            HelloController ctrl = loader.getController();
            ctrl.initData(
                    loadedState.getPlayerName(),
                    gameService.getVigore(),
                    gameService.getArcano(),
                    loadedState.getImagePath(),
                    gameService
            );

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 700, 750));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------
    // Nuova partita — selezione classe
    // -------------------------------------------------------

    @FXML private void selectWarrior(ActionEvent event) {
        startGame(event, 8, 3, "/images/tank.jpg", "Guerriero");
    }

    @FXML private void selectMage(ActionEvent event) {
        startGame(event, 3, 8, "/images/mago.jpg", "Mago");
    }

    @FXML private void selectDragon(ActionEvent event) {
        startGame(event, 6, 6, "/images/dragon.jpg", "Dracomante");
    }

    private void startGame(ActionEvent event, int vigore, int arcano,
                           String imagePath, String className) {
        try {
            String playerName = nameField.getText().trim();
            if (playerName.isEmpty()) { playerName = "Eroe Sconosciuto"; }

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/it/unicam/cs/mpgc/rpg123393/view/hello-view.fxml"));
            Parent root = loader.load();

            HelloController ctrl = loader.getController();
            ctrl.initData(playerName, vigore, arcano, imagePath, className);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 700, 750));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
