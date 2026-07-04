package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.service.GameService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller del nodo REST.
 *
 * Il player può scegliere tra due opzioni mutualmente esclusive:
 *   - Riposare: guarisce il 30% degli HP massimi.
 *   - Potenziare una carta: apre upgrade-view, poi torna alla mappa.
 *
 * Dopo la scelta, entrambe le opzioni diventano disabilitate
 * e appare il bottone "Continua" per andare alla mappa.
 *
 * FIX: il nodo REST viene marcato come cleared in entrambi i percorsi
 * (onHeal e onUpgrade), così la mappa non lo mostra più come nodo corrente
 * non completato dopo la visita all'upgrade-view.
 */
public class RestController {

    @FXML private Label  titleLabel;
    @FXML private Label  hpLabel;
    @FXML private Label  feedbackLabel;
    @FXML private Button healBtn;
    @FXML private Button upgradeBtn;
    @FXML private Button continueBtn;

    private GameService gameService;
    private String      playerName;
    private int         vigore;
    private int         arcano;
    private String      imagePath;
    private boolean     choiceMade = false;

    public void initData(GameService gs, String playerName, int vigore, int arcano, String imagePath) {
        this.gameService = gs;
        this.playerName  = playerName;
        this.vigore      = vigore;
        this.arcano      = arcano;
        this.imagePath   = imagePath;
        refreshHp();
        continueBtn.setVisible(false);
        continueBtn.setManaged(false);
        feedbackLabel.setText("");
    }

    @FXML
    private void onHeal() {
        if (choiceMade) return;
        choiceMade = true;
        var p = gameService.getPlayer();
        int healAmount = (int) (p.getMaxHp() * 0.30);
        p.heal(healAmount);
        feedbackLabel.setText("\u2764 Hai recuperato " + healAmount + " HP!");
        refreshHp();
        // Marca il nodo REST come completato
        gameService.getMapService().advance();
        lockChoices();
    }

    @FXML
    private void onUpgrade() {
        if (choiceMade) return;
        choiceMade = true;
        // Marca il nodo REST come completato prima di navigare all'upgrade
        gameService.getMapService().advance();
        lockChoices();
        try {
            Stage stage = (Stage) titleLabel.getScene().getWindow();
            FXMLLoader loader = SceneNavigator.navigateTo(
                    stage, "/it/unicam/cs/mpgc/rpg123393/view/upgrade-view.fxml");
            UpgradeController ctrl = loader.getController();
            ctrl.initDataFromRest(gameService, playerName, vigore, arcano, imagePath);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void onContinue() {
        navigateToMap();
    }

    private void lockChoices() {
        healBtn.setDisable(true);
        upgradeBtn.setDisable(true);
        continueBtn.setVisible(true);
        continueBtn.setManaged(true);
    }

    private void refreshHp() {
        var p = gameService.getPlayer();
        hpLabel.setText("\u2764 HP: " + p.getCurrentHp() + " / " + p.getMaxHp());
    }

    private void navigateToMap() {
        try {
            Stage stage = (Stage) titleLabel.getScene().getWindow();
            FXMLLoader loader = SceneNavigator.navigateTo(
                    stage, "/it/unicam/cs/mpgc/rpg123393/view/map-view.fxml");
            MapController ctrl = loader.getController();
            ctrl.initData(gameService, playerName, vigore, arcano, imagePath);
        } catch (IOException e) { e.printStackTrace(); }
    }
}
