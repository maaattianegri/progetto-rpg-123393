package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.model.CardPool;
import it.unicam.cs.mpgc.rpg123393.model.ICard;
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

public class CardRewardController {

    @FXML private HBox cardsBox;

    private GameService gameService;
    private String      playerName;
    private int         vigore;
    private int         arcano;
    private String      imagePath;
    private List<ICard> options;

    public void initData(GameService gameService, String playerName,
                         int vigore, int arcano, String imagePath) {
        this.gameService = gameService;
        this.playerName  = playerName;
        this.vigore      = vigore;
        this.arcano      = arcano;
        this.imagePath   = imagePath;
        options = CardPool.getRewardOptions(gameService.getClassName());
        buildCardButtons();
    }

    private void buildCardButtons() {
        cardsBox.getChildren().clear();
        for (ICard card : options)
            cardsBox.getChildren().add(buildCardNode(card));
    }

    private VBox buildCardNode(ICard card) {
        String color  = CardStyleHelper.borderColor(card.getName());
        String symbol = CardStyleHelper.symbol(card.getName());

        Label symbolLabel = new Label(symbol);
        symbolLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        Label nameLabel = new Label(card.getName());
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        nameLabel.setWrapText(true); nameLabel.setMaxWidth(180); nameLabel.setAlignment(Pos.CENTER);

        Label costLabel = new Label("Mana: " + card.getManaCost());
        costLabel.setStyle("-fx-text-fill: #a78bfa; -fx-font-size: 13px;");

        Label descLabel = new Label(CardStyleHelper.description(card.getName()));
        descLabel.setStyle("-fx-text-fill: #a0a0c0; -fx-font-size: 12px;");
        descLabel.setWrapText(true); descLabel.setMaxWidth(180); descLabel.setAlignment(Pos.CENTER);

        Button pickBtn = new Button("Scegli");
        pickBtn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: #1a1a2e;"
                + "-fx-font-weight: bold; -fx-padding: 10 28; -fx-background-radius: 8; -fx-cursor: hand;"
                + "-fx-effect: dropshadow(gaussian, " + color + ", 10, 0.3, 0, 0);");
        pickBtn.setOnAction(e -> pickCard(card));

        VBox box = new VBox(14, symbolLabel, nameLabel, costLabel, descLabel, pickBtn);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #1e1e3a; -fx-background-radius: 14;"
                + "-fx-border-color: " + color + "; -fx-border-radius: 14; -fx-border-width: 2;"
                + "-fx-padding: 28; -fx-pref-width: 200; -fx-pref-height: 320;"
                + "-fx-effect: dropshadow(gaussian, " + color + ", 12, 0.2, 0, 0);");
        return box;
    }

    private void pickCard(ICard card) {
        gameService.addCardToDeck(card);
        gameService.unlockCard(card.getName());
        navigateToMap();
    }

    @FXML private void onSkip() { navigateToMap(); }

    /** Dopo la scelta carta si torna sempre alla mappa. */
    private void navigateToMap() {
        try {
            Stage stage = (Stage) cardsBox.getScene().getWindow();
            FXMLLoader loader = SceneNavigator.navigateTo(
                    stage, "/it/unicam/cs/mpgc/rpg123393/view/map-view.fxml");
            MapController ctrl = loader.getController();
            ctrl.initData(gameService, playerName, vigore, arcano, imagePath);
        } catch (IOException e) { e.printStackTrace(); }
    }
}
