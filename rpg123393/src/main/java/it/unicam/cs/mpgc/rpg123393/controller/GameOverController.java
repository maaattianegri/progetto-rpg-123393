package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.service.GameService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class GameOverController {

    @FXML private Label defeatLabel;
    @FXML private Label levelReachedLabel;
    @FXML private Label enemyLabel;

    private String playerName;
    private int    vigore;
    private int    vitalita;
    private String imagePath;

    public void initData(GameService gameService, String playerName,
                         int vigore, int vitalita, String imagePath) {
        this.playerName = playerName;
        this.vigore     = vigore;
        this.vitalita   = vitalita;
        this.imagePath  = imagePath;

        defeatLabel.setText("Sei stato sconfitto da "
                + gameService.getEnemy().getName() + "...");
        levelReachedLabel.setText("Livello raggiunto: " + gameService.getPlayerLevel());
        enemyLabel.setText("Nemico: " + gameService.getEnemy().getName()
                + " | HP residui: " + gameService.getEnemy().getCurrentHp());
    }

    @FXML
    private void onRestart() {
        try {
            Stage stage = (Stage) defeatLabel.getScene().getWindow();
            FXMLLoader loader = SceneNavigator.navigateTo(
                    stage, "/it/unicam/cs/mpgc/rpg123393/view/hello-view.fxml");
            HelloController ctrl = loader.getController();
            ctrl.initData(playerName, vigore, vitalita, imagePath, new GameService());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onMainMenu() {
        try {
            Stage stage = (Stage) defeatLabel.getScene().getWindow();
            SceneNavigator.navigateTo(
                    stage, "/it/unicam/cs/mpgc/rpg123393/view/creation-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
