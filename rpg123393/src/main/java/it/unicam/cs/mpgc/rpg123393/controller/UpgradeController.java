package it.unicam.cs.mpgc.rpgr123393.controller;

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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
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

    public void initDataWithPrice(GameService gs, String playerName,
                                   int vigore, int arcano, String imagePath,
                                   List<ShopItem> shopItems, int forcedPrice) {
        this.gameService  = gs;
        this.playerName   = playerName;
        this.vigore       = vigore;
        this.arcano       = arcano;
        this.imagePath    = imagePath;
        this.shopItems    = shopItems;
        this.fromRest     = false;
        this.upgradePrice = forcedPrice;
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
        this.upgradePrice = gs.getUpgradePrice();
        buildDeckList();
    }

    private void buildDeckList() {
        deckFlow.getChildren().clear();
        List<ICard> deck = gameService.getDeck();
        for (int i = 0; i < deck.size(); i++) {
            final int idx = i;
            deckFlow.getChildren().add(buildCardTile(deck.get(i), idx));
        }
    }

    private VBox buildCardTile(ICard card, int idx) {
        String color = CardStyleHelper.borderColor(card.getName());
        String upgradedName = GameService.upgradedName(card.getName());

        Label tagLabel = new Label("POTENZIAMENTO");
        tagLabel.setStyle("-fx-background-color: " + color + "; -fx-text-fill: #1a1a2e;"
                + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 3 10;"
                + "-fx-background-radius: 10;");

        Label nameLabel = new Label(card.getName());
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(180);
        nameLabel.setAlignment(Pos.CENTER);

        Label arrowLabel = new Label("\u2192  " + upgradedName);
        arrowLabel.setStyle("-fx-text-fill: #e0c97f; -fx-font-size: 13px;");
        arrowLabel.setWrapText(true);
        arrowLabel.setMaxWidth(180);
        arrowLabel.setAlignment(Pos.CENTER);

        Button upgradeBtn = new Button("Potenzia");
        upgradeBtn.setStyle("-fx-background-color: " + color + ";"
                + "-fx-text-fill: #1a1a2e; -fx-font-weight: bold;"
                + "-fx-padding: 10 24; -fx-background-radius: 8; -fx-cursor: hand;");
        upgradeBtn.setOnAction(e -> upgradeCard(idx));

        VBox box = new VBox(10, tagLabel, nameLabel, arrowLabel, upgradeBtn);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #1e1e3a; -fx-background-radius: 14;"
                + "-fx-border-color: " + color + "; -fx-border-radius: 14; -fx-border-width: 2;"
                + "-fx-padding: 24; -fx-pref-width: 210; -fx-pref-height: 300;"
                + "-fx-effect: dropshadow(gaussian, " + color + ", 10, 0.15, 0, 0);");
        return box;
    }

    private void upgradeCard(int index) {
        if (upgradePrice > 0 && gameService.getGold() < upgradePrice) {
            showAlert(Alert.AlertType.WARNING,
                    "Oro insufficiente!",
                    "Ti mancano " + (upgradePrice - gameService.getGold())
                            + " 🪙 per potenziare questa carta. (Costo: " + upgradePrice + " 🪙)");
            return;
        }

        boolean ok = gameService.upgradeCard(index);
        if (!ok) {
            showAlert(Alert.AlertType.WARNING,
                    "Potenziamento non riuscito",
                    "Questa carta non può essere potenziata ulteriormente.");
            return;
        }

        if (upgradePrice > 0) {
            gameService.spendGold(upgradePrice);
        }

        navigateBack();
    }

    private void showAlert(Alert.AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle("Fucina");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void navigateBack() {
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
        navigateBack();
    }
}
