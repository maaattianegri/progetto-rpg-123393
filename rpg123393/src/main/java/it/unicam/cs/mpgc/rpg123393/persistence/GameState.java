package it.unicam.cs.mpgc.rpg123393.persistence;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO serializzabile su JSON che rappresenta lo stato completo di una partita.
 * Contiene sia i dati della run corrente che i contatori cross-run per gli achievement.
 */
public class GameState {

    // -------------------------------------------------------
    // Dati personaggio / run corrente
    // -------------------------------------------------------
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

    private List<String> unlockedCards  = new ArrayList<>();
    private List<String> deckCardNames  = new ArrayList<>();
    private String       currentNodeId;
    private List<String> clearedNodeIds = new ArrayList<>();
    private boolean      voidHeartObtained;

    // -------------------------------------------------------
    // Achievement: sblocchi e contatori cross-run
    // -------------------------------------------------------
    private List<String> unlockedAchievements = new ArrayList<>();
    private int  totalEnemiesDefeated  = 0;
    private int  totalBossesDefeated   = 0;
    private int  totalRunsCompleted    = 0;
    private int  totalGoldEarned       = 0;
    private int  totalUpgradesUsed     = 0;
    private List<String> classesCompleted = new ArrayList<>();

    // -------------------------------------------------------
    // Costruttori
    // -------------------------------------------------------

    public GameState() {}

    public GameState(String playerName, int playerMaxHp, int playerCurrentHp,
                     int playerMaxMana, int playerCurrentMana,
                     int playerLevel, int playerXp, String saveDate,
                     String className, String imagePath) {
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
    // Getter / Setter — dati run
    // -------------------------------------------------------

    public String getPlayerName()        { return playerName; }
    public void   setPlayerName(String v){ this.playerName = v; }

    public int  getPlayerMaxHp()         { return playerMaxHp; }
    public void setPlayerMaxHp(int v)    { this.playerMaxHp = v; }

    public int  getPlayerCurrentHp()     { return playerCurrentHp; }
    public void setPlayerCurrentHp(int v){ this.playerCurrentHp = v; }

    public int  getPlayerMaxMana()       { return playerMaxMana; }
    public void setPlayerMaxMana(int v)  { this.playerMaxMana = v; }

    public int  getPlayerCurrentMana()       { return playerCurrentMana; }
    public void setPlayerCurrentMana(int v)  { this.playerCurrentMana = v; }

    public int  getPlayerLevel()         { return playerLevel; }
    public void setPlayerLevel(int v)    { this.playerLevel = v; }

    public int  getPlayerXp()            { return playerXp; }
    public void setPlayerXp(int v)       { this.playerXp = v; }

    public String getSaveDate()          { return saveDate; }
    public void   setSaveDate(String v)  { this.saveDate = v; }

    public String getClassName()         { return className; }
    public void   setClassName(String v) { this.className = v; }

    public String getImagePath()         { return imagePath; }
    public void   setImagePath(String v) { this.imagePath = v; }

    public List<String> getUnlockedCards()          { return unlockedCards; }
    public void         setUnlockedCards(List<String> v) { this.unlockedCards = v; }

    public List<String> getDeckCardNames()              { return deckCardNames; }
    public void         setDeckCardNames(List<String> v){ this.deckCardNames = v; }

    public String getCurrentNodeId()             { return currentNodeId; }
    public void   setCurrentNodeId(String v)     { this.currentNodeId = v; }

    public List<String> getClearedNodeIds()              { return clearedNodeIds; }
    public void         setClearedNodeIds(List<String> v){ this.clearedNodeIds = v; }

    public boolean isVoidHeartObtained()           { return voidHeartObtained; }
    public void    setVoidHeartObtained(boolean v) { this.voidHeartObtained = v; }

    // -------------------------------------------------------
    // Getter / Setter — achievement cross-run
    // -------------------------------------------------------

    public List<String> getUnlockedAchievements()              { return unlockedAchievements; }
    public void         setUnlockedAchievements(List<String> v){ this.unlockedAchievements = v; }

    public int  getTotalEnemiesDefeated()          { return totalEnemiesDefeated; }
    public void setTotalEnemiesDefeated(int v)     { this.totalEnemiesDefeated = v; }

    public int  getTotalBossesDefeated()           { return totalBossesDefeated; }
    public void setTotalBossesDefeated(int v)      { this.totalBossesDefeated = v; }

    public int  getTotalRunsCompleted()            { return totalRunsCompleted; }
    public void setTotalRunsCompleted(int v)       { this.totalRunsCompleted = v; }

    public int  getTotalGoldEarned()               { return totalGoldEarned; }
    public void setTotalGoldEarned(int v)          { this.totalGoldEarned = v; }

    public int  getTotalUpgradesUsed()             { return totalUpgradesUsed; }
    public void setTotalUpgradesUsed(int v)        { this.totalUpgradesUsed = v; }

    public List<String> getClassesCompleted()              { return classesCompleted; }
    public void         setClassesCompleted(List<String> v){ this.classesCompleted = v; }
}
