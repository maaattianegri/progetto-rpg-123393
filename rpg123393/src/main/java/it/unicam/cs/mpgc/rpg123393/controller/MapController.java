package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.model.EncounterType;
import it.unicam.cs.mpgc.rpg123393.model.MapNode;
import it.unicam.cs.mpgc.rpg123393.model.NodeType;
import it.unicam.cs.mpgc.rpg123393.service.GameService;
import javafx.animation.ScaleTransition;
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
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;

public class MapController {

    private static final double COL_WIDTH  = 150.0;
    private static final double ROW_HEIGHT = 130.0;
    private static final double NODE_R     = 44.0;
    private static final double ORIGIN_X   = 80.0;
    private static final double ORIGIN_Y   = 70.0;

    @FXML private Pane   mapPane;
    @FXML private Label  progressLabel;
    @FXML private Label  playerHpLabel;
    @FXML private Label  playerGoldLabel;
    @FXML private Label  playerLevelLabel;
    @FXML private Label  selectedNodeLabel;
    @FXML private HBox   legendBox;

    private VBox hoverPanel;

    private GameService gameService;
    private String      playerName;
    private int         vigore;
    private int         arcano;
    private String      imagePath;

    private final Map<String, double[]> positions = new HashMap<>();

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
    // Layout BFS
    // -------------------------------------------------------

    private void buildLayout() {
        positions.clear();
        List<MapNode> all = gameService.getMapService().getMap().getAllNodes();
        Map<String, MapNode> byId = new HashMap<>();
        for (MapNode n : all) byId.put(n.getId(), n);

        Set<String> hasParent = new HashSet<>();
        for (MapNode n : all)
            for (String sid : n.getNextNodeIds()) hasParent.add(sid);
        MapNode root = all.stream().filter(n -> !hasParent.contains(n.getId())).findFirst().orElse(all.get(0));

        Map<String, Integer> colMap   = new LinkedHashMap<>();
        Map<String, Integer> colCount = new HashMap<>();
        Map<String, Integer> rowMap   = new HashMap<>();
        Queue<MapNode> queue = new LinkedList<>();
        queue.add(root);
        colMap.put(root.getId(), 0);

        while (!queue.isEmpty()) {
            MapNode cur = queue.poll();
            int col = colMap.get(cur.getId());
            colCount.merge(String.valueOf(col), 1, Integer::sum);
            rowMap.put(cur.getId(), colCount.get(String.valueOf(col)) - 1);
            for (String sid : cur.getNextNodeIds()) {
                if (!colMap.containsKey(sid) && byId.containsKey(sid)) {
                    colMap.put(sid, col + 1);
                    queue.add(byId.get(sid));
                }
            }
        }

        Map<Integer, Integer> colTotals = new HashMap<>();
        colMap.forEach((id, col) -> colTotals.merge(col, 1, Integer::sum));

        for (MapNode n : all) {
            int col = colMap.getOrDefault(n.getId(), 0);
            int row = rowMap.getOrDefault(n.getId(), 0);
            int tot = colTotals.getOrDefault(col, 1);
            double cx = ORIGIN_X + col * COL_WIDTH;
            double cy = ORIGIN_Y + row * ROW_HEIGHT
                      + ((double)(3 - tot) / 2.0) * ROW_HEIGHT;
            positions.put(n.getId(), new double[]{cx, cy});
        }

        int maxCol = colMap.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        mapPane.setMinWidth(ORIGIN_X * 2 + maxCol * COL_WIDTH + NODE_R * 2);
        mapPane.setMinHeight(ORIGIN_Y * 2 + 3 * ROW_HEIGHT);
    }

    // -------------------------------------------------------
    // Render
    // -------------------------------------------------------

