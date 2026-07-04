package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.model.EncounterType;
import it.unicam.cs.mpgc.rpg123393.model.MapNode;
import it.unicam.cs.mpgc.rpg123393.model.NodeType;
import it.unicam.cs.mpgc.rpg123393.service.GameService;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;

public class MapController {

    private static final double COL_WIDTH = 180.0;
    private static final double ROW_HEIGHT = 140.0;
    private static final double NODE_R = 40.0;
    private static final double ORIGIN_X = 120.0;
    private static final double ORIGIN_Y = 90.0;
    private static final double MIN_SCALE = 0.65;
    private static final double MAX_SCALE = 1.85;

    @FXML private ScrollPane mapScroll;
    @FXML private Pane mapPane;
    @FXML private Label progressLabel;
    @FXML private Label playerHpLabel;
    @FXML private Label playerGoldLabel;
    @FXML private Label playerLevelLabel;
    @FXML private Label selectedNodeLabel;
    @FXML private HBox legendBox;
    @FXML private HBox controlsLegendBox;

    private VBox hoverPanel;

    private GameService gameService;
    private String playerName;
    private int vigore;
    private int arcano;
    private String imagePath;

    private final Map<String, double[]> positions = new HashMap<>();
    private double currentScale = 1.0;
    private double dragStartX;
    private double dragStartY;
    private double dragStartH;
    private double dragStartV;

    public void initData(GameService gs, String playerName, int vigore, int arcano, String imagePath) {
        this.gameService = gs;
        this.playerName = playerName;
        this.vigore = vigore;
        this.arcano = arcano;
        this.imagePath = imagePath;
        buildLayout();
        render();
        setupPanAndZoom();
        Platform.runLater(this::animateToCurrentNode);
    }

    private void buildLayout() {
        positions.clear();
        List<MapNode> all = gameService.getMapService().getMap().getAllNodes();
        Map<String, MapNode> byId = new HashMap<>();
        for (MapNode n : all) byId.put(n.getId(), n);

        Set<String> hasParent = new HashSet<>();
        for (MapNode n : all)
            for (String sid : n.getNextNodeIds()) hasParent.add(sid);
        MapNode root = all.stream().filter(n -> !hasParent.contains(n.getId())).findFirst().orElse(all.get(0));

        Map<String, Integer> colMap = new LinkedHashMap<>();
        Queue<MapNode> queue = new LinkedList<>();
        queue.add(root);
        colMap.put(root.getId(), 0);
        while (!queue.isEmpty()) {
            MapNode cur = queue.poll();
            int col = colMap.get(cur.getId());
            for (String sid : cur.getNextNodeIds()) {
                if (!colMap.containsKey(sid) && byId.containsKey(sid)) {
                    colMap.put(sid, col + 1);
                    queue.add(byId.get(sid));
                }
            }
        }

        Map<Integer, List<String>> colNodes = new LinkedHashMap<>();
        colMap.forEach((id, col) -> colNodes.computeIfAbsent(col, k -> new ArrayList<>()).add(id));

        int maxCol = colMap.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        int maxRows = colNodes.values().stream().mapToInt(List::size).max().orElse(1);
        double totalH = ORIGIN_Y * 2 + (Math.max(maxRows, 3) - 1) * ROW_HEIGHT;

        for (Map.Entry<Integer, List<String>> entry : colNodes.entrySet()) {
            int col = entry.getKey();
            List<String> ids = entry.getValue();
            int count = ids.size();
            double cx = ORIGIN_X + col * COL_WIDTH;
            double blockHeight = (count - 1) * ROW_HEIGHT;
            double startY = totalH / 2.0 - blockHeight / 2.0;
            for (int i = 0; i < count; i++) {
                double cy = startY + i * ROW_HEIGHT;
                positions.put(ids.get(i), new double[]{cx, cy});
            }
        }

        mapPane.setMinWidth(ORIGIN_X * 2 + maxCol * COL_WIDTH + NODE_R * 4);
        mapPane.setMinHeight(totalH + NODE_R * 4);
        mapPane.setPrefWidth(ORIGIN_X * 2 + maxCol * COL_WIDTH + NODE_R * 4);
        mapPane.setPrefHeight(totalH + NODE_R * 4);
    }

