package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.model.ICard;
import it.unicam.cs.mpgc.rpg123393.service.CollectionService;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class CollectionController {

    @FXML private VBox      rootPane;
    @FXML private FlowPane  cardFlow;
    @FXML private ComboBox<String> classFilter;

    private CollectionService collectionService;

    @FXML
    public void initialize() {
        ImageLoaderHelper.applyBackground(rootPane, ImageLoaderHelper.backgroundPath("menu"));

        collectionService = new CollectionService();
        classFilter.getItems().add("Tutte");
        classFilter.getItems().addAll("Cavaliere", "Mago", "Dracomante", "Paladino", "Assassino");
        classFilter.setValue("Tutte");
        populateCards("Tutte");
    }

    @FXML
    private void onFilterChange() {
        String selected = classFilter.getValue();
        populateCards(selected);
    }

    private void populateCards(String filter) {
        cardFlow.getChildren().clear();
        List<ICard> cards = "Tutte".equals(filter)
                ? collectionService.getAllCards()
                : collectionService.getCardsByClass(filter);
        for (ICard card : cards) {
            cardFlow.getChildren().add(buildCardTile(card));
        }
    }

    private VBox buildCardTile(ICard card) {
        String color = CardStyleHelper.borderColor(card.getName());
        Label name = new Label(card.getName());
        name.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px; -fx-font-weight: bold;");
        name.setWrapText(true);
        name.setMaxWidth(140);

        Label cost = new Label("Mana: " + card.getManaCost());
        cost.setStyle("-fx-text-fill: #a78bfa; -fx-font-size: 11px;");

        Label desc = new Label(CardStyleHelper.description(card.getName()));
        desc.setStyle("-fx-text-fill: #a0a0c0; -fx-font-size: 10px;");
        desc.setWrapText(true);
        desc.setMaxWidth(140);

        VBox tile = new VBox(6, name, cost, desc);
        tile.setStyle("-fx-background-color: rgba(30,30,60,0.88); -fx-padding: 12;"
                + "-fx-background-radius: 10; -fx-border-color: " + color + ";"
                + "-fx-border-radius: 10; -fx-border-width: 1.5;"
                + "-fx-pref-width: 160; -fx-min-height: 90;");
        return tile;
    }

    @FXML
    private void onBack() {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            SceneNavigator.navigateTo(stage,
                    "/it/unicam/cs/mpgc/rpg123393/view/main-menu-view.fxml");
        } catch (IOException e) { e.printStackTrace(); }
    }
}
