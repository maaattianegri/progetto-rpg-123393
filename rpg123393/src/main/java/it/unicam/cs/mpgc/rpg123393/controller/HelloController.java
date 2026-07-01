package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.model.ICard;
import it.unicam.cs.mpgc.rpg123393.service.GameService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;

/**
 * Controller della schermata di battaglia.
 * Responsabilità: aggiornare la UI in base ai risultati restituiti da GameService.
 * NON contiene logica di gioco: ogni decisione è delegata a GameService.
 */
public class HelloController {

    // --- Elementi FXML ---
    @FXML private Label    playerStatsLabel;
    @FXML private Label    enemyStatsLabel;
    @FXML private Label    playerBlockLabel;
    @FXML private Label    enemyIntentLabel;
    @FXML private Label    levelLabel;
    @FXML private Button   cardBtn0;
    @FXML private Button   cardBtn1;
    @FXML private Button   cardBtn2;
    @FXML private TextArea consoleArea;
    @FXML private ImageView playerImage;

    // --- Servizio di gioco (unica dipendenza dalla logica) ---
    private GameService gameService;

    // Percorso immagine personaggio, passato da CreationController
    private String imagePath;

    // -------------------------------------------------------
    // Inizializzazione
    // -------------------------------------------------------

    /**
     * Chiamato da CreationController dopo la scelta della classe.
     * Inizializza GameService e avvia il primo scontro.
     */
    public void initData(String name, int forza, int vitalita, String imagePath) {
        this.imagePath   = imagePath;
        this.gameService = new GameService();

        gameService.createPlayer(name, forza, vitalita);

        playerImage.setImage(new Image(getClass().getResourceAsStream(imagePath)));

        log("Benvenuto, " + name + "! Preparati a combattere.");
        startBattle();
    }

    // -------------------------------------------------------
    // Flusso di battaglia
    // -------------------------------------------------------

    /** Avvia un nuovo scontro contro un nemico scalato sul livello corrente. */
    private void startBattle() {
        gameService.startBattle();
        log("\n=== NUOVO SCONTRO ===");
        log("Il tuo avversario è: " + gameService.getEnemy().getName());
        startPlayerTurn();
    }

    /** Prepara il turno del giocatore: aggiorna la mano e la UI. */
    private void startPlayerTurn() {
        gameService.startPlayerTurn();
        log("\n--- IL TUO TURNO ---");
        updateEnemyIntent();
        refreshCardButtons();
        updateUI();
    }

    // -------------------------------------------------------
    // Azioni giocatore (chiamate dai bottoni FXML)
    // -------------------------------------------------------

    @FXML private void onCard0Click() { playCard(0, cardBtn0); }
    @FXML private void onCard1Click() { playCard(1, cardBtn1); }
    @FXML private void onCard2Click() { playCard(2, cardBtn2); }

    private void playCard(int index, Button button) {
        if (!gameService.canPlayCard(index)) {
            log("Mana insufficiente per questa carta!");
            return;
        }
        String msg = gameService.playCard(index);
        log(msg);
        button.setDisable(true);
        updateUI();

        if (gameService.isBattleOver()) {
            handleBattleEnd();
        }
    }

    @FXML
    private void onEndTurnClick() {
        if (gameService.isBattleOver()) return;

        log("\n--- TURNO DEL NEMICO ---");
        String enemyMsg = gameService.doEnemyTurn();
        log(enemyMsg);
        updateUI();

        if (gameService.isBattleOver()) {
            handleBattleEnd();
        } else {
            startPlayerTurn();
        }
    }

    // -------------------------------------------------------
    // Gestione esito battaglia
    // -------------------------------------------------------

    private void handleBattleEnd() {
        log("\n" + gameService.getBattleResult());
        disableAllCardButtons();

        if (gameService.isPlayerVictory()) {
            // XP guadagnata: base 50 + livello nemico * 20
            int xpGained = 50 + gameService.getPlayerLevel() * 20;
            List<String> levelUpMsgs = gameService.addXpAndLevelUp(xpGained);

            log("Hai guadagnato " + xpGained + " XP!");
            for (String lvMsg : levelUpMsgs) {
                log("🌟 " + lvMsg);
            }
            updateUI();

            // TODO: aggiungere pulsante "Prossimo scontro" nella FXML
            // Per ora il giocatore può solo vedere il risultato
        }
    }

    // -------------------------------------------------------
    // Aggiornamento UI
    // -------------------------------------------------------

    /** Aggiorna tutte le label con i dati correnti di GameService. */
    private void updateUI() {
        var player = gameService.getPlayer();
        var enemy  = gameService.getEnemy();

        playerStatsLabel.setText(
            player.getName()
            + "  HP: " + player.getCurrentHp() + "/" + player.getMaxHp()
            + "  MANA: " + player.getCurrentMana() + "/" + player.getMaxHp()
        );
        playerBlockLabel.setText("Scudo: " + player.getBlock());
        enemyStatsLabel.setText(
            enemy.getName()
            + "  HP: " + enemy.getCurrentHp() + "/" + enemy.getMaxHp()
        );

        // Livello e XP (usa levelLabel se presente in FXML)
        if (levelLabel != null) {
            levelLabel.setText(
                "Lv. " + gameService.getPlayerLevel()
                + "  XP: " + gameService.getPlayerXp()
                + "/" + gameService.getXpRequired()
            );
        }
    }

    /** Mostra le carte pescate nella mano aggiornando i bottoni. */
    private void refreshCardButtons() {
        ICard[] hand    = gameService.getHand();
        Button[] buttons = {cardBtn0, cardBtn1, cardBtn2};

        for (int i = 0; i < buttons.length; i++) {
            ICard card = hand[i];
            Image cardImg = new Image(getClass().getResourceAsStream(card.getImagePath()));
            ImageView iv  = new ImageView(cardImg);
            iv.setFitWidth(200);
            iv.setFitHeight(280);
            iv.setPreserveRatio(false);
            buttons[i].setGraphic(iv);
            buttons[i].setDisable(false);
        }
    }

    /** Aggiorna la label dell'intento nemico (attacco stimato). */
    private void updateEnemyIntent() {
        if (enemyIntentLabel != null && gameService.getEnemy() != null) {
            enemyIntentLabel.setText("Intento: " + gameService.getEnemy().getName() + " si prepara ad agire.");
        }
    }

    private void disableAllCardButtons() {
        cardBtn0.setDisable(true);
        cardBtn1.setDisable(true);
        cardBtn2.setDisable(true);
    }

    /** Scrive una riga nel log visibile a schermo. */
    private void log(String msg) {
        consoleArea.appendText(msg + "\n");
    }
}
