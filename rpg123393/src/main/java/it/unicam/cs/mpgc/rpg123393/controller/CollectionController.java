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
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CollectionController {

    // ── FXML refs ─────────────────────────────────────────────────
    @FXML private Label     progressLabel;
    @FXML private HBox      filterBox;
    @FXML private FlowPane  cardsFlow;

    // Dettaglio destra
    @FXML private StackPane detailPane;
    @FXML private VBox      detailPlaceholder;
    @FXML private VBox      detailCard;
    @FXML private Label     detailSymbol;
    @FXML private Label     detailName;
    @FXML private Label     detailClass;
    @FXML private VBox      detailStatsBox;
    @FXML private Label     detailDesc;

    // ── Stato ─────────────────────────────────────────────────────
    private List<String> unlockedCards = new ArrayList<>();
    private String       activeFilter  = "Tutte";
    private VBox         selectedTile  = null;

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

    // ── Lifecycle ─────────────────────────────────────────────────

    @FXML
    public void initialize() {
        loadUnlocked();
        buildFilters();
        showCards("Tutte");
    }

    // ── Unlocked ──────────────────────────────────────────────────

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

    // ── Filtri ────────────────────────────────────────────────────

    private void buildFilters() {
        filterBox.getChildren().clear();
        for (String f : FILTERS) {
            Button btn = new Button(f);
            String color  = FILTER_COLORS.getOrDefault(f, "#aaa");
            boolean active = f.equals(activeFilter);
            btn.setStyle(active
                ? "-fx-background-color:" + color + "; -fx-text-fill:#1a1a2e;"
                  + "-fx-font-weight:bold; -fx-padding:5 14; -fx-background-radius:20; -fx-cursor:hand;"
                : "-fx-background-color:transparent; -fx-text-fill:" + color + ";"
                  + "-fx-border-color:" + color + "; -fx-border-radius:20; -fx-border-width:1;"
                  + "-fx-padding:5 14; -fx-cursor:hand;"
            );
            btn.setOnAction(e -> { activeFilter = f; buildFilters(); showCards(f); clearDetail(); });
            filterBox.getChildren().add(btn);
        }
    }

    // ── Griglia carte ─────────────────────────────────────────────

    private void showCards(String filter) {
        cardsFlow.getChildren().clear();
        selectedTile = null;

        List<ICard> pool = switch (filter) {
            case "Tutte"  -> CardPool.getAllCards();
            case "Neutro" -> { List<ICard> n = new ArrayList<>(CardPool.getNeutralPool());
                              n.addAll(CardPool.getUpgradedPool()); yield n; }
            default       -> CardPool.getClassPool(filter);
        };

        // Deduplicazione
        List<String> seen    = new ArrayList<>();
        List<ICard>  deduped = new ArrayList<>();
        for (ICard c : pool) {
            if (!seen.contains(c.getName())) { seen.add(c.getName()); deduped.add(c); }
        }

        int total    = deduped.size();
        int unlocked = (int) deduped.stream().filter(c -> unlockedCards.contains(c.getName())).count();
        progressLabel.setText(unlocked + " / " + total + " carte sbloccate");

        for (ICard card : deduped) {
            boolean isUnlocked = unlockedCards.contains(card.getName());
            cardsFlow.getChildren().add(buildTile(card, isUnlocked));
        }
    }

    /** Tessera compatta nella griglia (non deve essere troppo grande). */
    private VBox buildTile(ICard card, boolean unlocked) {
        String color  = unlocked ? CardStyleHelper.borderColor(card.getName()) : "#2a2a4a";
        String symbol = unlocked ? CardStyleHelper.symbol(card.getName()) : "🔒";

        Label symLbl = new Label(symbol);
        symLbl.setStyle("-fx-font-size: 26px;");

        Label nameLbl = new Label(unlocked ? card.getName() : "???");
        nameLbl.setStyle("-fx-text-fill: " + (unlocked ? "white" : "#3a3a5a") + ";"
                + "-fx-font-size: 11px; -fx-font-weight: bold;");
        nameLbl.setWrapText(true);
        nameLbl.setMaxWidth(100);
        nameLbl.setAlignment(Pos.CENTER);

        Label manaLbl = new Label("✨ " + card.getManaCost());
        manaLbl.setStyle("-fx-text-fill: #a78bfa; -fx-font-size: 10px;");

        VBox tile = new VBox(6, symLbl, nameLbl, manaLbl);
        tile.setAlignment(Pos.CENTER);
        tile.setStyle("-fx-background-color: #1a1a30; -fx-background-radius: 12;"
                + "-fx-border-color: " + color + "; -fx-border-radius: 12; -fx-border-width: 2;"
                + "-fx-padding: 12 10;"
                + "-fx-pref-width: 115; -fx-pref-height: 130;"
                + (unlocked ? "-fx-cursor: hand;" : "-fx-opacity: 0.45;")
                + (unlocked ? " -fx-effect: dropshadow(gaussian," + color + ",6,0.1,0,0);" : ""));

        if (unlocked) {
            tile.setOnMouseEntered(e -> {
                if (tile != selectedTile)
                    tile.setStyle("-fx-background-color: #22224a; -fx-background-radius: 12;"
                            + "-fx-border-color: " + color + "; -fx-border-radius: 12; -fx-border-width: 2;"
                            + "-fx-padding: 12 10; -fx-pref-width: 115; -fx-pref-height: 130;"
                            + "-fx-cursor: hand; -fx-effect: dropshadow(gaussian," + color + ",14,0.3,0,0);");
            });
            tile.setOnMouseExited(e -> {
                if (tile != selectedTile)
                    tile.setStyle("-fx-background-color: #1a1a30; -fx-background-radius: 12;"
                            + "-fx-border-color: " + color + "; -fx-border-radius: 12; -fx-border-width: 2;"
                            + "-fx-padding: 12 10; -fx-pref-width: 115; -fx-pref-height: 130;"
                            + "-fx-cursor: hand; -fx-effect: dropshadow(gaussian," + color + ",6,0.1,0,0);");
            });
            tile.setOnMouseClicked(e -> selectCard(card, tile, color));
        }
        return tile;
    }

    // ── Dettaglio carta (destra) ──────────────────────────────────

    private void selectCard(ICard card, VBox tile, String color) {
        // Ripristina tessera precedente
        if (selectedTile != null) {
            String prevColor = CardStyleHelper.borderColor(
                    ((Label) selectedTile.getChildren().get(1)).getText());
            selectedTile.setStyle("-fx-background-color: #1a1a30; -fx-background-radius: 12;"
                    + "-fx-border-color: " + prevColor + "; -fx-border-radius: 12; -fx-border-width: 2;"
                    + "-fx-padding: 12 10; -fx-pref-width: 115; -fx-pref-height: 130;"
                    + "-fx-cursor: hand; -fx-effect: dropshadow(gaussian," + prevColor + ",6,0.1,0,0);");
        }
        // Evidenzia nuova tessera
        selectedTile = tile;
        tile.setStyle("-fx-background-color: #22224a; -fx-background-radius: 12;"
                + "-fx-border-color: " + color + "; -fx-border-radius: 12; -fx-border-width: 3;"
                + "-fx-padding: 12 10; -fx-pref-width: 115; -fx-pref-height: 130;"
                + "-fx-cursor: hand; -fx-effect: dropshadow(gaussian," + color + ",20,0.5,0,0);");

        // ── Popola pannello dettaglio ──
        detailSymbol.setText(CardStyleHelper.symbol(card.getName()));
        detailName.setText(card.getName());

        // Classe
        String cls = switch (color) {
            case "#e74c3c" -> "⚔ Guerriero";
            case "#3498db" -> "🛡 Paladino";
            case "#e67e22" -> "🔥 Mago / Dracomante";
            case "#27ae60" -> "☠ Assassino";
            case "#c77dff" -> "✦ Neutrale";
            default        -> "";
        };
        detailClass.setText(cls);
        detailClass.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 14px; -fx-font-weight: bold;");

        // Stats
        detailStatsBox.getChildren().clear();
        detailStatsBox.getChildren().add(makeStatRow("✨ Costo mana",
                card.getManaCost() + " mana", "#a78bfa"));

        String raw = CardStyleHelper.description(card.getName());
        // Attacco
        java.util.regex.Matcher mAtk = java.util.regex.Pattern
                .compile("(\\d[\\d×+]*\\s*danni)").matcher(raw);
        if (mAtk.find())
            detailStatsBox.getChildren().add(makeStatRow("⚔ Attacco", mAtk.group(1), "#e74c3c"));
        // Scudo
        java.util.regex.Matcher mShld = java.util.regex.Pattern
                .compile("\\+(\\d+)\\s*scudo").matcher(raw);
        if (mShld.find())
            detailStatsBox.getChildren().add(makeStatRow("🛡 Scudo", "+" + mShld.group(1), "#3498db"));
        // Cura
        java.util.regex.Matcher mHeal = java.util.regex.Pattern
                .compile("(?:cura\\s*(\\d+)|(\\d+)\\s*cura)").matcher(raw);
        if (mHeal.find()) {
            String hv = mHeal.group(1) != null ? mHeal.group(1) : mHeal.group(2);
            detailStatsBox.getChildren().add(makeStatRow("❤ Cura", hv + " HP", "#2ecc71"));
        }
        // Veleno
        java.util.regex.Matcher mPsn = java.util.regex.Pattern
                .compile("(\\d+)\\s*veleno").matcher(raw);
        if (mPsn.find())
            detailStatsBox.getChildren().add(makeStatRow("☠ Veleno", mPsn.group(1) + " stack", "#27ae60"));
        // Mana extra
        java.util.regex.Matcher mMana = java.util.regex.Pattern
                .compile("(\\d+)\\s*mana(?!\\s*$)").matcher(raw);
        if (mMana.find())
            detailStatsBox.getChildren().add(makeStatRow("✨ Mana bonus", "+" + mMana.group(1), "#a78bfa"));

        // Descrizione
        detailDesc.setText(raw);

        // Bordo e glow carta
        detailCard.setStyle("-fx-background-color: #1e1e3a; -fx-background-radius: 22;"
                + "-fx-border-color: " + color + "; -fx-border-radius: 22; -fx-border-width: 3;"
                + "-fx-padding: 36 32;"
                + "-fx-effect: dropshadow(gaussian, " + color + ", 32, 0.4, 0, 0);");

        detailPlaceholder.setVisible(false);
        detailCard.setVisible(true);
    }

    private HBox makeStatRow(String label, String value, String color) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #7a7a9a; -fx-font-size: 12px;");
        HBox.setHgrow(lbl, Priority.ALWAYS);
        Label val = new Label(value);
        val.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 13px; -fx-font-weight: bold;");
        HBox row = new HBox(lbl, val);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setSpacing(8);
        row.setMaxWidth(280);
        return row;
    }

    private void clearDetail() {
        detailCard.setVisible(false);
        detailPlaceholder.setVisible(true);
        selectedTile = null;
    }

    // ── Back ──────────────────────────────────────────────────────

    @FXML
    private void onBack(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneNavigator.navigateTo(stage,
                    "/it/unicam/cs/mpgc/rpg123393/view/main-menu-view.fxml");
        } catch (IOException e) { e.printStackTrace(); }
    }
}
