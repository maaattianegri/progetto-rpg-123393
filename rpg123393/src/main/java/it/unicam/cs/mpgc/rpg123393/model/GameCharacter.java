package it.unicam.cs.mpgc.rpg123393.model;

public class GameCharacter {
    private String name;
    private int maxHp;
    private int currentHp;
    private int currentMana;
    private int maxMana;
    private int block;
    private int poison;

    public GameCharacter(String name, int maxHp, int maxMana) {
        this.name        = name;
        this.maxHp       = maxHp;
        this.currentHp   = maxHp;
        this.maxMana     = maxMana;
        this.currentMana = maxMana;
        this.block       = 0;
        this.poison      = 0;
    }

    // --- Danno, cura, scudo ---

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

    public void heal(int amount) {
        currentHp = Math.min(currentHp + amount, maxHp);
    }

    public void addBlock(int amount)  { this.block += amount; }
    public void resetBlock()          { this.block = 0; }

    // --- Mana ---

    public void useMana(int amount)   { this.currentMana = Math.max(0, this.currentMana - amount); }
    public void restoreMana()         { this.currentMana = this.maxMana; }

    /** Ripristina una quantità precisa di mana (senza superare il massimo). */
    public void addMana(int amount)   { this.currentMana = Math.min(this.currentMana + amount, this.maxMana); }

    // --- Veleno ---

    public void addPoison(int amount) { this.poison += amount; }

    public int applyPoison() {
        if (poison <= 0) return 0;
        int dmg = poison;
        currentHp = Math.max(0, currentHp - dmg);
        poison = Math.max(0, poison - 1);
        return dmg;
    }

    public int getPoison()            { return poison; }

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

    public void setMaxHp(int newMaxHp) {
        int bonus      = newMaxHp - this.maxHp;
        this.maxHp     = newMaxHp;
        this.currentHp = Math.min(this.currentHp + bonus, this.maxHp);
    }

    public void setMaxMana(int newMaxMana) {
        this.maxMana = newMaxMana;
    }

    // --- Setter per restore da salvataggio ---

    public void setCurrentHp(int currentHp) {
        this.currentHp = Math.max(0, Math.min(currentHp, this.maxHp));
    }

    public void setCurrentMana(int currentMana) {
        this.currentMana = Math.max(0, Math.min(currentMana, this.maxMana));
    }
}
