package it.unicam.cs.mpgc.rpg123393.controller;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Map;

public class ClassSelectController {

    @FXML private TextField nameField;

    @FXML private VBox cardWarrior;
    @FXML private VBox cardMage;
    @FXML private VBox cardDragon;
    @FXML private VBox cardPaladin;
    @FXML private VBox cardAssassin;

    @FXML
    private void initialize() {
        Map<VBox, String> cards = Map.of(
                cardWarrior,  "#e74c3c",
                cardMage,     "#9b59b6",
                cardDragon,   "#e67e22",
                cardPaladin,  "#f1c40f",
                cardAssassin, "#27ae60"
        );
        cards.forEach(this::attachHoverAnimation);
    }

    private void attachHoverAnimation(VBox card, String hexColor) {
        Color glowColor = Color.web(hexColor);
        DropShadow glow = new DropShadow(32, glowColor);
        glow.setSpread(0.2);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(150), card);
        scaleIn.setToX(1.03);
        scaleIn.setToY(1.03);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(150), card);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);

        card.setOnMouseEntered(e -> {
            scaleIn.playFromStart();
            card.setEffect(glow);
        });
        card.setOnMouseExited(e -> {
            scaleOut.playFromStart();
            card.setEffect(null);
        });
    }

    @FXML private void selectWarrior(ActionEvent e)  { startGame(e, 8, 3, "/images/tank.jpg",  "Cavaliere"); }
    @FXML private void selectMage(ActionEvent e)     { startGame(e, 3, 8, "/images/mago.jpg",  "Mago"); }
    @FXML private void selectDragon(ActionEvent e)   { startGame(e, 6, 6, "/images/dragon.jpg","Dracomante"); }
    @FXML private void selectPaladin(ActionEvent e)  { startGame(e, 7, 5, "/images/tank.jpg",  "Paladino"); }
    @FXML private void selectAssassin(ActionEvent e) { startGame(e, 4, 6, "/images/mago.jpg",  "Assassino"); }

    @FXML
    private void onBack(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneNavigator.navigateTo(stage,
                    "/it/unicam/cs/mpgc/rpg123393/view/main-menu-view.fxml");
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void startGame(ActionEvent event, int vigore, int arcano,
                           String imagePath, String className) {
        try {
            String name = nameField.getText().trim();
            if (name.isEmpty()) name = "Eroe Sconosciuto";
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = SceneNavigator.navigateTo(
                    stage, "/it/unicam/cs/mpgc/rpg123393/view/hello-view.fxml");
            HelloController ctrl = loader.getController();
            ctrl.initData(name, vigore, arcano, imagePath, className);
        } catch (IOException ex) { ex.printStackTrace(); }
    }
}
