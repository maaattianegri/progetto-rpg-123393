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

    @FXML private Label goldLabel;
    @FXML private HBox  itemsBox;

    private GameService    gameService;
    private String         playerName;
    private int            vigore;
    private int            arcano;
    private String         imagePath;
    private List<ShopItem> items;
    private ShopItem       pendingUpgradeItem;

    public void initData(GameService gs, String playerName,
                         int vigore, int arcano, String imagePath) {
        this.gameService = gs;
        this.playerName  = playerName;
        this.vigore      = vigore;
        this.arcano      = arcano;
        this.imagePath   = imagePath;
        if (items == null) items = gs.generateShopItems();
        refresh();
    }

    public void initDataKeepItems(GameService gs, String playerName,
                                   int vigore, int arcano, String imagePath,
                                   List<ShopItem> existingItems) {
        this.gameService = gs;
        this.playerName  = playerName;
        this.vigore      = vigore;
        this.arcano      = arcano;
        this.imagePath   = imagePath;
        this.items       = existingItems;
        if (pendingUpgradeItem != null) {
            items.remove(pendingUpgradeItem);
            pendingUpgradeItem = null;
        }
        refresh();
    }

    private void refresh() {
        goldLabel.setText("\uD83E\uDE99  " + gameService.getGold() + " oro");
        itemsBox.getChildren().clear();
        for (ShopItem item : items) itemsBox.getChildren().add(buildItemTile(item));
    }

    /**
     * Aggiorna solo la label dell'oro e lo stato disabilitato dei bottoni,
     * senza ricostruire l'intera lista di tile.
     */
    private void refreshGoldAndButtons() {
        goldLabel.setText("\uD83E\uDE99  " + gameService.getGold() + " oro");
        itemsBox.getChildren().forEach(node -> {
            if (node instanceof VBox tile) {
                // L'ultimo figlio di ogni tile è il bottone "Acquista"
                tile.getChildren().stream()
                    .filter(n -> n instanceof Button)
                    .map(n -> (Button) n)
                    .findFirst()
                    .ifPresent(btn -> {
                        // Il prezzo è nel tag userData impostato in buildItemTile
                        if (btn.getUserData() instanceof Integer price) {
                            boolean canAfford = gameService.getGold() >= price;
                            btn.setDisable(!canAfford);
                            // Aggiorna anche il colore del bottone
                            String color = (String) tile.getUserData();
                            if (color != null) {
                                btn.setStyle("-fx-background-color: " + (canAfford ? color : "#3a3a5a") + ";"
                                        + "-fx-text-fill: " + (canAfford ? "#1a1a2e" : "#6a6a9a") + ";"
                                        + "-fx-font-weight: bold; -fx-padding: 10 24;"
                                        + "-fx-background-radius: 8; -fx-cursor: hand;");
                            }
                        }
                    });
            }
        });
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
                + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 3 10;"
                + "-fx-background-radius: 10;");

        Label nameLabel = new Label(item.getName());
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");
        nameLabel.setWrapText(true); nameLabel.setMaxWidth(180); nameLabel.setAlignment(Pos.CENTER);

        Label descLabel = new Label(item.getDescription());
        descLabel.setStyle("-fx-text-fill: #a0a0c0; -fx-font-size: 12px;");
        descLabel.setWrapText(true); descLabel.setMaxWidth(180); descLabel.setAlignment(Pos.CENTER);

        Label priceLabel = new Label("\uD83E\uDE99 " + item.getPrice() + " oro");
        priceLabel.setStyle("-fx-text-fill: #e0c97f; -fx-font-size: 14px; -fx-font-weight: bold;");

        // canAfford calcolato live al momento della costruzione iniziale
        boolean canAfford = gameService.getGold() >= item.getPrice();
        Button buyBtn = new Button("Acquista");
        buyBtn.setDisable(!canAfford);
        // Salva prezzo e colore nel bottone/tile per refreshGoldAndButtons()
        buyBtn.setUserData(item.getPrice());
        buyBtn.setStyle("-fx-background-color: " + (canAfford ? color : "#3a3a5a") + ";"
                + "-fx-text-fill: " + (canAfford ? "#1a1a2e" : "#6a6a9a") + ";"
                + "-fx-font-weight: bold; -fx-padding: 10 24; -fx-background-radius: 8; -fx-cursor: hand;");
        buyBtn.setOnAction(e -> handleBuy(item, buyBtn));

        VBox box = new VBox(10, tagLabel, nameLabel, descLabel, priceLabel, buyBtn);
        box.setAlignment(Pos.CENTER);
        // Salva il colore nella tile per refreshGoldAndButtons()
        box.setUserData(color);
        box.setStyle("-fx-background-color: #1e1e3a; -fx-background-radius: 14;"
                + "-fx-border-color: " + color + "; -fx-border-radius: 14; -fx-border-width: 2;"
                + "-fx-padding: 24; -fx-pref-width: 210; -fx-pref-height: 300;"
                + "-fx-effect: dropshadow(gaussian, " + color + ", 10, 0.15, 0, 0);");
        return box;
    }

    private void handleBuy(ShopItem item, Button btn) {
        // Check live al momento del click — non si fida del canAfford statico del bottone
        if (gameService.getGold() < item.getPrice()) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setHeaderText(null);
            a.setContentText("Oro insufficiente!");
            a.showAndWait();
            return;
        }
        boolean ok = gameService.buyItem(item);
        if (!ok) return;
        if (item.getType() == ShopItem.ItemType.UPGRADE) {
            pendingUpgradeItem = item;
            openUpgradeView(item);
        } else {
            items.remove(item);
            refresh();
        }
    }

    private void openUpgradeView(ShopItem upgradeItem) {
        try {
            Stage stage = (Stage) itemsBox.getScene().getWindow();
            FXMLLoader loader = SceneNavigator.navigateTo(
                    stage, "/it/unicam/cs/mpgc/rpg123393/view/upgrade-view.fxml");
            UpgradeController ctrl = loader.getController();
            ctrl.initData(gameService, playerName, vigore, arcano, imagePath, items);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void onContinue() {
        try {
            Stage stage = (Stage) itemsBox.getScene().getWindow();
            FXMLLoader loader = SceneNavigator.navigateTo(
                    stage, "/it/unicam/cs/mpgc/rpg123393/view/map-view.fxml");
            MapController ctrl = loader.getController();
            ctrl.initData(gameService, playerName, vigore, arcano, imagePath);
        } catch (IOException e) { e.printStackTrace(); }
    }
}
