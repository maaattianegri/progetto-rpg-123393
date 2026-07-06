package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.model.achievement.Achievement;
import it.unicam.cs.mpgc.rpg123393.model.achievement.AchievementCategory;
import it.unicam.cs.mpgc.rpg123393.model.achievement.AchievementRegistry;
import it.unicam.cs.mpgc.rpg123393.persistence.AchievementStore;
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

    @FXML private VBox  contentBox;
    @FXML private Label progressLabel;

    private final AchievementStore store = new AchievementStore();

    @FXML
    public void initialize() {
        AchievementStore.AchievementData data = null;
        try { data = store.load(); } catch (IOException e) { /* nessun file */ }

        List<String> unlocked = (data != null) ? data.unlocked : List.of();
        int totalUnlocked = 0;
        int total = AchievementRegistry.count();

        for (AchievementCategory cat : AchievementCategory.values()) {
            List<Achievement> items = AchievementRegistry.getByCategory(cat);
            if (items.isEmpty()) continue;

            int catUnlocked = (int) items.stream()
                    .filter(a -> unlocked.contains(a.getId())).count();
            totalUnlocked += catUnlocked;

            Label catHeader = new Label(cat.getDisplayName()
                    + "  " + catUnlocked + "/" + items.size());
            catHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;"
                    + " -fx-text-fill: #e0c97f;"
                    + " -fx-padding: 20 0 8 0;"
                    + " -fx-border-color: transparent transparent #3a3a5e transparent;"
                    + " -fx-border-width: 0 0 1 0;");

            FlowPane flow = new FlowPane();
            flow.setHgap(14);
            flow.setVgap(14);
            flow.setPrefWrapLength(900);
            flow.setStyle("-fx-background-color: transparent; -fx-padding: 4 0 8 0;");

            for (Achievement a : items) {
                boolean isUnlocked = unlocked.contains(a.getId());
                flow.getChildren().add(buildCard(a, isUnlocked));
            }

            contentBox.getChildren().addAll(catHeader, flow);
        }

        progressLabel.setText(totalUnlocked + " / " + total + " sbloccati");
    }

    @FXML
    private void onBack(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneNavigator.navigateTo(stage,
                    "/it/unicam/cs/mpgc/rpg123393/view/main-menu-view.fxml");
        } catch (IOException e) { e.printStackTrace(); }
    }

    private VBox buildCard(Achievement a, boolean unlocked) {
        Label icon = new Label(a.getDisplayIcon(unlocked));
        icon.setStyle("-fx-font-size: 32px;");

        Label name = new Label(a.getDisplayName(unlocked));
        name.setWrapText(true);
        name.setMaxWidth(155);
        name.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; "
                + (unlocked ? "-fx-text-fill: #e0c97f;" : "-fx-text-fill: #5a5a7a;"));

        Label desc = new Label(a.getDisplayDescription(unlocked));
        desc.setWrapText(true);
        desc.setMaxWidth(155);
        desc.setStyle("-fx-font-size: 11px; "
                + (unlocked ? "-fx-text-fill: #a0a0c0;" : "-fx-text-fill: #3a3a5a;"));

        VBox card = new VBox(7, icon, name, desc);
        card.setPrefWidth(175);
        card.setMinHeight(120);
        String borderColor = unlocked ? "#4ecca3" : "#2a2a3e";
        String bgColor     = unlocked ? "#1e2a4a" : "#111120";
        String baseStyle   = "-fx-background-color: " + bgColor
                + "; -fx-background-radius: 12;"
                + " -fx-border-color: " + borderColor
                + "; -fx-border-radius: 12; -fx-border-width: 1.5;"
                + " -fx-padding: 12;";
        card.setStyle(baseStyle);

        if (unlocked) {
            Tooltip tip = new Tooltip(a.getDescription());
            tip.setShowDelay(Duration.millis(400));
            tip.setStyle("-fx-background-color: #2a2a4e; -fx-text-fill: #e0c97f;"
                    + " -fx-font-size: 12px; -fx-padding: 8;");
            Tooltip.install(card, tip);
        }

        card.setOnMouseEntered(e -> card.setStyle(baseStyle
                + " -fx-effect: dropshadow(gaussian, "
                + (unlocked ? "#4ecca3" : "#3a3a5e")
                + ", 12, 0.3, 0, 0);"));
        card.setOnMouseExited(e -> card.setStyle(baseStyle));

        return card;
    }
}
