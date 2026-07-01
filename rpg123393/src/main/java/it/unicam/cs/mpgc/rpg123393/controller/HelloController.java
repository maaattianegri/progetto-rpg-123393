package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.model.ICard;
import it.unicam.cs.mpgc.rpg123393.service.GameService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.InputStream;
import java.util.List;

/**
 * Controller della schermata di battaglia.
 * Responsabilità: aggiornare la UI in base ai risultati restituiti da GameService.
 * NON contiene logica di gioco: ogni decisione è delegata a GameService.
 */
public class HelloController {

    // --- Elementi FXML ---
    @FXML private Label     playerStatsLabel;
    @FXML private Label     enemyStatsLabel;
    @FXML private Label     playerBlockLabel;
    @FXML private Label     enemyIntentLabel;
    @FXML private Label     levelLabel;
    @FXML private Button    cardBtn0;
    @FXML private Button    cardBtn1;
    @FXML private Button    cardBtn2;
    @FXML private TextArea  consoleArea;
    @FXML private ImageView playerImage;

    // --- Servizio di gioco ---
    private GameService gameService;
    private String      imagePath;

    // -------------------------------------------------------
    // Inizializzazione
    // -------------------------------------------------------

    public void initData(String name, int forza, int vitalita, String imagePath) {
        this.imagePath   = imagePath;
        this.gameService = new GameService();
        gameService.createPlayer(name, forza, vitalita);

        loadPlayerImage(imagePath);
        log("Benvenuto, " + name + "! Preparati a combattere.");
        startBattle();
    }

    // -------------------------------------------------------
    // Flusso di battaglia
    // -------------------------------------------------------

    private void startBattle() {
        gameService.startBattle();
        log("\n=== NUOVO SCONTRO ===");
        log("Il tuo avversario è: " + gameService.getEnemy().getName());
        startPlayerTurn();
    }

    private void startPlayerTurn() {
        gameService.startPlayerTurn();
        log("\n--- IL TUO TURNO ---");
        updateEnemyIntent();
        refreshCardButtons();
        updateUI();
    }

    // -------------------------------------------------------
    // Azioni giocatore
    // -------------------------------------------------------

    @FXML private void onCard0Click() { playCard(0, cardBtn0); }
    @FXML private void onCard1Click() { playCard(1, cardBtn1); }
    @FXML private void onCard2Click() { playCard(2, cardBtn2); }

    private void playCard(int index, Button button) {
        if (!gameService.canPlayCard(index)) {
            log("Mana insufficiente per questa carta!");
            return;
        }
        log(gameService.playCard(index));
        button.setDisable(true);
        updateUI();
        if (gameService.isBattleOver()) handleBattleEnd();
    }

    @FXML
    private void onEndTurnClick() {
        if (gameService.isBattleOver()) return;
        log("\n--- TURNO DEL NEMICO ---");
        log(gameService.doEnemyTurn());
        updateUI();
        if (gameService.isBattleOver()) handleBattleEnd();
        else startPlayerTurn();
    }

    // -------------------------------------------------------
    // Esito battaglia
    // -------------------------------------------------------

    private void handleBattleEnd() {
        log("\n" + gameService.getBattleResult());
        disableAllCardButtons();

        if (gameService.isPlayerVictory()) {
            int xpGained = 50 + gameService.getPlayerLevel() * 20;
            List<String> msgs = gameService.addXpAndLevelUp(xpGained);
            log("Hai guadagnato " + xpGained + " XP!");
            msgs.forEach(m -> log("⭐ " + m));
            updateUI();
            // Mostra il bottone "Prossimo Scontro"
            showNextBattleButton();
        }
    }

