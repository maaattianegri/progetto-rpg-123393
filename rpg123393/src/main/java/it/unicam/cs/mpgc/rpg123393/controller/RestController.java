package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.service.GameService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class RestController {

    @FXML private StackPane rootPane;
    @FXML private Label     titleLabel;
    @FXML private Label     hpLabel;
    @FXML private Label     feedbackLabel;
    @FXML private Button    healBtn;
    @FXML private Button    upgradeBtn;
    @FXML private Button    continueBtn;

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
        gameService.getMapService().advance();
        lockChoices();
    }

    @FXML
    private void onUpgrade() {
        if (choiceMade) return;
        choiceMade = true;
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