    private void render() {
        mapPane.getChildren().clear();

        List<MapNode> all = gameService.getMapService().getMap().getAllNodes();
        Map<String, MapNode> byId = new HashMap<>();
        for (MapNode n : all) byId.put(n.getId(), n);

        Optional<MapNode> curOpt = gameService.getCurrentNode();
        String currentId = curOpt.map(MapNode::getId).orElse("");
        List<MapNode> reachable = gameService.getReachableNodes();
        Set<String> reachableIds = new HashSet<>();
        reachable.forEach(n -> reachableIds.add(n.getId()));

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

        hoverPanel = buildHoverPanel();
        mapPane.getChildren().add(hoverPanel);

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

        var p = gameService.getPlayer();
        playerHpLabel.setText("❤ " + p.getCurrentHp() + "/" + p.getMaxHp());
        playerGoldLabel.setText("🪙 " + gameService.getGold());
        playerLevelLabel.setText("Lv. " + gameService.getPlayerLevel());
        progressLabel.setText(gameService.getEncounterIndex() + " / " + gameService.getEncounterTotal() + " nodi");
        selectedNodeLabel.setText("Mappa ampliata: trascina per muoverti, usa la rotella per zoomare e clicca un nodo raggiungibile per avanzare.");
        buildLegend();
        buildControlsLegend();
    }

    private void setupPanAndZoom() {
        mapPane.setOnMousePressed(this::handleMousePressed);
        mapPane.setOnMouseDragged(this::handleMouseDragged);
        mapPane.setOnScroll(this::handleScroll);
    }

    private void handleMousePressed(MouseEvent e) {
        dragStartX = e.getSceneX();
        dragStartY = e.getSceneY();
        dragStartH = mapScroll.getHvalue();
        dragStartV = mapScroll.getVvalue();
    }

    private void handleMouseDragged(MouseEvent e) {
        Bounds viewport = mapScroll.getViewportBounds();
        Bounds content = mapPane.getLayoutBounds();
        double deltaX = e.getSceneX() - dragStartX;
        double deltaY = e.getSceneY() - dragStartY;

        double extraWidth = content.getWidth() * currentScale - viewport.getWidth();
        double extraHeight = content.getHeight() * currentScale - viewport.getHeight();

        if (extraWidth > 0) {
            double h = dragStartH - deltaX / extraWidth;
            mapScroll.setHvalue(clamp(h, 0, 1));
        }
        if (extraHeight > 0) {
            double v = dragStartV - deltaY / extraHeight;
            mapScroll.setVvalue(clamp(v, 0, 1));
        }
    }

    private void handleScroll(ScrollEvent e) {
        double delta = e.getDeltaY() > 0 ? 1.10 : 0.90;
        double newScale = clamp(currentScale * delta, MIN_SCALE, MAX_SCALE);
        if (newScale == currentScale) return;

        Bounds viewport = mapScroll.getViewportBounds();
        Bounds content = mapPane.getLayoutBounds();
        double mouseX = e.getX();
        double mouseY = e.getY();
        double oldScale = currentScale;
        currentScale = newScale;
        mapPane.setScaleX(currentScale);
        mapPane.setScaleY(currentScale);

        double widthBefore = content.getWidth() * oldScale;
        double heightBefore = content.getHeight() * oldScale;
        double widthAfter = content.getWidth() * currentScale;
        double heightAfter = content.getHeight() * currentScale;

        if (widthAfter > viewport.getWidth()) {
            double valX = (mapScroll.getHvalue() * Math.max(1, widthBefore - viewport.getWidth()) + mouseX) / Math.max(1, widthBefore);
            double newH = (valX * widthAfter - mouseX) / Math.max(1, widthAfter - viewport.getWidth());
            mapScroll.setHvalue(clamp(newH, 0, 1));
        }
        if (heightAfter > viewport.getHeight()) {
            double valY = (mapScroll.getVvalue() * Math.max(1, heightBefore - viewport.getHeight()) + mouseY) / Math.max(1, heightBefore);
            double newV = (valY * heightAfter - mouseY) / Math.max(1, heightAfter - viewport.getHeight());
            mapScroll.setVvalue(clamp(newV, 0, 1));
        }
        e.consume();
    }

    private void animateToCurrentNode() {
        Optional<MapNode> cur = gameService.getCurrentNode();
        if (cur.isEmpty()) return;
        double[] pos = positions.get(cur.get().getId());
        if (pos == null) return;

        centerOn(pos[0], pos[1], true);
    }

