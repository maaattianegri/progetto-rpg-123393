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
    private String      classKey;

    @FXML
    public void initialize() {
        ImageLoaderHelper.applyBackground(rootPane, ImageLoaderHelper.backgroundPath("rest"));
    }

    public void initData(GameService gs, String playerName, int vigore, int arcano, String classKey) {
        this.gameService = gs;
        this.playerName  = playerName;
        this.vigore      = vigore;
        this.arcano      = arcano;
        this.classKey    = classKey;
        var p = gs.getPlayer();
        hpLabel.setText("HP: " + p.getCurrentHp() + " / " + p.getMaxHp());
    }

    @FXML
    private void onRest() {
        String result = gameService.rest();
        var p = gameService.getPlayer();
        hpLabel.setText("HP: " + p.getCurrentHp() + " / " + p.getMaxHp());
        resultLabel.setText(result);
    }

    @FXML
    private void onForge() {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            FXMLLoader loader = SceneNavigator.navigateTo(
                    stage, "/it/unicam/cs/mpgc/rpg123393/view/upgrade-view.fxml");
            UpgradeController ctrl = loader.getController();
            ctrl.initData(gameService, playerName, vigore, arcano, classKey);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void onContinue() {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            FXMLLoader loader = SceneNavigator.navigateTo(
                    stage, "/it/unicam/cs/mpgc/rpg123393/view/map-view.fxml");
            MapController ctrl = loader.getController();
            ctrl.initData(playerName, vigore, arcano, classKey, gameService);
        } catch (IOException e) { e.printStackTrace(); }
    }
}
