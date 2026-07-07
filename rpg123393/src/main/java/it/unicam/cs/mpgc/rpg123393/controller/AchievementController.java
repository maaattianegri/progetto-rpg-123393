package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.model.achievement.Achievement;
import it.unicam.cs.mpgc.rpg123393.model.achievement.AchievementCategory;
import it.unicam.cs.mpgc.rpg123393.model.achievement.AchievementRegistry;
import it.unicam.cs.mpgc.rpg123393.service.AchievementService;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
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

        // Raggruppa per categoria
        for (AchievementCategory cat : AchievementCategory.values()) {
            List<Achievement> inCat = AchievementRegistry.getByCategory(cat);
            if (inCat.isEmpty()) continue;

            // Intestazione categoria
            Label catLabel = new Label(cat.name());
            catLabel.setStyle("-fx-text-fill: #e0c97f; -fx-font-size: 13px; -fx-font-weight: bold;"
                    + "-fx-padding: 14 16 4 16;");
            achievementList.getChildren().add(catLabel);

            for (Achievement ach : inCat) {
                boolean isUnlocked = unlocked.contains(ach.getId());
                achievementList.getChildren().add(buildRow(ach, isUnlocked));
            }
        }
    }

    private HBox buildRow(Achievement ach, boolean isUnlocked) {
        String icon    = ach.getDisplayIcon(isUnlocked);
        String color   = isUnlocked ? "#e0c97f" : "#5a5a7a";
        String opacity = isUnlocked ? "1.0" : "0.5";

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 22px; -fx-min-width: 36;");

        Label nameLabel = new Label(ach.getDisplayName(isUnlocked));
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        Label descLabel = new Label(ach.getDisplayDescription(isUnlocked));
        descLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(500);

        VBox textBox = new VBox(2, nameLabel, descLabel);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        Label statusLabel = new Label(isUnlocked ? "\u2713" : "\u25CB");
        statusLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: "
                + (isUnlocked ? "#4ecca3" : "#3a3a5a") + ";");

        HBox row = new HBox(12, iconLabel, textBox, statusLabel);
        row.setAlignment(Pos.CENTER_LEFT);
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
