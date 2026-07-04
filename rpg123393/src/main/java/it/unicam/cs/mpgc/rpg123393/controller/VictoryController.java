package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.model.EncounterType;
import it.unicam.cs.mpgc.rpg123393.persistence.JsonSaveRepository;
import it.unicam.cs.mpgc.rpg123393.service.GameService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class VictoryController {

    @FXML private Label  summaryLabel;
    @FXML private Label  xpLabel;
    @FXML private Label  levelUpBadge;
    @FXML private Label  hpStatLabel;
    @FXML private Label  enemyStatLabel;
    @FXML private Label  levelStatLabel;
    @FXML private Label  xpProgressLabel;
    @FXML private Button nextButton;

    @FXML private VBox  runStatsPanel;
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

        this.completedType = gs.currentEncounter();
        this.wasLastBoss   = gs.isLastBoss();
        this.goldDrop      = gs.collectGoldDrop();

        gs.advanceEncounter();

        var p = gs.getPlayer();
        xpLabel.setText("+" + xpGained + " XP");
        hpStatLabel.setText(p.getCurrentHp() + "/" + p.getMaxHp());
        enemyStatLabel.setText(gs.getEnemy().getName());
        levelStatLabel.setText("Lv. " + gs.getPlayerLevel());

        summaryLabel.setText("Hai sconfitto " + gs.getEnemy().getName() + "!  +" + goldDrop + " \uD83E\uDE99");

        if (!levelUpMsgs.isEmpty()) {
            levelUpBadge.setText("LEVEL UP!");
            levelUpBadge.setVisible(true);
            levelUpBadge.setManaged(true);
        }
        xpProgressLabel.setText("XP verso prossimo livello: " + gs.getPlayerXp() + " / " + gs.getXpRequired());

        if (wasLastBoss) {
            // Cambia testo bottone per l'ultimo boss
            nextButton.setText("\u2727  Vedi il Riepilogo Finale");
            nextButton.setStyle(
                    "-fx-background-color: #ffd700; -fx-text-fill: #0d0d1a;"
                    + "-fx-font-size: 16px; -fx-font-weight: bold;"
                    + "-fx-padding: 14 32; -fx-background-radius: 10; -fx-cursor: hand;"
                    + "-fx-effect: dropshadow(gaussian, #ffd700, 14, 0.5, 0, 0);");

            // Mostra statistiche run
            long total   = gs.getMapService().getMap().getAllNodes().size();
            long cleared = gs.getMapService().getMap().getAllNodes().stream()
                    .filter(n -> n.isCleared()).count();
            nodesStatLabel.setText("\uD83D\uDDFA Nodi completati: " + cleared + "/" + total);
            goldStatLabel.setText("\uD83E\uDE99 Oro totale guadagnato: " + gs.getTotalGoldEarned());
            cardsStatLabel.setText("\uD83C\uDCCF Carte nel mazzo: " + gs.getDeck().size());
            upgradesStatLabel.setText("\u2B50 Potenziamenti eseguiti: " + gs.getTotalUpgradesUsed());
            runStatsPanel.setVisible(true);
            runStatsPanel.setManaged(true);
            // Cancella il save: la run è finita
            new JsonSaveRepository().deleteSave();
        } else {
            // Run in corso: salva normalmente
            try {
                new JsonSaveRepository().save(gs.toGameState());
            } catch (IOException e) {
                System.err.println("[WARN] Salvataggio fallito: " + e.getMessage());
            }
        }
    }

    @FXML
    private void onNextBattle() {
        try {
            Stage stage = (Stage) xpLabel.getScene().getWindow();

            // Boss finale: vai alla schermata vittoria run dedicata
            if (completedType == EncounterType.BOSS && wasLastBoss) {
                FXMLLoader loader = SceneNavigator.navigateTo(
                        stage, "/it/unicam/cs/mpgc/rpg123393/view/run-victory-view.fxml");
                RunVictoryController ctrl = loader.getController();
                ctrl.initData(gameService, playerName);
                return;
            }

            // Boss intermedio: ricompensa carta
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
