package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.model.ICard;
import it.unicam.cs.mpgc.rpg123393.service.GameService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
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

    // --- Stato della sessione ---
    private GameService gameService;
    private String      imagePath;
    private String      playerName;
    private int         forza;
    private int         vitalita;

    // -------------------------------------------------------
    // Inizializzazione — due overload:
    //   1) nuova partita (CreationController): crea un GameService fresco
    //   2) continua partita (VictoryController): riusa il GameService esistente
    // -------------------------------------------------------

    /** Chiamato da CreationController: avvia una nuova partita. */
    public void initData(String name, int forza, int vitalita, String imagePath) {
        initData(name, forza, vitalita, imagePath, new GameService());
        gameService.createPlayer(name, forza, vitalita);
    }

    /** Chiamato da VictoryController: riusa il GameService con progressione intatta. */
    public void initData(String name, int forza, int vitalita, String imagePath, GameService existingService) {
        this.playerName  = name;
        this.forza       = forza;
        this.vitalita    = vitalita;
        this.imagePath   = imagePath;
        this.gameService = existingService;

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
    // Esito battaglia → naviga a Victory o Game Over
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
            navigateToVictory(xpGained, msgs);
        } else {
            navigateToGameOver();
        }
    }

    private void navigateToVictory(int xpGained, List<String> levelUpMsgs) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/it/unicam/cs/mpgc/rpg123393/view/victory-view.fxml"));
            Stage stage = (Stage) consoleArea.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 800, 600));

            VictoryController ctrl = loader.getController();
            ctrl.initData(gameService, xpGained, levelUpMsgs,
                    playerName, forza, vitalita, imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void navigateToGameOver() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/it/unicam/cs/mpgc/rpg123393/view/gameover-view.fxml"));
            Stage stage = (Stage) consoleArea.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 800, 500));

            GameOverController ctrl = loader.getController();
            ctrl.initData(gameService, playerName, forza, vitalita, imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------
    // Aggiornamento UI
    // -------------------------------------------------------

    private void updateUI() {
        var p = gameService.getPlayer();
        var e = gameService.getEnemy();

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

    private void refreshCardButtons() {
        ICard[]  hand    = gameService.getHand();
        Button[] buttons = {cardBtn0, cardBtn1, cardBtn2};

        for (int i = 0; i < buttons.length; i++) {
            ICard  card   = hand[i];
            Button button = buttons[i];

            InputStream stream = getClass().getResourceAsStream(card.getImagePath());
            if (stream != null) {
                ImageView iv = new ImageView(new Image(stream));
                iv.setFitWidth(200);
                iv.setFitHeight(280);
                iv.setPreserveRatio(false);
                button.setGraphic(iv);
                button.setText("");
            } else {
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

    private void loadPlayerImage(String path) {
        if (path == null) return;
        InputStream stream = getClass().getResourceAsStream(path);
        if (stream != null) playerImage.setImage(new Image(stream));
    }

    private void log(String msg) {
        consoleArea.appendText(msg + "\n");
    }
}
