package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;      // IMPORTANTE
import javafx.scene.image.ImageView;  // IMPORTANTE

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HelloController {

    @FXML private Label playerStatsLabel, enemyStatsLabel, playerBlockLabel, enemyIntentLabel;
    @FXML private Button cardBtn0, cardBtn1, cardBtn2;
    @FXML private TextArea consoleArea;
    @FXML private ImageView playerImage; // Riceve l'immagine dalla creazione

    private GameCharacter player;
    private GameCharacter enemy;

    private List<ICard> deck;
    private ICard[] hand = new ICard[3];
    private int enemyNextAttack = 0;
    private Random random = new Random();

    public void initData(String name, int forza, int vitalita, String imagePath) {
        // Calcolo HP e Mana
        int maxHp = 50 + (vitalita * 10);
        player = new GameCharacter(name, maxHp, 3 + (forza / 2));
        enemy = new GameCharacter("Goblin Guerriero", 60, 0);

        // Carica l'immagine scelta!
        playerImage.setImage(new Image(getClass().getResourceAsStream(imagePath)));

        deck = new ArrayList<>();
        deck.add(new StrikeCard());
        deck.add(new StrikeCard());
        deck.add(new DefendCard());
        deck.add(new DefendCard());
        deck.add(new FireballCard());

        consoleArea.appendText("Scontro Iniziato! Carte caricate nel mazzo.\n");
        startPlayerTurn();
    }

    private void startPlayerTurn() {
        // 1. Azzera lo scudo del turno precedente
        player.resetBlock();

        // 2. Ricarica il mana in modo corretto (Senza creare un nuovo oggetto!)
        player.restoreMana();

        // 3. Il nemico decide cosa fare in questo turno
        enemyNextAttack = 5 + random.nextInt(8);
        enemyIntentLabel.setText("Intento Nemico: Sta per attaccare per " + enemyNextAttack + " danni!");

        // 4. Pesca 3 carte casuali dal mazzo
        drawHand();
        updateUI();
    }


    private void drawHand() {
        Button[] buttons = {cardBtn0, cardBtn1, cardBtn2};
        for (int i = 0; i < 3; i++) {
            ICard randomCard = deck.get(random.nextInt(deck.size()));
            hand[i] = randomCard;

            Image cardImg = new Image(getClass().getResourceAsStream(randomCard.getImagePath()));
            ImageView iv = new ImageView(cardImg);

            // --- MODIFICA QUI: Misure raddoppiate per farle giganti! ---
            iv.setFitWidth(200);
            iv.setFitHeight(280);
            iv.setPreserveRatio(false);

            buttons[i].setGraphic(iv);
            buttons[i].setDisable(false);
        }
    }

    @FXML private void onCard0Click() { playCardFromHand(0, cardBtn0); }
    @FXML private void onCard1Click() { playCardFromHand(1, cardBtn1); }
    @FXML private void onCard2Click() { playCardFromHand(2, cardBtn2); }

    private void playCardFromHand(int index, Button button) {
        ICard card = hand[index];
        if (player.getCurrentMana() >= card.getManaCost()) {
            card.play(player, enemy);
            consoleArea.appendText("Hai giocato " + card.getName() + ".\n");
            button.setDisable(true);
            updateUI();

            if (!enemy.isAlive()) {
                consoleArea.appendText("VITTORIA! Il " + enemy.getName() + " è crollato a terra!\n");
                disableAllCardButtons();
            }
        } else {
            consoleArea.appendText("Non hai abbastanza Mana per questa carta!\n");
        }
    }

    @FXML
    private void onEndTurnClick() {
        if (!enemy.isAlive() || !player.isAlive()) return;

        consoleArea.appendText("\n--- TURNO DEL NEMICO ---\n");

        player.takeDamage(enemyNextAttack);
        consoleArea.appendText(enemy.getName() + " ti colpisce per " + enemyNextAttack + " danni!\n");

        if (!player.isAlive()) {
            consoleArea.appendText("SEI MORTO! Game Over nell'arena.\n");
            disableAllCardButtons();
        } else {
            consoleArea.appendText("--- TUO TURNO ---\n");
            startPlayerTurn();
        }
        updateUI();
    }

    private void disableAllCardButtons() {
        cardBtn0.setDisable(true);
        cardBtn1.setDisable(true);
        cardBtn2.setDisable(true);
    }

    private void updateUI() {
        playerStatsLabel.setText(player.getName() + " HP: " + player.getCurrentHp() + "/" + player.getMaxHp() + " | MANA: " + player.getCurrentMana() + "/3");
        playerBlockLabel.setText("Scudo Giocatore: " + player.getBlock() + " (Assorbe il prossimo attacco)");
        enemyStatsLabel.setText(enemy.getName() + " HP: " + enemy.getCurrentHp() + "/" + enemy.getMaxHp());
    }
}