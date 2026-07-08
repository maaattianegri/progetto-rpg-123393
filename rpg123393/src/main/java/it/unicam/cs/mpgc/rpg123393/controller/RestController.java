package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.service.GameService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class RestController {

    @FXML private VBox  rootPane;
    @FXML private Label hpLabel;
    @FXML private Label resultLabel;

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
        ImageLoaderHelper.applyBackground(rootPane, ImageLoaderHelper.backgroundPath("rest"));
        refreshHp();
        resultLabel.setText("");
    }

    @FXML
    private void onRest() {
        if (choiceMade) return;
        choiceMade = true;
        var p = gameService.getPlayer();
        int healAmount = (int) (p.getMaxHp() * 0.30);
        p.heal(healAmount);
        // Avanza il nodo correttamente tramite GameService (notifica onNodeVisited)
        gameService.advanceEncounter();
        // Naviga subito alla mappa, simmetrico alla Forgia
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            FXMLLoader loader = SceneNavigator.navigateTo(
                    stage, "/it/unicam/cs/mpgc/rpg123393/view/map-view.fxml");
            MapController ctrl = loader.getController();
            ctrl.initData(gameService, playerName, vigore, arcano, imagePath);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void onForge() {
        if (choiceMade) return;
        choiceMade = true;
        // NON chiamare advance() qui: l'avanzamento avviene quando
        // UpgradeController.navigateBack() porta direttamente alla mappa.
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            FXMLLoader loader = SceneNavigator.navigateTo(
                    stage, "/it/unicam/cs/mpgc/rpg123393/view/upgrade-view.fxml");
            UpgradeController ctrl = loader.getController();
            ctrl.initDataFromRest(gameService, playerName, vigore, arcano, imagePath);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void onContinue() {
        if (choiceMade) {
            try {
                Stage stage = (Stage) rootPane.getScene().getWindow();
                FXMLLoader loader = SceneNavigator.navigateTo(
                        stage, "/it/unicam/cs/mpgc/rpg123393/view/map-view.fxml");
                MapController ctrl = loader.getController();
                ctrl.initData(gameService, playerName, vigore, arcano, imagePath);
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    private void refreshHp() {
        var p = gameService.getPlayer();
        hpLabel.setText("\u2764 HP: " + p.getCurrentHp() + " / " + p.getMaxHp());
    }
}