    private void render() {
        mapPane.getChildren().clear();

        List<MapNode> all = gameService.getMapService().getMap().getAllNodes();
        Map<String, MapNode> byId = new HashMap<>();
        for (MapNode n : all) byId.put(n.getId(), n);

        Optional<MapNode> curOpt = gameService.getCurrentNode();
        String currentId         = curOpt.map(MapNode::getId).orElse("");
        List<MapNode> reachable  = gameService.getReachableNodes();
        Set<String> reachableIds = new HashSet<>();
        reachable.forEach(n -> reachableIds.add(n.getId()));

        // 1. Linee gradient
        for (MapNode n : all) {
            double[] from = positions.get(n.getId());
            if (from == null) continue;
            for (String sid : n.getNextNodeIds()) {
                MapNode dest = byId.get(sid);
                if (dest == null) continue;
                double[] to = positions.get(sid);
                if (to == null) continue;
                drawConnection(from, to, n, dest, currentId);
            }
        }

        // 2. Hover panel
        hoverPanel = buildHoverPanel();
        mapPane.getChildren().add(hoverPanel);

        // 3. Nodi
        for (MapNode n : all) {
            double[] pos = positions.get(n.getId());
            if (pos == null) continue;
            mapPane.getChildren().add(buildNodeGraphic(
                n, pos[0], pos[1],
                n.isCleared(),
                n.getId().equals(currentId),
                reachableIds.contains(n.getId())
            ));
        }

        // 4. Header stats
        var p = gameService.getPlayer();
        playerHpLabel.setText("\u2764 " + p.getCurrentHp() + "/" + p.getMaxHp());
        playerGoldLabel.setText("\uD83E\uDE99 " + gameService.getGold());
        playerLevelLabel.setText("Lv. " + gameService.getPlayerLevel());
        progressLabel.setText(gameService.getEncounterIndex() + " / " + gameService.getEncounterTotal() + " nodi");

        buildLegend();
    }

    // -------------------------------------------------------
    // Connessioni con gradient
    // -------------------------------------------------------

    private void drawConnection(double[] from, double[] to,
                                 MapNode src, MapNode dest, String currentId) {
        boolean active = src.isCleared() || src.getId().equals(currentId);
        Line line = new Line(from[0], from[1], to[0], to[1]);
        line.setStrokeLineCap(StrokeLineCap.ROUND);
        if (active) {
            Color cSrc  = Color.web(nodeColor(src.getType()));
            Color cDest = Color.web(nodeColor(dest.getType()));
            double r = (cSrc.getRed()   + cDest.getRed())   / 2;
            double g = (cSrc.getGreen() + cDest.getGreen()) / 2;
            double b = (cSrc.getBlue()  + cDest.getBlue())  / 2;
            line.setStroke(Color.color(r, g, b));
            line.setStrokeWidth(3.0);
            line.setOpacity(0.85);
        } else {
            line.setStroke(Color.web("#2a2a4a"));
            line.setStrokeWidth(1.5);
            line.getStrokeDashArray().addAll(6.0, 4.0);
            line.setOpacity(0.5);
        }
        mapPane.getChildren().add(line);
    }

    // -------------------------------------------------------
    // Hover panel
    // -------------------------------------------------------

    private VBox buildHoverPanel() {
        VBox panel = new VBox(6);
        panel.setAlignment(Pos.CENTER_LEFT);
        panel.setStyle(
            "-fx-background-color: #12122a;"
            + "-fx-background-radius: 12;"
            + "-fx-border-color: #4c1d95;"
            + "-fx-border-radius: 12;"
            + "-fx-border-width: 1.5;"
            + "-fx-padding: 14 18;"
            + "-fx-effect: dropshadow(gaussian, #000, 16, 0.4, 0, 4);"
        );
        panel.setPrefWidth(200);
        panel.setVisible(false);
        panel.setManaged(false);
        return panel;
    }

    private void showHoverPanel(MapNode node, double cx, double cy) {
        hoverPanel.getChildren().clear();
        String color = nodeColor(node.getType());

        Label iconLbl = new Label(nodeIcon(node.getType()));
        iconLbl.setStyle("-fx-font-size: 28px;");

        Label nameLbl = new Label(node.getName());
        nameLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        Label typeLbl = new Label(node.getType().name());
        typeLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #9ca3af; -fx-font-weight: bold;"
                + "-fx-background-color: #1e1e3a; -fx-background-radius: 6; -fx-padding: 2 8;");

