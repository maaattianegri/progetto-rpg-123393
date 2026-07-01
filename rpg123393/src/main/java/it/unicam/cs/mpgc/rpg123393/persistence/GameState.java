package it.unicam.cs.mpgc.rpg123393.persistence;

/**
 * Rappresenta lo stato serializzabile di una partita.
 * Contiene solo tipi primitivi e String per facilitare
 * la serializzazione JSON senza dipendenze esterne.
 *
 * Viene popolato da GameService prima del salvataggio
 * e riletto da GameService dopo il caricamento.
 */
public class GameState {

    // --- Dati del personaggio ---
    private String playerName;
    private int    playerMaxHp;
    private int    playerCurrentHp;
    private int    playerMaxMana;
    private int    playerCurrentMana;

    // --- Progressione ---
    private int    playerLevel;
    private int    playerXp;

    // --- Metadati partita ---
    private String saveDate;   // es. "2026-07-01T19:00:00"
    private String className;  // es. "Guerriero", "Mago", "Dracomante"
    private String imagePath;  // percorso immagine personaggio

    // Costruttore vuoto necessario per la deserializzazione JSON
    public GameState() {}

    public GameState(String playerName, int playerMaxHp, int playerCurrentHp,
                     int playerMaxMana, int playerCurrentMana,
                     int playerLevel, int playerXp,
                     String saveDate, String className, String imagePath) {
        this.playerName       = playerName;
        this.playerMaxHp      = playerMaxHp;
        this.playerCurrentHp  = playerCurrentHp;
        this.playerMaxMana    = playerMaxMana;
        this.playerCurrentMana = playerCurrentMana;
        this.playerLevel      = playerLevel;
        this.playerXp         = playerXp;
        this.saveDate         = saveDate;
        this.className        = className;
        this.imagePath        = imagePath;
    }

    // --- Getter e Setter ---

    public String getPlayerName()        { return playerName; }
    public void   setPlayerName(String v)  { this.playerName = v; }

    public int  getPlayerMaxHp()         { return playerMaxHp; }
    public void setPlayerMaxHp(int v)      { this.playerMaxHp = v; }

    public int  getPlayerCurrentHp()     { return playerCurrentHp; }
    public void setPlayerCurrentHp(int v)  { this.playerCurrentHp = v; }

    public int  getPlayerMaxMana()       { return playerMaxMana; }
    public void setPlayerMaxMana(int v)    { this.playerMaxMana = v; }

    public int  getPlayerCurrentMana()   { return playerCurrentMana; }
    public void setPlayerCurrentMana(int v){ this.playerCurrentMana = v; }

    public int  getPlayerLevel()         { return playerLevel; }
    public void setPlayerLevel(int v)      { this.playerLevel = v; }

    public int  getPlayerXp()            { return playerXp; }
    public void setPlayerXp(int v)         { this.playerXp = v; }

    public String getSaveDate()          { return saveDate; }
    public void   setSaveDate(String v)    { this.saveDate = v; }

    public String getClassName()         { return className; }
    public void   setClassName(String v)   { this.className = v; }

    public String getImagePath()         { return imagePath; }
    public void   setImagePath(String v)   { this.imagePath = v; }
}
