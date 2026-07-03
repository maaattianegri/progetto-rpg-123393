package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.model.EncounterType;
import it.unicam.cs.mpgc.rpg123393.model.MapNode;
import it.unicam.cs.mpgc.rpg123393.model.NodeType;
import it.unicam.cs.mpgc.rpg123393.service.GameService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller della mappa grafica a nodi.
 * Disegna su un Pane: cerchi per i nodi, linee per le connessioni.
 * Stati visivi: CLEARED (verde opaco), CURRENT (pulsante, evidenziato),
 * REACHABLE (colorato, cliccabile), LOCKED (grigio scuro).
 */
public class MapController {

    // Dimensioni layout grafico
    private static final double COL_WIDTH  = 140.0;
    private static final double ROW_HEIGHT = 120.0;
    private static final double NODE_R     = 36.0;
    private static final double ORIGIN_X   = 70.0;
    private static final double ORIGIN_Y   = 60.0;

    @FXML private Pane   mapPane;
    @FXML private Label  progressLabel;
    @FXML private Label  playerHpLabel;
    @FXML private Label  playerGoldLabel;
    @FXML private Label  playerLevelLabel;
    @FXML private Label  selectedNodeLabel;
    @FXML private HBox   legendBox;

    private GameService gameService;
    private String      playerName;
    private int         vigore;
    private int         arcano;
    private String      imagePath;

    // Posizioni calcolate per ogni nodo (id -> [cx, cy])
    private final Map<String, double[]> positions = new HashMap<>();

    // -------------------------------------------------------
    // Inizializzazione
    // -------------------------------------------------------

    public void initData(GameService gs, String playerName, int vigore, int arcano, String imagePath) {
        this.gameService = gs;
        this.playerName  = playerName;
        this.vigore      = vigore;
        this.arcano      = arcano;
        this.imagePath   = imagePath;
        buildLayout();
        render();
    }

    // -------------------------------------------------------
    // Layout: assegna posizioni colonna/riga ai nodi
    // -------------------------------------------------------

    /**
     * Assegna a ogni nodo una posizione (cx, cy) sul Pane.
     * La colonna = profondità BFS dalla radice.
     * La riga = indice del nodo nella colonna (per gestire i bivi).
     */
    private void buildLayout() {
        positions.clear();
        List<MapNode> all = gameService.getMapService().getMap().getAllNodes();

        // BFS per assegnare colonne
        Map<String, Integer> colMap = new HashMap<>();
        Map<String, Integer> rowMap = new HashMap<>();
        Map<String, Integer> colCount = new HashMap<>();

        // Trova la radice (nodo senza predecessori)
        java.util.Set<String> hasParent = new java.util.HashSet<>();
        for (MapNode n : all)
            for (MapNode s : n.getSuccessors()) hasParent.add(s.getId());
        MapNode root = all.stream().filter(n -> !hasParent.contains(n.getId())).findFirst().orElse(all.get(0));

        java.util.Queue<MapNode> queue = new java.util.LinkedList<>();
        queue.add(root);
        colMap.put(root.getId(), 0);

        while (!queue.isEmpty()) {
            MapNode cur = queue.poll();
            int col = colMap.get(cur.getId());
            colCount.merge(String.valueOf(col), 1, Integer::sum);
            int row = colCount.get(String.valueOf(col)) - 1;
            rowMap.put(cur.getId(), row);
            for (MapNode s : cur.getSuccessors()) {
                if (!colMap.containsKey(s.getId())) {
                    colMap.put(s.getId(), col + 1);
                    queue.add(s);
                }
            }
        }

        // Centra verticalmente i nodi per ogni colonna
        Map<Integer, Integer> colTotals = new HashMap<>();
        colMap.forEach((id, col) -> colTotals.merge(col, 1, Integer::sum));

        for (MapNode n : all) {
            int col  = colMap.getOrDefault(n.getId(), 0);
            int row  = rowMap.getOrDefault(n.getId(), 0);
            int tot  = colTotals.getOrDefault(col, 1);
            double cx = ORIGIN_X + col * COL_WIDTH;
            double cy = ORIGIN_Y + row * ROW_HEIGHT
                        + ((double)(3 - tot) / 2.0) * ROW_HEIGHT; // centra la colonna
            positions.put(n.getId(), new double[]{cx, cy});
        }

        // Dimensiona il Pane
        int maxCol = colMap.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        mapPane.setMinWidth(ORIGIN_X * 2 + maxCol * COL_WIDTH + NODE_R * 2);
        mapPane.setMinHeight(ORIGIN_Y * 2 + 3 * ROW_HEIGHT);
    }

