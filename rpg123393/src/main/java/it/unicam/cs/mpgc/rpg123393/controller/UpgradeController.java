package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.model.ICard;
import it.unicam.cs.mpgc.rpg123393.service.GameService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class UpgradeController {

    @FXML private FlowPane deckFlow;
    @FXML private Label    infoLabel;

    private GameService gameService;
    private String      playerName;
    private int         vigore;
    private int         arcano;
    private String      imagePath;

    public void initData(GameService gs, String playerName,
                         int vigore, int arcano, String imagePath) {
        this.gameService = gs;
        this.playerName  = playerName;
        this.vigore      = vigore;
        this.arcano      = arcano;
        this.imagePath   = imagePath;
        buildDeck();
    }

    private void buildDeck() {
        deckFlow.getChildren().clear();
        List<ICard> deck = gameService.getDeck();
        for (int i = 0; i < deck.size(); i++) {
            ICard card = deck.get(i);
            boolean canUpgrade = it.unicam.cs.mpgc.rpg123393.model.CardPool
                    .getUpgradedCard(card.getName()) != null;
            deckFlow.getChildren().add(buildTile(card, i, canUpgrade));
        }
    }

    private VBox buildTile(ICard card, int index, boolean canUpgrade) {
        String color = CardStyleHelper.borderColor(card.getName());

        Label nameLabel = new Label(card.getName());
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");
        nameLabel.setWrapText(true); nameLabel.setMaxWidth(140);

        Label descLabel = new Label(CardStyleHelper.description(card.getName()));
        descLabel.setStyle("-fx-text-fill: #a0a0c0; -fx-font-size: 11px;");
        descLabel.setWrapText(true); descLabel.setMaxWidth(140);

        Button upgradeBtn = new Button(canUpgrade ? "Potenzia \u2191" : "Max");
        upgradeBtn.setDisable(!canUpgrade);
        upgradeBtn.setStyle("-fx-background-color: " + (canUpgrade ? "#e67e22" : "#3a3a5a") + ";"
                + "-fx-text-fill: " + (canUpgrade ? "#1a1a2e" : "#6a6a9a") + ";"
                + "-fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
        upgradeBtn.setOnAction(e -> {
            if (gameService.upgradeCard(index)) {
                infoLabel.setText(card.getName() + " → " + GameService.upgradedName(card.getName()) + " ✓");
                buildDeck();
                goBackToShop();
            }
        });

        VBox box = new VBox(8, nameLabel, descLabel, upgradeBtn);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #1e1e3a; -fx-background-radius: 10;"
                + "-fx-border-color: " + color + "; -fx-border-radius: 10; -fx-border-width: 2;"
                + "-fx-padding: 14; -fx-pref-width: 155; -fx-pref-height: 170;");
        return box;
    }

    private void goBackToShop() {
        try {
            Stage stage = (Stage) deckFlow.getScene().getWindow();
            FXMLLoader loader = SceneNavigator.navigateTo(
                    stage, "/it/unicam/cs/mpgc/rpg123393/view/shop-view.fxml");
            ShopController ctrl = loader.getController();
            ctrl.initData(gameService, playerName, vigore, arcano, imagePath);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void onCancel() { goBackToShop(); }
}
