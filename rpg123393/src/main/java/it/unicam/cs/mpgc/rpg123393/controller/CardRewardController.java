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
        for (ICard card : options) {
            VBox cardNode = buildCardNode(card);
            cardsBox.getChildren().add(cardNode);
        }
    }

    private VBox buildCardNode(ICard card) {
        String name      = card.getName();
        int    cost      = card.getManaCost();
        String nameLower = name.toLowerCase();

        String borderColor;
        String symbol;
        if (nameLower.contains("veleno") || nameLower.contains("ombra") || nameLower.contains("letale") || nameLower.contains("lama")) {
            borderColor = "#27ae60"; symbol = "~~~";
        } else if (nameLower.contains("scudo") || nameLower.contains("divina") || nameLower.contains("luce") || nameLower.contains("mana") || nameLower.contains("armatura") || nameLower.contains("scaglie")) {
            borderColor = "#3498db"; symbol = "[ ]";
        } else if (nameLower.contains("fuoco") || nameLower.contains("fireball") || nameLower.contains("tempesta") || nameLower.contains("artiglio") || nameLower.contains("soffio") || nameLower.contains("ghiaccio")) {
            borderColor = "#e67e22"; symbol = "***";
        } else if (nameLower.contains("pozione") || nameLower.contains("cura") || nameLower.contains("mulinello") || nameLower.contains("grida") || nameLower.contains("punizione")) {
            borderColor = "#c77dff"; symbol = "(+)";
        } else {
            borderColor = "#e74c3c"; symbol = "/ \\";
        }

        Label symbolLabel = new Label(symbol);
        symbolLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: " + borderColor + ";");

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(180);
        nameLabel.setAlignment(Pos.CENTER);

        Label costLabel = new Label("Mana: " + cost);
        costLabel.setStyle("-fx-text-fill: #a78bfa; -fx-font-size: 13px;");

        Label descLabel = new Label(getDescription(card));
        descLabel.setStyle("-fx-text-fill: #a0a0c0; -fx-font-size: 12px;");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(180);
        descLabel.setAlignment(Pos.CENTER);

        Button pickBtn = new Button("Scegli");
        pickBtn.setStyle("-fx-background-color: " + borderColor + "; -fx-text-fill: #1a1a2e;"
                + "-fx-font-weight: bold; -fx-padding: 10 28; -fx-background-radius: 8; -fx-cursor: hand;"
                + "-fx-effect: dropshadow(gaussian, " + borderColor + ", 10, 0.3, 0, 0);");
        pickBtn.setOnAction(e -> pickCard(card));

        VBox box = new VBox(14, symbolLabel, nameLabel, costLabel, descLabel, pickBtn);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #1e1e3a; -fx-background-radius: 14;"
                + "-fx-border-color: " + borderColor + "; -fx-border-radius: 14; -fx-border-width: 2;"
                + "-fx-padding: 28; -fx-pref-width: 200; -fx-pref-height: 320;"
                + "-fx-effect: dropshadow(gaussian, " + borderColor + ", 12, 0.2, 0, 0);");
        return box;
    }

    private void pickCard(ICard card) {
        gameService.addCardToDeck(card);
        navigateToBattle();
    }

    @FXML
    private void onSkip() {
        navigateToBattle();
    }

    private void navigateToBattle() {
        try {
            Stage stage = (Stage) cardsBox.getScene().getWindow();
            FXMLLoader loader = SceneNavigator.navigateTo(
                    stage, "/it/unicam/cs/mpgc/rpg123393/view/hello-view.fxml");
            HelloController ctrl = loader.getController();
            ctrl.initData(playerName, vigore, arcano, imagePath, gameService);
        } catch (IOException e) { e.printStackTrace(); }
    }

    /** Descrizione testuale breve per ogni carta. */
    private String getDescription(ICard card) {
        return switch (card.getName()) {
            case "Colpo"                -> "6 danni al nemico";
            case "Difesa"               -> "+6 scudo";
            case "Fireball"             -> "8 danni al nemico";
            case "Colpo Devastante"     -> "12 danni al nemico";
            case "Tempesta Arcana"      -> "10 + 2\u00d7veleno nemico danni";
            case "Artiglio del Drago"   -> "6 danni + 4 scudo";
            case "Scudo Sacro"          -> "12 scudo + 4 HP";
            case "Lama Avvelenata"      -> "3 danni + 3 stack veleno";
            case "Grida di Battaglia"   -> "8 danni + 6 scudo";
            case "Mulinello"            -> "5 danni + cura 5 HP";
            case "Dardo di Ghiaccio"    -> "7 danni + 2 veleno";
            case "Scudo di Mana"        -> "+10 scudo, recupera 1 mana";
            case "Soffio del Drago"     -> "9 danni + 3 veleno";
            case "Armatura di Scaglie"  -> "+8 scudo + cura 4 HP";
            case "Luce Divina"          -> "Cura 12 HP + 4 scudo";
            case "Punizione Divina"     -> "10 danni (+4 se hai scudo)";
            case "Passo nell'Ombra"     -> "4 danni + 5 veleno";
            case "Colpo Letale"         -> "6 + veleno nemico danni";
            case "Pozione Rapida"       -> "Cura 10 HP";
            default                     -> "";
        };
    }
}
