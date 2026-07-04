package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.model.EncounterType;
import it.unicam.cs.mpgc.rpg123393.model.NodeType;
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

    // --- statistiche run (visibili solo quando wasLastBoss) ---
    @FXML private Label nodesStatLabel;
    @FXML private Label goldStatLabel;
    @FXML private Label cardsStatLabel;
    @FXML private Label upgradesStatLabel;

    private GameService   gameService;
    private String        playerName;
    private int           vigore;
    private int           arcano;
    private String        imagePath;
    private int           goldDrop;
    private EncounterType completedType;
    private boolean       wasLastBoss;

    public void initData(GameService gs, int xpGained, List<String> levelUpMsgs,
                         String playerName, int vigore, int arcano, String imagePath) {
        this.gameService = gs;
        this.playerName  = playerName;
        this.vigore      = vigore;
        this.arcano      = arcano;
        this.imagePath   = imagePath;

        // 1. Cattura stato PRIMA di muovere il cursore
        this.completedType = gs.currentEncounter();
        this.wasLastBoss   = gs.isLastBoss();
        this.goldDrop      = gs.collectGoldDrop();

        // 2. Avanza sulla mappa
        gs.advanceEncounter();

        // 3. UI base
        var p = gs.getPlayer();
        xpLabel.setText("+" + xpGained + " XP");
        hpStatLabel.setText(p.getCurrentHp() + "/" + p.getMaxHp());
        enemyStatLabel.setText(gs.getEnemy().getName());
        levelStatLabel.setText("Lv. " + gs.getPlayerLevel());

        String goldMsg = "  +" + goldDrop + " \uD83E\uDE99";
        summaryLabel.setText("Hai sconfitto " + gs.getEnemy().getName() + "!" + goldMsg);

        if (!levelUpMsgs.isEmpty()) {
            levelUpBadge.setText("LEVEL UP!");
            levelUpBadge.setVisible(true);
            levelUpBadge.setManaged(true);
        }
        int xpReq = gs.getXpRequired();
        xpProgressLabel.setText("XP verso prossimo livello: " + gs.getPlayerXp() + " / " + xpReq);

        // 4. Statistiche run complete (mostrate sempre, utili dopo ogni boss)
        if (wasLastBoss) {
            long nodesCleared = gs.getMapService().getMap().getAllNodes().stream()
                    .filter(n -> n.isCleared()).count();
            safeSet(nodesStatLabel,   "\uD83D\uDDFA Nodi completati: " + nodesCleared
                    + "/" + gs.getMapService().getMap().getAllNodes().size());
            safeSet(goldStatLabel,    "\uD83E\uDE99 Oro totale guadagnato: " + gs.getTotalGoldEarned());
            safeSet(cardsStatLabel,   "\uD83C\uDCCF Carte nel mazzo: " + p.getDeck().size());
            safeSet(upgradesStatLabel,"\u2B50 Potenziamenti eseguiti: " + gs.getTotalUpgradesUsed());
            showRunStats(true);
        } else {
            showRunStats(false);
        }

        // 5. Salvataggio automatico
        try {
            new JsonSaveRepository().save(gs.toGameState());
        } catch (IOException e) {
            System.err.println("[WARN] Salvataggio fallito: " + e.getMessage());
        }
    }

    private void showRunStats(boolean visible) {
        safeVisible(nodesStatLabel,   visible);
        safeVisible(goldStatLabel,    visible);
        safeVisible(cardsStatLabel,   visible);
        safeVisible(upgradesStatLabel,visible);
    }

    private void safeSet(Label lbl, String text) {
        if (lbl != null) lbl.setText(text);
    }

    private void safeVisible(Label lbl, boolean visible) {
        if (lbl != null) { lbl.setVisible(visible); lbl.setManaged(visible); }
    }

    @FXML
    private void onNextBattle() {
        try {
            Stage stage = (Stage) xpLabel.getScene().getWindow();

            if (completedType == EncounterType.BOSS && wasLastBoss) {
                SceneNavigator.navigateTo(stage,
                        "/it/unicam/cs/mpgc/rpg123393/view/main-menu-view.fxml");
                return;
            }

            if (completedType == EncounterType.BOSS) {
                FXMLLoader loader = SceneNavigator.navigateTo(
                        stage, "/it/unicam/cs/mpgc/rpg123393/view/card-reward-view.fxml");
                CardRewardController ctrl = loader.getController();
                ctrl.initData(gameService, playerName, vigore, arcano, imagePath);
                return;
            }

            navigateToMap(stage);

        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void onMainMenu() {
        try {
            Stage stage = (Stage) xpLabel.getScene().getWindow();
            SceneNavigator.navigateTo(stage,
                    "/it/unicam/cs/mpgc/rpg123393/view/main-menu-view.fxml");
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void navigateToMap(Stage stage) throws IOException {
        FXMLLoader loader = SceneNavigator.navigateTo(
                stage, "/it/unicam/cs/mpgc/rpg123393/view/map-view.fxml");
        MapController ctrl = loader.getController();
        ctrl.initData(gameService, playerName, vigore, arcano, imagePath);
    }
}
