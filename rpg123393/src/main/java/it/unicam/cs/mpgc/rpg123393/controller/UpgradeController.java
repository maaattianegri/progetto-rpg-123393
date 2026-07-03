package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.model.ICard;
import it.unicam.cs.mpgc.rpg123393.model.ShopItem;
import it.unicam.cs.mpgc.rpg123393.service.GameService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class UpgradeController {

    @FXML private FlowPane deckFlow;
    @FXML private Label    infoLabel;

    private GameService    gameService;
    private String         playerName;
    private int            vigore;
    private int            arcano;
    private String         imagePath;
    private List<ShopItem> shopItems;
    private boolean        fromRest;
    private int            upgradePrice;

    public void initData(GameService gs, String playerName,
                         int vigore, int arcano, String imagePath,
                         List<ShopItem> shopItems) {
        this.gameService  = gs;
        this.playerName   = playerName;
        this.vigore       = vigore;
        this.arcano       = arcano;
        this.imagePath    = imagePath;
        this.shopItems    = shopItems;
        this.fromRest     = false;
        this.upgradePrice = gs.getUpgradePrice();
        buildDeckList();
    }

    public void initDataFromRest(GameService gs, String playerName,
                                  int vigore, int arcano, String imagePath) {
        this.gameService  = gs;
        this.playerName   = playerName;
        this.vigore       = vigore;
        this.arcano       = arcano;
        this.imagePath    = imagePath;
        this.shopItems    = null;
        this.fromRest     = true;
        this.upgradePrice = 0;
        buildDeckList();
    }

    private void buildDeckList() {
        deckFlow.getChildren().clear();
        List<ICard> deck = gameService.getDeck();
        for (int i = 0; i < deck.size(); i++) {
            final int idx = i;
            ICard card = deck.get(i);
            String color = CardStyleHelper.borderColor(card.getName());

            Button btn = new Button(card.getName() + "  (\u2192 " + GameService.upgradedName(card.getName()) + ")");
            btn.setPrefWidth(210);
            btn.setMinWidth(210);
            btn.setPrefHeight(80);
            btn.setWrapText(true);
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
                + "-fx-alignment: center;"
            );
            btn.setOnAction(e -> upgradeCard(idx));
            deckFlow.getChildren().add(btn);
        }
    }

    private void upgradeCard(int index) {
        boolean ok = gameService.upgradeCard(index);
        if (!ok) return;
        if (upgradePrice > 0) {
            gameService.spendGold(upgradePrice);
        }
        try {
            Stage stage = (Stage) deckFlow.getScene().getWindow();
            if (fromRest) {
                FXMLLoader loader = SceneNavigator.navigateTo(
                        stage, "/it/unicam/cs/mpgc/rpg123393/view/map-view.fxml");
                MapController ctrl = loader.getController();
                ctrl.initData(gameService, playerName, vigore, arcano, imagePath);
            } else {
                FXMLLoader loader = SceneNavigator.navigateTo(
                        stage, "/it/unicam/cs/mpgc/rpg123393/view/shop-view.fxml");
                ShopController ctrl = loader.getController();
                ctrl.initDataKeepItems(gameService, playerName, vigore, arcano, imagePath, shopItems);
            }
        } catch (IOException e) {
            throw new RuntimeException("Errore navigazione dopo upgrade", e);
        }
    }

    @FXML
    private void onCancel() {
        try {
            Stage stage = (Stage) deckFlow.getScene().getWindow();
            if (fromRest) {
                FXMLLoader loader = SceneNavigator.navigateTo(
                        stage, "/it/unicam/cs/mpgc/rpg123393/view/map-view.fxml");
                MapController ctrl = loader.getController();
                ctrl.initData(gameService, playerName, vigore, arcano, imagePath);
            } else {
                FXMLLoader loader = SceneNavigator.navigateTo(
                        stage, "/it/unicam/cs/mpgc/rpg123393/view/shop-view.fxml");
                ShopController ctrl = loader.getController();
                ctrl.initDataKeepItems(gameService, playerName, vigore, arcano, imagePath, shopItems);
            }
        } catch (IOException e) {
            throw new RuntimeException("Errore navigazione onCancel", e);
        }
    }
}
