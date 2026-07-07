package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.service.SaveService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenuController {

    @FXML private VBox  rootPane;
    @FXML private Button continueBtn;
    @FXML private Label  saveInfoLabel;

    @FXML
    public void initialize() {
        ImageLoaderHelper.applyBackground(rootPane, ImageLoaderHelper.backgroundPath("menu"));

        SaveService save = new SaveService();
        boolean hasSave = save.hasSave();
        continueBtn.setDisable(!hasSave);
        continueBtn.setOpacity(hasSave ? 1.0 : 0.4);
        if (hasSave) {
            saveInfoLabel.setText(save.getSaveInfo());
        }
    }

    @FXML private void onNewGame() {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            SceneNavigator.navigateTo(stage,
                    "/it/unicam/cs/mpgc/rpg123393/view/class-select-view.fxml");
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void onContinue() {
        try {
            SaveService save = new SaveService();
            if (!save.hasSave()) return;
            Stage stage = (Stage) rootPane.getScene().getWindow();
            FXMLLoader loader = SceneNavigator.navigateTo(
                    stage, "/it/unicam/cs/mpgc/rpg123393/view/map-view.fxml");
            MapController ctrl = loader.getController();
            ctrl.initFromSave(save.loadSave());
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void onCollection() {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            SceneNavigator.navigateTo(stage,
                    "/it/unicam/cs/mpgc/rpg123393/view/collection-view.fxml");
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void onAchievements() {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            SceneNavigator.navigateTo(stage,
                    "/it/unicam/cs/mpgc/rpg123393/view/achievement-view.fxml");
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void onExit() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }
}