    // -------------------------------------------------------
    // Render: disegna linee e nodi
    // -------------------------------------------------------

    private void render() {
        mapPane.getChildren().clear();

        List<MapNode> all       = gameService.getMapService().getMap().getAllNodes();
        Optional<MapNode> curOpt = gameService.getCurrentNode();
        String currentId        = curOpt.map(MapNode::getId).orElse("");
        List<MapNode> reachable = gameService.getReachableNodes();
        java.util.Set<String> reachableIds = new java.util.HashSet<>();
        reachable.forEach(n -> reachableIds.add(n.getId()));

        // 1. Disegna le linee di connessione PRIMA dei nodi (z-order)
        for (MapNode n : all) {
            double[] from = positions.get(n.getId());
            if (from == null) continue;
            for (MapNode s : n.getSuccessors()) {
                double[] to = positions.get(s.getId());
                if (to == null) continue;
                Line line = new Line(from[0], from[1], to[0], to[1]);
                boolean active = n.isCleared() || n.getId().equals(currentId);
                line.setStroke(active ? Color.web("#4c1d95") : Color.web("#2a2a4a"));
                line.setStrokeWidth(active ? 2.5 : 1.5);
                line.getStrokeDashArray().addAll(active ? List.of() : List.of(6.0, 4.0));
                mapPane.getChildren().add(line);
            }
        }

        // 2. Disegna i nodi
        for (MapNode n : all) {
            double[] pos = positions.get(n.getId());
            if (pos == null) continue;
            boolean isCleared   = n.isCleared();
            boolean isCurrent   = n.getId().equals(currentId);
            boolean isReachable = reachableIds.contains(n.getId());
            mapPane.getChildren().add(buildNodeGraphic(n, pos[0], pos[1], isCleared, isCurrent, isReachable));
        }

        // 3. Aggiorna header
        var p = gameService.getPlayer();
        playerHpLabel.setText("\u2764 " + p.getCurrentHp() + "/" + p.getMaxHp());
        playerGoldLabel.setText("\uD83E\uDE99 " + gameService.getGold());
        playerLevelLabel.setText("Lv. " + gameService.getPlayerLevel());
        int idx   = gameService.getEncounterIndex();
        int total = gameService.getEncounterTotal();
        progressLabel.setText(idx + " / " + total + " nodi");

        // 4. Legenda
        buildLegend();
    }

    // -------------------------------------------------------
    // Nodo grafico
    // -------------------------------------------------------

    private StackPane buildNodeGraphic(MapNode node, double cx, double cy,
                                        boolean isCleared, boolean isCurrent, boolean isReachable) {
        String color   = nodeColor(node.getType());
        String icon    = nodeIcon(node.getType());

        // Cerchio di sfondo
        Circle bg = new Circle(NODE_R);
        if (isCurrent) {
            bg.setFill(Color.web(color, 0.35));
            bg.setStroke(Color.web(color));
            bg.setStrokeWidth(3.5);
            DropShadow glow = new DropShadow(18, Color.web(color));
            bg.setEffect(glow);
        } else if (isCleared) {
            bg.setFill(Color.web("#1a2e1a"));
            bg.setStroke(Color.web("#4ade80", 0.6));
            bg.setStrokeWidth(2);
        } else if (isReachable) {
            bg.setFill(Color.web(color, 0.2));
            bg.setStroke(Color.web(color));
            bg.setStrokeWidth(2.5);
            DropShadow glow = new DropShadow(12, Color.web(color, 0.6));
            bg.setEffect(glow);
        } else {
            bg.setFill(Color.web("#1a1a2e"));
            bg.setStroke(Color.web("#2a2a4a"));
            bg.setStrokeWidth(1.5);
        }

        // Icona emoji
        Label iconLabel = new Label(isCleared ? "\u2714" : icon);
        iconLabel.setStyle("-fx-font-size: " + (isCleared ? "16px" : "20px") + ";"
                + "-fx-text-fill: " + (isCleared ? "#4ade80" : "white") + ";");

        // Nome sotto
        Label nameLabel = new Label(node.getName());
        nameLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: "
                + (isCurrent ? color : isCleared ? "#4ade80" : isReachable ? "white" : "#4a4a6a")
                + "; -fx-font-weight: " + (isCurrent ? "bold" : "normal") + ";");
        nameLabel.setMaxWidth(NODE_R * 2 + 20);
        nameLabel.setWrapText(true);
        nameLabel.setAlignment(Pos.CENTER);

