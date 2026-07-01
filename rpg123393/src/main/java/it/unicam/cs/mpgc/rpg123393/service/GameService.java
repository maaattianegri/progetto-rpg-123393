package it.unicam.cs.mpgc.rpg123393.service;

import it.unicam.cs.mpgc.rpg123393.model.DefendCard;
import it.unicam.cs.mpgc.rpg123393.model.FireballCard;
import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;
import it.unicam.cs.mpgc.rpg123393.model.StrikeCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Coordina il flusso principale del gioco.
 * Tiene lo stato della sessione corrente (giocatore, nemico, mazzo, mano)
 * e delega le regole a BattleService, LevelService ed EnemyFactory.
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

    private List<ICard>   deck = new ArrayList<>();
    private ICard[]       hand = new ICard[3];

    // -------------------------------------------------------
    // Inizializzazione partita
    // -------------------------------------------------------

    public void createPlayer(String name, int forza, int vitalita) {
        int maxHp   = 50 + (vitalita * 10);
        int maxMana = 3  + (forza / 2);
        player = new GameCharacter(name, maxHp, maxMana);
        buildStarterDeck();
    }

    private void buildStarterDeck() {
        deck.clear();
        deck.add(new StrikeCard());
        deck.add(new StrikeCard());
        deck.add(new DefendCard());
        deck.add(new DefendCard());
        deck.add(new FireballCard());
    }

    // -------------------------------------------------------
    // Gestione battaglia
    // -------------------------------------------------------

    public void startBattle() {
        enemy = enemyFactory.createForLevel(playerLevel);
        startPlayerTurn();
    }

    public void startPlayerTurn() {
        battleService.startTurn(player);
        drawHand();
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
        battleService.startTurn(enemy);
        List<ICard> enemyCards = enemyFactory.getCardsForEnemy(enemy);
        return battleService.enemyPlayRandomCard(enemy, player, enemyCards);
    }

    public boolean isBattleOver()    { return battleService.isBattleOver(player, enemy); }
    public String getBattleResult()  { return battleService.getBattleResultMessage(player, enemy); }
    public boolean isPlayerVictory() { return battleService.isPlayerVictory(player, enemy); }

    // -------------------------------------------------------
    // Progressione (XP e level up)
    // -------------------------------------------------------

    /**
     * Aggiunge XP al giocatore e gestisce level up multipli.
     * Ad ogni salto di livello applica i bonus HP e Mana al personaggio.
     * Restituisce i messaggi da mostrare nel log.
     */
    public List<String> addXpAndLevelUp(int xpGained) {
        playerXp += xpGained;
        List<String> messages = new ArrayList<>();

        while (levelService.shouldLevelUp(playerXp, playerLevel)) {
            playerXp = levelService.consumeXpForLevelUp(playerXp, playerLevel);
            playerLevel++;

            // Applica i bonus al personaggio
            int newMaxHp   = player.getMaxHp()   + levelService.hpBonusOnLevelUp(playerLevel);
            int newMaxMana = player.getMaxMana()  + levelService.manaBonusOnLevelUp(playerLevel);
            player.setMaxHp(newMaxHp);
            player.setMaxMana(newMaxMana);

            messages.add(levelService.levelUpMessage(player.getName(), playerLevel));
        }
        return messages;
    }

    // -------------------------------------------------------
    // Getter per la UI
    // -------------------------------------------------------

    public GameCharacter getPlayer()      { return player; }
    public GameCharacter getEnemy()       { return enemy; }
    public ICard[]       getHand()        { return hand; }
    public int           getPlayerLevel() { return playerLevel; }
    public int           getPlayerXp()    { return playerXp; }
    public int           getXpRequired()  { return levelService.xpRequiredForNextLevel(playerLevel); }
}
