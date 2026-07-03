package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.model.EncounterType;
import it.unicam.cs.mpgc.rpg123393.model.MapNode;
import it.unicam.cs.mpgc.rpg123393.model.NodeType;
import it.unicam.cs.mpgc.rpg123393.service.GameService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controller della schermata mappa a nodi.
 *
 * Non contiene logica di gioco: chiede solo a GameService cosa mostrare
 * e delega la navigazione a MapService tramite gameService.moveToNode().
 *
 * Gestisce tutti i NodeType: BATTLE, ELITE, BOSS, SHOP, REST, EVENT.
 */
public class MapController {

    @FXML private Label currentNodeLabel;
    @FXML private Label currentNodeDescLabel;
    @FXML private Label progressLabel;
    @FXML private HBox  nodeButtonsBox;
    @FXML private Label noNodesLabel;
    @FXML private Label playerHpLabel;
    @FXML private Label playerGoldLabel;
    @FXML private Label playerLevelLabel;

    private GameService gameService;
    private String      playerName;
    private int         vigore;
    private int         arcano;
    private String      imagePath;

    // -------------------------------------------------------
    // Inizializzazione
    // -------------------------------------------------------

    public void initData(GameService gs, String playerName, int vigore, int arcano, String imagePath) {
        this.gameService = gs;
        this.playerName  = playerName;
        this.vigore      = vigore;
        this.arcano      = arcano;
        this.imagePath   = imagePath;
        refresh();
    }

    // -------------------------------------------------------
    // Render
    // -------------------------------------------------------

    private void refresh() {
        Optional<MapNode> currentOpt = gameService.getCurrentNode();
        currentOpt.ifPresentOrElse(
            node -> {
                currentNodeLabel.setText(nodeIcon(node.getType()) + "  " + node.getName());
                currentNodeDescLabel.setText(node.getDescription());
            },
            () -> {
                currentNodeLabel.setText("—");
                currentNodeDescLabel.setText("");
            }
        );

        int idx   = gameService.getEncounterIndex();
        int total = gameService.getEncounterTotal();
        progressLabel.setText("Nodo " + idx + " / " + total);

        var p = gameService.getPlayer();
        playerHpLabel.setText("❤ " + p.getCurrentHp() + "/" + p.getMaxHp());
        playerGoldLabel.setText("🪙 " + gameService.getGold());
        playerLevelLabel.setText("Lv. " + gameService.getPlayerLevel());

        nodeButtonsBox.getChildren().clear();
        List<MapNode> reachable = gameService.getReachableNodes();

        if (reachable.isEmpty()) {
            noNodesLabel.setText("Nessun percorso disponibile — fine della run!");
            noNodesLabel.setVisible(true);
            noNodesLabel.setManaged(true);
        } else {
            noNodesLabel.setVisible(false);
            noNodesLabel.setManaged(false);
            for (MapNode node : reachable) {
                nodeButtonsBox.getChildren().add(buildNodeButton(node));
            }
        }
    }

    private Button buildNodeButton(MapNode node) {
        String icon  = nodeIcon(node.getType());
        String color = nodeColor(node.getType());
        Button btn = new Button(icon + "  " + node.getName() + "\n" + node.getDescription());
        btn.setWrapText(true);
        btn.setMaxWidth(220);
        btn.setStyle(
            "-fx-background-color: #1e1e3a;"
            + "-fx-text-fill: white;"
            + "-fx-font-size: 13px;"
            + "-fx-border-color: " + color + ";"
            + "-fx-border-radius: 10;"
            + "-fx-background-radius: 10;"
            + "-fx-border-width: 2;"
            + "-fx-padding: 16 20;"
            + "-fx-cursor: hand;"
            + "-fx-effect: dropshadow(gaussian, " + color + ", 10, 0.3, 0, 0);"
        );
        btn.setOnAction(e -> onNodeSelected(node));
        return btn;
    }

    // -------------------------------------------------------
    // Navigazione verso il nodo scelto
    // -------------------------------------------------------

    private void onNodeSelected(MapNode node) {
        gameService.moveToNode(node.getId());
        EncounterType encounter = gameService.currentEncounter();
        try {
            Stage stage = (Stage) currentNodeLabel.getScene().getWindow();
            switch (encounter) {
                case SHOP -> {
                    FXMLLoader loader = SceneNavigator.navigateTo(
                        stage, "/it/unicam/cs/mpgc/rpg123393/view/shop-view.fxml");
                    ShopController ctrl = loader.getController();
                    ctrl.initData(gameService, playerName, vigore, arcano, imagePath);
                }
                case REST -> {
                    FXMLLoader loader = SceneNavigator.navigateTo(
                        stage, "/it/unicam/cs/mpgc/rpg123393/view/rest-view.fxml");
                    RestController ctrl = loader.getController();
                    ctrl.initData(gameService, playerName, vigore, arcano, imagePath);
                }
                case EVENT -> {
                    FXMLLoader loader = SceneNavigator.navigateTo(
                        stage, "/it/unicam/cs/mpgc/rpg123393/view/event-view.fxml");
                    EventController ctrl = loader.getController();
                    ctrl.initData(gameService, playerName, vigore, arcano, imagePath);
                }
                default -> {
                    // NORMAL, BATTLE, ELITE, BOSS
                    FXMLLoader loader = SceneNavigator.navigateTo(
                        stage, "/it/unicam/cs/mpgc/rpg123393/view/hello-view.fxml");
                    HelloController ctrl = loader.getController();
                    ctrl.initData(playerName, vigore, arcano, imagePath, gameService);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // -------------------------------------------------------
    // Utility
    // -------------------------------------------------------

    private static String nodeIcon(NodeType type) {
        return switch (type) {
            case BATTLE -> "⚔";
            case ELITE  -> "💀";
            case BOSS   -> "🐉";
            case SHOP   -> "🛒";
            case REST   -> "🔥";
            case EVENT  -> "❓";
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
