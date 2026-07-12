package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.persistence.GameState;
import it.unicam.cs.mpgc.rpg123393.persistence.JsonSaveRepository;
import it.unicam.cs.mpgc.rpg123393.service.GameService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class MainMenuController {

    @FXML private VBox   rootPane;
    @FXML private Button continueBtn;
    @FXML private Label  saveInfoLabel;

    private final JsonSaveRepository saveRepo = new JsonSaveRepository();
    private GameState loadedState = null;

    @FXML
    public void initialize() {
        ImageLoaderHelper.applyBackground(rootPane, ImageLoaderHelper.backgroundPath("menu"));

        if (saveRepo.saveExists()) {
            try { loadedState = saveRepo.load(); }
            catch (IOException e) { System.err.println("[WARN] " + e.getMessage()); }
        }
        if (loadedState != null) {
            String date = loadedState.getSaveDate() != null
                    ? loadedState.getSaveDate().substring(0, 10) : "";
            saveInfoLabel.setText(
                    loadedState.getPlayerName()
                    + "  \u00b7  " + loadedState.getClassName()
                    + "  \u00b7  Lv. " + loadedState.getPlayerLevel()
                    + "  \u00b7  " + date);
            continueBtn.setDisable(false);
        } else {
            saveInfoLabel.setText("Nessun salvataggio trovato");
            continueBtn.setDisable(true);
            continueBtn.setOpacity(0.4);
        }
    }

    @FXML
    private void onNewGame(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneNavigator.navigateTo(stage,
                    "/it/unicam/cs/mpgc/rpg123393/view/class-select-view.fxml");
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void onContinue(ActionEvent event) {
        if (loadedState == null) return;
        try {
            GameService gs = new GameService();
            gs.restoreFromState(loadedState);
            // Ricava classKey dalla className salvata (es. "Guerriero" → "guerriero")
            // in modo che HelloController carichi correttamente l'immagine battle.
            String classKey = ImageLoaderHelper.classKey(loadedState.getClassName());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = SceneNavigator.navigateTo(
                    stage, "/it/unicam/cs/mpgc/rpg123393/view/hello-view.fxml");
            HelloController ctrl = loader.getController();
            ctrl.initData(loadedState.getPlayerName(),
                    gs.getVigore(), gs.getArcano(),
                    classKey, gs);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void onCollection(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneNavigator.navigateTo(stage,
                    "/it/unicam/cs/mpgc/rpg123393/view/collection-view.fxml");
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void onAchievements(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneNavigator.navigateTo(stage,
                    "/it/unicam/cs/mpgc/rpg123393/view/achievement-view.fxml");
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void onExit(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Esci dal gioco");
        alert.setHeaderText(null);
        alert.setContentText("Sei sicuro di voler uscire?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) Platform.exit();
    }
}