    /**
     * Crea dinamicamente un bottone "Prossimo Scontro" e lo aggiunge
     * al pannello centrale (workaround finché non è in FXML).
     * Fix 4 lo sposterà nella FXML in modo permanente.
     */
    private void showNextBattleButton() {
        // cardBtn2 viene riusato come "Prossimo Scontro" finché non abbiamo la FXML definitiva
        cardBtn2.setDisable(false);
        cardBtn2.setGraphic(null);
        cardBtn2.setText("⚔️ Prossimo Scontro");
        cardBtn2.setStyle("-fx-background-color: #4ecca3; -fx-text-fill: #1a1a2e; "
                        + "-fx-font-size: 14px; -fx-font-weight: bold; "
                        + "-fx-background-radius: 10; -fx-cursor: hand;");
        // Sovrascrivi l'handler: al click lancia un nuovo scontro
        cardBtn2.setOnAction(e -> {
            cardBtn2.setText("Carta 3");
            cardBtn2.setStyle("-fx-background-color: #2a2a4a; -fx-text-fill: white; "
                            + "-fx-background-radius: 10; -fx-cursor: hand;");
            cardBtn2.setOnAction(ev -> onCard2Click());
            startBattle();
        });
    }

    // -------------------------------------------------------
    // Aggiornamento UI
    // -------------------------------------------------------

    private void updateUI() {
        var p = gameService.getPlayer();
        var e = gameService.getEnemy();

        // FIX 3: getMaxMana() al posto di getMaxHp() per il mana
        playerStatsLabel.setText(
            p.getName()
            + "  HP: " + p.getCurrentHp() + "/" + p.getMaxHp()
            + "  MANA: " + p.getCurrentMana() + "/" + p.getMaxMana()
        );
        playerBlockLabel.setText("Scudo: " + p.getBlock());
        enemyStatsLabel.setText(
            e.getName() + "  HP: " + e.getCurrentHp() + "/" + e.getMaxHp()
        );
        if (levelLabel != null) {
            levelLabel.setText(
                "Lv. " + gameService.getPlayerLevel()
                + "  XP: " + gameService.getPlayerXp()
                + "/" + gameService.getXpRequired()
            );
        }
    }

    /**
     * Aggiorna i bottoni delle carte con le immagini della mano corrente.
     * Null-safe: se l'immagine non si carica mostra un fallback testuale
     * invece di crashare.
     */
    private void refreshCardButtons() {
        ICard[]  hand    = gameService.getHand();
        Button[] buttons = {cardBtn0, cardBtn1, cardBtn2};

        for (int i = 0; i < buttons.length; i++) {
            ICard  card   = hand[i];
            Button button = buttons[i];

            InputStream stream = getClass().getResourceAsStream(card.getImagePath());
            if (stream != null) {
                // Immagine trovata: mostrala normalmente
                ImageView iv = new ImageView(new Image(stream));
                iv.setFitWidth(200);
                iv.setFitHeight(280);
                iv.setPreserveRatio(false);
                button.setGraphic(iv);
                button.setText("");
            } else {
                // Immagine mancante: fallback testuale, nessun crash
                button.setGraphic(null);
                button.setText(card.getName() + "\n(" + card.getManaCost() + " mana)");
            }
            button.setDisable(false);
        }
    }

    private void updateEnemyIntent() {
        if (enemyIntentLabel != null && gameService.getEnemy() != null) {
            enemyIntentLabel.setText(
                "Intento: " + gameService.getEnemy().getName() + " si prepara ad agire."
            );
        }
    }

    private void disableAllCardButtons() {
        cardBtn0.setDisable(true);
        cardBtn1.setDisable(true);
        cardBtn2.setDisable(true);
    }

    /**
     * Carica l'immagine del personaggio in modo null-safe.
     * Se il path non esiste non crasha, lascia l'ImageView vuota.
     */
    private void loadPlayerImage(String path) {
        if (path == null) return;
        InputStream stream = getClass().getResourceAsStream(path);
        if (stream != null) playerImage.setImage(new Image(stream));
    }

    private void log(String msg) {
        consoleArea.appendText(msg + "\n");
    }
}