    private void centerOn(double x, double y, boolean animated) {
        Bounds viewport = mapScroll.getViewportBounds();
        double contentW = mapPane.getLayoutBounds().getWidth() * currentScale;
        double contentH = mapPane.getLayoutBounds().getHeight() * currentScale;

        double targetHX = (x * currentScale - viewport.getWidth() / 2.0) / Math.max(1, contentW - viewport.getWidth());
        double targetVY = (y * currentScale - viewport.getHeight() / 2.0) / Math.max(1, contentH - viewport.getHeight());
        double h = clamp(targetHX, 0, 1);
        double v = clamp(targetVY, 0, 1);

        if (!animated) {
            mapScroll.setHvalue(h);
            mapScroll.setVvalue(v);
            return;
        }

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(mapScroll.hvalueProperty(), mapScroll.getHvalue()),
                        new KeyValue(mapScroll.vvalueProperty(), mapScroll.getVvalue())),
                new KeyFrame(Duration.millis(500),
                        new KeyValue(mapScroll.hvalueProperty(), h),
                        new KeyValue(mapScroll.vvalueProperty(), v))
        );
        timeline.play();
    }

    private void drawConnection(double[] from, double[] to, MapNode src, MapNode dest, String currentId) {
        boolean active = src.isCleared() || src.getId().equals(currentId);
        Line line = new Line(from[0], from[1], to[0], to[1]);
        line.setStrokeLineCap(StrokeLineCap.ROUND);
        if (active) {
            Color cSrc = Color.web(nodeColor(src.getType()));
            Color cDest = Color.web(nodeColor(dest.getType()));
            double r = (cSrc.getRed() + cDest.getRed()) / 2;
            double g = (cSrc.getGreen() + cDest.getGreen()) / 2;
            double b = (cSrc.getBlue() + cDest.getBlue()) / 2;
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

    private VBox buildHoverPanel() {
        VBox panel = new VBox(6);
        panel.setAlignment(Pos.CENTER_LEFT);
        panel.setStyle("-fx-background-color: #12122a;"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: #4c1d95;"
                + "-fx-border-radius: 12;"
                + "-fx-border-width: 1.5;"
                + "-fx-padding: 14 18;"
                + "-fx-effect: dropshadow(gaussian, #000, 16, 0.4, 0, 4);");
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

        double paneW = mapPane.getWidth() > 0 ? mapPane.getWidth() : mapPane.getPrefWidth();
        double px = cx + NODE_R + 10;
        double py = cy - NODE_R;
        if (paneW > 0 && px + 210 > paneW) px = cx - NODE_R - 220;
        hoverPanel.setLayoutX(Math.max(0, px));
        hoverPanel.setLayoutY(Math.max(0, py));
        hoverPanel.setVisible(true);
        hoverPanel.setManaged(true);
        hoverPanel.toFront();
    }

    private void hideHoverPanel() {
        hoverPanel.setVisible(false);
        hoverPanel.setManaged(false);
    }

    private StackPane buildNodeGraphic(MapNode node, double cx, double cy,
                                       boolean isCleared, boolean isCurrent, boolean isReachable) {
        String color = nodeColor(node.getType());
        String icon = nodeIcon(node.getType());

        Circle pulseRing = null;
        if (isCurrent) {
            pulseRing = new Circle(NODE_R + 8);
            pulseRing.setFill(Color.TRANSPARENT);
            pulseRing.setStroke(Color.web(color, 0.5));
            pulseRing.setStrokeWidth(2);
            ScaleTransition pulse = new ScaleTransition(Duration.millis(900), pulseRing);
            pulse.setFromX(0.85); pulse.setFromY(0.85);
            pulse.setToX(1.15); pulse.setToY(1.15);
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

        Label iconLabel = new Label(isCleared ? "✔" : icon);
        iconLabel.setStyle("-fx-font-size: " + (isCleared ? "16px" : "22px") + ";"
                + "-fx-text-fill: " + (isCleared ? "#4ade80" : "white") + ";");

        Label nameLabel = new Label(node.getName());
        nameLabel.setStyle("-fx-font-size: 9px; -fx-text-fill: "
                + (isCurrent ? color : isCleared ? "#4ade80" : isReachable ? "white" : "#4a4a6a")
                + "; -fx-font-weight: " + (isCurrent ? "bold" : "normal") + ";");
        nameLabel.setMaxWidth(NODE_R * 2 + 10);
        nameLabel.setWrapText(true);
        nameLabel.setAlignment(Pos.CENTER);

        VBox nodeBox = new VBox(2, iconLabel, nameLabel);
        nodeBox.setAlignment(Pos.CENTER);
        nodeBox.setMaxWidth(NODE_R * 2);

        StackPane sp = pulseRing != null ? new StackPane(pulseRing, bg, nodeBox) : new StackPane(bg, nodeBox);
        double spSize = pulseRing != null ? (NODE_R + 8) * 2 : NODE_R * 2;
        sp.setLayoutX(cx - spSize / 2);
        sp.setLayoutY(cy - spSize / 2);
        sp.setPrefSize(spSize, spSize);

        if (isReachable || isCurrent) {
            sp.setOnMouseEntered(e -> showHoverPanel(node, cx, cy));
            sp.setOnMouseExited(e -> hideHoverPanel());
        }
        if (isReachable) {
            sp.setStyle("-fx-cursor: hand;");
            sp.setOnMouseClicked(e -> onNodeSelected(node));
        }

        return sp;
    }

    private void buildLegend() {
        legendBox.getChildren().clear();
        String[][] items = {
                {"✔", "#4ade80", "Completato"},
                {"●", "#c4b5fd", "Posizione attuale"},
                {"○", "white", "Raggiungibile"},
                {"○", "#2a2a4a", "Bloccato"},
                {"?", "#c084fc", "Evento / segreto"}
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

    private void buildControlsLegend() {
        controlsLegendBox.getChildren().clear();
        String[][] items = {
                {"Click + drag", "Muoviti nella mappa"},
                {"Scroll su", "Aumenta zoom"},
                {"Scroll giù", "Riduci zoom"},
                {"Click nodo", "Avanza al nodo raggiungibile"}
        };
        for (String[] it : items) {
            Label key = new Label(it[0]);
            key.setStyle("-fx-font-size: 11px; -fx-text-fill: #e5e7eb; -fx-font-weight: bold;"
                    + "-fx-background-color: #1e1e3a; -fx-background-radius: 6; -fx-padding: 4 8;");
            Label desc = new Label(it[1]);
            desc.setStyle("-fx-font-size: 11px; -fx-text-fill: #9ca3af;");
            HBox entry = new HBox(6, key, desc);
            entry.setAlignment(Pos.CENTER_LEFT);
            controlsLegendBox.getChildren().add(entry);
        }
    }

    private void onNodeSelected(MapNode node) {
        gameService.moveToNode(node.getId());
        EncounterType encounter = gameService.currentEncounter();
        try {
            Stage stage = (Stage) mapPane.getScene().getWindow();
            switch (encounter) {
                case SHOP -> {
                    gameService.resetUpgradeForNextShop();
                    FXMLLoader loader = SceneNavigator.navigateTo(stage, "/it/unicam/cs/mpgc/rpg123393/view/shop-view.fxml");
                    loader.<ShopController>getController().initData(gameService, playerName, vigore, arcano, imagePath);
                }
                case REST -> {
                    FXMLLoader loader = SceneNavigator.navigateTo(stage, "/it/unicam/cs/mpgc/rpg123393/view/rest-view.fxml");
                    loader.<RestController>getController().initData(gameService, playerName, vigore, arcano, imagePath);
                }
                case EVENT -> {
                    FXMLLoader loader = SceneNavigator.navigateTo(stage, "/it/unicam/cs/mpgc/rpg123393/view/event-view.fxml");
                    loader.<EventController>getController().initData(gameService, playerName, vigore, arcano, imagePath);
                }
                default -> {
                    FXMLLoader loader = SceneNavigator.navigateTo(stage, "/it/unicam/cs/mpgc/rpg123393/view/hello-view.fxml");
                    loader.<HelloController>getController().initData(playerName, vigore, arcano, imagePath, gameService);
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Errore navigazione mappa", ex);
        }
    }

    private static String nodeIcon(NodeType type) {
        return switch (type) {
            case BATTLE -> "⚔";
            case ELITE -> "💀";
            case BOSS -> "🐉";
            case SHOP -> "🛒";
            case REST -> "🔥";
            case EVENT -> "?";
        };
    }

    private static String nodeColor(NodeType type) {
        return switch (type) {
            case BATTLE -> "#60a5fa";
            case ELITE -> "#f97316";
            case BOSS -> "#ef4444";
            case SHOP -> "#fbbf24";
            case REST -> "#4ade80";
            case EVENT -> "#c084fc";
        };
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
