package it.unicam.cs.mpgc.rpg123393.model;

public class GameCharacter {
    private String name;
    private int maxHp;
    private int currentHp;
    private int currentMana;
    private int maxMana;
    private int block;

    public GameCharacter(String name, int maxHp, int maxMana) {
        this.name        = name;
        this.maxHp       = maxHp;
        this.currentHp   = maxHp;
        this.maxMana     = maxMana;
        this.currentMana = maxMana;
        this.block       = 0;
    }

    // --- Danno e blocco ---

    public void takeDamage(int damage) {
        if (block >= damage) {
            block -= damage;
        } else {
            int remaining = damage - block;
            block = 0;
            currentHp -= remaining;
        }
        if (currentHp < 0) currentHp = 0;
    }

    public void addBlock(int amount)  { this.block += amount; }
    public void resetBlock()          { this.block = 0; }

    // --- Mana ---

    public void useMana(int amount)   { this.currentMana -= amount; }
    public void restoreMana()         { this.currentMana = this.maxMana; }

    // --- Stato ---

    public boolean isAlive()          { return currentHp > 0; }

    // --- Getter ---

    public String getName()           { return name; }
    public int getCurrentHp()         { return currentHp; }
    public int getMaxHp()             { return maxHp; }
    public int getCurrentMana()       { return currentMana; }
    public int getMaxMana()           { return maxMana; }
    public int getBlock()             { return block; }

    // --- Setter per level up ---

    /**
     * Aumenta gli HP massimi e ripristina quelli correnti della stessa quantità.
     * Usato da GameService.addXpAndLevelUp() quando il personaggio sale di livello.
     */
    public void setMaxHp(int newMaxHp) {
        int bonus      = newMaxHp - this.maxHp;
        this.maxHp     = newMaxHp;
        this.currentHp = Math.min(this.currentHp + bonus, this.maxHp);
    }

    /**
     * Aumenta il mana massimo.
     * Usato da GameService.addXpAndLevelUp() quando il personaggio sale di livello.
     */
    public void setMaxMana(int newMaxMana) {
        this.maxMana = newMaxMana;
    }

    // --- Setter per restore da salvataggio ---

    /**
     * Imposta direttamente gli HP correnti.
     * Usato SOLO da GameService.restoreFromState() per ripristinare un save.
     * Non usare durante il combattimento: usa takeDamage() invece.
     */
    public void setCurrentHp(int currentHp) {
        this.currentHp = Math.max(0, Math.min(currentHp, this.maxHp));
    }

    /**
     * Imposta direttamente il mana corrente.
     * Usato SOLO da GameService.restoreFromState() per ripristinare un save.
     * Non usare durante il combattimento: usa useMana()/restoreMana() invece.
     */
    public void setCurrentMana(int currentMana) {
        this.currentMana = Math.max(0, Math.min(currentMana, this.maxMana));
    }
}
