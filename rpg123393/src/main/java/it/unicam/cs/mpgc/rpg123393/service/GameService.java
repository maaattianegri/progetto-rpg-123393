package it.unicam.cs.mpgc.rpg123393.service;

import it.unicam.cs.mpgc.rpg123393.model.DefendCard;
import it.unicam.cs.mpgc.rpg123393.model.FireballCard;
import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;
import it.unicam.cs.mpgc.rpg123393.model.StrikeCard;
import it.unicam.cs.mpgc.rpg123393.model.player.ArcaneStormCard;
import it.unicam.cs.mpgc.rpg123393.model.player.DevastatingStrikeCard;
import it.unicam.cs.mpgc.rpg123393.model.player.DragonClawCard;
import it.unicam.cs.mpgc.rpg123393.model.player.HolyShieldCard;
import it.unicam.cs.mpgc.rpg123393.model.player.PoisonBladeCard;
import it.unicam.cs.mpgc.rpg123393.persistence.GameState;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Coordina il flusso principale del gioco.
 */
public class GameService {

    private final BattleService battleService = new BattleService();
    private final LevelService  levelService  = new LevelService();
    private final EnemyFactory  enemyFactory  = new EnemyFactory();
    private final Random        random        = new Random();

    private GameCharacter player;
    private GameCharacter enemy;
    private int           playerLevel = 1;
    private int           playerXp    = 0;

    private int    vigore;
    private int    arcano;
    private String className;
    private String imagePath;

    private List<ICard> deck = new ArrayList<>();
    private ICard[]     hand = new ICard[3];

    // -------------------------------------------------------
    // Inizializzazione
    // -------------------------------------------------------

    public void createPlayer(String name, int vigore, int arcano, String className, String imagePath) {
        this.vigore    = vigore;
        this.arcano    = arcano;
        this.className = className;
        this.imagePath = imagePath;

        int maxHp   = 50 + (vigore * 10);
        int maxMana = 3  + (arcano / 2);
        player = new GameCharacter(name, maxHp, maxMana);
        buildStarterDeck(className);
    }

    public void createPlayer(String name, int vigore, int arcano) {
        createPlayer(name, vigore, arcano, null, null);
    }

    /**
     * Costruisce il mazzo iniziale in base alla classe scelta.
     * Ogni classe ha 2 carte base comuni + 2 carte esclusive che definiscono lo stile.
     */
    private void buildStarterDeck(String className) {
        deck.clear();
        if (className == null) className = "";
        switch (className) {
            case "Guerriero" -> {
                deck.add(new StrikeCard());           // 6 danni, 1 mana
                deck.add(new StrikeCard());
                deck.add(new DefendCard());           // 6 scudo, 1 mana
                deck.add(new DevastatingStrikeCard()); // 12 danni, 2 mana
                deck.add(new DevastatingStrikeCard());
            }
            case "Mago" -> {
                deck.add(new FireballCard());         // 8 danni, 2 mana
                deck.add(new FireballCard());
                deck.add(new DefendCard());
                deck.add(new ArcaneStormCard());      // 10+2*veleno danni, 3 mana
                deck.add(new ArcaneStormCard());
            }
            case "Dracomante" -> {
                deck.add(new StrikeCard());
                deck.add(new FireballCard());
                deck.add(new DefendCard());
                deck.add(new DragonClawCard());       // 6 danni + 4 scudo, 2 mana
                deck.add(new DragonClawCard());
            }
            case "Paladino" -> {
                deck.add(new StrikeCard());
                deck.add(new DefendCard());           // 6 scudo, 1 mana
                deck.add(new DefendCard());
                deck.add(new HolyShieldCard());       // 12 scudo + 4 HP, 2 mana
                deck.add(new HolyShieldCard());
            }
            case "Assassino" -> {
                deck.add(new StrikeCard());
                deck.add(new PoisonBladeCard());      // 3 danni + 3 stack veleno, 1 mana
                deck.add(new PoisonBladeCard());
                deck.add(new PoisonBladeCard());
                deck.add(new DefendCard());
            }
            default -> {
                // Fallback generico (save vecchi senza className)
                deck.add(new StrikeCard());
                deck.add(new StrikeCard());
                deck.add(new DefendCard());
                deck.add(new DefendCard());
                deck.add(new FireballCard());
            }
        }
    }

    // -------------------------------------------------------
    // Gestione battaglia
    // -------------------------------------------------------

    public void startBattle() {
        enemy = enemyFactory.createForLevel(playerLevel);
        startPlayerTurn();
    }

