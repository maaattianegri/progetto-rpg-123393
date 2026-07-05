package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.model.EventPool;
import it.unicam.cs.mpgc.rpg123393.model.GameEvent;
import it.unicam.cs.mpgc.rpg123393.model.MapNode;
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
 * Ogni scelta ha un effetto meccanico tramite GameService.
 * Dopo la scelta appare il bottone "Continua" che porta alla mappa.
 *
 * Il metodo {@link #initVoidEvent} gestisce il bivio speciale del nodo nHK4
 * ("Il Velo tra i Mondi"): accettare il Cuore di Vuoto setta il flag
 * {@code voidHeartObtained} in GameService e naviga al boss segreto;
 * rifiutare reindirizza al Drago Antico (nBB2).
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

    /** Usato solo dal flusso Void (nHK4) per sapere dove navigare dopo il rifiuto. */
    private boolean      voidEventMode = false;
    /** true = giocatore ha accettato il Cuore di Vuoto → naviga a nHKB. */
    private boolean      voidAccepted  = false;

    // -------------------------------------------------------
    // Inizializzazione evento standard
    // -------------------------------------------------------

    public void initData(GameService gs, String playerName, int vigore, int arcano, String imagePath) {
        this.gameService = gs;
        this.playerName  = playerName;
        this.vigore      = vigore;
        this.arcano      = arcano;
        this.imagePath   = imagePath;
        this.voidEventMode = false;
        this.currentEvent = EventPool.random();

        titleLabel.setText("\u2753  " + currentEvent.getTitle());
        descriptionLabel.setText(currentEvent.getDescription());
        outcomeLabel.setText("");
        continueBtn.setVisible(false);
        continueBtn.setManaged(false);

        buildChoiceButtons(currentEvent.getChoices());
    }

    // -------------------------------------------------------
    // Inizializzazione evento speciale HK — nHK4
    // -------------------------------------------------------

    /**
     * Mostra il bivio "Il Velo tra i Mondi".
     * Accettare: setta voidHeartObtained + avanza verso nHKB.
     * Rifiutare: reindirizza verso nBB2 (Drago Antico).
     */
    public void initVoidEvent(GameService gs, String playerName, int vigore, int arcano, String imagePath) {
        this.gameService   = gs;
        this.playerName    = playerName;
        this.vigore        = vigore;
        this.arcano        = arcano;
        this.imagePath     = imagePath;
        this.voidEventMode = true;

        titleLabel.setText("\u25fc  Il Velo tra i Mondi");
        titleLabel.setStyle("-fx-text-fill: #c084fc; -fx-font-size: 20px; -fx-font-weight: bold;");
        descriptionLabel.setText(
                "Davanti a te galleggia qualcosa di oscuro e pulsante.\n"
                + "Una forza antica ti chiama dall'altra parte del Velo.\n\n"
                + "Accetti il Cuore di Vuoto e affronta il tuo stesso riflesso?\n"
                + "Oppure rifuiti e torna al percorso principale?"
        );
        outcomeLabel.setText("");
        continueBtn.setVisible(false);
        continueBtn.setManaged(false);

        choicesBox.getChildren().clear();
        buildVoidChoiceButton(
                "\u25fc  Accetta il Cuore di Vuoto",
                "-fx-background-color: #1e0338; -fx-text-fill: #c084fc;"
                + " -fx-border-color: #9333ea; -fx-border-width: 2; -fx-border-radius: 8;"
                + " -fx-background-radius: 8; -fx-font-size: 14px; -fx-padding: 14 20; -fx-cursor: hand;"
                + " -fx-effect: dropshadow(gaussian, #9333ea, 12, 0.4, 0, 0);",
                true
        );
        buildVoidChoiceButton(
                "Rifiuta e torna indietro",
                "-fx-background-color: #1e1e3a; -fx-text-fill: #9ca3af;"
                + " -fx-border-color: #374151; -fx-border-width: 1.5; -fx-border-radius: 8;"
                + " -fx-background-radius: 8; -fx-font-size: 14px; -fx-padding: 14 20; -fx-cursor: hand;",
                false
        );
    }

    private void buildVoidChoiceButton(String text, String style, boolean accept) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setWrapText(true);
        btn.setStyle(style);
        btn.setOnAction(e -> applyVoidChoice(accept));
        choicesBox.getChildren().add(btn);
    }

    private void applyVoidChoice(boolean accepted) {
        this.voidAccepted = accepted;
        choicesBox.getChildren().forEach(n -> n.setDisable(true));

        if (accepted) {
            gameService.obtainVoidHeart();
            outcomeLabel.setText(
                    "\u25fc  Il Cuore di Vuoto è tuo.\n"
                    + "Senti il Vuoto fluire in te. Il Cavaliere Vacuo ti aspetta."
            );
            outcomeLabel.setStyle("-fx-text-fill: #c084fc; -fx-font-size: 13px;");
        } else {
            outcomeLabel.setText(
                    "Hai rifiutato il richiamo del Vuoto.\n"
                    + "Il sentiero ti riporta verso il Drago Antico."
            );
            outcomeLabel.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 13px;");
        }

        continueBtn.setVisible(true);
        continueBtn.setManaged(true);
    }

    // -------------------------------------------------------
    // Evento standard
    // -------------------------------------------------------

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
        choice.applyEffect(gameService);
        outcomeLabel.setText(choice.getOutcomeDescription());
        choicesBox.getChildren().forEach(n -> n.setDisable(true));
        continueBtn.setVisible(true);
        continueBtn.setManaged(true);
    }

    // -------------------------------------------------------
    // Continua — naviga alla mappa (o al boss segreto se void accettato)
    // -------------------------------------------------------

    @FXML
    private void onContinue() {
        try {
            Stage stage = (Stage) titleLabel.getScene().getWindow();

            if (voidEventMode && voidAccepted) {
                // Accettato: avanza al nodo nHKB (VOID_BOSS)
                gameService.moveToNode("nHKB");
                FXMLLoader loader = SceneNavigator.navigateTo(
                        stage, "/it/unicam/cs/mpgc/rpg123393/view/hello-view.fxml");
                loader.<HelloController>getController()
                      .initData(playerName, vigore, arcano, imagePath, gameService);
            } else if (voidEventMode) {
                // Rifiutato: avanza verso nBB2 (Drago Antico, già collegato in MapService)
                gameService.moveToNode("nBB2");
                FXMLLoader loader = SceneNavigator.navigateTo(
                        stage, "/it/unicam/cs/mpgc/rpg123393/view/hello-view.fxml");
                loader.<HelloController>getController()
                      .initData(playerName, vigore, arcano, imagePath, gameService);
            } else {
                // Evento normale: torna alla mappa
                FXMLLoader loader = SceneNavigator.navigateTo(
                        stage, "/it/unicam/cs/mpgc/rpg123393/view/map-view.fxml");
                loader.<MapController>getController()
                      .initData(gameService, playerName, vigore, arcano, imagePath);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}
