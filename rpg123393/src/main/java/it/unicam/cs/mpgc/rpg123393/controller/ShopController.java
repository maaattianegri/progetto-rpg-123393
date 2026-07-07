package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.model.ICard;
import it.unicam.cs.mpgc.rpg123393.service.GameService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ShopController {

    @FXML private VBox  rootPane;
    @FXML private VBox  shopItemList;
    @FXML private Label goldLabel;

    private GameService gameService;
    private String      playerName;
    private int         vigore;
    private int         arcano;
    private String      classKey;

    @FXML
    public void initialize() {
        ImageLoaderHelper.applyBackground(rootPane, ImageLoaderHelper.backgroundPath("shop"));
    }

    public void initData(GameService gs, String playerName, int vigore, int arcano, String classKey) {
        this.gameService  = gs;
        this.playerName   = playerName;
        this.vigore       = vigore;
        this.arcano       = arcano;
        this.classKey     = classKey;
        refreshGold();
        populateShop();
    }

    private void refreshGold() {
        goldLabel.setText("Oro: " + gameService.getPlayer().getGold());
    }

    private void populateShop() {
        shopItemList.getChildren().clear();
        List<ICard> offers = gameService.getShopOffers();
        for (ICard card : offers) {
            shopItemList.getChildren().add(buildShopRow(card));
        }
    }

    private HBox buildShopRow(ICard card) {
        String color = CardStyleHelper.borderColor(card.getName());
        int price = gameService.getCardPrice(card);

        Label nameLabel = new Label(card.getName());
        nameLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 14px; -fx-font-weight: bold;");

        Label descLabel = new Label(CardStyleHelper.description(card.getName()));
        descLabel.setStyle("-fx-text-fill: #a0a0c0; -fx-font-size: 12px;");

        Label priceLabel = new Label(price + " oro");
        priceLabel.setStyle("-fx-text-fill: #ffd166; -fx-font-size: 13px; -fx-font-weight: bold;");

        Button buyBtn = new Button("Acquista");
        buyBtn.setStyle("-fx-background-color: #e0c97f; -fx-text-fill: #1a1a2e;"
                + "-fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
        buyBtn.setOnAction(e -> {
            if (gameService.buyCard(card)) {
                buyBtn.setDisable(true);
                buyBtn.setText("Acquistato");
                refreshGold();
            } else {
                priceLabel.setText("Oro insufficiente!");
                priceLabel.setStyle("-fx-text-fill: #e63946; -fx-font-size: 12px;");
            }
        });

        VBox info = new VBox(3, nameLabel, descLabel);
        HBox row = new HBox(16, info, priceLabel, buyBtn);
        row.setStyle("-fx-background-color: rgba(30,30,60,0.88); -fx-padding: 14 20;"
                + "-fx-background-radius: 10; -fx-border-color: " + color
                + "; -fx-border-radius: 10; -fx-border-width: 1;"
                + "-fx-alignment: CENTER_LEFT;");
        javafx.scene.layout.HBox.setHgrow(info, javafx.scene.layout.Priority.ALWAYS);
        return row;
    }

    @FXML
    private void onLeave() {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            FXMLLoader loader = SceneNavigator.navigateTo(
                    stage, "/it/unicam/cs/mpgc/rpg123393/view/map-view.fxml");
            MapController ctrl = loader.getController();
            ctrl.initData(playerName, vigore, arcano, classKey, gameService);
        } catch (IOException e) { e.printStackTrace(); }
    }
}
