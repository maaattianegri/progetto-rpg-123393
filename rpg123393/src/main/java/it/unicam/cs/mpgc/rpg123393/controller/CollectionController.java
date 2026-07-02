package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.model.CardPool;
import it.unicam.cs.mpgc.rpg123393.model.ICard;
import it.unicam.cs.mpgc.rpg123393.persistence.GameState;
import it.unicam.cs.mpgc.rpg123393.persistence.JsonSaveRepository;
import it.unicam.cs.mpgc.rpg123393.service.GameService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CollectionController {

    @FXML private Label    progressLabel;
    @FXML private HBox     filterBox;
    @FXML private FlowPane cardsFlow;

    private List<String> unlockedCards = new ArrayList<>();
    private String       activeFilter  = "Tutte";

    private static final List<String> FILTERS =
            List.of("Tutte", "Guerriero", "Paladino", "Mago", "Dracomante", "Assassino", "Neutro");

    private static final java.util.Map<String, String> CLASS_COLORS = java.util.Map.of(
            "Guerriero",  "#e74c3c",
            "Mago",       "#e67e22",
            "Dracomante", "#e67e22",
            "Paladino",   "#3498db",
            "Assassino",  "#27ae60",
            "Neutro",     "#c77dff",
            "Tutte",      "#e0c97f"
    );

    @FXML
    public void initialize() {
        loadUnlocked();
        buildFilters();
        showCards("Tutte");
    }

    private void loadUnlocked() {
        // Modalità debug: mostra subito tutte le carte senza save
        if (GameService.isDebugUnlockAll()) {
            for (ICard c : CardPool.getAllCards()) {
                if (!unlockedCards.contains(c.getName()))
                    unlockedCards.add(c.getName());
            }
            return;
        }
        // Modalità normale: legge dal file di salvataggio
        try {
            JsonSaveRepository repo = new JsonSaveRepository();
            if (repo.saveExists()) {
                GameState s = repo.load();
                if (s != null && s.getUnlockedCards() != null)
                    unlockedCards = new ArrayList<>(s.getUnlockedCards());
            }
        } catch (IOException ignored) {}
    }

    private void buildFilters() {
        filterBox.getChildren().clear();
        for (String filter : FILTERS) {
            Button btn = new Button(filter);
            String color = CLASS_COLORS.getOrDefault(filter, "#aaa");
            styleFilterBtn(btn, color, filter.equals(activeFilter));
            btn.setOnAction(e -> {
                activeFilter = filter;
                buildFilters();
                showCards(filter);
            });
            filterBox.getChildren().add(btn);
        }
    }

    private void styleFilterBtn(Button btn, String color, boolean active) {
        if (active) {
            btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: #1a1a2e;"
                    + "-fx-font-weight: bold; -fx-padding: 6 16; -fx-background-radius: 20; -fx-cursor: hand;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + color + ";"
                    + "-fx-border-color: " + color + "; -fx-border-radius: 20; -fx-border-width: 1;"
                    + "-fx-padding: 6 16; -fx-cursor: hand;");
        }
    }

    private void showCards(String filter) {
        cardsFlow.getChildren().clear();

        List<ICard> pool;
        if (filter.equals("Tutte")) {
            pool = CardPool.getAllCards();
        } else if (filter.equals("Neutro")) {
            pool = new ArrayList<>(CardPool.getNeutralPool());
            pool.addAll(CardPool.getUpgradedPool());
        } else {
            pool = CardPool.getClassPool(filter);
        }

        // Deduplicazione per nome
        List<String> seen = new ArrayList<>();
        List<ICard> deduped = new ArrayList<>();
        for (ICard c : pool) {
            if (!seen.contains(c.getName())) {
                seen.add(c.getName());
                deduped.add(c);
            }
        }

        int total    = deduped.size();
        int unlocked = (int) deduped.stream().filter(c -> unlockedCards.contains(c.getName())).count();
        progressLabel.setText(unlocked + " / " + total + " carte sbloccate");

        for (ICard card : deduped) {
            boolean isUnlocked = unlockedCards.contains(card.getName());
            cardsFlow.getChildren().add(buildCardTile(card, isUnlocked));
        }
    }

    private VBox buildCardTile(ICard card, boolean unlocked) {
        if (unlocked) {
            String color  = CardStyleHelper.borderColor(card.getName());
            String symbol = CardStyleHelper.symbol(card.getName());
            String desc   = CardStyleHelper.description(card.getName());

            Label symbolLabel = new Label(symbol);
            symbolLabel.setStyle("-fx-font-size: 32px;");

            Label nameLabel = new Label(card.getName());
            nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");
            nameLabel.setWrapText(true); nameLabel.setMaxWidth(145); nameLabel.setAlignment(Pos.CENTER);

            Label manaLabel = new Label("✨ Mana: " + card.getManaCost());
            manaLabel.setStyle("-fx-text-fill: #a78bfa; -fx-font-size: 11px; -fx-font-weight: bold;");

            Label descLabel = new Label(desc);
            descLabel.setStyle("-fx-text-fill: #c0c0d8; -fx-font-size: 11px;");
            descLabel.setWrapText(true); descLabel.setMaxWidth(145); descLabel.setAlignment(Pos.CENTER);

            VBox box = new VBox(8, symbolLabel, nameLabel, manaLabel, descLabel);
            box.setAlignment(Pos.CENTER);
            box.setStyle("-fx-background-color: #1e1e3a; -fx-background-radius: 12;"
                    + "-fx-border-color: " + color + "; -fx-border-radius: 12; -fx-border-width: 2;"
                    + "-fx-padding: 16; -fx-pref-width: 160; -fx-pref-height: 215;"
                    + "-fx-effect: dropshadow(gaussian, " + color + ", 10, 0.2, 0, 0);");
            return box;
        } else {
            Label lockLabel = new Label("🔒");
            lockLabel.setStyle("-fx-font-size: 32px;");
            Label unknownLabel = new Label("???");
            unknownLabel.setStyle("-fx-text-fill: #3a3a5a; -fx-font-size: 14px; -fx-font-weight: bold;");

            VBox box = new VBox(10, lockLabel, unknownLabel);
            box.setAlignment(Pos.CENTER);
            box.setStyle("-fx-background-color: #12122a; -fx-background-radius: 12;"
                    + "-fx-border-color: #2a2a4a; -fx-border-radius: 12; -fx-border-width: 2;"
                    + "-fx-padding: 16; -fx-pref-width: 160; -fx-pref-height: 215;"
                    + "-fx-opacity: 0.5;");
            return box;
        }
    }

    @FXML
    private void onBack(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneNavigator.navigateTo(stage,
                    "/it/unicam/cs/mpgc/rpg123393/view/main-menu-view.fxml");
        } catch (IOException e) { e.printStackTrace(); }
    }
}
