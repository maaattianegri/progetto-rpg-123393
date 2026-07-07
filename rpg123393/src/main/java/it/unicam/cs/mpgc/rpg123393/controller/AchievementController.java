package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.model.achievement.Achievement;
import it.unicam.cs.mpgc.rpg123393.model.achievement.AchievementRegistry;
import it.unicam.cs.mpgc.rpg123393.service.AchievementService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class AchievementController {

    @FXML private VBox  rootPane;
    @FXML private Label progressLabel;
    @FXML private VBox  achievementList;

    @FXML
    public void initialize() {
        ImageLoaderHelper.applyBackground(rootPane, ImageLoaderHelper.backgroundPath("menu"));

        AchievementService service = new AchievementService();
        List<String> unlocked = service.getUnlocked();
        List<Achievement> all = AchievementRegistry.getAll();

        progressLabel.setText(unlocked.size() + " / " + all.size() + " sbloccati");

        for (Achievement ach : all) {
            boolean isUnlocked = unlocked.contains(ach.getId());
            achievementList.getChildren().add(buildRow(ach, isUnlocked));
        }
    }

    private HBox buildRow(Achievement ach, boolean isUnlocked) {
        String icon    = isUnlocked ? ach.getIcon() : "\u26AA";
        String color   = isUnlocked ? "#e0c97f" : "#5a5a7a";
        String opacity = isUnlocked ? "1.0" : "0.5";

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 20px;");

        Label nameLabel = new Label(ach.getDisplayName(isUnlocked));
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        Label descLabel = new Label(ach.getDisplayDescription(isUnlocked));
        descLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");

        VBox textBox = new VBox(2, nameLabel, descLabel);

        HBox row = new HBox(12, iconLabel, textBox);
        row.setStyle("-fx-background-color: rgba(30,30,60,0.75); -fx-padding: 10 16;"
                + "-fx-background-radius: 8; -fx-opacity: " + opacity + ";");
        return row;
    }

    @FXML
    private void onBack() {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            SceneNavigator.navigateTo(stage,
                    "/it/unicam/cs/mpgc/rpg123393/view/main-menu-view.fxml");
        } catch (IOException e) { e.printStackTrace(); }
    }
}
