package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.model.EncounterType;
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
    private boolean     isBoss;

    public void initData(GameService gs, int xpGained, List<String> levelUpMsgs,
                         String playerName, int vigore, int arcano, String imagePath) {
        this.gameService = gs;
        this.playerName  = playerName;
        this.vigore      = vigore;
        this.arcano      = arcano;
        this.imagePath   = imagePath;

        // Determina tipo incontro appena completato, poi avanza
        EncounterType type = gs.advanceEncounter();
        this.isBoss     = (type == EncounterType.BOSS);
        this.goldDrop   = gs.collectGoldDrop();

        var p = gs.getPlayer();

        xpLabel.setText("+" + xpGained + " XP");
        hpStatLabel.setText(p.getCurrentHp() + "/" + p.getMaxHp());
        enemyStatLabel.setText(gs.getEnemy().getName());
        levelStatLabel.setText("Lv. " + gs.getPlayerLevel());

        String goldMsg = "  +" + goldDrop + " \uD83E\uDE99";
        summaryLabel.setText("Hai sconfitto " + gs.getEnemy().getName() + "!" + goldMsg);

        if (!levelUpMsgs.isEmpty()) {
            levelUpBadge.setText("LEVEL UP!");
            levelUpBadge.setVisible(true); levelUpBadge.setManaged(true);
        }
        int xpReq = gs.getXpRequired();
        xpProgressLabel.setText("XP verso prossimo livello: " + gs.getPlayerXp() + " / " + xpReq);
    }

    @FXML
    private void onNextBattle() {
        try {
            Stage stage = (Stage) xpLabel.getScene().getWindow();
            EncounterType next = gameService.currentEncounter();

            if (next == EncounterType.SHOP) {
                // Naviga allo shop
                FXMLLoader loader = SceneNavigator.navigateTo(
                        stage, "/it/unicam/cs/mpgc/rpg123393/view/shop-view.fxml");
                ShopController ctrl = loader.getController();
                ctrl.initData(gameService, playerName, vigore, arcano, imagePath);
            } else if (isBoss) {
                // Dopo un boss: scelta carta gratis
                FXMLLoader loader = SceneNavigator.navigateTo(
                        stage, "/it/unicam/cs/mpgc/rpg123393/view/card-reward-view.fxml");
                CardRewardController ctrl = loader.getController();
                ctrl.initData(gameService, playerName, vigore, arcano, imagePath);
            } else {
                // Normale/Elite: vai direttamente alla prossima battaglia
                FXMLLoader loader = SceneNavigator.navigateTo(
                        stage, "/it/unicam/cs/mpgc/rpg123393/view/hello-view.fxml");
                HelloController ctrl = loader.getController();
                ctrl.initData(playerName, vigore, arcano, imagePath, gameService);
            }
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
}
