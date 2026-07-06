package it.unicam.cs.mpgc.rpg123393.persistence;

import java.util.ArrayList;
import java.util.List;

public class GameState {

    private String playerName;
    private int    playerMaxHp;
    private int    playerCurrentHp;
    private int    playerMaxMana;
    private int    playerCurrentMana;
    private int    playerLevel;
    private int    playerXp;
    private String saveDate;
    private String className;
    private String imagePath;

    /** Carte sbloccate nella collezione (meta-progressione, non si resettano). */
    private List<String> unlockedCards = new ArrayList<>();

    /**
     * Deck della run corrente (carte nel mazzo durante la partita).
     * Distinto da unlockedCards: queste sono le carte effettivamente usate in battaglia,
     * incluse quelle acquistate allo shop o scelte come ricompensa.
     */
    private List<String> deckCardNames = new ArrayList<>();

    // -------------------------------------------------------
    // Stato mappa
    // -------------------------------------------------------

    /** ID del nodo corrente nella mappa. Null se la run usa ancora il RunManager legacy. */
    private String       currentNodeId   = null;

    /** ID di tutti i nodi già completati (cleared) nella run corrente. */
    private List<String> clearedNodeIds  = new ArrayList<>();

    /**
     * Flag easter egg Hollow Knight.
     * True se il Cavaliere ha ottenuto il Cuore di Vuoto visitando il nodo VOID.
     */
    private boolean voidHeartObtained = false;

    // -------------------------------------------------------
    // Achievement (meta-progressione, non si resettano mai)
    // -------------------------------------------------------

    /** ID degli achievement già sbloccati. */
    private List<String> unlockedAchievements = new ArrayList<>();

    // --- Contatori globali tra tutte le run ---
    private int totalRunsStarted    = 0;
    private int totalRunsCompleted  = 0;
    private int totalEnemiesKilled  = 0;
    private int totalBossesKilled   = 0;
    private int totalGoldEarned     = 0;
    private int totalForgeUses      = 0;

    // --- Flag run corrente (si resettano a inizio run) ---
    /** Oro speso nello shop nella run corrente. */
    private int  runShopCardsBought  = 0;
    /** Oro speso per reliquie nella run corrente (usato per "Solo Reliquie"). */
    private int  runShopRelicsBought = 0;
    /** Volte che la fucina è stata usata nella run corrente. */
    private int  runForgeUses        = 0;
    /** Nodi visitati nella run corrente (per speed run). */
    private int  runNodesVisited     = 0;

    public GameState() {}

    public GameState(String playerName, int playerMaxHp, int playerCurrentHp,
                     int playerMaxMana, int playerCurrentMana,
                     int playerLevel, int playerXp,
                     String saveDate, String className, String imagePath) {
        this.playerName        = playerName;
        this.playerMaxHp       = playerMaxHp;
        this.playerCurrentHp   = playerCurrentHp;
        this.playerMaxMana     = playerMaxMana;
        this.playerCurrentMana = playerCurrentMana;
        this.playerLevel       = playerLevel;
        this.playerXp          = playerXp;
        this.saveDate          = saveDate;
        this.className         = className;
        this.imagePath         = imagePath;
    }

    // -------------------------------------------------------
    // Getter / Setter base
    // -------------------------------------------------------
    public String getPlayerName()          { return playerName; }
    public void   setPlayerName(String v)  { this.playerName = v; }
    public int    getPlayerMaxHp()         { return playerMaxHp; }
    public void   setPlayerMaxHp(int v)    { this.playerMaxHp = v; }
    public int    getPlayerCurrentHp()     { return playerCurrentHp; }
    public void   setPlayerCurrentHp(int v){ this.playerCurrentHp = v; }
    public int    getPlayerMaxMana()       { return playerMaxMana; }
    public void   setPlayerMaxMana(int v)  { this.playerMaxMana = v; }
    public int    getPlayerCurrentMana()   { return playerCurrentMana; }
    public void   setPlayerCurrentMana(int v){ this.playerCurrentMana = v; }
    public int    getPlayerLevel()         { return playerLevel; }
    public void   setPlayerLevel(int v)    { this.playerLevel = v; }
    public int    getPlayerXp()            { return playerXp; }
    public void   setPlayerXp(int v)       { this.playerXp = v; }
    public String getSaveDate()            { return saveDate; }
    public void   setSaveDate(String v)    { this.saveDate = v; }
    public String getClassName()           { return className; }
    public void   setClassName(String v)   { this.className = v; }
    public String getImagePath()           { return imagePath; }
    public void   setImagePath(String v)   { this.imagePath = v; }