        Label descLbl = new Label(node.getDescription());
        descLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #d1d5db;");
        descLbl.setWrapText(true);
        descLbl.setMaxWidth(176);

        hoverPanel.getChildren().addAll(iconLbl, nameLbl, typeLbl, descLbl);

        double px = cx + NODE_R + 10;
        double py = cy - NODE_R;
        if (px + 210 > mapPane.getWidth() && mapPane.getWidth() > 0)
            px = cx - NODE_R - 220;
        hoverPanel.setLayoutX(px);
        hoverPanel.setLayoutY(py);
        hoverPanel.setVisible(true);
        hoverPanel.setManaged(true);
        hoverPanel.toFront();
    }

    private void hideHoverPanel() {
        hoverPanel.setVisible(false);
        hoverPanel.setManaged(false);
    }

    // -------------------------------------------------------
    // Nodo grafico
    // -------------------------------------------------------

    private StackPane buildNodeGraphic(MapNode node, double cx, double cy,
                                        boolean isCleared, boolean isCurrent, boolean isReachable) {
        String color = nodeColor(node.getType());
        String icon  = nodeIcon(node.getType());

        Circle pulseRing = null;
        if (isCurrent) {
            pulseRing = new Circle(NODE_R + 8);
            pulseRing.setFill(Color.TRANSPARENT);
            pulseRing.setStroke(Color.web(color, 0.5));
            pulseRing.setStrokeWidth(2);
            ScaleTransition pulse = new ScaleTransition(Duration.millis(900), pulseRing);
            pulse.setFromX(0.85); pulse.setFromY(0.85);
            pulse.setToX(1.15);   pulse.setToY(1.15);
            pulse.setAutoReverse(true);
            pulse.setCycleCount(ScaleTransition.INDEFINITE);
            pulse.play();
        }

        Circle bg = new Circle(NODE_R);
        if (isCurrent) {
            bg.setFill(Color.web(color, 0.35));
            bg.setStroke(Color.web(color));
            bg.setStrokeWidth(3.5);
            bg.setEffect(new DropShadow(22, Color.web(color)));
        } else if (isCleared) {
            bg.setFill(Color.web("#1a2e1a"));
            bg.setStroke(Color.web("#4ade80", 0.7));
            bg.setStrokeWidth(2);
        } else if (isReachable) {
            bg.setFill(Color.web(color, 0.2));
            bg.setStroke(Color.web(color));
            bg.setStrokeWidth(2.5);
            bg.setEffect(new DropShadow(14, Color.web(color, 0.6)));
        } else {
            bg.setFill(Color.web("#1a1a2e"));
            bg.setStroke(Color.web("#2a2a4a"));
            bg.setStrokeWidth(1.5);
        }

        Label iconLabel = new Label(isCleared ? "\u2714" : icon);
        iconLabel.setStyle("-fx-font-size: " + (isCleared ? "18px" : "24px") + ";"
                + "-fx-text-fill: " + (isCleared ? "#4ade80" : "white") + ";");

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

        StackPane sp;
        if (pulseRing != null) {
            sp = new StackPane(pulseRing, bg, nodeBox);
        } else {
            sp = new StackPane(bg, nodeBox);
        }
        double spSize = (pulseRing != null) ? (NODE_R + 8) * 2 : NODE_R * 2;
        sp.setLayoutX(cx - spSize / 2);
        sp.setLayoutY(cy - spSize / 2);
        sp.setPrefSize(spSize, spSize);

        if (isReachable || isCurrent) {
            sp.setOnMouseEntered(e -> showHoverPanel(node, cx, cy));
            sp.setOnMouseExited(e  -> hideHoverPanel());
        }
        if (isReachable) {
            sp.setStyle("-fx-cursor: hand;");
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
            {"\u2714", "#4ade80", "Completato"},
            {"\u25CF", "#c4b5fd", "Posizione attuale"},
            {"\u25CB", "white",   "Raggiungibile"},
            {"\u25CB", "#2a2a4a", "Bloccato"},
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
                    // Nuovo shop: la Fucina torna disponibile per questa visita
                    gameService.resetUpgradeForNextShop();
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
