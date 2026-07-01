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
    @FXML private Label levelLabel;
    @FXML private Label statsLabel;

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

        if (gameService.getImagePath() == null || gameService.getImagePath().isEmpty()) {
            gameService.setImagePath(imagePath);
        }

        summaryLabel.setText("Hai sconfitto " + gameService.getEnemy().getName() + "!");
        xpLabel.setText("+ " + xpGained + " XP");

        if (!levelUpMsgs.isEmpty()) {
            levelLabel.setText(String.join(" | ", levelUpMsgs));
        } else {
            levelLabel.setText("Livello: " + gameService.getPlayerLevel()
                    + "  (" + gameService.getPlayerXp() + "/" + gameService.getXpRequired() + " XP)");
        }

        var p = gameService.getPlayer();
        statsLabel.setText("HP: " + p.getCurrentHp() + "/" + p.getMaxHp()
                + "  |  MANA: " + p.getCurrentMana() + "/" + p.getMaxMana());

        saveGame();
    }

    private void saveGame() {
        try {
            new JsonSaveRepository().save(gameService.toGameState());
        } catch (IOException e) {
            System.err.println("[WARN] Salvataggio fallito: " + e.getMessage());
        }
    }

    @FXML
    private void onNextBattle() {
        try {
            Stage stage = (Stage) summaryLabel.getScene().getWindow();
            FXMLLoader loader = SceneNavigator.navigateTo(
                    stage, "/it/unicam/cs/mpgc/rpg123393/view/hello-view.fxml");
            HelloController ctrl = loader.getController();
            ctrl.initData(playerName, vigore, arcano, imagePath, gameService);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onMainMenu() {
        try {
            Stage stage = (Stage) summaryLabel.getScene().getWindow();
            SceneNavigator.navigateTo(
                    stage, "/it/unicam/cs/mpgc/rpg123393/view/creation-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
