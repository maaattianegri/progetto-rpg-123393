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

public class AchievementToastManager {

    private static final AchievementToastManager INSTANCE = new AchievementToastManager();

    private static final double TOAST_WIDTH   = 320;
    private static final double TOAST_HEIGHT  = 80;
    private static final double MARGIN_RIGHT  = 24;
    private static final double MARGIN_BOTTOM = 24;
    private static final double SHOW_SECONDS  = 3.5;

    private Stage     primaryStage;
    private StackPane rootPane;
    private boolean   showing = false;
    private final Queue<String> queue = new LinkedList<>();

    private AchievementToastManager() {}

    public static AchievementToastManager getInstance() { return INSTANCE; }

    public void init(Stage stage, StackPane rootPane) {
        this.primaryStage = stage;
        this.rootPane     = rootPane;
        // Il rootPane stesso non deve mai bloccare i click sul contenuto sottostante
        rootPane.setPickOnBounds(false);
    }

    public void show(String achievementId) {
        Platform.runLater(() -> {
            queue.add(achievementId);
            if (!showing) showNext();
        });
    }

    private void showNext() {
        if (queue.isEmpty() || rootPane == null) { showing = false; return; }
        showing = true;
        String id = queue.poll();
        Achievement a = AchievementRegistry.getById(id);
        if (a == null) { showNext(); return; }

        VBox toast = buildToast(a);

        // Il toast non deve intercettare nessun evento mouse
        toast.setMouseTransparent(true);

        StackPane.setAlignment(toast, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(toast, new Insets(0, MARGIN_RIGHT, MARGIN_BOTTOM, 0));
        toast.setTranslateX(TOAST_WIDTH + MARGIN_RIGHT + 10);
        toast.setOpacity(0);

        rootPane.getChildren().add(toast);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(350), toast);
        slideIn.setToX(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(350), toast);
        fadeIn.setToValue(1.0);
        ParallelTransition enter = new ParallelTransition(slideIn, fadeIn);

        PauseTransition pause = new PauseTransition(Duration.seconds(SHOW_SECONDS));

        TranslateTransition slideOut = new TranslateTransition(Duration.millis(400), toast);
        slideOut.setToX(TOAST_WIDTH + MARGIN_RIGHT + 10);
        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), toast);
        fadeOut.setToValue(0.0);
        ParallelTransition exit = new ParallelTransition(slideOut, fadeOut);

        SequentialTransition seq = new SequentialTransition(enter, pause, exit);
        seq.setOnFinished(e -> {
            rootPane.getChildren().remove(toast);
            showNext();
        });
        seq.play();
    }

    private VBox buildToast(Achievement a) {
        Label icon = new Label(a.getIcon());
        icon.setStyle("-fx-font-size: 28px;");

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

        VBox card = new VBox(content);
        card.setPrefWidth(TOAST_WIDTH);
        card.setMaxWidth(TOAST_WIDTH);
        card.setMinHeight(TOAST_HEIGHT);
        card.setStyle(
                "-fx-background-color: #0d0d1a;"
                + " -fx-background-radius: 0 0 10 10;"
                + " -fx-border-color: #4ecca3;"
                + " -fx-border-radius: 0 0 10 10;"
                + " -fx-border-width: 0 1.5 1.5 1.5;"
                + " -fx-effect: dropshadow(gaussian, rgba(78,204,163,0.45), 18, 0.3, 0, 0);"
        );

        Rectangle bar = new Rectangle(TOAST_WIDTH, 5);
        bar.setFill(Color.web("#4ecca3"));

        VBox toast = new VBox(bar, card);
        toast.setPrefWidth(TOAST_WIDTH);
        toast.setStyle("-fx-background-color: transparent;");
        return toast;
    }
}
