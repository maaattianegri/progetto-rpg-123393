package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.model.EncounterType;
import it.unicam.cs.mpgc.rpg123393.model.MapNode;
import it.unicam.cs.mpgc.rpg123393.model.NodeType;
import it.unicam.cs.mpgc.rpg123393.service.GameService;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
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

    private static final double COL_WIDTH  = 180.0;
    private static final double ROW_HEIGHT = 140.0;
    private static final double NODE_R     = 40.0;
    private static final double ORIGIN_X   = 120.0;
    private static final double ORIGIN_Y   = 90.0;
    private static final double MAX_SCALE  = 2.00;
    private static final double MARGIN     = 1.15;

    private static final int    NEAR_DIST    = 1;
    private static final int    MID_DIST     = 3;
    private static final double OPACITY_MID  = 0.45;
    private static final double OPACITY_FAR  = 0.15;

    private static final String VOID_COLOR      = "#9333ea";
    private static final String VOID_BOSS_COLOR = "#581c87";

    @FXML private ScrollPane mapScroll;
    @FXML private Pane       mapPane;
    @FXML private Label      progressLabel;
    @FXML private Label      playerHpLabel;
    @FXML private Label      playerGoldLabel;
    @FXML private Label      playerLevelLabel;
    @FXML private Label      selectedNodeLabel;
    @FXML private HBox       legendBox;
    @FXML private HBox       controlsLegendBox;

    private Pane   canvas;
    private Group  canvasWrapper;
    private VBox   hoverPanel;

    private GameService gameService;
    private String      playerName;
    private int         vigore;
    private int         arcano;
    private String      imagePath;

    private final Map<String, double[]> positions   = new HashMap<>();
    private final Map<String, Integer>  bfsDistance = new HashMap<>();
    private double currentScale = 1.0;
    private double minScale     = 0.40;
    private double dragStartX, dragStartY, dragStartH, dragStartV;

    public void initData(GameService gs, String name, int vigore, int arcano, String imagePath) {
        this.gameService = gs;
        this.playerName  = name;
        this.vigore      = vigore;
        this.arcano      = arcano;
        this.imagePath   = imagePath;

        canvas = new Pane();
        canvas.setStyle("-fx-background-color: #0d0d1e;");
        canvasWrapper = new Group(canvas);

        mapScroll.setContent(canvasWrapper);
        mapScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mapScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mapScroll.setPannable(false);
        mapScroll.setFitToHeight(false);
        mapScroll.setFitToWidth(false);

        buildLayout();
        render();
        setupPanAndZoom();

        Platform.runLater(() -> Platform.runLater(this::applyInitialZoomAndCenter));
    }

    private void buildLayout() {
        positions.clear();
        List<MapNode> all = gameService.getMapService().getMap().getAllNodes();
        Map<String, MapNode> byId = new HashMap<>();
        for (MapNode n : all) byId.put(n.getId(), n);

        Set<String> hasParent = new HashSet<>();
        for (MapNode n : all) for (String sid : n.getNextNodeIds()) hasParent.add(sid);
        MapNode root = all.stream().filter(n -> !hasParent.contains(n.getId())).findFirst().orElse(all.get(0));

        Map<String, Integer> colMap = new LinkedHashMap<>();
        Queue<MapNode> queue = new LinkedList<>();
        queue.add(root); colMap.put(root.getId(), 0);
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

        int maxCol  = colMap.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        int maxRows = colNodes.values().stream().mapToInt(List::size).max().orElse(1);
        double totalH = ORIGIN_Y * 2 + (Math.max(maxRows, 3) - 1) * ROW_HEIGHT;

        for (Map.Entry<Integer, List<String>> entry : colNodes.entrySet()) {
            int col = entry.getKey();
            List<String> ids = entry.getValue();
            double cx = ORIGIN_X + col * COL_WIDTH;
            double blockH = (ids.size() - 1) * ROW_HEIGHT;
            double startY = totalH / 2.0 - blockH / 2.0;
            for (int i = 0; i < ids.size(); i++)
                positions.put(ids.get(i), new double[]{cx, startY + i * ROW_HEIGHT});
        }

        double w = ORIGIN_X * 2 + maxCol * COL_WIDTH + NODE_R * 4;
        double h = totalH + NODE_R * 4;
        canvas.setMinWidth(w);  canvas.setPrefWidth(w);
        canvas.setMinHeight(h); canvas.setPrefHeight(h);
    }

    private void computeBfsDistances() {
        bfsDistance.clear();
        String currentId = gameService.getCurrentNode().map(MapNode::getId).orElse("");
        if (currentId.isEmpty()) return;

        List<MapNode> all = gameService.getMapService().getMap().getAllNodes();
        Map<String, Set<String>> adj = new HashMap<>();
        for (MapNode n : all) {
            adj.computeIfAbsent(n.getId(), k -> new HashSet<>());
            for (String sid : n.getNextNodeIds()) {
                adj.computeIfAbsent(n.getId(), k -> new HashSet<>()).add(sid);
                adj.computeIfAbsent(sid,        k -> new HashSet<>()).add(n.getId());
            }
        }

        Queue<String> q = new LinkedList<>();
        bfsDistance.put(currentId, 0);
        q.add(currentId);
        while (!q.isEmpty()) {
            String id   = q.poll();
            int    dist = bfsDistance.get(id);
            for (String nb : adj.getOrDefault(id, Collections.emptySet())) {
                if (!bfsDistance.containsKey(nb)) {
                    bfsDistance.put(nb, dist + 1);
                    q.add(nb);
                }
            }
        }
    }

    private void render() {
        canvas.getChildren().clear();
        computeBfsDistances();

        List<MapNode> all = gameService.getMapService().getMap().getAllNodes();
        Map<String, MapNode> byId = new HashMap<>();
        for (MapNode n : all) byId.put(n.getId(), n);

        String currentId = gameService.getCurrentNode().map(MapNode::getId).orElse("");
        Set<String> reachableIds = new HashSet<>();
        gameService.getReachableNodes().forEach(n -> reachableIds.add(n.getId()));
        String playerClass = gameService.getClassName();

        // True se il giocatore ha gia' cleared almeno un nodo VOID
        boolean voidPathVisited = gameService.hasVisitedVoidPath();

        Set<String> currentNodeNextIds = gameService.getCurrentNode()
                .map(n -> new HashSet<>(n.getNextNodeIds()))
                .orElse(new HashSet<>());

        for (MapNode n : all) {
            double[] from = positions.get(n.getId());
            if (from == null) continue;
            for (String sid : n.getNextNodeIds()) {
                double[] to = positions.get(sid);
                if (to == null) continue;
                int distSrc  = bfsDistance.getOrDefault(n.getId(), Integer.MAX_VALUE);
                int distDest = bfsDistance.getOrDefault(sid,       Integer.MAX_VALUE);
                // Anche le linee verso nodi del percorso segreto non visitato vanno nascoste
                MapNode destNode = byId.get(sid);
                boolean destSecretHidden = isSecretNodeHidden(destNode, voidPathVisited);
                boolean srcSecretHidden  = isSecretNodeHidden(n,        voidPathVisited);
                if (!destSecretHidden && !srcSecretHidden) {
                    drawConnection(from, to, n, destNode, currentId, distSrc, distDest);
                } else {
                    // Disegna la linea come farFog (quasi invisibile)
                    drawConnectionHidden(from, to);
                }
            }
        }

        hoverPanel = buildHoverPanel();
        canvas.getChildren().add(hoverPanel);

        for (MapNode n : all) {
            double[] pos = positions.get(n.getId());
            if (pos == null) continue;
            boolean classLocked = n.isLockedFor(playerClass);
            boolean flagLocked  = n.getType() == NodeType.VOID_BOSS && !gameService.isVoidHeartObtained();
            boolean locked      = classLocked || flagLocked;
            boolean isNewlyReachable = currentNodeNextIds.contains(n.getId());
            int dist = bfsDistance.getOrDefault(n.getId(), Integer.MAX_VALUE);

            // Forza farFog per nodi del percorso segreto non ancora scoperti
            boolean secretHidden = isSecretNodeHidden(n, voidPathVisited);

            canvas.getChildren().add(buildNodeGraphic(
                    n, pos[0], pos[1],
                    n.isCleared(), n.getId().equals(currentId),
                    reachableIds.contains(n.getId()) && !locked,
                    locked, isNewlyReachable, dist, secretHidden));
        }

        var p = gameService.getPlayer();
        playerHpLabel.setText("\u2764 " + p.getCurrentHp() + "/" + p.getMaxHp());
        playerGoldLabel.setText("\ud83e\ude99 " + gameService.getGold());
        playerLevelLabel.setText("Lv. " + gameService.getPlayerLevel());
        progressLabel.setText(gameService.getEncounterIndex() + " / " + gameService.getEncounterTotal() + " nodi");
        selectedNodeLabel.setText("Trascina per muoverti \u2022 Rotella per zoomare \u2022 Clicca un nodo raggiungibile per avanzare");
        buildLegend();
        buildControlsLegend();
    }

    /**
     * Un nodo e' "segreto nascosto" se ha requiredClass != null, e' di tipo VOID o VOID_BOSS,
     * e il giocatore non ha ancora visitato il percorso dell'Abisso.
     * Eccezione: se il nodo stesso e' gia' cleared, e' gia' stato scoperto.
     */
    private boolean isSecretNodeHidden(MapNode node, boolean voidPathVisited) {
        if (node == null) return false;
        if (node.isCleared()) return false;  // gia' visitato: sempre visibile
        boolean isSecretPath = node.getRequiredClass() != null
                && (node.getType() == NodeType.VOID || node.getType() == NodeType.VOID_BOSS);
        return isSecretPath && !voidPathVisited;
    }

    private void setupPanAndZoom() {
        mapScroll.addEventFilter(MouseEvent.MOUSE_PRESSED,  this::handleMousePressed);
        mapScroll.addEventFilter(MouseEvent.MOUSE_DRAGGED,  this::handleMouseDragged);
        mapScroll.addEventFilter(ScrollEvent.SCROLL,        this::handleScroll);
    }

    private void handleMousePressed(MouseEvent e) {
        dragStartX = e.getSceneX(); dragStartY = e.getSceneY();
        dragStartH = mapScroll.getHvalue(); dragStartV = mapScroll.getVvalue();
        e.consume();
    }

    private void handleMouseDragged(MouseEvent e) {
        e.consume();
        double dx = e.getSceneX() - dragStartX;
        double dy = e.getSceneY() - dragStartY;
        Bounds cb = canvasWrapper.getLayoutBounds();
        Bounds vp = mapScroll.getViewportBounds();
        double extraW = cb.getWidth()  - vp.getWidth();
        double extraH = cb.getHeight() - vp.getHeight();
        if (extraW > 0) mapScroll.setHvalue(clamp(dragStartH - dx / extraW, 0, 1));
        if (extraH > 0) mapScroll.setVvalue(clamp(dragStartV - dy / extraH, 0, 1));
    }

    private void handleScroll(ScrollEvent e) {
        e.consume();
        double factor   = e.getDeltaY() > 0 ? 1.10 : 0.90;
        double newScale = clamp(currentScale * factor, minScale, MAX_SCALE);
        if (newScale == currentScale) return;

        Bounds vp = mapScroll.getViewportBounds();
        double scrolledX = mapScroll.getHvalue() * Math.max(0, canvasWrapper.getLayoutBounds().getWidth()  - vp.getWidth());
        double scrolledY = mapScroll.getVvalue() * Math.max(0, canvasWrapper.getLayoutBounds().getHeight() - vp.getHeight());
        double cursorCanvasX = (scrolledX + e.getX()) / currentScale;
        double cursorCanvasY = (scrolledY + e.getY()) / currentScale;

        currentScale = newScale;
        canvas.setScaleX(currentScale); canvas.setScaleY(currentScale);
        canvas.setTranslateX((1 - currentScale) * canvas.getPrefWidth()  / 2);
        canvas.setTranslateY((1 - currentScale) * canvas.getPrefHeight() / 2);

        Platform.runLater(() -> {
            Bounds nb  = canvasWrapper.getLayoutBounds();
            Bounds nvp = mapScroll.getViewportBounds();
            double newScrollX = cursorCanvasX * currentScale - e.getX();
            double newScrollY = cursorCanvasY * currentScale - e.getY();
            double extraW = nb.getWidth()  - nvp.getWidth();
            double extraH = nb.getHeight() - nvp.getHeight();
            if (extraW > 0) mapScroll.setHvalue(clamp(newScrollX / extraW, 0, 1));
            if (extraH > 0) mapScroll.setVvalue(clamp(newScrollY / extraH, 0, 1));
        });
    }

    private void applyInitialZoomAndCenter() {
        Bounds vp = mapScroll.getViewportBounds();
        double canvasW = canvas.getPrefWidth();
        double canvasH = canvas.getPrefHeight();

        if (vp.getWidth() <= 1 || vp.getHeight() <= 1 || canvasW <= 0 || canvasH <= 0) {
            new Timeline(new KeyFrame(Duration.millis(80), e -> applyInitialZoomAndCenter())).play();
            return;
        }

        double minByW = (vp.getWidth()  / canvasW) * MARGIN;
        double minByH = (vp.getHeight() / canvasH) * MARGIN;
        minScale = Math.min(Math.max(minByW, minByH), MAX_SCALE);
        currentScale = clamp(minScale * 1.3, minScale, MAX_SCALE);
        canvas.setScaleX(currentScale); canvas.setScaleY(currentScale);
        canvas.setTranslateX((1 - currentScale) * canvasW / 2);
        canvas.setTranslateY((1 - currentScale) * canvasH / 2);

        gameService.getCurrentNode().ifPresent(cur -> {
            double[] pos = positions.get(cur.getId());
            if (pos == null) return;
            Platform.runLater(() -> {
                Bounds nb  = canvasWrapper.getLayoutBounds();
                Bounds nvp = mapScroll.getViewportBounds();
                double extraW = nb.getWidth()  - nvp.getWidth();
                double extraH = nb.getHeight() - nvp.getHeight();
                double h = extraW > 0 ? clamp((pos[0] * currentScale - nvp.getWidth()  / 2) / extraW, 0, 1) : 0.5;
                double v = extraH > 0 ? clamp((pos[1] * currentScale - nvp.getHeight() / 2) / extraH, 0, 1) : 0.5;
                Timeline tl = new Timeline(
                        new KeyFrame(Duration.ZERO,
                                new KeyValue(mapScroll.hvalueProperty(), mapScroll.getHvalue()),
                                new KeyValue(mapScroll.vvalueProperty(), mapScroll.getVvalue())),
                        new KeyFrame(Duration.millis(520),
                                new KeyValue(mapScroll.hvalueProperty(), h),
                                new KeyValue(mapScroll.vvalueProperty(), v)));
                tl.play();
            });
        });
    }

    private void drawConnection(double[] from, double[] to, MapNode src, MapNode dest,
                                String currentId, int distSrc, int distDest) {
        if (dest == null) return;
        int maxDist = Math.max(
                src.isCleared()  ? 0 : distSrc,
                dest.isCleared() ? 0 : distDest);
        double lineOpacity;
        if      (maxDist <= NEAR_DIST) lineOpacity = 0.85;
        else if (maxDist <= MID_DIST)  lineOpacity = OPACITY_MID;
        else                           lineOpacity = OPACITY_FAR;

        boolean isVoidLink = src.getType() == NodeType.VOID || src.getType() == NodeType.VOID_BOSS
                || dest.getType() == NodeType.VOID || dest.getType() == NodeType.VOID_BOSS;
        boolean active = src.isCleared() || src.getId().equals(currentId);
        Line line = new Line(from[0], from[1], to[0], to[1]);
        line.setStrokeLineCap(StrokeLineCap.ROUND);
        if (active && maxDist <= NEAR_DIST) {
            if (isVoidLink) {
                line.setStroke(Color.web(VOID_COLOR, 0.9));
                line.setStrokeWidth(3.0);
                line.getStrokeDashArray().addAll(8.0, 4.0);
            } else {
                Color cs = Color.web(nodeColor(src.getType()));
                Color cd = Color.web(nodeColor(dest.getType()));
                line.setStroke(Color.color((cs.getRed()+cd.getRed())/2,(cs.getGreen()+cd.getGreen())/2,(cs.getBlue()+cd.getBlue())/2));
                line.setStrokeWidth(3.0);
            }
        } else {
            line.setStroke(isVoidLink ? Color.web(VOID_COLOR, 0.3) : Color.web("#2a2a4a"));
            line.setStrokeWidth(maxDist <= MID_DIST ? 1.5 : 1.0);
            line.getStrokeDashArray().addAll(6.0, 4.0);
        }
        line.setOpacity(lineOpacity);
        canvas.getChildren().add(line);
    }

    /** Linea placeholder per connessioni verso nodi segreti non ancora scoperti: quasi invisibile. */
    private void drawConnectionHidden(double[] from, double[] to) {
        Line line = new Line(from[0], from[1], to[0], to[1]);
        line.setStroke(Color.web("#111128"));
        line.setStrokeWidth(1.0);
        line.setOpacity(OPACITY_FAR);
        canvas.getChildren().add(line);
    }

    private VBox buildHoverPanel() {
        VBox p = new VBox(6);
        p.setAlignment(Pos.CENTER_LEFT);
        p.setStyle("-fx-background-color:#12122a;-fx-background-radius:12;-fx-border-color:#4c1d95;"
                + "-fx-border-radius:12;-fx-border-width:1.5;-fx-padding:14 18;"
                + "-fx-effect:dropshadow(gaussian,#000,16,0.4,0,4);");
        p.setPrefWidth(200); p.setVisible(false); p.setManaged(false);
        return p;
    }

    private void showHoverPanel(MapNode node, double cx, double cy, boolean locked, int dist) {
        if (!node.isCleared() && dist > NEAR_DIST) return;
        hoverPanel.getChildren().clear();
        String color = locked ? "#6b7280" : nodeColor(node.getType());
        Label iconLbl = new Label(locked ? "\ud83d\udd12" : nodeIcon(node.getType()));
        iconLbl.setStyle("-fx-font-size:28px;");
        Label nameLbl = new Label(node.getName());
        nameLbl.setStyle("-fx-font-size:14px;-fx-font-weight:bold;-fx-text-fill:" + color + ";");
        String typeDisplay = locked ? "BLOCCATO" : node.getType().name();
        if (!locked && node.getType() == NodeType.VOID)      typeDisplay = "\u2736 ABISSO";
        if (!locked && node.getType() == NodeType.VOID_BOSS) typeDisplay = "\u2736 BOSS SEGRETO";
        Label typeLbl = new Label(typeDisplay);
        String typeBg = (node.getType() == NodeType.VOID || node.getType() == NodeType.VOID_BOSS) && !locked
                ? "-fx-background-color:#3b0764;" : "-fx-background-color:#1e1e3a;";
        typeLbl.setStyle("-fx-font-size:10px;-fx-text-fill:#9ca3af;-fx-font-weight:bold;"
                + typeBg + "-fx-background-radius:6;-fx-padding:2 8;");
        String lockedDesc;
        if (locked && node.getType() == NodeType.VOID_BOSS)
            lockedDesc = "Richiede il Cuore di Vuoto";
        else if (locked && node.getRequiredClass() != null)
            lockedDesc = "Richiesto: " + node.getRequiredClass().replace("|", " o ");
        else
            lockedDesc = node.getDescription();
        Label descLbl = new Label(locked ? lockedDesc : node.getDescription());
        descLbl.setStyle("-fx-font-size:12px;-fx-text-fill:#d1d5db;");
        descLbl.setWrapText(true); descLbl.setMaxWidth(176);
        hoverPanel.getChildren().addAll(iconLbl, nameLbl, typeLbl, descLbl);
        double paneW = canvas.getPrefWidth();
        double px = cx + NODE_R + 10;
        double py = cy - NODE_R;
        if (paneW > 0 && px + 210 > paneW) px = cx - NODE_R - 220;
        hoverPanel.setLayoutX(Math.max(0, px));
        hoverPanel.setLayoutY(Math.max(0, py));
        hoverPanel.setVisible(true); hoverPanel.setManaged(true); hoverPanel.toFront();
    }

    private void hideHoverPanel() {
        hoverPanel.setVisible(false); hoverPanel.setManaged(false);
    }

    private StackPane buildNodeGraphic(MapNode node, double cx, double cy,
                                       boolean isCleared, boolean isCurrent,
                                       boolean isReachable, boolean isLocked,
                                       boolean isNewlyReachable, int dist,
                                       boolean secretHidden) {
        // Se il nodo e' segreto e non ancora scoperto, trattalo sempre come farFog
        // indipendentemente dalla distanza BFS (che lo vederebbe vicino se adiacente al boss)
        boolean revealed, midFog, farFog;
        if (secretHidden) {
            revealed = false;
            midFog   = false;
            farFog   = true;
        } else {
            revealed = isCleared || dist <= NEAR_DIST;
            midFog   = !revealed && dist <= MID_DIST;
            farFog   = !revealed && dist >  MID_DIST;
        }

        boolean isVoidNode = node.getType() == NodeType.VOID || node.getType() == NodeType.VOID_BOSS;

        String color = revealed ? (isLocked ? "#4b5563" : nodeColor(node.getType())) : "#3a3a5a";
        String icon  = revealed ? (isLocked ? "\ud83d\udd12" : nodeIcon(node.getType())) : "?";
        boolean isSecretUnlocked = revealed && node.getRequiredClass() != null && !isLocked;

        Circle pulseRing = null;
        if (isCurrent) {
            pulseRing = new Circle(NODE_R + 8);
            pulseRing.setFill(Color.TRANSPARENT);
            pulseRing.setStroke(Color.web(color, 0.5));
            pulseRing.setStrokeWidth(2);
            ScaleTransition pulse = new ScaleTransition(Duration.millis(900), pulseRing);
            pulse.setFromX(0.85); pulse.setFromY(0.85); pulse.setToX(1.15); pulse.setToY(1.15);
            pulse.setAutoReverse(true); pulse.setCycleCount(ScaleTransition.INDEFINITE); pulse.play();
        }

        Circle bg = new Circle(NODE_R);
        if (farFog) {
            bg.setFill(Color.web("#111128")); bg.setStroke(Color.web("#1e1e38")); bg.setStrokeWidth(1.0);
        } else if (midFog) {
            bg.setFill(Color.web("#14142a")); bg.setStroke(Color.web("#2e2e50")); bg.setStrokeWidth(1.5);
        } else if (isCurrent) {
            bg.setFill(Color.web(color, 0.35)); bg.setStroke(Color.web(color));
            bg.setStrokeWidth(3.5); bg.setEffect(new DropShadow(22, Color.web(color)));
        } else if (isCleared) {
            bg.setFill(Color.web("#1a2e1a")); bg.setStroke(Color.web("#4ade80", 0.7)); bg.setStrokeWidth(2);
        } else if (isLocked) {
            bg.setFill(Color.web("#1a1a2e")); bg.setStroke(Color.web("#4b5563")); bg.setStrokeWidth(1.5);
            ColorAdjust ca = new ColorAdjust(); ca.setSaturation(-0.8); bg.setEffect(ca);
        } else if (isVoidNode && isReachable) {
            bg.setFill(Color.web(VOID_COLOR, 0.2)); bg.setStroke(Color.web(VOID_COLOR)); bg.setStrokeWidth(2.5);
            bg.setEffect(new DropShadow(24, Color.web(VOID_COLOR, 0.9)));
        } else if (isSecretUnlocked && isNewlyReachable) {
            bg.setFill(Color.web(color, 0.25)); bg.setStroke(Color.web("#fbbf24")); bg.setStrokeWidth(2.5);
            bg.setEffect(new DropShadow(20, Color.web("#fbbf24", 0.8)));
        } else if (isReachable) {
            bg.setFill(Color.web(color, 0.2)); bg.setStroke(Color.web(color)); bg.setStrokeWidth(2.5);
            bg.setEffect(new DropShadow(14, Color.web(color, 0.6)));
        } else {
            bg.setFill(Color.web("#1a1a2e")); bg.setStroke(Color.web("#2a2a4a")); bg.setStrokeWidth(1.5);
        }

        String iconFontSize = (isCleared && revealed) ? "16" : "22";
        String iconColor    = farFog || midFog ? "#252540" : isCleared ? "#4ade80" : isLocked ? "#6b7280" : "white";
        Label iconLabel = new Label(revealed && isCleared ? "\u2714" : icon);
        iconLabel.setStyle("-fx-font-size:" + iconFontSize + "px;-fx-text-fill:" + iconColor + ";");

        Label nameLabel = new Label(revealed ? node.getName() : "");
        if (revealed) {
            String nameColor = isCurrent ? color : isCleared ? "#4ade80" : isLocked ? "#4b5563"
                    : (isVoidNode && isReachable) ? VOID_COLOR
                    : (isSecretUnlocked && isNewlyReachable) ? "#fbbf24"
                    : isReachable ? "white" : "#4a4a6a";
            boolean nameBold = isCurrent || (isVoidNode && isReachable) || (isSecretUnlocked && isNewlyReachable);
            nameLabel.setStyle("-fx-font-size:9px;-fx-text-fill:" + nameColor
                    + ";-fx-font-weight:" + (nameBold ? "bold" : "normal") + ";");
            nameLabel.setMaxWidth(NODE_R * 2 + 10); nameLabel.setWrapText(true); nameLabel.setAlignment(Pos.CENTER);
        }

        VBox nodeBox = new VBox(2, iconLabel, nameLabel);
        nodeBox.setAlignment(Pos.CENTER); nodeBox.setMaxWidth(NODE_R * 2);

        StackPane sp = pulseRing != null ? new StackPane(pulseRing, bg, nodeBox) : new StackPane(bg, nodeBox);
        double spSize = pulseRing != null ? (NODE_R + 8) * 2 : NODE_R * 2;
        sp.setLayoutX(cx - spSize / 2); sp.setLayoutY(cy - spSize / 2); sp.setPrefSize(spSize, spSize);

        if      (farFog) { sp.setOpacity(OPACITY_FAR); sp.setEffect(new GaussianBlur(4)); }
        else if (midFog) { sp.setOpacity(OPACITY_MID); }

        if (isVoidNode && isReachable && isNewlyReachable) {
            sp.setOpacity(0); sp.setScaleX(0.6); sp.setScaleY(0.6);
            FadeTransition ft = new FadeTransition(Duration.millis(800), sp); ft.setFromValue(0); ft.setToValue(1);
            ScaleTransition st = new ScaleTransition(Duration.millis(800), sp);
            st.setFromX(0.6); st.setFromY(0.6); st.setToX(1.0); st.setToY(1.0);
            new SequentialTransition(new javafx.animation.PauseTransition(Duration.millis(200)),
                    new javafx.animation.ParallelTransition(ft, st)).play();
        } else if (isSecretUnlocked && isNewlyReachable) {
            sp.setOpacity(0); sp.setScaleX(0.7); sp.setScaleY(0.7);
            FadeTransition ft = new FadeTransition(Duration.millis(600), sp); ft.setFromValue(0); ft.setToValue(1);
            ScaleTransition st = new ScaleTransition(Duration.millis(600), sp);
            st.setFromX(0.7); st.setFromY(0.7); st.setToX(1.0); st.setToY(1.0);
            new SequentialTransition(new javafx.animation.PauseTransition(Duration.millis(150)),
                    new javafx.animation.ParallelTransition(ft, st)).play();
        }

        sp.setOnMouseEntered(e -> showHoverPanel(node, cx, cy, isLocked, dist));
        sp.setOnMouseExited(e  -> hideHoverPanel());
        // I nodi segreti nascosti non sono cliccabili
        if (isReachable && revealed && !secretHidden) {
            sp.setStyle("-fx-cursor:hand;");
            sp.setOnMouseClicked(e -> onNodeSelected(node));
        }
        return sp;
    }

    private void buildLegend() {
        legendBox.getChildren().clear();
        String[][] items = {
                {"\u2714",       "#4ade80", "Completato (rivelato)"},
                {"\u25cf",       "#c4b5fd", "Posizione attuale"},
                {"\u25cb",       "white",   "Raggiungibile"},
                {"\u2726",       "#fbbf24", "Sbloccato dalla tua classe"},
                {"\u25c6",       VOID_COLOR,"Percorso dell'Abisso (Cavaliere)"},
                {"?",            "#3a3a5a", "Inesplorato (vicino)"},
                {"\u25cb",       "#1e1e38", "Nella nebbia"},
                {"\ud83d\udd12", "#6b7280", "Solo certa classe"}
        };
        for (String[] it : items) {
            Label dot = new Label(it[0]); dot.setStyle("-fx-font-size:14px;-fx-text-fill:" + it[1] + ";");
            Label lbl = new Label(it[2]); lbl.setStyle("-fx-font-size:11px;-fx-text-fill:#9ca3af;");
            HBox entry = new HBox(5, dot, lbl); entry.setAlignment(Pos.CENTER_LEFT);
            legendBox.getChildren().add(entry);
        }
    }

    private void buildControlsLegend() {
        controlsLegendBox.getChildren().clear();
        String[][] items = {
                {"Click + drag", "Muoviti nella mappa (orizzontale e verticale)"},
                {"Scroll \u2191",     "Aumenta zoom"},
                {"Scroll \u2193",     "Riduci zoom"},
                {"Click nodo",   "Avanza al nodo raggiungibile"}
        };
        for (String[] it : items) {
            Label key  = new Label(it[0]);
            key.setStyle("-fx-font-size:11px;-fx-text-fill:#e5e7eb;-fx-font-weight:bold;"
                    + "-fx-background-color:#1e1e3a;-fx-background-radius:6;-fx-padding:4 8;");
            Label desc = new Label(it[1]); desc.setStyle("-fx-font-size:11px;-fx-text-fill:#9ca3af;");
            HBox entry = new HBox(6, key, desc); entry.setAlignment(Pos.CENTER_LEFT);
            controlsLegendBox.getChildren().add(entry);
        }
    }

    private void onNodeSelected(MapNode node) {
        gameService.moveToNode(node.getId());
        EncounterType encounter = gameService.currentEncounter();
        try {
            Stage stage = (Stage) mapScroll.getScene().getWindow();
            switch (encounter) {
                case SHOP -> { gameService.resetUpgradeForNextShop();
                    FXMLLoader l = SceneNavigator.navigateTo(stage, "/it/unicam/cs/mpgc/rpg123393/view/shop-view.fxml");
                    l.<ShopController>getController().initData(gameService, playerName, vigore, arcano, imagePath); }
                case REST -> { FXMLLoader l = SceneNavigator.navigateTo(stage, "/it/unicam/cs/mpgc/rpg123393/view/rest-view.fxml");
                    l.<RestController>getController().initData(gameService, playerName, vigore, arcano, imagePath); }
                case EVENT -> {
                    String nodeId = gameService.getCurrentNode().map(MapNode::getId).orElse("");
                    FXMLLoader l = SceneNavigator.navigateTo(stage, "/it/unicam/cs/mpgc/rpg123393/view/event-view.fxml");
                    if ("nHK4".equals(nodeId))
                        l.<EventController>getController().initVoidEvent(gameService, playerName, vigore, arcano, imagePath);
                    else
                        l.<EventController>getController().initData(gameService, playerName, vigore, arcano, imagePath);
                }
                default -> { FXMLLoader l = SceneNavigator.navigateTo(stage, "/it/unicam/cs/mpgc/rpg123393/view/hello-view.fxml");
                    l.<HelloController>getController().initData(playerName, vigore, arcano, imagePath, gameService); }
            }
        } catch (IOException ex) { throw new RuntimeException("Errore navigazione mappa", ex); }
    }

    private static String nodeIcon(NodeType type) {
        return switch (type) {
            case BATTLE    -> "\u2694";
            case ELITE     -> "\ud83d\udc80";
            case BOSS      -> "\ud83d\udc09";
            case SHOP      -> "\ud83d\uded2";
            case REST      -> "\ud83d\udd25";
            case EVENT     -> "?";
            case VOID      -> "\u25c6";
            case VOID_BOSS -> "\u2620";
        };
    }

    private static String nodeColor(NodeType type) {
        return switch (type) {
            case BATTLE    -> "#60a5fa";
            case ELITE     -> "#f97316";
            case BOSS      -> "#ef4444";
            case SHOP      -> "#fbbf24";
            case REST      -> "#4ade80";
            case EVENT     -> "#c084fc";
            case VOID      -> VOID_COLOR;
            case VOID_BOSS -> VOID_BOSS_COLOR;
        };
    }

    private static double clamp(double v, double min, double max) { return Math.max(min, Math.min(max, v)); }
}
