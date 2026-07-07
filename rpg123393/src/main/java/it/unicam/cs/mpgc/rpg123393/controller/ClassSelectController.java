package it.unicam.cs.mpgc.rpg123393.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ClassSelectController {

    @FXML private VBox      rootPane;
    @FXML private TextField nameField;

    @FXML private ImageView imgWarrior;
    @FXML private ImageView imgMage;
    @FXML private ImageView imgDragon;
    @FXML private ImageView imgPaladin;
    @FXML private ImageView imgAssassin;

    @FXML
    public void initialize() {
        ImageLoaderHelper.applyBackground(rootPane, ImageLoaderHelper.backgroundPath("menu"));
        ImageLoaderHelper.load(imgWarrior,  ImageLoaderHelper.classImagePath("knight"));
        ImageLoaderHelper.load(imgMage,     ImageLoaderHelper.classImagePath("mage"));
        ImageLoaderHelper.load(imgDragon,   ImageLoaderHelper.classImagePath("draco"));
        ImageLoaderHelper.load(imgPaladin,  ImageLoaderHelper.classImagePath("paladin"));
        ImageLoaderHelper.load(imgAssassin, ImageLoaderHelper.classImagePath("assassin"));
    }

    private void goToBattle(String classKey, String className, int vigore, int arcano) {
        String name = nameField.getText().trim();
        if (name.isEmpty()) name = className;
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            FXMLLoader loader = SceneNavigator.navigateTo(
                    stage, "/it/unicam/cs/mpgc/rpg123393/view/hello-view.fxml");
            HelloController ctrl = loader.getController();
            ctrl.initData(name, vigore, arcano, classKey, className);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void selectWarrior()  { goToBattle("knight",  "Cavaliere",  8, 3); }
    @FXML private void selectMage()     { goToBattle("mage",    "Mago",        3, 8); }
    @FXML private void selectDragon()   { goToBattle("draco",   "Dracomante",  6, 6); }
    @FXML private void selectPaladin()  { goToBattle("paladin", "Paladino",    7, 5); }
    @FXML private void selectAssassin() { goToBattle("assassin","Assassino",   4, 6); }

    @FXML private void onBack() {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            SceneNavigator.navigateTo(stage,
                    "/it/unicam/cs/mpgc/rpg123393/view/main-menu-view.fxml");
        } catch (IOException e) { e.printStackTrace(); }
    }
}
