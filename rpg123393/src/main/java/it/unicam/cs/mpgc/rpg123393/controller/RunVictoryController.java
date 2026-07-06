package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.service.GameService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class RunVictoryController {

    @FXML private Label titleLabel;
    @FXML private Label subtitleLabel;
    @FXML private Label epilogueLabel;

    @FXML private Label playerStatLabel;
    @FXML private Label levelStatLabel;
    @FXML private Label hpStatLabel;
    @FXML private Label nodesStatLabel;
    @FXML private Label goldStatLabel;
    @FXML private Label cardsStatLabel;
    @FXML private Label upgradesStatLabel;

    public void initData(GameService gs, String playerName) {
        var p = gs.getPlayer();

        epilogueLabel.setText(buildEpilogue(gs.getEnemy().getName(), playerName));

        playerStatLabel.setText("\u2694  Eroe: " + playerName + "  (" + gs.getClassName() + ")");
        levelStatLabel.setText("\u2b50  Livello raggiunto: " + gs.getPlayerLevel());
        hpStatLabel.setText("\u2764  HP finali: " + p.getCurrentHp() + " / " + p.getMaxHp());

        long total   = gs.getMapService().getMap().getAllNodes().size();
        long cleared = gs.getMapService().getMap().getAllNodes().stream()
                .filter(n -> n.isCleared()).count();
        nodesStatLabel.setText("\uD83D\uDDFA  Nodi completati: " + cleared + " / " + total);
        goldStatLabel.setText("\uD83E\uDE99  Oro totale guadagnato: " + gs.getTotalGoldEarned());
        cardsStatLabel.setText("\uD83C\uDCCF  Carte nel mazzo: " + gs.getDeck().size());
        upgradesStatLabel.setText("\u2728  Potenziamenti eseguiti: " + gs.getTotalUpgradesUsed());

        // Hook achievement: run completata
        gs.getAchievementService().onRunCompleted(
                gs.getClassName(),
                (int) cleared,
                gs.getGold(),
                gs.getTotalGoldEarned(),
                gs.getUnlockedCards().size(),
                p.getCurrentHp() == p.getMaxHp()
        );
    }

    private String buildEpilogue(String bossName, String playerName) {
        return switch (bossName) {
            case "Drago Antico" ->
                    playerName + " ha spento le fiamme del Drago Antico. " +
                    "Le sue ossa giacciono nel silenzio della cripta, " +
                    "e il suo nome risuona tra le stelle per l'eternit\u00e0.";
            case "Orco Berserker" ->
                    "La furia dell'Orco Berserker si \u00e8 spezzata sotto i colpi di " +
                    playerName + ". La pace torna finalmente nelle terre del Nord.";
            case "Troll Rigenerante" ->
                    "Nessuna rigenerazione ha potuto salvare il Troll dalla determinazione di " +
                    playerName + ". La palude \u00e8 finalmente libera.";
            default ->
                    playerName + " ha trionfato sulle tenebre. " +
                    "La run \u00e8 completa: ogni nemico \u00e8 caduto, ogni sfida \u00e8 stata superata.";
        };
    }

    @FXML
    private void onMainMenu() {
        try {
            Stage stage = (Stage) titleLabel.getScene().getWindow();
            SceneNavigator.navigateTo(stage,
                    "/it/unicam/cs/mpgc/rpg123393/view/main-menu-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
