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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CollectionController {

    // ── FXML refs ──────────────────────────────────────────────────────────
    @FXML private Label      progressLabel;
    @FXML private HBox       filterBox;
    @FXML private ScrollPane listScroll;
    @FXML private VBox       cardListBox;

    // Dettaglio destra
    @FXML private StackPane  detailPane;
    @FXML private VBox       detailPlaceholder;
    @FXML private VBox       detailCard;
    @FXML private Label      detailSymbol;
    @FXML private Label      detailName;
    @FXML private Label      detailClass;
    @FXML private VBox       detailStatsBox;
    @FXML private Label      detailDesc;

    // ── Stato ──────────────────────────────────────────────────────────────
    private List<String> unlockedCards = new ArrayList<>();
    private String       activeFilter  = "Tutte";
    private Node         selectedRow   = null;

    private static final List<String> FILTERS =
            List.of("Tutte", "Guerriero", "Paladino", "Mago", "Dracomante", "Assassino", "Neutro");

    private static final java.util.Map<String, String> FILTER_COLORS = java.util.Map.of(
            "Guerriero",  "#e74c3c",
            "Paladino",   "#3498db",
            "Mago",       "#e67e22",
            "Dracomante", "#e67e22",
            "Assassino",  "#27ae60",
            "Neutro",     "#c77dff",
            "Tutte",      "#e0c97f"
    );

    /** Mappa carta → classe leggibile (per il pannello dettaglio). */
    private static String classOfCard(String name) {
        return switch (CardStyleHelper.borderColor(name)) {
            case "#e74c3c" -> "⚔ Guerriero";
            case "#3498db" -> "🛡 Paladino";
            case "#e67e22" -> "🔥 Mago / Dracomante";
            case "#27ae60" -> "☠ Assassino";
            case "#c77dff" -> "✦ Neutrale";
            default        -> "";
        };
    }

    /** Scompone la descrizione in righe per le stat nel pannello dettaglio. */
    private static List<String> statsFromDesc(String desc) {
        List<String> lines = new ArrayList<>();
        for (String part : desc.split("\\+")) {
            String s = part.strip();
            if (!s.isEmpty()) lines.add(s);
        }
        return lines;
    }

    // ── Lifecycle ──────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        loadUnlocked();
        buildFilters();
        showCards("Tutte");
    }

    // ── Unlocked ───────────────────────────────────────────────────────────

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

    // ── Filtri ─────────────────────────────────────────────────────────────

    private void buildFilters() {
        filterBox.getChildren().clear();
        for (String f : FILTERS) {
            Button btn = new Button(f);
            String color = FILTER_COLORS.getOrDefault(f, "#aaa");
            boolean active = f.equals(activeFilter);
            if (active) {
                btn.setStyle("-fx-background-color:" + color + "; -fx-text-fill:#1a1a2e;"
                        + "-fx-font-weight:bold; -fx-padding:5 13; -fx-background-radius:20; -fx-cursor:hand;");
            } else {
                btn.setStyle("-fx-background-color:transparent; -fx-text-fill:" + color + ";"
                        + "-fx-border-color:" + color + "; -fx-border-radius:20; -fx-border-width:1;"
                        + "-fx-padding:5 13; -fx-cursor:hand;");
            }
            btn.setOnAction(e -> { activeFilter = f; buildFilters(); showCards(f); clearDetail(); });
            filterBox.getChildren().add(btn);
        }
    }

    // ── Lista carte (sinistra) ─────────────────────────────────────────────

    private void showCards(String filter) {
        cardListBox.getChildren().clear();
        selectedRow = null;

        List<ICard> pool = switch (filter) {
            case "Tutte"  -> CardPool.getAllCards();
            case "Neutro" -> { List<ICard> n = new ArrayList<>(CardPool.getNeutralPool());
                              n.addAll(CardPool.getUpgradedPool()); yield n; }
            default       -> CardPool.getClassPool(filter);
        };

        List<String> seen = new ArrayList<>();
        List<ICard>  deduped = new ArrayList<>();
        for (ICard c : pool) {
            if (!seen.contains(c.getName())) { seen.add(c.getName()); deduped.add(c); }
        }

        int total    = deduped.size();
        int unlocked = (int) deduped.stream().filter(c -> unlockedCards.contains(c.getName())).count();
        progressLabel.setText(unlocked + " / " + total + " carte sbloccate");

        for (ICard card : deduped) {
            boolean isUnlocked = unlockedCards.contains(card.getName());
            HBox row = buildRow(card, isUnlocked);
            cardListBox.getChildren().add(row);
        }
    }

    private HBox buildRow(ICard card, boolean unlocked) {
        String color  = unlocked ? CardStyleHelper.borderColor(card.getName()) : "#2a2a4a";
        String symbol = unlocked ? CardStyleHelper.symbol(card.getName()) : "🔒";

        Label symLbl = new Label(symbol);
        symLbl.setStyle("-fx-font-size:20px; -fx-min-width:36px;");

        Label nameLbl = new Label(unlocked ? card.getName() : "???");
        nameLbl.setStyle("-fx-text-fill:" + (unlocked ? "white" : "#3a3a5a") + ";"
                + "-fx-font-size:13px; -fx-font-weight:bold;");
        HBox.setHgrow(nameLbl, Priority.ALWAYS);

        Label manaLbl = new Label("✨ " + card.getManaCost());
        manaLbl.setStyle("-fx-text-fill:#a78bfa; -fx-font-size:12px; -fx-min-width:36px;"
                + "-fx-alignment:center-right;");

        HBox row = new HBox(12, symLbl, nameLbl, manaLbl);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding:10 16; -fx-background-color:transparent;"
                + "-fx-border-color: transparent transparent #1e1e3a transparent; -fx-border-width:1;"
                + (unlocked ? "-fx-cursor:hand;" : "-fx-opacity:0.45;"));

        if (unlocked) {
            row.setOnMouseEntered(e -> {
                if (row != selectedRow)
                    row.setStyle("-fx-padding:10 16; -fx-background-color:#1e1e3a;"
                            + "-fx-border-color:transparent transparent #1e1e3a transparent;"
                            + "-fx-border-width:1; -fx-cursor:hand;");
            });
            row.setOnMouseExited(e -> {
                if (row != selectedRow)
                    row.setStyle("-fx-padding:10 16; -fx-background-color:transparent;"
                            + "-fx-border-color:transparent transparent #1e1e3a transparent;"
                            + "-fx-border-width:1; -fx-cursor:hand;");
            });
            row.setOnMouseClicked(e -> selectCard(card, row, color));
        }

        return row;
    }

    // ── Dettaglio carta (destra) ───────────────────────────────────────────

    private void selectCard(ICard card, HBox row, String color) {
        // Deseleziona riga precedente
        if (selectedRow != null)
            selectedRow.setStyle("-fx-padding:10 16; -fx-background-color:transparent;"
                    + "-fx-border-color:transparent transparent #1e1e3a transparent;"
                    + "-fx-border-width:1; -fx-cursor:hand;");
        // Evidenzia nuova riga
        selectedRow = row;
        row.setStyle("-fx-padding:10 16; -fx-background-color:#1e1e3a;"
                + "-fx-border-color:" + color + " transparent " + color + " transparent;"
                + "-fx-border-width:2; -fx-cursor:hand;");

        // Popola il pannello dettaglio
        detailSymbol.setText(CardStyleHelper.symbol(card.getName()));
        detailName.setText(card.getName());
        detailClass.setText(classOfCard(card.getName()));
        detailClass.setStyle("-fx-text-fill:" + color + "; -fx-font-size:13px; -fx-font-weight:bold;");
        detailDesc.setText(CardStyleHelper.description(card.getName()));

        // Stats singole
        detailStatsBox.getChildren().clear();
        detailStatsBox.getChildren().add(makeStatRow("✨ Costo mana", String.valueOf(card.getManaCost()), "#a78bfa"));
        String raw = CardStyleHelper.description(card.getName());
        if (raw.contains("danni")) {
            String atk = raw.replaceAll(".*?(\\d[^\\s]*\\s*danni).*", "$1");
            detailStatsBox.getChildren().add(makeStatRow("⚔ Attacco", atk, "#e74c3c"));
        }
        if (raw.contains("scudo")) {
            String shld = raw.replaceAll(".*?([+\\d]+\\s*scudo).*", "$1");
            detailStatsBox.getChildren().add(makeStatRow("🛡 Scudo", shld, "#3498db"));
        }
        if (raw.contains("cura")) {
            String heal = raw.replaceAll(".*?(cura\\s+\\d+|\\d+\\s*cura).*", "$1");
            detailStatsBox.getChildren().add(makeStatRow("❤ Cura", heal, "#2ecc71"));
        }
        if (raw.contains("veleno")) {
            String psn = raw.replaceAll(".*?([+\\d]+\\s*veleno).*", "$1");
            detailStatsBox.getChildren().add(makeStatRow("☠ Veleno", psn, "#27ae60"));
        }

        // Bordo carta
        detailCard.setStyle("-fx-background-color:#1e1e3a; -fx-background-radius:20;"
                + "-fx-border-color:" + color + "; -fx-border-radius:20; -fx-border-width:3;"
                + "-fx-padding:40 36;"
                + "-fx-effect:dropshadow(gaussian," + color + ",28,0.35,0,0);");

        detailPlaceholder.setVisible(false);
        detailCard.setVisible(true);
    }

    private HBox makeStatRow(String label, String value, String color) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill:#7a7a9a; -fx-font-size:12px;");
        HBox.setHgrow(lbl, Priority.ALWAYS);
        Label val = new Label(value);
        val.setStyle("-fx-text-fill:" + color + "; -fx-font-size:13px; -fx-font-weight:bold;");
        HBox row = new HBox(lbl, val);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setSpacing(8);
        row.setMaxWidth(280);
        return row;
    }

    private void clearDetail() {
        detailCard.setVisible(false);
        detailPlaceholder.setVisible(true);
        selectedRow = null;
    }

    // ── Back ───────────────────────────────────────────────────────────────

    @FXML
    private void onBack(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneNavigator.navigateTo(stage,
                    "/it/unicam/cs/mpgc/rpg123393/view/main-menu-view.fxml");
        } catch (IOException e) { e.printStackTrace(); }
    }
}
