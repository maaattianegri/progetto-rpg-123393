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

        // Epilogo narrativo basato sul boss sconfitto
        epilogueLabel.setText(buildEpilogue(gs.getEnemy().getName(), playerName));

        // Statistiche
        playerStatLabel.setText("⚔  Eroe: " + playerName
                + "  (" + gs.getClassName() + ")");
        levelStatLabel.setText("⭐  Livello raggiunto: " + gs.getPlayerLevel());
        hpStatLabel.setText("❤  HP finali: "
                + p.getCurrentHp() + " / " + p.getMaxHp());

        long total   = gs.getMapService().getMap().getAllNodes().size();
        long cleared = gs.getMapService().getMap().getAllNodes().stream()
                .filter(n -> n.isCleared()).count();
        nodesStatLabel.setText("🗺  Nodi completati: " + cleared + " / " + total);
        goldStatLabel.setText("🪙  Oro totale guadagnato: " + gs.getTotalGoldEarned());
        cardsStatLabel.setText("🃏  Carte nel mazzo: " + gs.getDeck().size());
        upgradesStatLabel.setText("✨  Potenziamenti eseguiti: " + gs.getTotalUpgradesUsed());
    }

    private String buildEpilogue(String bossName, String playerName) {
        return switch (bossName) {
            case "Drago Antico" ->
                    playerName + " ha spento le fiamme del Drago Antico. " +
                    "Le sue ossa giacciono nel silenzio della cripta, " +
                    "e il suo nome risuona tra le stelle per l'eternità.";
            case "Orco Berserker" ->
                    "La furia dell'Orco Berserker si è spezzata sotto i colpi di " +
                    playerName + ". La pace torna finalmente nelle terre del Nord.";
            case "Troll Rigenerante" ->
                    "Nessuna rigenerazione ha potuto salvare il Troll dalla determinazione di " +
                    playerName + ". La palude è finalmente libera.";
            default ->
                    playerName + " ha trionfato sulle tenebre. " +
                    "La run è completa: ogni nemico è caduto, ogni sfida è stata superata.";
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
