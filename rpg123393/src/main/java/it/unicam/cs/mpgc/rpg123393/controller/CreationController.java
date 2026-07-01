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

    @FXML private void selectWarrior(ActionEvent event) {
        startGame(event, 3, 8, "/images/tank.jpg");
    }

    @FXML private void selectMage(ActionEvent event) {
        startGame(event, 8, 3, "/images/mago.jpg");
    }

    @FXML private void selectDragon(ActionEvent event) {
        startGame(event, 6, 6, "/images/dragon.jpg");
    }

    private void startGame(ActionEvent event, int forza, int vitalita, String imagePath) {
        try {
            String playerName = nameField.getText().trim();
            if (playerName.isEmpty()) { playerName = "Eroe Sconosciuto"; }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unicam/cs/mpgc/rpg123393/view/hello-view.fxml"));
            Parent root = loader.load();

            HelloController battleController = loader.getController();
            battleController.initData(playerName, forza, vitalita, imagePath);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 700, 750));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
