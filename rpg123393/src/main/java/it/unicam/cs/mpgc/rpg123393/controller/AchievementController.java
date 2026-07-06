package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.model.achievement.Achievement;
import it.unicam.cs.mpgc.rpg123393.model.achievement.AchievementRegistry;
import it.unicam.cs.mpgc.rpg123393.persistence.GameState;
import it.unicam.cs.mpgc.rpg123393.persistence.JsonSaveRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class AchievementController {

    @FXML private FlowPane achievementsFlow;
    @FXML private Label    progressLabel;

    private final JsonSaveRepository saveRepo = new JsonSaveRepository();

    @FXML
    public void initialize() {
        GameState state = null;
        try { state = saveRepo.load(); } catch (IOException e) { /* nessun save */ }

        List<String> unlocked = state != null ? state.getUnlockedAchievements() : List.of();
        List<Achievement> all  = AchievementRegistry.getAll();

        int count = 0;
        for (Achievement a : all) {
            boolean isUnlocked = unlocked.contains(a.getId());
            if (isUnlocked) count++;
            achievementsFlow.getChildren().add(buildCard(a, isUnlocked));
        }

        progressLabel.setText(count + " / " + all.size() + " sbloccati");
    }

    @FXML
    private void onBack(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneNavigator.navigateTo(stage,
                    "/it/unicam/cs/mpgc/rpg123393/view/main-menu-view.fxml");
        } catch (IOException e) { e.printStackTrace(); }
    }

    // -------------------------------------------------------
    // Builder card achievement
    // -------------------------------------------------------

    private VBox buildCard(Achievement a, boolean unlocked) {
        // Icona
        Label icon = new Label(a.getDisplayIcon(unlocked));
        icon.setStyle("-fx-font-size: 34px;");

        // Nome
        Label name = new Label(a.getDisplayName(unlocked));
        name.setWrapText(true);
        name.setMaxWidth(160);
        name.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; "
                + (unlocked ? "-fx-text-fill: #e0c97f;" : "-fx-text-fill: #5a5a7a;"));

        // Descrizione
        Label desc = new Label(a.getDisplayDescription(unlocked));
        desc.setWrapText(true);
        desc.setMaxWidth(160);
        desc.setStyle("-fx-font-size: 11px; "
                + (unlocked ? "-fx-text-fill: #a0a0c0;" : "-fx-text-fill: #3a3a5a;"));

        // Card container
        VBox card = new VBox(8, icon, name, desc);
        card.setPrefWidth(180);
        card.setMinHeight(130);
        card.setStyle("-fx-background-color: "
                + (unlocked ? "#1e2a4a" : "#111120")
                + "; -fx-background-radius: 12;"
                + " -fx-border-color: "
                + (unlocked ? "#4ecca3" : "#2a2a3e")
                + "; -fx-border-radius: 12; -fx-border-width: 1.5;"
                + " -fx-padding: 14; -fx-cursor: "
                + (unlocked ? "default" : "default") + ";");

        // Tooltip con descrizione completa solo se sbloccato
        if (unlocked) {
            Tooltip tip = new Tooltip(a.getDescription());
            tip.setShowDelay(Duration.millis(400));
            tip.setStyle("-fx-background-color: #2a2a4e; -fx-text-fill: #e0c97f;"
                    + " -fx-font-size: 12px; -fx-padding: 8;");
            Tooltip.install(card, tip);
        }

        // Hover effect
        String baseStyle = card.getStyle();
        card.setOnMouseEntered(e -> card.setStyle(baseStyle
                + " -fx-effect: dropshadow(gaussian, "
                + (unlocked ? "#4ecca3" : "#3a3a5e")
                + ", 12, 0.3, 0, 0);"));
        card.setOnMouseExited(e -> card.setStyle(baseStyle));

        return card;
    }
}
