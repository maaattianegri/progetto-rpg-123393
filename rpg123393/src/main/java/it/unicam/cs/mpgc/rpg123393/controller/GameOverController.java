package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.service.GameService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class GameOverController {

    @FXML private Label defeatLabel;
    @FXML private Label epilogueLabel;
    @FXML private Label hpStatLabel;
    @FXML private Label enemyStatLabel;
    @FXML private Label levelStatLabel;

    private String playerName;
    private int    vigore;
    private int    arcano;
    private String imagePath;

    public void initData(GameService gameService, String playerName,
                         int vigore, int arcano, String imagePath) {
        this.playerName = playerName;
        this.vigore     = vigore;
        this.arcano     = arcano;
        this.imagePath  = imagePath;

        var p = gameService.getPlayer();
        var e = gameService.getEnemy();

        defeatLabel.setText("Sei stato sconfitto da " + e.getName() + "...");
        epilogueLabel.setText(buildEpilogue(e.getName()));
        hpStatLabel.setText(p.getCurrentHp() + "/" + p.getMaxHp());
        enemyStatLabel.setText(e.getName());
        levelStatLabel.setText(String.valueOf(gameService.getPlayerLevel()));
    }

    private String buildEpilogue(String enemyName) {
        return switch (enemyName) {
            case "Goblin"             -> "Un semplice Goblin ha posto fine alla tua avventura. Ricomincia e vendica il tuo onore.";
            case "Orco Berserker"    -> "La furia dell'Orco si \u00e8 abbattuta su di te senza piet\u00e0.";
            case "Scheletro Arcano"  -> "La maledizione dello Scheletro ha prosciugato le tue ultime energie.";
            case "Troll Rigenerante" -> "Il Troll si \u00e8 rigenerato mentre tu cedevi. Una battaglia di resistenza perduta.";
            case "Drago Antico"      -> "Il Fiato di Fuoco del Drago Antico ha incenerito ogni speranza.";
            default                  -> "Le tenebre ti hanno inghiottito. Rialzati e riprova.";
        };
    }

    @FXML
    private void onRestart() {
        try {
            Stage stage = (Stage) defeatLabel.getScene().getWindow();
            FXMLLoader loader = SceneNavigator.navigateTo(
                    stage, "/it/unicam/cs/mpgc/rpg123393/view/hello-view.fxml");
            HelloController ctrl = loader.getController();
            ctrl.initData(playerName, vigore, arcano, imagePath, new GameService());
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void onMainMenu() {
        try {
            Stage stage = (Stage) defeatLabel.getScene().getWindow();
            SceneNavigator.navigateTo(stage,
                    "/it/unicam/cs/mpgc/rpg123393/view/main-menu-view.fxml");
        } catch (IOException e) { e.printStackTrace(); }
    }
}
