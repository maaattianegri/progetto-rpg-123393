package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.persistence.JsonSaveRepository;
import it.unicam.cs.mpgc.rpg123393.service.GameService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class VictoryController {

    @FXML private Label summaryLabel;
    @FXML private Label xpLabel;
    @FXML private Label levelUpBadge;
    @FXML private Label hpStatLabel;
    @FXML private Label enemyStatLabel;
    @FXML private Label levelStatLabel;
    @FXML private Label xpProgressLabel;

    private GameService gameService;
    private String      playerName;
    private int         vigore;
    private int         arcano;
    private String      imagePath;

    public void initData(GameService gameService, int xpGained, List<String> levelUpMsgs,
                         String playerName, int vigore, int arcano, String imagePath) {
        this.gameService = gameService;
        this.playerName  = playerName;
        this.vigore      = vigore;
        this.arcano      = arcano;
        this.imagePath   = imagePath;

        if (gameService.getImagePath() == null || gameService.getImagePath().isEmpty())
            gameService.setImagePath(imagePath);

        var p = gameService.getPlayer();
        var e = gameService.getEnemy();

        summaryLabel.setText("Hai sconfitto " + e.getName() + "!");
        xpLabel.setText("+" + xpGained + " XP");

        if (!levelUpMsgs.isEmpty()) {
            levelUpBadge.setText("LEVEL UP! Lv. " + gameService.getPlayerLevel());
            levelUpBadge.setVisible(true);
            levelUpBadge.setManaged(true);
        }

        hpStatLabel.setText(p.getCurrentHp() + "/" + p.getMaxHp());
        enemyStatLabel.setText(e.getName());
        levelStatLabel.setText(String.valueOf(gameService.getPlayerLevel()));
        xpProgressLabel.setText("Prossimo livello: "
                + gameService.getPlayerXp() + " / " + gameService.getXpRequired() + " XP");

        saveGame();
    }

    private void saveGame() {
        try { new JsonSaveRepository().save(gameService.toGameState()); }
        catch (IOException e) { System.err.println("[WARN] " + e.getMessage()); }
    }

    /** Naviga alla scelta carta prima di continuare. */
    @FXML
    private void onNextBattle() {
        try {
            Stage stage = (Stage) summaryLabel.getScene().getWindow();
            FXMLLoader loader = SceneNavigator.navigateTo(
                    stage, "/it/unicam/cs/mpgc/rpg123393/view/card-reward-view.fxml");
            CardRewardController ctrl = loader.getController();
            ctrl.initData(gameService, playerName, vigore, arcano, imagePath);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void onMainMenu() {
        try {
            Stage stage = (Stage) summaryLabel.getScene().getWindow();
            SceneNavigator.navigateTo(stage,
                    "/it/unicam/cs/mpgc/rpg123393/view/main-menu-view.fxml");
        } catch (IOException e) { e.printStackTrace(); }
    }
}
