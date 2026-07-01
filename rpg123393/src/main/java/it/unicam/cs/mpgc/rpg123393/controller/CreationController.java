package it.unicam.cs.mpgc.rpg123393.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class CreationController {
    @FXML private TextField nameField;

    // Guerriero: Vigore 8 (molti HP) · Arcano 3 (poco mana)
    @FXML private void selectWarrior(ActionEvent event) {
        startGame(event, 8, 3, "/images/tank.jpg");
    }

    // Mago: Vigore 3 (pochi HP) · Arcano 8 (molto mana)
    @FXML private void selectMage(ActionEvent event) {
        startGame(event, 3, 8, "/images/mago.jpg");
    }

    // Dracomante: Vigore 6 · Arcano 6 (bilanciato)
    @FXML private void selectDragon(ActionEvent event) {
        startGame(event, 6, 6, "/images/dragon.jpg");
    }

    private void startGame(ActionEvent event, int vigore, int arcano, String imagePath) {
        try {
            String playerName = nameField.getText().trim();
            if (playerName.isEmpty()) { playerName = "Eroe Sconosciuto"; }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unicam/cs/mpgc/rpg123393/view/hello-view.fxml"));
            Parent root = loader.load();

            HelloController battleController = loader.getController();
            battleController.initData(playerName, vigore, arcano, imagePath);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 700, 750));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
