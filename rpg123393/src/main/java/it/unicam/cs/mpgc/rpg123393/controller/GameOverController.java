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

    @FXML private Label nodesStatLabel;
    @FXML private Label goldStatLabel;
    @FXML private Label cardsStatLabel;
    @FXML private Label upgradesStatLabel;

    private String playerName;
    private int    vigore;
    private int    arcano;
    private String imagePath;
    private String className;  // necessario per ricreare il GameService correttamente in onRestart

    public void initData(GameService gameService, String playerName,
                         int vigore, int arcano, String imagePath, String className) {
        this.playerName = playerName;
        this.vigore     = vigore;
        this.arcano     = arcano;
        this.imagePath  = imagePath;
        this.className  = className;

        var p = gameService.getPlayer();
        var e = gameService.getEnemy();

        defeatLabel.setText("Sei stato sconfitto da " + e.getName() + "...");
        epilogueLabel.setText(buildEpilogue(e.getName()));
        hpStatLabel.setText(p.getCurrentHp() + "/" + p.getMaxHp());
        enemyStatLabel.setText(e.getName());
        levelStatLabel.setText("Lv. " + gameService.getPlayerLevel());

        long nodesCleared = gameService.getMapService().getMap().getAllNodes().stream()
                .filter(n -> n.isCleared()).count();

        safeSet(nodesStatLabel,    "Nodi completati: " + nodesCleared);
        safeSet(goldStatLabel,     "\uD83E\uDE99 Oro accumulato: " + gameService.getTotalGoldEarned());
        safeSet(cardsStatLabel,    "\uD83C\uDCCF Carte nel mazzo: " + gameService.getDeck().size());
        safeSet(upgradesStatLabel, "\u2B50 Potenziamenti usati: " + gameService.getTotalUpgradesUsed());
    }

    private void safeSet(Label lbl, String text) {
        if (lbl != null) lbl.setText(text);
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
            // Usa l'overload con className: crea un GameService nuovo ma inizializzato
            // correttamente con classe, stats e mazzo di partenza del personaggio.
            // NON usare il terzo overload (GameService esistente) perche' passerebbe
            // un GameService vuoto che causa carte/nemico placeholder.
            ctrl.initData(playerName, vigore, arcano, imagePath, className);
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
