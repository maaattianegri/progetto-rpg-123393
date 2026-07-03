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

    /** true dopo che un upgrade e' stato eseguito in questa sessione della fucina. */
    private boolean        upgradeDone = false;

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

        if (infoLabel != null) {
            String priceInfo = upgradePrice > 0
                    ? "Costo potenziamento: " + upgradePrice + " \uD83E\uDE99   |   Oro disponibile: " + gameService.getGold() + " \uD83E\uDE99"
                    : "Potenziamento gratuito   |   Oro disponibile: " + gameService.getGold() + " \uD83E\uDE99";
            if (upgradeDone) priceInfo = "\u2705 Potenziamento eseguito! Puoi tornare allo shop.";
            infoLabel.setText(priceInfo);
        }

        List<ICard> deck = gameService.getDeck();
        for (int i = 0; i < deck.size(); i++) {
            final int idx = i;
            deckFlow.getChildren().add(buildCardTile(deck.get(i), idx));
        }
    }

    private VBox buildCardTile(ICard card, int idx) {
        boolean alreadyUpgraded = card.getName().endsWith("+");
        boolean canUpgrade      = !alreadyUpgraded
                && GameService.getUpgradedCardName(card.getName()) != null;

        boolean blocked = upgradeDone || (!canUpgrade && !alreadyUpgraded);

        String color = alreadyUpgraded ? "#4ade80"
                     : canUpgrade      ? CardStyleHelper.borderColor(card.getName())
                                       : "#4a4a6a";

        String tagText = alreadyUpgraded ? "\u2705 GI\u00C0 POTENZIATA"
                       : canUpgrade      ? "POTENZIAMENTO"
                                        : "NON POTENZIABILE";

        Label tagLabel = new Label(tagText);
        tagLabel.setStyle("-fx-background-color: " + color + "; -fx-text-fill: #1a1a2e;"
                + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 3 10;"
                + "-fx-background-radius: 10;");

        Label nameLabel = new Label(card.getName());
        nameLabel.setStyle("-fx-text-fill: " + (alreadyUpgraded ? "#4ade80" : "white")
                + "; -fx-font-size: 15px; -fx-font-weight: bold;");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(180);
        nameLabel.setAlignment(Pos.CENTER);

        String upgradedName = alreadyUpgraded ? card.getName()
                            : GameService.upgradedName(card.getName());
        Label arrowLabel = new Label(alreadyUpgraded ? "\u2714 Potenziata!"
                                                     : "\u2192  " + upgradedName);
        arrowLabel.setStyle("-fx-text-fill: " + (alreadyUpgraded ? "#4ade80" : "#e0c97f")
                + "; -fx-font-size: 13px;");
        arrowLabel.setWrapText(true);
        arrowLabel.setMaxWidth(180);
        arrowLabel.setAlignment(Pos.CENTER);

        boolean btnEnabled = canUpgrade && !upgradeDone
                && (upgradePrice <= 0 || gameService.getGold() >= upgradePrice);

        Button upgradeBtn = new Button(
                alreadyUpgraded ? "Gi\u00e0 potenziata" :
                upgradeDone     ? "Hai gi\u00e0 potenziato" :
                !canUpgrade     ? "Non potenziabile" : "Potenzia");
        upgradeBtn.setDisable(!btnEnabled);
        upgradeBtn.setStyle("-fx-background-color: " + (btnEnabled ? color : "#3a3a5a") + ";"
                + "-fx-text-fill: " + (btnEnabled ? "#1a1a2e" : "#6a6a9a") + ";"
                + "-fx-font-weight: bold;"
                + "-fx-padding: 10 24; -fx-background-radius: 8; -fx-cursor: "
                + (btnEnabled ? "hand" : "default") + ";");
        if (btnEnabled) upgradeBtn.setOnAction(e -> upgradeCard(idx));

        VBox box = new VBox(10, tagLabel, nameLabel, arrowLabel, upgradeBtn);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: " + (alreadyUpgraded ? "#1a2e1a" : "#1e1e3a")
                + "; -fx-background-radius: 14;"
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
                            + " \uD83E\uDE99 per potenziare questa carta. (Costo: " + upgradePrice + " \uD83E\uDE99)");
            return;
        }

        boolean ok = gameService.upgradeCard(index);
        if (!ok) {
            showAlert(Alert.AlertType.WARNING,
                    "Potenziamento non riuscito",
                    "Questa carta non pu\u00F2 essere potenziata ulteriormente.");
            return;
        }

        if (upgradePrice > 0) {
            gameService.spendGold(upgradePrice);
        }

        upgradeDone = true;
        buildDeckList();
    }

    private void showAlert(Alert.AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle("Fucina");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Torna allo shop o alla mappa, passando il flag upgradeDone:
     * - upgradeDone=true  -> ShopController rimuove la tile UPGRADE dalla lista
     * - upgradeDone=false -> la tile rimane (utente ha premuto Annulla senza fare nulla)
     */
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
                ctrl.initDataKeepItems(gameService, playerName, vigore, arcano,
                        imagePath, shopItems, upgradeDone);
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
