package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.model.EventPool;
import it.unicam.cs.mpgc.rpg123393.model.GameEvent;
import it.unicam.cs.mpgc.rpg123393.service.GameService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * Controller del nodo EVENT.
 *
 * Mostra un evento casuale estratto da EventPool.
 * Ogni scelta ha un effetto meccanico sul player.
 * Dopo la scelta appare il bottone "Continua" che porta alla mappa.
 */
public class EventController {

    @FXML private Label titleLabel;
    @FXML private Label descriptionLabel;
    @FXML private VBox  choicesBox;
    @FXML private Label outcomeLabel;
    @FXML private Button continueBtn;

    private GameService gameService;
    private String      playerName;
    private int         vigore;
    private int         arcano;
    private String      imagePath;
    private GameEvent   currentEvent;

    public void initData(GameService gs, String playerName, int vigore, int arcano, String imagePath) {
        this.gameService = gs;
        this.playerName  = playerName;
        this.vigore      = vigore;
        this.arcano      = arcano;
        this.imagePath   = imagePath;
        this.currentEvent = EventPool.random();

        titleLabel.setText("\u2753  " + currentEvent.getTitle());
        descriptionLabel.setText(currentEvent.getDescription());
        outcomeLabel.setText("");
        continueBtn.setVisible(false);
        continueBtn.setManaged(false);

        buildChoiceButtons(currentEvent.getChoices());
    }

    private void buildChoiceButtons(List<GameEvent.EventChoice> choices) {
        choicesBox.getChildren().clear();
        for (GameEvent.EventChoice choice : choices) {
            Button btn = new Button(choice.getText());
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setWrapText(true);
            btn.setStyle(
                "-fx-background-color: #1e1e3a;"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 14px;"
                + "-fx-border-color: #c084fc;"
                + "-fx-border-radius: 8;"
                + "-fx-background-radius: 8;"
                + "-fx-border-width: 2;"
                + "-fx-padding: 14 20;"
                + "-fx-cursor: hand;"
                + "-fx-effect: dropshadow(gaussian, #c084fc, 8, 0.2, 0, 0);"
            );
            btn.setOnAction(e -> applyChoice(choice));
            choicesBox.getChildren().add(btn);
        }
    }

    private void applyChoice(GameEvent.EventChoice choice) {
        // Applica l'effetto meccanico
        choice.applyEffect(gameService.getPlayer());

        // Mostra l'esito
        outcomeLabel.setText(choice.getOutcomeDescription());

        // Disabilita tutti i bottoni
        choicesBox.getChildren().forEach(n -> n.setDisable(true));

        // Mostra il bottone continua
        continueBtn.setVisible(true);
        continueBtn.setManaged(true);
    }

    @FXML
    private void onContinue() {
        try {
            Stage stage = (Stage) titleLabel.getScene().getWindow();
            FXMLLoader loader = SceneNavigator.navigateTo(
                    stage, "/it/unicam/cs/mpgc/rpg123393/view/map-view.fxml");
            MapController ctrl = loader.getController();
            ctrl.initData(gameService, playerName, vigore, arcano, imagePath);
        } catch (IOException e) { e.printStackTrace(); }
    }
}
