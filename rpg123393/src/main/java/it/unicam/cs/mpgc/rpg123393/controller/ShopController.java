package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.model.ICard;
import it.unicam.cs.mpgc.rpg123393.model.ShopItem;
import it.unicam.cs.mpgc.rpg123393.service.GameService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ShopController {

    @FXML private VBox  rootPane;
    @FXML private Label goldLabel;
    @FXML private HBox  itemsBox;

    private GameService    gameService;
    private String         playerName;
    private int            vigore;
    private int            arcano;
    private String         imagePath;
    private List<ShopItem> items;

    public void initData(GameService gs, String playerName,
                         int vigore, int arcano, String imagePath) {
        this.gameService = gs;
        this.playerName  = playerName;
        this.vigore      = vigore;
        this.arcano      = arcano;
        this.imagePath   = imagePath;
        ImageLoaderHelper.applyBackground(rootPane, ImageLoaderHelper.backgroundPath("shop"));
        items = gs.generateShopItems();
        refresh();
    }

    @FXML
    public void initialize() {
        // Forza l'HBox a espandersi quanto il viewport del ScrollPane
        itemsBox.prefWidthProperty().bind(
            itemsBox.parentProperty().flatMap(p ->
                p instanceof javafx.scene.layout.Region r
                    ? r.widthProperty().map(w -> w)
                    : javafx.beans.binding.Bindings.createObjectBinding(() -> 0.0)
            )
        );
    }

    private void refresh() {
        goldLabel.setText("\uD83E\uDE99  " + gameService.getGold() + " oro");
        itemsBox.getChildren().clear();
        for (ShopItem item : items) itemsBox.getChildren().add(buildItemTile(item));
    }

    private String itemColor(ShopItem item) {
        return switch (item.getType()) {
            case CARD -> {
                Object payload = item.getPayload();
                if (payload instanceof ICard card)
                    yield CardStyleHelper.borderColor(card.getName());
                yield "#c77dff";
            }
            case RELIC      -> "#e0c97f";
            case CONSUMABLE -> "#4ecca3";
            case UPGRADE    -> "#e67e22";
        };
    }

    private VBox buildItemTile(ShopItem item) {
        String color = itemColor(item);
        String typeTag = switch (item.getType()) {
            case CARD       -> "CARTA";
            case RELIC      -> "RELIQUIA";
            case CONSUMABLE -> "CONSUMABILE";
            case UPGRADE    -> "UPGRADE";
        };

        Label tagLabel = new Label(typeTag);
        tagLabel.setStyle("-fx-background-color: " + color + "; -fx-text-fill: #1a1a2e;"
                + "-fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4 14;"
                + "-fx-background-radius: 10;");

        Label nameLabel = new Label(item.getName());
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(230);
        nameLabel.setAlignment(Pos.CENTER);

        Label descLabel = new Label(item.getDescription());
        descLabel.setStyle("-fx-text-fill: #a0a0c0; -fx-font-size: 13px;");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(230);
        descLabel.setAlignment(Pos.CENTER);

        Label priceLabel = new Label("\uD83E\uDE99 " + item.getPrice() + " oro");
        priceLabel.setStyle("-fx-text-fill: #e0c97f; -fx-font-size: 16px; -fx-font-weight: bold;");

        boolean canAfford = gameService.getGold() >= item.getPrice();
        Button buyBtn = new Button("Acquista");
        buyBtn.setDisable(!canAfford);
        buyBtn.setPrefWidth(210);
        buyBtn.setStyle("-fx-background-color: " + (canAfford ? color : "#3a3a5a") + ";"
                + "-fx-text-fill: " + (canAfford ? "#1a1a2e" : "#6a6a9a") + ";"
                + "-fx-font-weight: bold; -fx-font-size: 14px;"
                + "-fx-padding: 12 24; -fx-background-radius: 8; -fx-cursor: hand;");
        buyBtn.setOnAction(e -> handleBuy(item, buyBtn));

        VBox box = new VBox(14, tagLabel, nameLabel, descLabel, priceLabel, buyBtn);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(30,30,58,0.88); -fx-background-radius: 18;"
                + "-fx-border-color: " + color + "; -fx-border-radius: 18; -fx-border-width: 2;"
                + "-fx-padding: 36 30; -fx-pref-width: 270; -fx-pref-height: 390;"
                + "-fx-effect: dropshadow(gaussian, " + color + ", 18, 0.25, 0, 0);");
        return box;
    }

    private void handleBuy(ShopItem item, Button btn) {
        boolean ok = gameService.buyItem(item);
        if (!ok) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setHeaderText(null);
            a.setContentText("Oro insufficiente!");
            a.showAndWait();
            return;
        }
        if (item.getType() == ShopItem.ItemType.UPGRADE) {
            openUpgradeView();
        } else {
            items.remove(item);
            refresh();
        }
    }

    private void openUpgradeView() {
        try {
            Stage stage = (Stage) itemsBox.getScene().getWindow();
            FXMLLoader loader = SceneNavigator.navigateTo(
                    stage, "/it/unicam/cs/mpgc/rpg123393/view/upgrade-view.fxml");
            UpgradeController ctrl = loader.getController();
            if (ctrl == null) throw new IllegalStateException(
                    "UpgradeController non inizializzato");
            ctrl.initDataWithPrice(gameService, playerName, vigore, arcano, imagePath,
                    items, gameService.getUpgradePrice());
        } catch (IOException e) {
            throw new RuntimeException("Impossibile aprire upgrade-view.fxml", e);
        }
    }

    @FXML
    private void onContinue() {
        try {
            Stage stage = (Stage) itemsBox.getScene().getWindow();
            FXMLLoader loader = SceneNavigator.navigateTo(
                    stage, "/it/unicam/cs/mpgc/rpg123393/view/map-view.fxml");
            MapController ctrl = loader.getController();
            if (ctrl == null) throw new IllegalStateException(
                    "MapController non inizializzato");
            ctrl.initData(gameService, playerName, vigore, arcano, imagePath);
        } catch (IOException e) {
            throw new RuntimeException("Impossibile aprire map-view.fxml", e);
        }
    }
}
