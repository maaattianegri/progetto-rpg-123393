package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.model.ICard;
import it.unicam.cs.mpgc.rpg123393.model.ShopItem;
import it.unicam.cs.mpgc.rpg123393.service.GameService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class UpgradeController {

    @FXML private VBox deckList;

    private GameService    gameService;
    private String         playerName;
    private int            vigore;
    private int            arcano;
    private String         imagePath;
    private List<ShopItem> shopItems;   // presente solo se arriva da ShopController
    private boolean        fromRest;    // true se arriva da RestController

    /** Chiamato da ShopController quando si acquista un upgrade. */
    public void initData(GameService gs, String playerName,
                         int vigore, int arcano, String imagePath,
                         List<ShopItem> shopItems) {
        this.gameService = gs;
        this.playerName  = playerName;
        this.vigore      = vigore;
        this.arcano      = arcano;
        this.imagePath   = imagePath;
        this.shopItems   = shopItems;
        this.fromRest    = false;
        buildDeckList();
    }

    /** Chiamato da RestController per la scelta "potenzia carta". */
    public void initDataFromRest(GameService gs, String playerName,
                                  int vigore, int arcano, String imagePath) {
        this.gameService = gs;
        this.playerName  = playerName;
        this.vigore      = vigore;
        this.arcano      = arcano;
        this.imagePath   = imagePath;
        this.shopItems   = null;
        this.fromRest    = true;
        buildDeckList();
    }

    private void buildDeckList() {
        deckList.getChildren().clear();
        List<ICard> deck = gameService.getDeck();
        for (int i = 0; i < deck.size(); i++) {
            final int idx = i;
            ICard card = deck.get(i);
            String color = CardStyleHelper.borderColor(card.getName());

            Button btn = new Button(card.getName() + "  (→ " + GameService.upgradedName(card.getName()) + ")");
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setStyle(
                "-fx-background-color: #1e1e3a;"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 13px;"
                + "-fx-border-color: " + color + ";"
                + "-fx-border-radius: 8;"
                + "-fx-background-radius: 8;"
                + "-fx-border-width: 2;"
                + "-fx-padding: 10 18;"
                + "-fx-cursor: hand;"
            );
            btn.setOnAction(e -> upgradeCard(idx));
            deckList.getChildren().add(btn);
        }
    }

    private void upgradeCard(int index) {
        boolean ok = gameService.upgradeCard(index);
        if (!ok) return;
        try {
            Stage stage = (Stage) deckList.getScene().getWindow();
            if (fromRest) {
                // Torna alla mappa direttamente
                FXMLLoader loader = SceneNavigator.navigateTo(
                        stage, "/it/unicam/cs/mpgc/rpg123393/view/map-view.fxml");
                MapController ctrl = loader.getController();
                ctrl.initData(gameService, playerName, vigore, arcano, imagePath);
            } else {
                // Torna allo shop con la lista aggiornata
                FXMLLoader loader = SceneNavigator.navigateTo(
                        stage, "/it/unicam/cs/mpgc/rpg123393/view/shop-view.fxml");
                ShopController ctrl = loader.getController();
                ctrl.initDataKeepItems(gameService, playerName, vigore, arcano, imagePath, shopItems);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}