    public List<String> getUnlockedCards()               { return unlockedCards; }
    public void         setUnlockedCards(List<String> v)  { this.unlockedCards = v != null ? v : new ArrayList<>(); }

    public List<String> getDeckCardNames()               { return deckCardNames; }
    public void         setDeckCardNames(List<String> v)  { this.deckCardNames = v != null ? v : new ArrayList<>(); }

    // Mappa
    public String       getCurrentNodeId()                { return currentNodeId; }
    public void         setCurrentNodeId(String v)        { this.currentNodeId = v; }
    public List<String> getClearedNodeIds()               { return clearedNodeIds; }
    public void         setClearedNodeIds(List<String> v) { this.clearedNodeIds = v != null ? v : new ArrayList<>(); }

    // Easter egg Hollow Knight
    public boolean isVoidHeartObtained()           { return voidHeartObtained; }
    public void    setVoidHeartObtained(boolean v) { this.voidHeartObtained = v; }

    // -------------------------------------------------------
    // Achievement
    // -------------------------------------------------------
    public List<String> getUnlockedAchievements()               { return unlockedAchievements; }
    public void         setUnlockedAchievements(List<String> v) { this.unlockedAchievements = v != null ? v : new ArrayList<>(); }

    public void unlockAchievement(String id) {
        if (!unlockedAchievements.contains(id)) unlockedAchievements.add(id);
    }
    public boolean isAchievementUnlocked(String id) { return unlockedAchievements.contains(id); }

    // Contatori globali
    public int  getTotalRunsStarted()          { return totalRunsStarted; }
    public void setTotalRunsStarted(int v)     { this.totalRunsStarted = v; }
    public void incrementRunsStarted()         { this.totalRunsStarted++; }

    public int  getTotalRunsCompleted()        { return totalRunsCompleted; }
    public void setTotalRunsCompleted(int v)   { this.totalRunsCompleted = v; }
    public void incrementRunsCompleted()       { this.totalRunsCompleted++; }

    public int  getTotalEnemiesKilled()        { return totalEnemiesKilled; }
    public void setTotalEnemiesKilled(int v)   { this.totalEnemiesKilled = v; }
    public void incrementEnemiesKilled()       { this.totalEnemiesKilled++; }

    public int  getTotalBossesKilled()         { return totalBossesKilled; }
    public void setTotalBossesKilled(int v)    { this.totalBossesKilled = v; }
    public void incrementBossesKilled()        { this.totalBossesKilled++; }

    public int  getTotalGoldEarned()           { return totalGoldEarned; }
    public void setTotalGoldEarned(int v)      { this.totalGoldEarned = v; }
    public void addGoldEarned(int amount)      { this.totalGoldEarned += amount; }

    public int  getTotalForgeUses()            { return totalForgeUses; }
    public void setTotalForgeUses(int v)       { this.totalForgeUses = v; }
    public void incrementForgeUses()           { this.totalForgeUses++; }

    // Flag run corrente
    public int  getRunShopCardsBought()        { return runShopCardsBought; }
    public void setRunShopCardsBought(int v)   { this.runShopCardsBought = v; }
    public void incrementRunShopCardsBought()  { this.runShopCardsBought++; }

    public int  getRunShopRelicsBought()       { return runShopRelicsBought; }
    public void setRunShopRelicsBought(int v)  { this.runShopRelicsBought = v; }
    public void incrementRunShopRelicsBought() { this.runShopRelicsBought++; }

    public int  getRunForgeUses()              { return runForgeUses; }
    public void setRunForgeUses(int v)         { this.runForgeUses = v; }
    public void incrementRunForgeUses()        { this.runForgeUses++; }

    public int  getRunNodesVisited()           { return runNodesVisited; }
    public void setRunNodesVisited(int v)      { this.runNodesVisited = v; }
    public void incrementRunNodesVisited()     { this.runNodesVisited++; }

    /**
     * Resetta tutti i flag/contatori legati alla singola run.
     * Da chiamare all'inizio di ogni nuova run.
     */
    public void resetRunFlags() {
        runShopCardsBought  = 0;
        runShopRelicsBought = 0;
        runForgeUses        = 0;
        runNodesVisited     = 0;
    }

    /** Aggiunge una carta alla collezione se non già presente. */
    public void unlockCard(String cardName) {
        if (!unlockedCards.contains(cardName)) unlockedCards.add(cardName);
    }
}
