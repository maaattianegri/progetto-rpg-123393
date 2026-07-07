package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.model.CardPool;
import it.unicam.cs.mpgc.rpg123393.model.ICard;
import it.unicam.cs.mpgc.rpg123393.persistence.GameState;
import it.unicam.cs.mpgc.rpg123393.persistence.JsonSaveRepository;
import it.unicam.cs.mpgc.rpg123393.service.GameService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CollectionController {

    @FXML private VBox      rootPane;
    @FXML private ComboBox<String> classFilter;
    @FXML private FlowPane  cardFlow;

    private List<String> unlockedCards = new ArrayList<>();

    private static final List<String> FILTERS =
            List.of("Tutte", "Starter", "Guerriero", "Paladino", "Mago", "Dracomante", "Assassino", "Neutro");

    private static final java.util.Map<String, String> FILTER_COLORS = java.util.Map.ofEntries(
            java.util.Map.entry("Tutte",      "#e0c97f"),
            java.util.Map.entry("Starter",    "#9aaaba"),
            java.util.Map.entry("Guerriero",  "#e74c3c"),
            java.util.Map.entry("Paladino",   "#f1c40f"),
            java.util.Map.entry("Mago",       "#9b59b6"),
            java.util.Map.entry("Dracomante", "#e67e22"),
            java.util.Map.entry("Assassino",  "#27ae60"),
            java.util.Map.entry("Neutro",     "#00bcd4")
    );

    @FXML
    public void initialize() {
        ImageLoaderHelper.applyBackground(rootPane, ImageLoaderHelper.backgroundPath("menu"));
        loadUnlocked();
        classFilter.setItems(FXCollections.observableArrayList(FILTERS));
        classFilter.setValue("Tutte");
        showCards("Tutte");
    }

    private void loadUnlocked() {
        if (GameService.isDebugUnlockAll()) {
            for (ICard c : CardPool.getAllCards())
                if (!unlockedCards.contains(c.getName()))
                    unlockedCards.add(c.getName());
            return;
        }
        try {
            JsonSaveRepository repo = new JsonSaveRepository();
            if (repo.saveExists()) {
                GameState s = repo.load();
                if (s != null && s.getUnlockedCards() != null)
                    unlockedCards = new ArrayList<>(s.getUnlockedCards());
            }
        } catch (IOException ignored) {}
    }

    @FXML
    private void onFilterChange() {
        String selected = classFilter.getValue();
        if (selected != null) showCards(selected);
    }

    private void showCards(String filter) {
        cardFlow.getChildren().clear();

        List<ICard> pool = switch (filter) {
            case "Tutte"   -> CardPool.getAllCards();
            case "Starter" -> CardPool.getStarterPool();
            case "Neutro"  -> new ArrayList<>(CardPool.getNeutralPool());
            default        -> CardPool.getClassPool(filter);
        };

        List<String> seen    = new ArrayList<>();
        List<ICard>  deduped = new ArrayList<>();
        for (ICard c : pool) {
            if (!seen.contains(c.getName())) { seen.add(c.getName()); deduped.add(c); }
        }

        String color = FILTER_COLORS.getOrDefault(filter, "#e0c97f");
        for (ICard card : deduped) {
            boolean isUnlocked = unlockedCards.contains(card.getName());
            cardFlow.getChildren().add(buildTile(card, isUnlocked, color));
        }
    }

    private VBox buildTile(ICard card, boolean unlocked, String filterColor) {
        String color  = unlocked ? CardStyleHelper.borderColor(card.getName()) : "#2a2a4a";
        String symbol = unlocked ? CardStyleHelper.symbol(card.getName()) : "\uD83D\uDD12";

        Label symLbl = new Label(symbol);
        symLbl.setStyle("-fx-font-size: 26px;");

        Label nameLbl = new Label(unlocked ? card.getName() : "???");
        nameLbl.setStyle("-fx-text-fill: " + (unlocked ? "white" : "#3a3a5a") + ";"
                + "-fx-font-size: 11px; -fx-font-weight: bold;");
        nameLbl.setWrapText(true);
        nameLbl.setMaxWidth(100);
        nameLbl.setAlignment(Pos.CENTER);

        Label manaLbl = new Label("\u2728 " + card.getManaCost());
        manaLbl.setStyle("-fx-text-fill: #a78bfa; -fx-font-size: 10px;");

        VBox tile = new VBox(6, symLbl, nameLbl, manaLbl);
        tile.setAlignment(Pos.CENTER);
        tile.setStyle("-fx-background-color: #1a1a30; -fx-background-radius: 12;"
                + "-fx-border-color: " + color + "; -fx-border-radius: 12; -fx-border-width: 2;"
                + "-fx-padding: 12 10; -fx-pref-width: 115; -fx-pref-height: 130;"
                + (unlocked ? "-fx-cursor: hand;" : "-fx-opacity: 0.45;")
                + (unlocked ? " -fx-effect: dropshadow(gaussian," + color + ",6,0.1,0,0);" : ""));
        return tile;
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
