package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.model.ICard;
import it.unicam.cs.mpgc.rpg123393.model.MapNode;
import it.unicam.cs.mpgc.rpg123393.model.NodeType;
import it.unicam.cs.mpgc.rpg123393.service.AchievementService;
import it.unicam.cs.mpgc.rpg123393.service.GameService;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class HelloController {

    @FXML private BorderPane rootPane;

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

    @FXML private ImageView  playerImage;
    @FXML private ImageView  enemyImage;
    @FXML private StackPane  enemyViewport;
    @FXML private Label      playerNameCenterLabel;
    @FXML private Label      enemyNameCenterLabel;

    private static final double PLAYER_SIZE = 410;

    private GameService gameService;
    private String      classKey;
    private String      playerName;
    private int         vigore;
    private int         arcano;

    private int     playerHpAtBattleStart;
    private boolean enemyKilledByPoison;
    private boolean currentEnemyIsBoss;

    // -------------------------------------------------------
    // Inizializzazione
    // -------------------------------------------------------

    public void initData(String name, int vigore, int arcano, String imagePath) {
        this.playerName  = name;
        this.vigore      = vigore;
        this.arcano      = arcano;
        this.classKey    = null;
        this.gameService = new GameService();
        gameService.createPlayer(name, vigore, arcano);
        gameService.setImagePath(imagePath);
        loadPlayerBattleImage();
        log("Benvenuto, " + name + "! Preparati a combattere.");
        startBattle();
    }

    public void initData(String name, int vigore, int arcano,
                         String classKey, String className) {
        this.playerName  = name;
        this.vigore      = vigore;
        this.arcano      = arcano;
        this.classKey    = classKey;
        this.gameService = new GameService();
        gameService.createPlayer(name, vigore, arcano, className,
                ImageLoaderHelper.battleImagePath(classKey));
        loadPlayerBattleImage();
        log("Benvenuto, " + name + "! Preparati a combattere.");
        startBattle();
    }

    public void initData(String name, int vigore, int arcano,
                         String classKey, GameService existingService) {
        this.playerName  = name;
        this.vigore      = vigore;
        this.arcano      = arcano;
        this.classKey    = classKey;
        this.gameService = existingService;
        loadPlayerBattleImage();

        NodeType nodeType = existingService.getCurrentNode()
                .map(MapNode::getType)
                .orElse(NodeType.BATTLE);

        String reqClass = existingService.getCurrentNode()
                .map(MapNode::getRequiredClass)
                .orElse(null);

        boolean isVoidBranch = (reqClass != null && reqClass.contains("Cavaliere"))
                && (nodeType == NodeType.BATTLE || nodeType == NodeType.VOID || nodeType == NodeType.VOID_BOSS);

        if (nodeType == NodeType.VOID_BOSS) {
            gameService.startVoidBoss();
            log("\u25fc  UN RIFLESSO EMERGE DALL'OSCURIT\u00c0...");
            log("\ud83d\udfe3  Cavaliere Vacuo \u2014 Il tuo stesso riflesso. Senza esitazione.");
        } else if (isVoidBranch) {
            gameService.startVoidBattle();
            log("\u25fc  Il Vuoto si muove...");
            log("Una creatura dell'Abisso ti sfida.");
        } else {
            log("Bentornato, " + name + "! Continua la tua avventura.");
            gameService.startBattle();
        }

        refreshBossFlag();
        applyBattleBackground(nodeType);
        loadEnemyImage();
        startPlayerTurnUI();
    }

    // -------------------------------------------------------
    // Sfondo battaglia dinamico
    // -------------------------------------------------------

    private void applyBattleBackground(NodeType nodeType) {
        if (rootPane == null) return;
        String bgKey = switch (nodeType) {
            case BOSS, VOID_BOSS -> "boss";
            case ELITE           -> "elite";
            default              -> "battle";
        };
        ImageLoaderHelper.applyBackground(rootPane, ImageLoaderHelper.backgroundPath(bgKey));
    }

    // -------------------------------------------------------
    // Flusso battaglia
    // -------------------------------------------------------

    private void startBattle() {
        gameService.startBattle();
        playerHpAtBattleStart = gameService.getPlayer().getCurrentHp();
        enemyKilledByPoison   = false;
        refreshBossFlag();
        NodeType nodeType = gameService.getCurrentNode()
                .map(MapNode::getType)
                .orElse(NodeType.BATTLE);
        applyBattleBackground(nodeType);
        loadEnemyImage();
        log("\n=== NUOVO SCONTRO ===");
        log("Il tuo avversario e': " + gameService.getEnemy().getName());
        startPlayerTurnUI();
    }

    private void refreshBossFlag() {
        NodeType nodeType = gameService.getCurrentNode()
                .map(MapNode::getType)
                .orElse(NodeType.BATTLE);
        currentEnemyIsBoss = nodeType == NodeType.BOSS
                || nodeType == NodeType.VOID_BOSS
                || gameService.isLastBoss();
    }

    private void startPlayerTurnUI() {
        gameService.startPlayerTurn();
        String pending = gameService.getPendingMessage();
        if (pending != null && !pending.isEmpty()) {
            log(pending);
            if (gameService.isBattleOver() && gameService.isPlayerVictory()) {
                int enemyPoison = gameService.getEnemy().getPoison();
                boolean poisonMentioned = pending.toLowerCase().contains("velen");
                enemyKilledByPoison = poisonMentioned || enemyPoison >= 0;
            }
        }
        log("\n--- IL TUO TURNO ---");
        updateEnemyIntent();
        refreshCardButtons();
        updateUI();
        if (gameService.isBattleOver()) handleBattleEnd();
    }

    @FXML private void onCard0Click() { playCard(0, cardBtn0); }
    @FXML private void onCard1Click() { playCard(1, cardBtn1); }
    @FXML private void onCard2Click() { playCard(2, cardBtn2); }

    private void playCard(int index, Button button) {
        if (!gameService.canPlayCard(index)) {
            log("[!] Mana insufficiente per questa carta!");
            return;
        }
        int enemyPoisonBefore = gameService.getEnemy().getPoison();

        if (button.getGraphic() instanceof VBox cardGraphic) {
            animateCardPlay(cardGraphic, button, () -> {
                String result = gameService.playCard(index);
                log(result);
                if (gameService.isBattleOver() && gameService.isPlayerVictory() && enemyPoisonBefore > 0) {
                    enemyKilledByPoison = true;
                }
                button.setDisable(true);
                updateUI();
                if (gameService.isBattleOver()) handleBattleEnd();
            });
        } else {
            String result = gameService.playCard(index);
            log(result);
            if (gameService.isBattleOver() && gameService.isPlayerVictory() && enemyPoisonBefore > 0) {
                enemyKilledByPoison = true;
            }
            button.setDisable(true);
            updateUI();
            if (gameService.isBattleOver()) handleBattleEnd();
        }
    }

    @FXML
    private void onEndTurnClick() {
        if (gameService.isBattleOver()) return;
        log("\n--- TURNO DEL NEMICO ---");
        log(gameService.doEnemyTurn());
        updateUI();
        if (gameService.isBattleOver()) handleBattleEnd();
        else startPlayerTurnUI();
    }

    private void handleBattleEnd() {
        log("\n" + gameService.getBattleResult());
        disableAllCardButtons();
        if (gameService.isPlayerVictory()) {
            var player = gameService.getPlayer();
            boolean tookNoDamage = player.getCurrentHp() == playerHpAtBattleStart;
            boolean atFullHp     = player.getCurrentHp() == player.getMaxHp();
            boolean belowTenHp   = player.getCurrentHp() < 10;
            int currentMana      = player.getCurrentMana();

            AchievementService ach = gameService.getAchievementService();
            ach.onEnemyDefeated(
                    gameService.getEnemy().getName(),
                    tookNoDamage,
                    currentEnemyIsBoss,
                    atFullHp,
                    enemyKilledByPoison,
                    belowTenHp,
                    player.getCurrentHp(),
                    player.getMaxHp(),
                    currentMana,
                    currentMana
            );

            NodeType nodeType = gameService.getCurrentNode()
                    .map(MapNode::getType).orElse(NodeType.BATTLE);
            if (nodeType == NodeType.VOID_BOSS) {
                ach.onHollowKnightDefeated();
            }

            int xpGained = 50 + gameService.getPlayerLevel() * 20;
            List<String> msgs = gameService.addXpAndLevelUp(xpGained);
            log("Hai guadagnato " + xpGained + " XP!");
            msgs.forEach(m -> log("** " + m));
            updateUI();
            navigateToVictory(xpGained, msgs);
        } else {
            if (gameService.getEncounterIndex() <= 1) {
                gameService.getAchievementService().onDiedAtFirstNode();
            }
            navigateToGameOver();
        }
    }

    private void navigateToVictory(int xpGained, List<String> levelUpMsgs) {
        try {
            Stage stage = (Stage) consoleArea.getScene().getWindow();
            FXMLLoader loader = SceneNavigator.navigateTo(
                    stage, "/it/unicam/cs/mpgc/rpg123393/view/victory-view.fxml");
            VictoryController ctrl = loader.getController();
            ctrl.initData(gameService, xpGained, levelUpMsgs, playerName, vigore, arcano, classKey);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void navigateToGameOver() {
        try {
            Stage stage = (Stage) consoleArea.getScene().getWindow();
            FXMLLoader loader = SceneNavigator.navigateTo(
                    stage, "/it/unicam/cs/mpgc/rpg123393/view/gameover-view.fxml");
            GameOverController ctrl = loader.getController();
            ctrl.initData(gameService, playerName, vigore, arcano, classKey,
                    gameService.getClassName());
        } catch (IOException e) { e.printStackTrace(); }
    }

    // -------------------------------------------------------
    // Caricamento immagini
    // -------------------------------------------------------

    private void loadPlayerBattleImage() {
        if (classKey != null) {
            ImageLoaderHelper.load(playerImage, ImageLoaderHelper.battleImagePath(classKey));
        }
        playerImage.setFitWidth(PLAYER_SIZE);
        playerImage.setFitHeight(0);
        if (playerNameCenterLabel != null && playerName != null) {
            playerNameCenterLabel.setText(playerName);
        }
    }

    private void loadEnemyImage() {
        if (gameService == null || gameService.getEnemy() == null) return;
        String enemyName = gameService.getEnemy().getName();
        String key = toImageKey(enemyName);
        ImageLoaderHelper.load(enemyImage, ImageLoaderHelper.enemyImagePath(key));
        if (enemyNameCenterLabel != null) {
            enemyNameCenterLabel.setText(enemyName);
        }
        double fitW = enemySizeFor(key);
        enemyImage.setFitWidth(fitW);
        enemyImage.setFitHeight(0);
        if (enemyViewport != null) {
            enemyViewport.setPrefWidth(fitW + 40);
        }
    }

    private double enemySizeFor(String key) {
        return switch (key) {
            case "cuore_dell_abisso", "drago_antico", "cavaliere_vacuo" -> 660;
            case "negromante", "vampiro_lord", "re_ombra"               -> 580;
            case "custode_delle_ombre", "sentinella_abissale",
                 "sentinella_cremisi", "troll_rigenerante"               -> 520;
            default -> PLAYER_SIZE;
        };
    }

    private String toImageKey(String displayName) {
        return displayName
                .toLowerCase()
                .replace(" ", "_")
                .replace("'", "_")
                .replace("\u00e0", "a")
                .replace("\u00e8", "e")
                .replace("\u00e9", "e")
                .replace("\u00ec", "i")
                .replace("\u00f2", "o")
                .replace("\u00f9", "u");
    }

    // -------------------------------------------------------
    // Animazioni carte
    // -------------------------------------------------------

    private void attachHoverAnimation(VBox cardGraphic) {
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(150), cardGraphic);
        scaleIn.setToX(1.15); scaleIn.setToY(1.15);
        TranslateTransition moveUp = new TranslateTransition(Duration.millis(150), cardGraphic);
        moveUp.setToY(-16);
        ParallelTransition hoverIn = new ParallelTransition(scaleIn, moveUp);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(120), cardGraphic);
        scaleOut.setToX(1.0); scaleOut.setToY(1.0);
        TranslateTransition moveDown = new TranslateTransition(Duration.millis(120), cardGraphic);
        moveDown.setToY(0);
        ParallelTransition hoverOut = new ParallelTransition(scaleOut, moveDown);

        cardGraphic.setOnMouseEntered(e -> { hoverOut.stop(); cardGraphic.toFront(); hoverIn.playFromStart(); });
        cardGraphic.setOnMouseExited( e -> { hoverIn.stop();  hoverOut.playFromStart(); });
    }

    private void animateCardPlay(VBox cardGraphic, Button button, Runnable onComplete) {
        cardGraphic.setScaleX(1.0); cardGraphic.setScaleY(1.0);
        TranslateTransition fly = new TranslateTransition(Duration.millis(200), cardGraphic);
        fly.setByY(-80);
        FadeTransition fade = new FadeTransition(Duration.millis(200), cardGraphic);
        fade.setFromValue(1.0); fade.setToValue(0.0);
        ScaleTransition shrink = new ScaleTransition(Duration.millis(200), cardGraphic);
        shrink.setToX(0.85); shrink.setToY(0.85);
        ParallelTransition playAnim = new ParallelTransition(fly, fade, shrink);
        playAnim.setOnFinished(e -> {
            cardGraphic.setTranslateY(0);
            cardGraphic.setOpacity(1.0);
            cardGraphic.setScaleX(1.0); cardGraphic.setScaleY(1.0);
            onComplete.run();
        });
        playAnim.play();
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
            // FIX bug #7: reset esplicito di disabled e opacità prima di
            // ricostruire il graphic, per evitare che lo stile 'disabled'
            // di JavaFX persista sul nodo anche dopo setDisable(false).
            buttons[i].setDisable(false);
            buttons[i].setOpacity(1.0);
            VBox cardGraphic = buildCardGraphic(hand[i]);
            attachHoverAnimation(cardGraphic);
            buttons[i].setGraphic(cardGraphic);
            buttons[i].setText("");
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

        Label manaBarLabel = new Label(manaStr);
        manaBarLabel.setStyle("-fx-text-fill: #7c3aed; -fx-font-size: 10px; -fx-font-family: monospace;");

        VBox cardBox = new VBox(8, symbolLabel, nameLabel, descLabel, manaBarLabel);
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

    private void log(String msg) { consoleArea.appendText(msg + "\n"); }
}
