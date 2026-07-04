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
    private static final double MIN_SCALE  = 0.40;
    private static final double MAX_SCALE  = 2.00;

    // mapPane e' il Pane dichiarato nel FXML come content dello ScrollPane.
    // canvas e' il Pane su cui disegniamo nodi e linee; viene wrappato in
    // un Group (canvasWrapper) che viene impostato come content dello ScrollPane
    // via codice. Il Group ridimensiona il suo bounding box seguendo
    // setScaleX/Y del figlio, quindi lo ScrollPane calcola correttamente
    // il range scrollabile dopo ogni zoom.
    @FXML private ScrollPane mapScroll;
    @FXML private Pane       mapPane;   // non usato come content - rimane per compatibilita' FXML
    @FXML private Label      progressLabel;
    @FXML private Label      playerHpLabel;
    @FXML private Label      playerGoldLabel;
    @FXML private Label      playerLevelLabel;
    @FXML private Label      selectedNodeLabel;
    @FXML private HBox       legendBox;
    @FXML private HBox       controlsLegendBox;

    private Pane   canvas;         // superficie di disegno
    private Group  canvasWrapper;  // wrapper che lo ScrollPane misura
    private VBox   hoverPanel;

    private GameService gameService;
    private String      playerName;
    private int         vigore;
    private int         arcano;
    private String      imagePath;

    private final Map<String, double[]> positions = new HashMap<>();
    private double currentScale = 1.0;
    private double dragStartX, dragStartY, dragStartH, dragStartV;

    public void initData(GameService gs, String name, int vigore, int arcano, String imagePath) {
        this.gameService = gs;
        this.playerName  = name;
        this.vigore      = vigore;
        this.arcano      = arcano;
        this.imagePath   = imagePath;

        // canvas: il Pane su cui disegniamo. Ha background scuro e dimensioni
        // fisiche che corrispondono all'area logica della mappa.
        canvas = new Pane();
        canvas.setStyle("-fx-background-color: #0d0d1e;");

        // canvasWrapper: Group che wrappa canvas. Un Group calcola il suo
        // layoutBounds come l'unione dei boundsInParent dei figli, includendo
        // le trasformazioni (scaleX/Y). Cosi' lo ScrollPane vede le dimensioni
        // reali dopo lo zoom e aggiorna correttamente hmin/hmax/vmin/vmax.
        canvasWrapper = new Group(canvas);

        // Sostituiamo il content dello ScrollPane (era mapPane dal FXML)
        // con il nostro wrapper programmato.
        mapScroll.setContent(canvasWrapper);
        mapScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mapScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mapScroll.setPannable(false);
        mapScroll.setFitToHeight(false);
        mapScroll.setFitToWidth(false);

        buildLayout();
        render();
        setupPanAndZoom();
        Platform.runLater(this::animateToCurrentNode);
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

    // -------------------------------------------------------
    // Render
    // -------------------------------------------------------

    private void render() {
        canvas.getChildren().clear();

        List<MapNode> all = gameService.getMapService().getMap().getAllNodes();
        Map<String, MapNode> byId = new HashMap<>();
        for (MapNode n : all) byId.put(n.getId(), n);

        String currentId = gameService.getCurrentNode().map(MapNode::getId).orElse("");
        Set<String> reachableIds = new HashSet<>();
        gameService.getReachableNodes().forEach(n -> reachableIds.add(n.getId()));
        String playerClass = gameService.getClassName();

        Set<String> currentNodeNextIds = gameService.getCurrentNode()
                .map(n -> new HashSet<>(n.getNextNodeIds()))
                .orElse(new HashSet<>());

        for (MapNode n : all) {
            double[] from = positions.get(n.getId());
            if (from == null) continue;
            for (String sid : n.getNextNodeIds()) {
                double[] to = positions.get(sid);
                if (to == null) continue;
                drawConnection(from, to, n, byId.get(sid), currentId);
            }
        }

        hoverPanel = buildHoverPanel();
        canvas.getChildren().add(hoverPanel);

        for (MapNode n : all) {
            double[] pos = positions.get(n.getId());
            if (pos == null) continue;
            boolean locked = n.isLockedFor(playerClass);
            boolean isNewlyReachable = currentNodeNextIds.contains(n.getId());
            canvas.getChildren().add(buildNodeGraphic(
                    n, pos[0], pos[1],
                    n.isCleared(),
                    n.getId().equals(currentId),
                    reachableIds.contains(n.getId()) && !locked,
                    locked,
                    isNewlyReachable
            ));
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

    // -------------------------------------------------------
    // Pan & Zoom
    // -------------------------------------------------------

    private void setupPanAndZoom() {
        mapScroll.addEventFilter(MouseEvent.MOUSE_PRESSED,  this::handleMousePressed);
        mapScroll.addEventFilter(MouseEvent.MOUSE_DRAGGED,  this::handleMouseDragged);
        mapScroll.addEventFilter(ScrollEvent.SCROLL,        this::handleScroll);
    }

    private void handleMousePressed(MouseEvent e) {
        dragStartX = e.getSceneX();
        dragStartY = e.getSceneY();
        dragStartH = mapScroll.getHvalue();
        dragStartV = mapScroll.getVvalue();
        e.consume();
    }

    private void handleMouseDragged(MouseEvent e) {
        e.consume();
        double dx = e.getSceneX() - dragStartX;
        double dy = e.getSceneY() - dragStartY;

        // Il bounding box del canvasWrapper include gia' lo zoom (setScaleX/Y su canvas).
        // Usiamo i layoutBounds del wrapper che lo ScrollPane usa internamente.
        Bounds contentBounds = canvasWrapper.getLayoutBounds();
        Bounds vp = mapScroll.getViewportBounds();
        double extraW = contentBounds.getWidth()  - vp.getWidth();
        double extraH = contentBounds.getHeight() - vp.getHeight();
        if (extraW > 0) mapScroll.setHvalue(clamp(dragStartH - dx / extraW, 0, 1));
        if (extraH > 0) mapScroll.setVvalue(clamp(dragStartV - dy / extraH, 0, 1));
    }

    private void handleScroll(ScrollEvent e) {
        e.consume();
        double factor   = e.getDeltaY() > 0 ? 1.10 : 0.90;
        double newScale = clamp(currentScale * factor, MIN_SCALE, MAX_SCALE);
        if (newScale == currentScale) return;

        // Coordinate del cursore nel sistema di riferimento del canvas (pre-zoom)
        Bounds vp = mapScroll.getViewportBounds();
        double scrolledX = mapScroll.getHvalue() * Math.max(0, canvasWrapper.getLayoutBounds().getWidth()  - vp.getWidth());
        double scrolledY = mapScroll.getVvalue() * Math.max(0, canvasWrapper.getLayoutBounds().getHeight() - vp.getHeight());
        double cursorCanvasX = (scrolledX + e.getX()) / currentScale;
        double cursorCanvasY = (scrolledY + e.getY()) / currentScale;

        currentScale = newScale;
        // setScaleX/Y su canvas: il Group wrapper aggiorna automaticamente
        // il suo layoutBounds e lo ScrollPane ricalcola hmin/hmax/vmin/vmax.
        canvas.setScaleX(currentScale);
        canvas.setScaleY(currentScale);
        // Riposiziona il pivot tramite translateX/Y per mantenere il punto
        // sotto il cursore fisso durante lo zoom.
        canvas.setTranslateX((1 - currentScale) * canvas.getPrefWidth()  / 2);
        canvas.setTranslateY((1 - currentScale) * canvas.getPrefHeight() / 2);

        // Ricalcola Hvalue/Vvalue per mantenere il punto del cursore fermo.
        Platform.runLater(() -> {
            Bounds nb = canvasWrapper.getLayoutBounds();
            Bounds nvp = mapScroll.getViewportBounds();
            double newScrollX = cursorCanvasX * currentScale - e.getX();
            double newScrollY = cursorCanvasY * currentScale - e.getY();
            double extraW = nb.getWidth()  - nvp.getWidth();
            double extraH = nb.getHeight() - nvp.getHeight();
            if (extraW > 0) mapScroll.setHvalue(clamp(newScrollX / extraW, 0, 1));
            if (extraH > 0) mapScroll.setVvalue(clamp(newScrollY / extraH, 0, 1));
        });
    }

    private void animateToCurrentNode() {
        gameService.getCurrentNode().ifPresent(cur -> {
            double[] pos = positions.get(cur.getId());
            if (pos == null) return;
            Bounds nb  = canvasWrapper.getLayoutBounds();
            Bounds vp  = mapScroll.getViewportBounds();
            double extraW = nb.getWidth()  - vp.getWidth();
            double extraH = nb.getHeight() - vp.getHeight();
            double h = extraW > 0 ? clamp((pos[0] * currentScale - vp.getWidth()  / 2) / extraW, 0, 1) : 0;
            double v = extraH > 0 ? clamp((pos[1] * currentScale - vp.getHeight() / 2) / extraH, 0, 1) : 0;
            Timeline tl = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(mapScroll.hvalueProperty(), mapScroll.getHvalue()),
                            new KeyValue(mapScroll.vvalueProperty(), mapScroll.getVvalue())),
                    new KeyFrame(Duration.millis(520),
                            new KeyValue(mapScroll.hvalueProperty(), h),
                            new KeyValue(mapScroll.vvalueProperty(), v))
            );
            tl.play();
        });
    }

    // -------------------------------------------------------
    // Connessioni
    // -------------------------------------------------------

    private void drawConnection(double[] from, double[] to, MapNode src, MapNode dest, String currentId) {
        if (dest == null) return;
        boolean active = src.isCleared() || src.getId().equals(currentId);
        Line line = new Line(from[0], from[1], to[0], to[1]);
        line.setStrokeLineCap(StrokeLineCap.ROUND);
        if (active) {
            Color cs = Color.web(nodeColor(src.getType()));
            Color cd = Color.web(nodeColor(dest.getType()));
            line.setStroke(Color.color((cs.getRed()+cd.getRed())/2,(cs.getGreen()+cd.getGreen())/2,(cs.getBlue()+cd.getBlue())/2));
            line.setStrokeWidth(3.0); line.setOpacity(0.85);
        } else {
            line.setStroke(Color.web("#2a2a4a"));
            line.setStrokeWidth(1.5);
            line.getStrokeDashArray().addAll(6.0, 4.0);
            line.setOpacity(0.5);
        }
        canvas.getChildren().add(line);
    }

    // -------------------------------------------------------
    // Hover panel
    // -------------------------------------------------------

    private VBox buildHoverPanel() {
        VBox p = new VBox(6);
        p.setAlignment(Pos.CENTER_LEFT);
        p.setStyle("-fx-background-color:#12122a;-fx-background-radius:12;-fx-border-color:#4c1d95;"
                + "-fx-border-radius:12;-fx-border-width:1.5;-fx-padding:14 18;"
                + "-fx-effect:dropshadow(gaussian,#000,16,0.4,0,4);");
        p.setPrefWidth(200); p.setVisible(false); p.setManaged(false);
        return p;
    }

    private void showHoverPanel(MapNode node, double cx, double cy, boolean locked) {
        hoverPanel.getChildren().clear();
        String color = locked ? "#6b7280" : nodeColor(node.getType());
        Label iconLbl = new Label(locked ? "\ud83d\udd12" : nodeIcon(node.getType()));
        iconLbl.setStyle("-fx-font-size:28px;");
        Label nameLbl = new Label(node.getName());
        nameLbl.setStyle("-fx-font-size:14px;-fx-font-weight:bold;-fx-text-fill:" + color + ";");
        Label typeLbl = new Label(locked ? "BLOCCATO" : node.getType().name());
        typeLbl.setStyle("-fx-font-size:10px;-fx-text-fill:#9ca3af;-fx-font-weight:bold;"
                + "-fx-background-color:#1e1e3a;-fx-background-radius:6;-fx-padding:2 8;");
        Label descLbl = new Label(locked
                ? "Richiesto: " + node.getRequiredClass().replace("|", " o ")
                : node.getDescription());
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

    // -------------------------------------------------------
    // Nodo grafico
    // -------------------------------------------------------

    private StackPane buildNodeGraphic(MapNode node, double cx, double cy,
                                       boolean isCleared, boolean isCurrent,
                                       boolean isReachable, boolean isLocked,
                                       boolean isNewlyReachable) {
        String color = isLocked ? "#4b5563" : nodeColor(node.getType());
        String icon  = isLocked ? "\ud83d\udd12" : nodeIcon(node.getType());

        boolean isSecretUnlocked = node.getRequiredClass() != null && !isLocked;

        Circle pulseRing = null;
        if (isCurrent) {
            pulseRing = new Circle(NODE_R + 8);
            pulseRing.setFill(Color.TRANSPARENT);
            pulseRing.setStroke(Color.web(color, 0.5));
            pulseRing.setStrokeWidth(2);
            ScaleTransition pulse = new ScaleTransition(Duration.millis(900), pulseRing);
            pulse.setFromX(0.85); pulse.setFromY(0.85);
            pulse.setToX(1.15);   pulse.setToY(1.15);
            pulse.setAutoReverse(true); pulse.setCycleCount(ScaleTransition.INDEFINITE); pulse.play();
        }

        Circle bg = new Circle(NODE_R);
        if (isCurrent) {
            bg.setFill(Color.web(color, 0.35)); bg.setStroke(Color.web(color));
            bg.setStrokeWidth(3.5); bg.setEffect(new DropShadow(22, Color.web(color)));
        } else if (isCleared) {
            bg.setFill(Color.web("#1a2e1a")); bg.setStroke(Color.web("#4ade80", 0.7)); bg.setStrokeWidth(2);
        } else if (isLocked) {
            bg.setFill(Color.web("#1a1a2e")); bg.setStroke(Color.web("#4b5563")); bg.setStrokeWidth(1.5);
            ColorAdjust ca = new ColorAdjust(); ca.setSaturation(-0.8); bg.setEffect(ca);
        } else if (isSecretUnlocked && isNewlyReachable) {
            bg.setFill(Color.web(color, 0.25)); bg.setStroke(Color.web("#fbbf24"));
            bg.setStrokeWidth(2.5);
            bg.setEffect(new DropShadow(20, Color.web("#fbbf24", 0.8)));
        } else if (isReachable) {
            bg.setFill(Color.web(color, 0.2)); bg.setStroke(Color.web(color));
            bg.setStrokeWidth(2.5); bg.setEffect(new DropShadow(14, Color.web(color, 0.6)));
        } else {
            bg.setFill(Color.web("#1a1a2e")); bg.setStroke(Color.web("#2a2a4a")); bg.setStrokeWidth(1.5);
        }

        Label iconLabel = new Label(isCleared ? "\u2714" : icon);
        iconLabel.setStyle("-fx-font-size:" + (isCleared ? "16" : "22") + "px;"
                + "-fx-text-fill:" + (isCleared ? "#4ade80" : isLocked ? "#6b7280" : "white") + ";");

        String nameColor = isCurrent        ? color
                         : isCleared        ? "#4ade80"
                         : isLocked         ? "#4b5563"
                         : (isSecretUnlocked && isNewlyReachable) ? "#fbbf24"
                         : isReachable      ? "white"
                         : "#4a4a6a";
        boolean nameBold = isCurrent || (isSecretUnlocked && isNewlyReachable);

        Label nameLabel = new Label(node.getName());
        nameLabel.setStyle("-fx-font-size:9px;-fx-text-fill:" + nameColor
                + ";-fx-font-weight:" + (nameBold ? "bold" : "normal") + ";");
        nameLabel.setMaxWidth(NODE_R * 2 + 10); nameLabel.setWrapText(true); nameLabel.setAlignment(Pos.CENTER);

        VBox nodeBox = new VBox(2, iconLabel, nameLabel);
        nodeBox.setAlignment(Pos.CENTER); nodeBox.setMaxWidth(NODE_R * 2);

        StackPane sp = pulseRing != null ? new StackPane(pulseRing, bg, nodeBox) : new StackPane(bg, nodeBox);
        double spSize = pulseRing != null ? (NODE_R + 8) * 2 : NODE_R * 2;
        sp.setLayoutX(cx - spSize / 2); sp.setLayoutY(cy - spSize / 2); sp.setPrefSize(spSize, spSize);

        if (isSecretUnlocked && isNewlyReachable) {
            sp.setOpacity(0);
            sp.setScaleX(0.7); sp.setScaleY(0.7);
            FadeTransition ft = new FadeTransition(Duration.millis(600), sp);
            ft.setFromValue(0); ft.setToValue(1);
            ScaleTransition st = new ScaleTransition(Duration.millis(600), sp);
            st.setFromX(0.7); st.setFromY(0.7); st.setToX(1.0); st.setToY(1.0);
            SequentialTransition seq = new SequentialTransition(
                    new javafx.animation.PauseTransition(Duration.millis(150)),
                    new javafx.animation.ParallelTransition(ft, st));
            seq.play();
        }

        sp.setOnMouseEntered(e -> showHoverPanel(node, cx, cy, isLocked));
        sp.setOnMouseExited(e  -> hideHoverPanel());
        if (isReachable) {
            sp.setStyle("-fx-cursor:hand;");
            sp.setOnMouseClicked(e -> onNodeSelected(node));
        }
        return sp;
    }

    // -------------------------------------------------------
    // Legende
    // -------------------------------------------------------

    private void buildLegend() {
        legendBox.getChildren().clear();
        String[][] items = {
                {"\u2714",  "#4ade80", "Completato"},
                {"\u25cf",  "#c4b5fd", "Posizione attuale"},
                {"\u25cb",  "white",   "Raggiungibile"},
                {"\u2726",  "#fbbf24", "Sbloccato dalla tua classe"},
                {"\u25cb",  "#2a2a4a", "Non ancora accessibile"},
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

    // -------------------------------------------------------
    // Navigazione
    // -------------------------------------------------------

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
                case EVENT -> { FXMLLoader l = SceneNavigator.navigateTo(stage, "/it/unicam/cs/mpgc/rpg123393/view/event-view.fxml");
                    l.<EventController>getController().initData(gameService, playerName, vigore, arcano, imagePath); }
                default -> { FXMLLoader l = SceneNavigator.navigateTo(stage, "/it/unicam/cs/mpgc/rpg123393/view/hello-view.fxml");
                    l.<HelloController>getController().initData(playerName, vigore, arcano, imagePath, gameService); }
            }
        } catch (IOException ex) { throw new RuntimeException("Errore navigazione mappa", ex); }
    }

    // -------------------------------------------------------
    // Utility
    // -------------------------------------------------------

    private static String nodeIcon(NodeType type) {
        return switch (type) {
            case BATTLE -> "\u2694";
            case ELITE  -> "\ud83d\udc80";
            case BOSS   -> "\ud83d\udc09";
            case SHOP   -> "\ud83d\uded2";
            case REST   -> "\ud83d\udd25";
            case EVENT  -> "?";
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

    private static double clamp(double v, double min, double max) { return Math.max(min, Math.min(max, v)); }
}
