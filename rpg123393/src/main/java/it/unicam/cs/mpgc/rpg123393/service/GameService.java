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
 *
 * Il controller JavaFX chiama GameService e poi aggiorna la UI con i risultati.
 */
public class GameService {

    // --- Collaboratori (dipendenze) ---
    private final BattleService battleService = new BattleService();
    private final LevelService  levelService  = new LevelService();
    private final EnemyFactory  enemyFactory  = new EnemyFactory();
    private final Random        random        = new Random();

    // --- Stato della sessione ---
    private GameCharacter player;
    private GameCharacter enemy;
    private int           playerLevel = 1;
    private int           playerXp    = 0;

    private List<ICard>   deck = new ArrayList<>();
    private ICard[]       hand = new ICard[3];

    // -------------------------------------------------------
    // Inizializzazione partita
    // -------------------------------------------------------

    /**
     * Crea il personaggio del giocatore e prepara il mazzo base.
     * Chiamato da CreationController dopo la scelta della classe.
     *
     * @param name      nome scelto dal giocatore
     * @param forza     valore statistica forza (influenza mana)
     * @param vitalita  valore statistica vitalità (influenza HP)
     */
    public void createPlayer(String name, int forza, int vitalita) {
        int maxHp   = 50 + (vitalita * 10);
        int maxMana = 3  + (forza / 2);
        player = new GameCharacter(name, maxHp, maxMana);
        buildStarterDeck();
    }

    /**
     * Crea un mazzo iniziale standard con Strike, Defend e Fireball.
     */
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

    /**
     * Inizia un nuovo scontro: crea il nemico scalato sul livello corrente
     * e prepara il turno del giocatore.
     */
    public void startBattle() {
        enemy = enemyFactory.createForLevel(playerLevel);
        startPlayerTurn();
    }

    /**
     * Prepara il turno del giocatore: resetta block e ripristina mana.
     */
    public void startPlayerTurn() {
        battleService.startTurn(player);
        drawHand();
    }

    /**
     * Pesca 3 carte casuali dal mazzo e le mette nella mano.
     */
    private void drawHand() {
        for (int i = 0; i < hand.length; i++) {
            hand[i] = deck.get(random.nextInt(deck.size()));
        }
    }

    /**
     * Il giocatore gioca la carta all'indice indicato.
     * Restituisce il messaggio dell'azione (da mostrare nel log).
     */
    public String playCard(int handIndex) {
        if (handIndex < 0 || handIndex >= hand.length || hand[handIndex] == null) {
            return "Carta non valida.";
        }
        return battleService.playCard(hand[handIndex], player, enemy);
    }

    /**
     * Verifica se una carta in mano è giocabile (mana sufficiente).
     */
    public boolean canPlayCard(int handIndex) {
        if (handIndex < 0 || handIndex >= hand.length || hand[handIndex] == null) return false;
        return battleService.canPlayCard(hand[handIndex], player);
    }

    /**
     * Esegue il turno del nemico.
     * Restituisce il messaggio dell'attacco nemico.
     */
    public String doEnemyTurn() {
        battleService.startTurn(enemy);
        List<ICard> enemyCards = enemyFactory.getCardsForEnemy(enemy);
        return battleService.enemyPlayRandomCard(enemy, player, enemyCards);
    }

    /**
     * Controlla se la battaglia è terminata.
     */
    public boolean isBattleOver() {
        return battleService.isBattleOver(player, enemy);
    }

    /**
     * Restituisce il messaggio di esito finale della battaglia.
     */
    public String getBattleResult() {
        return battleService.getBattleResultMessage(player, enemy);
    }

    /**
     * Restituisce true se il giocatore ha vinto.
     */
    public boolean isPlayerVictory() {
        return battleService.isPlayerVictory(player, enemy);
    }

    // -------------------------------------------------------
    // Progressione (XP e level up)
    // -------------------------------------------------------

    /**
     * Aggiunge XP al giocatore dopo una vittoria e gestisce eventuali level up.
     * Restituisce una lista di messaggi da mostrare nel log (vuota se nessun level up).
     *
     * @param xpGained XP guadagnata (es. dal nemico sconfitto)
     */
    public List<String> addXpAndLevelUp(int xpGained) {
        playerXp += xpGained;
        List<String> messages = new ArrayList<>();
        while (levelService.shouldLevelUp(playerXp, playerLevel)) {
            playerXp = levelService.consumeXpForLevelUp(playerXp, playerLevel);
            playerLevel++;
            // TODO: quando Player avrà setMaxHp/setMaxMana, applicare i bonus qui
            messages.add(levelService.levelUpMessage(player.getName(), playerLevel));
        }
        return messages;
    }

    // -------------------------------------------------------
    // Getter per la UI
    // -------------------------------------------------------

    public GameCharacter getPlayer()  { return player; }
    public GameCharacter getEnemy()   { return enemy; }
    public ICard[]       getHand()    { return hand; }
    public int           getPlayerLevel() { return playerLevel; }
    public int           getPlayerXp()    { return playerXp; }
    public int           getXpRequired()  { return levelService.xpRequiredForNextLevel(playerLevel); }
}
