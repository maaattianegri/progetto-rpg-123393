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
    // Stato mappa (Fase 1 — mappa a nodi)
    // Retrocompatibile: se assente nel JSON salvato, resta null.
    // -------------------------------------------------------

    /** ID del nodo corrente nella mappa. Null se la run usa ancora il RunManager legacy. */
    private String       currentNodeId   = null;

    /** ID di tutti i nodi già completati (cleared) nella run corrente. */
    private List<String> clearedNodeIds  = new ArrayList<>();

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

    /** Aggiunge una carta alla collezione se non già presente. */
    public void unlockCard(String cardName) {
        if (!unlockedCards.contains(cardName)) unlockedCards.add(cardName);
    }
}
