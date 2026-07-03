package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.model.EncounterType;
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
    private int         goldDrop;
    /**
     * Tipo dell'incontro che il player ha appena completato.
     * Viene determinato PRIMA di chiamare advanceEncounter(), che sposta il cursore.
     */
    private EncounterType completedType;

    public void initData(GameService gs, int xpGained, List<String> levelUpMsgs,
                         String playerName, int vigore, int arcano, String imagePath) {
        this.gameService = gs;
        this.playerName  = playerName;
        this.vigore      = vigore;
        this.arcano      = arcano;
        this.imagePath   = imagePath;

        // Registra il tipo appena completato, poi raccoglie oro e avanza
        this.completedType = gs.currentEncounter();
        this.goldDrop      = gs.collectGoldDrop();
        // Avanza sulla mappa (marca il nodo come cleared)
        gs.advanceEncounter();

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

        // Salvataggio automatico dopo ogni vittoria
        try {
            new JsonSaveRepository().save(gs.toGameState());
        } catch (IOException e) {
            System.err.println("[WARN] Salvataggio fallito: " + e.getMessage());
        }
    }

    @FXML
    private void onNextBattle() {
        try {
            Stage stage = (Stage) xpLabel.getScene().getWindow();

            if (completedType == EncounterType.BOSS && gs().isLastBoss()) {
                // Vittoria finale — nessun nodo successivo
                SceneNavigator.navigateTo(stage,
                        "/it/unicam/cs/mpgc/rpg123393/view/main-menu-view.fxml");
                return;
            }

            if (completedType == EncounterType.BOSS) {
                // Dopo un boss intermedio: scelta carta gratis, poi mappa
                FXMLLoader loader = SceneNavigator.navigateTo(
                        stage, "/it/unicam/cs/mpgc/rpg123393/view/card-reward-view.fxml");
                CardRewardController ctrl = loader.getController();
                ctrl.initData(gameService, playerName, vigore, arcano, imagePath);
                return;
            }

            // Dopo ogni altra vittoria: mostra la mappa con i nodi disponibili
            FXMLLoader loader = SceneNavigator.navigateTo(
                    stage, "/it/unicam/cs/mpgc/rpg123393/view/map-view.fxml");
            MapController ctrl = loader.getController();
            ctrl.initData(gameService, playerName, vigore, arcano, imagePath);

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

    /** Alias interno per leggibilità. */
    private GameService gs() { return gameService; }
}