        VBox nodeBox = new VBox(2, iconLabel, nameLabel);
        nodeBox.setAlignment(Pos.CENTER);
        nodeBox.setMaxWidth(NODE_R * 2);

        StackPane sp = new StackPane(bg, nodeBox);
        sp.setLayoutX(cx - NODE_R);
        sp.setLayoutY(cy - NODE_R);
        sp.setPrefSize(NODE_R * 2, NODE_R * 2);

        // Tooltip
        Tooltip tip = new Tooltip(node.getName() + "\n" + node.getDescription()
                + (isCurrent ? "\n\u25CF Posizione attuale" : "")
                + (isCleared ? "\n\u2714 Completato" : "")
                + (isReachable ? "\n\u2192 Clicca per andare qui" : ""));
        tip.setStyle("-fx-font-size: 12px;");
        Tooltip.install(sp, tip);

        // Click solo su raggiungibili
        if (isReachable) {
            sp.setStyle("-fx-cursor: hand;");
            sp.setOnMouseEntered(e -> selectedNodeLabel.setText(
                nodeIcon(node.getType()) + "  " + node.getName() + " — " + node.getDescription()));
            sp.setOnMouseExited(e  -> selectedNodeLabel.setText("Clicca un nodo raggiungibile per avanzare"));
            sp.setOnMouseClicked(e -> onNodeSelected(node));
        }

        return sp;
    }

    // -------------------------------------------------------
    // Legenda
    // -------------------------------------------------------

    private void buildLegend() {
        legendBox.getChildren().clear();
        String[][] items = {
            {"\u2714", "#4ade80",  "Completato"},
            {"\u25CF", "#c4b5fd",  "Posizione attuale"},
            {"\u25CB", "white",    "Raggiungibile"},
            {"\u25CB", "#2a2a4a",  "Bloccato"},
        };
        for (String[] it : items) {
            Label dot = new Label(it[0]);
            dot.setStyle("-fx-font-size: 14px; -fx-text-fill: " + it[1] + ";");
            Label lbl = new Label(it[2]);
            lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #9ca3af;");
            HBox entry = new HBox(5, dot, lbl);
            entry.setAlignment(Pos.CENTER_LEFT);
            legendBox.getChildren().add(entry);
        }
    }

    // -------------------------------------------------------
    // Navigazione
    // -------------------------------------------------------

    private void onNodeSelected(MapNode node) {
        gameService.moveToNode(node.getId());
        EncounterType encounter = gameService.currentEncounter();
        try {
            Stage stage = (Stage) mapPane.getScene().getWindow();
            switch (encounter) {
                case SHOP -> {
                    FXMLLoader loader = SceneNavigator.navigateTo(
                        stage, "/it/unicam/cs/mpgc/rpg123393/view/shop-view.fxml");
                    loader.<ShopController>getController()
                          .initData(gameService, playerName, vigore, arcano, imagePath);
                }
                case REST -> {
                    FXMLLoader loader = SceneNavigator.navigateTo(
                        stage, "/it/unicam/cs/mpgc/rpg123393/view/rest-view.fxml");
                    loader.<RestController>getController()
                          .initData(gameService, playerName, vigore, arcano, imagePath);
                }
                case EVENT -> {
                    FXMLLoader loader = SceneNavigator.navigateTo(
                        stage, "/it/unicam/cs/mpgc/rpg123393/view/event-view.fxml");
                    loader.<EventController>getController()
                          .initData(gameService, playerName, vigore, arcano, imagePath);
                }
                default -> {
                    FXMLLoader loader = SceneNavigator.navigateTo(
                        stage, "/it/unicam/cs/mpgc/rpg123393/view/hello-view.fxml");
                    loader.<HelloController>getController()
                          .initData(playerName, vigore, arcano, imagePath, gameService);
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Errore navigazione mappa", ex);
        }
    }

    // -------------------------------------------------------
    // Utility
    // -------------------------------------------------------

    private static String nodeIcon(NodeType type) {
        return switch (type) {
            case BATTLE -> "\u2694";
            case ELITE  -> "\uD83D\uDC80";
            case BOSS   -> "\uD83D\uDC09";
            case SHOP   -> "\uD83D\uDED2";
            case REST   -> "\uD83D\uDD25";
            case EVENT  -> "\u2753";
        };
    }

    private static String nodeColor(NodeType type) {
        return switch (type) {
            case BATTLE -> "#60a5fa";
            case ELITE  -> "#f97316";
            case BOSS   -> "#ef4444";
            case SHOP   -> "#fbbf24";
            case REST   -> "#4ade80";
            case EVENT  -> "#c084fc";
        };
    }
}