    public void startPlayerTurn() {
        String poisonMsg = battleService.startTurn(player);
        drawHand();
        // Il messaggio veleno viene gestito da HelloController tramite getPendingMessage()
        this.pendingMessage = poisonMsg;
    }

    private String pendingMessage = "";

    /** Messaggio generato durante startPlayerTurn (es. danno veleno). */
    public String getPendingMessage() {
        String msg = pendingMessage;
        pendingMessage = "";
        return msg;
    }

    private void drawHand() {
        for (int i = 0; i < hand.length; i++) {
            hand[i] = deck.get(random.nextInt(deck.size()));
        }
    }

    public String playCard(int handIndex) {
        if (handIndex < 0 || handIndex >= hand.length || hand[handIndex] == null) {
            return "Carta non valida.";
        }
        return battleService.playCard(hand[handIndex], player, enemy);
    }

    public boolean canPlayCard(int handIndex) {
        if (handIndex < 0 || handIndex >= hand.length || hand[handIndex] == null) return false;
        return battleService.canPlayCard(hand[handIndex], player);
    }

    public String doEnemyTurn() {
        String poisonMsg = battleService.startTurn(enemy);
        List<ICard> enemyCards = enemyFactory.getCardsForEnemy(enemy);
        String actionMsg = battleService.enemyPlayRandomCard(enemy, player, enemyCards);
        return poisonMsg.isEmpty() ? actionMsg : poisonMsg + "\n" + actionMsg;
    }

    public boolean isBattleOver()    { return battleService.isBattleOver(player, enemy); }
    public String getBattleResult()  { return battleService.getBattleResultMessage(player, enemy); }
    public boolean isPlayerVictory() { return battleService.isPlayerVictory(player, enemy); }

    // -------------------------------------------------------
    // Progressione
    // -------------------------------------------------------

    public List<String> addXpAndLevelUp(int xpGained) {
        playerXp += xpGained;
        List<String> messages = new ArrayList<>();
        while (levelService.shouldLevelUp(playerXp, playerLevel)) {
            playerXp = levelService.consumeXpForLevelUp(playerXp, playerLevel);
            playerLevel++;
            int newMaxHp   = player.getMaxHp()  + levelService.hpBonusOnLevelUp(playerLevel);
            int newMaxMana = player.getMaxMana() + levelService.manaBonusOnLevelUp(playerLevel);
            player.setMaxHp(newMaxHp);
            player.setMaxMana(newMaxMana);
            messages.add(levelService.levelUpMessage(player.getName(), playerLevel));
        }
        return messages;
    }

    // -------------------------------------------------------
    // Persistenza
    // -------------------------------------------------------

    public GameState toGameState() {
        String now = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        return new GameState(
                player.getName(), player.getMaxHp(), player.getCurrentHp(),
                player.getMaxMana(), player.getCurrentMana(),
                playerLevel, playerXp, now,
                className  != null ? className  : "",
                imagePath  != null ? imagePath  : ""
        );
    }

    public void restoreFromState(GameState state) {
        this.vigore    = (state.getPlayerMaxHp()  - 50) / 10;
        this.arcano    = (state.getPlayerMaxMana() - 3)  * 2;
        this.className = state.getClassName();
        this.imagePath = state.getImagePath();
        this.playerLevel = state.getPlayerLevel();
        this.playerXp    = state.getPlayerXp();
        player = new GameCharacter(
                state.getPlayerName(), state.getPlayerMaxHp(), state.getPlayerMaxMana());
        player.setCurrentHp(state.getPlayerCurrentHp());
        player.setCurrentMana(state.getPlayerCurrentMana());
        buildStarterDeck(this.className);
    }

    // -------------------------------------------------------
    // Getter
    // -------------------------------------------------------

    public GameCharacter getPlayer()      { return player; }
    public GameCharacter getEnemy()       { return enemy; }
    public ICard[]       getHand()        { return hand; }
    public int           getPlayerLevel() { return playerLevel; }
    public int           getPlayerXp()    { return playerXp; }
    public int           getXpRequired()  { return levelService.xpRequiredForNextLevel(playerLevel); }
    public int           getVigore()      { return vigore; }
    public int           getArcano()      { return arcano; }
    public String        getClassName()   { return className; }
    public String        getImagePath()   { return imagePath; }
    public void setClassName(String className)  { this.className = className; }
    public void setImagePath(String imagePath)  { this.imagePath = imagePath; }
}
