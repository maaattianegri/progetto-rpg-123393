package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.model.ICard;
import it.unicam.cs.mpgc.rpg123393.service.GameService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class HelloController {

    @FXML private Label       enemyNameLabel;
    @FXML private Label       enemyStatsLabel;
    @FXML private Label       enemyIntentLabel;
    @FXML private ProgressBar enemyHpBar;

    @FXML private Label       playerNameLabel;
    @FXML private Label       playerHpLabel;
    @FXML private Label       playerManaLabel;
    @FXML private Label       playerXpLabel;
    @FXML private Label       playerBlockLabel;
    @FXML private Label       levelLabel;
    @FXML private ProgressBar playerHpBar;
    @FXML private ProgressBar playerManaBar;
    @FXML private ProgressBar playerXpBar;

    @FXML private Button    cardBtn0;
    @FXML private Button    cardBtn1;
    @FXML private Button    cardBtn2;
    @FXML private TextArea  consoleArea;
    @FXML private ImageView playerImage;

    private GameService gameService;
    private String      imagePath;
    private String      playerName;
    private int         vigore;
    private int         arcano;

    public void initData(String name, int vigore, int arcano, String imagePath) {
        this.playerName  = name;
        this.vigore      = vigore;
        this.arcano      = arcano;
        this.imagePath   = imagePath;
        this.gameService = new GameService();
        gameService.createPlayer(name, vigore, arcano);
        gameService.setImagePath(imagePath);
        loadPlayerImage(imagePath);
        log("Benvenuto, " + name + "! Preparati a combattere.");
        startBattle();
    }

    public void initData(String name, int vigore, int arcano,
                         String imagePath, String className) {
        this.playerName  = name;
        this.vigore      = vigore;
        this.arcano      = arcano;
        this.imagePath   = imagePath;
        this.gameService = new GameService();
        gameService.createPlayer(name, vigore, arcano, className, imagePath);
        loadPlayerImage(imagePath);
        log("Benvenuto, " + name + "! Preparati a combattere.");
        startBattle();
    }

    public void initData(String name, int vigore, int arcano,
                         String imagePath, GameService existingService) {
        this.playerName  = name;
        this.vigore      = vigore;
        this.arcano      = arcano;
        this.imagePath   = imagePath;
        this.gameService = existingService;
        loadPlayerImage(imagePath);
        log("Bentornato, " + name + "! Continua la tua avventura.");
        startBattle();
    }

    // -------------------------------------------------------
    // Flusso battaglia
    // -------------------------------------------------------

    private void startBattle() {
        gameService.startBattle();
        log("\n=== NUOVO SCONTRO ===");
        log("Il tuo avversario e': " + gameService.getEnemy().getName());
        startPlayerTurn();
    }

    private void startPlayerTurn() {
        gameService.startPlayerTurn();
        String pending = gameService.getPendingMessage();
        if (pending != null && !pending.isEmpty()) log(pending);
        log("\n--- IL TUO TURNO ---");
        updateEnemyIntent();
        refreshCardButtons();
        updateUI();
    }

    @FXML private void onCard0Click() { playCard(0, cardBtn0); }
    @FXML private void onCard1Click() { playCard(1, cardBtn1); }
    @FXML private void onCard2Click() { playCard(2, cardBtn2); }

    private void playCard(int index, Button button) {
        if (!gameService.canPlayCard(index)) {
            log("[!] Mana insufficiente per questa carta!");
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

    private void handleBattleEnd() {
        log("\n" + gameService.getBattleResult());
        disableAllCardButtons();
        if (gameService.isPlayerVictory()) {
            int xpGained = 50 + gameService.getPlayerLevel() * 20;
            List<String> msgs = gameService.addXpAndLevelUp(xpGained);
            log("Hai guadagnato " + xpGained + " XP!");
            msgs.forEach(m -> log("** " + m));
            updateUI();
            navigateToVictory(xpGained, msgs);
        } else {
            navigateToGameOver();
        }
    }

    private void navigateToVictory(int xpGained, List<String> levelUpMsgs) {
        try {
            Stage stage = (Stage) consoleArea.getScene().getWindow();
            FXMLLoader loader = SceneNavigator.navigateTo(
                    stage, "/it/unicam/cs/mpgc/rpg123393/view/victory-view.fxml");
            VictoryController ctrl = loader.getController();
            ctrl.initData(gameService, xpGained, levelUpMsgs, playerName, vigore, arcano, imagePath);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void navigateToGameOver() {
        try {
            Stage stage = (Stage) consoleArea.getScene().getWindow();
            FXMLLoader loader = SceneNavigator.navigateTo(
                    stage, "/it/unicam/cs/mpgc/rpg123393/view/gameover-view.fxml");
            GameOverController ctrl = loader.getController();
            ctrl.initData(gameService, playerName, vigore, arcano, imagePath);
        } catch (IOException e) { e.printStackTrace(); }
    }

    // -------------------------------------------------------
    // UI
    // -------------------------------------------------------

    private void updateUI() {
        var p = gameService.getPlayer();
        var e = gameService.getEnemy();
        if (playerNameLabel != null) playerNameLabel.setText(p.getName());
        playerHpLabel.setText(p.getCurrentHp() + "/" + p.getMaxHp());
        playerManaLabel.setText(p.getCurrentMana() + "/" + p.getMaxMana());
        String blockText = "[Scudo: " + p.getBlock() + "]";
        if (p.getPoison() > 0) blockText += "  [Veleno: " + p.getPoison() + "]";
        playerBlockLabel.setText(blockText);
        playerHpBar.setProgress((double) p.getCurrentHp()    / p.getMaxHp());
        playerManaBar.setProgress((double) p.getCurrentMana() / p.getMaxMana());
        int xpReq = gameService.getXpRequired();
        int xpCur = gameService.getPlayerXp();
        playerXpLabel.setText(xpCur + "/" + xpReq);
        playerXpBar.setProgress(xpReq > 0 ? (double) xpCur / xpReq : 0);
        levelLabel.setText("Lv. " + gameService.getPlayerLevel());
        String enemyName = e.getName();
        if (e.getPoison() > 0) enemyName += "  [Veleno: " + e.getPoison() + "]";
        enemyNameLabel.setText(enemyName);
        enemyStatsLabel.setText("HP: " + e.getCurrentHp() + "/" + e.getMaxHp());
        enemyHpBar.setProgress((double) e.getCurrentHp() / e.getMaxHp());
    }

    private void refreshCardButtons() {
        ICard[]  hand    = gameService.getHand();
        Button[] buttons = {cardBtn0, cardBtn1, cardBtn2};
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setGraphic(buildCardGraphic(hand[i]));
            buttons[i].setText("");
            buttons[i].setDisable(false);
        }
    }

    private VBox buildCardGraphic(ICard card) {
        String name        = card.getName();
        String borderColor = CardStyleHelper.borderColor(name);
        String symbol      = CardStyleHelper.symbol(name);
        String desc        = CardStyleHelper.description(name);
        String manaStr     = "[" + "*".repeat(Math.min(card.getManaCost(), 6))
                           + ".".repeat(Math.max(0, 6 - card.getManaCost())) + "]";

        Label symbolLabel = new Label(symbol);
        symbolLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: " + borderColor + ";");

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;");
        nameLabel.setMaxWidth(160); nameLabel.setWrapText(true); nameLabel.setAlignment(javafx.geometry.Pos.CENTER);

        Label descLabel = new Label(desc);
        descLabel.setStyle("-fx-text-fill: #a0a0c0; -fx-font-size: 11px;");
        descLabel.setMaxWidth(160); descLabel.setWrapText(true); descLabel.setAlignment(javafx.geometry.Pos.CENTER);

        Label costLabel = new Label("Mana: " + card.getManaCost());
        costLabel.setStyle("-fx-text-fill: #a78bfa; -fx-font-size: 11px;");

        Label manaBarLabel = new Label(manaStr);
        manaBarLabel.setStyle("-fx-text-fill: #7c3aed; -fx-font-size: 10px; -fx-font-family: monospace;");

        VBox cardBox = new VBox(8, symbolLabel, nameLabel, descLabel, costLabel, manaBarLabel);
        cardBox.setAlignment(Pos.CENTER);
        cardBox.setStyle(
                "-fx-background-color: #1e1e3a;"
                + "-fx-background-radius: 10;"
                + "-fx-border-color: " + borderColor + ";"
                + "-fx-border-radius: 10;"
                + "-fx-border-width: 2;"
                + "-fx-padding: 12;"
                + "-fx-pref-width: 170;"
                + "-fx-pref-height: 240;"
                + "-fx-effect: dropshadow(gaussian, " + borderColor + ", 10, 0.3, 0, 0);"
        );
        return cardBox;
    }

    private void updateEnemyIntent() {
        if (enemyIntentLabel != null && gameService.getEnemy() != null)
            enemyIntentLabel.setText(">> " + gameService.getEnemy().getName() + " si prepara ad agire");
    }

    private void disableAllCardButtons() {
        cardBtn0.setDisable(true); cardBtn1.setDisable(true); cardBtn2.setDisable(true);
    }

    private void loadPlayerImage(String path) {
        if (path == null) return;
        InputStream stream = getClass().getResourceAsStream(path);
        if (stream == null)
            stream = getClass().getClassLoader().getResourceAsStream(path.replaceFirst("^/", ""));
        if (stream != null) playerImage.setImage(new Image(stream));
    }

    private void log(String msg) { consoleArea.appendText(msg + "\n"); }
}
