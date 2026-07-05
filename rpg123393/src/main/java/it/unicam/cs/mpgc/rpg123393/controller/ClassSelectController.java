package it.unicam.cs.mpgc.rpg123393.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ClassSelectController {

    @FXML private TextField nameField;

    // Guerriero → Cavaliere (easter egg HK: accesso al ramo VOID)
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
