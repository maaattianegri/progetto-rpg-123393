package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.model.achievement.Achievement;
import it.unicam.cs.mpgc.rpg123393.model.achievement.AchievementRegistry;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Singleton che mostra notifiche toast stile Steam per gli achievement sbloccati.
 *
 * Utilizzo:
 *   1. All'avvio: AchievementToastManager.getInstance().init(stage);
 *   2. Quando si sblocca: AchievementToastManager.getInstance().show("achievement_id");
 *
 * Il manager gestisce una coda: se più achievement si sbloccano in rapida successione
 * i toast escono in sequenza, non sovrapposti.
 */
public class AchievementToastManager {

    private static final AchievementToastManager INSTANCE = new AchievementToastManager();

    private static final double TOAST_WIDTH  = 320;
    private static final double TOAST_HEIGHT = 80;
    private static final double MARGIN       = 20;
    private static final double SHOW_SECONDS = 3.5;

    private Stage         primaryStage;
    private StackPane     overlay;
    private boolean       showing = false;
    private final Queue<String> queue = new LinkedList<>();

    private AchievementToastManager() {}

    public static AchievementToastManager getInstance() { return INSTANCE; }

    // -------------------------------------------------------
    // Inizializzazione (chiamare una volta in HelloApplication)
    // -------------------------------------------------------

    public void init(Stage stage) {
        this.primaryStage = stage;
        // Ogni volta che la scena cambia, re-inietta l'overlay
        stage.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) injectOverlay(newScene);
        });
        if (stage.getScene() != null) injectOverlay(stage.getScene());
    }

    private void injectOverlay(javafx.scene.Scene scene) {
        // Cerca se c'è già un StackPane root; altrimenti wrappa
        if (scene.getRoot() instanceof StackPane sp) {
            overlay = sp;
        } else {
            StackPane wrapper = new StackPane(scene.getRoot());
            wrapper.setStyle("-fx-background-color: transparent;");
            scene.setRoot(wrapper);
            overlay = wrapper;
        }
    }

    // -------------------------------------------------------
    // API pubblica
    // -------------------------------------------------------

    /** Accoda un toast per l'achievement con questo id. Thread-safe. */
    public void show(String achievementId) {
        Platform.runLater(() -> {
            queue.add(achievementId);
            if (!showing) showNext();
        });
    }

    // -------------------------------------------------------
    // Logica interna
    // -------------------------------------------------------

    private void showNext() {
        if (queue.isEmpty() || overlay == null) { showing = false; return; }
        showing = true;
        String id = queue.poll();
        Achievement a = AchievementRegistry.getById(id);
        if (a == null) { showNext(); return; }

        VBox toast = buildToast(a);

        // Posiziona in basso a destra, fuori schermo
        double sceneW = primaryStage.getScene().getWidth();
        double sceneH = primaryStage.getScene().getHeight();

        StackPane.setAlignment(toast, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(toast, new Insets(0, MARGIN, MARGIN, 0));
        toast.setTranslateX(TOAST_WIDTH + MARGIN + 10); // fuori schermo a destra
        toast.setOpacity(0);

        overlay.getChildren().add(toast);

        // --- Animazione: slide-in + fade-in ---
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(350), toast);
        slideIn.setToX(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(350), toast);
        fadeIn.setToValue(1);
        ParallelTransition enter = new ParallelTransition(slideIn, fadeIn);

        // --- Pausa ---
        PauseTransition pause = new PauseTransition(Duration.seconds(SHOW_SECONDS));

        // --- Fade-out ---
        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), toast);
        fadeOut.setToValue(0);
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(400), toast);
        slideOut.setToX(TOAST_WIDTH + MARGIN + 10);
        ParallelTransition exit = new ParallelTransition(fadeOut, slideOut);

        SequentialTransition seq = new SequentialTransition(enter, pause, exit);
        seq.setOnFinished(e -> {
            overlay.getChildren().remove(toast);
            showNext();
        });
        seq.play();
    }

    private VBox buildToast(Achievement a) {
        // Icona
        Label icon = new Label(a.getIcon());
        icon.setStyle("-fx-font-size: 28px;");

        // Testi
        Label header = new Label("Achievement sbloccato!");
        header.setStyle("-fx-font-size: 11px; -fx-text-fill: #4ecca3; -fx-font-weight: bold;");

        Label name = new Label(a.getName());
        name.setStyle("-fx-font-size: 14px; -fx-text-fill: #e0c97f; -fx-font-weight: bold;");
        name.setWrapText(true);
        name.setMaxWidth(220);

        VBox textBox = new VBox(2, header, name);
        textBox.setAlignment(Pos.CENTER_LEFT);

        HBox content = new HBox(12, icon, textBox);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setPadding(new Insets(12, 16, 12, 14));

        VBox toast = new VBox(content);
        toast.setPrefWidth(TOAST_WIDTH);
        toast.setMaxWidth(TOAST_WIDTH);
        toast.setMinHeight(TOAST_HEIGHT);
        toast.setStyle(
                "-fx-background-color: #0d0d1a;"
                + " -fx-background-radius: 10;"
                + " -fx-border-color: #4ecca3;"
                + " -fx-border-radius: 10;"
                + " -fx-border-width: 1.5;"
                + " -fx-effect: dropshadow(gaussian, rgba(78,204,163,0.45), 18, 0.3, 0, 0);"
        );

        // Barra verde in cima (stile Steam)
        Rectangle bar = new Rectangle(TOAST_WIDTH, 4);
        bar.setFill(Color.web("#4ecca3"));
        bar.setArcWidth(10);
        bar.setArcHeight(10);

        VBox wrapper = new VBox(bar, toast);
        wrapper.setStyle("-fx-background-color: transparent;");
        wrapper.setPrefWidth(TOAST_WIDTH);
        return wrapper;
    }
}
