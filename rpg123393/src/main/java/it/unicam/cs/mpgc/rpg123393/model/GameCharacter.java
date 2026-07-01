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

    /** Recupera HP senza superare il massimo. Usato da Regen e carte curative. */
    public void heal(int amount) {
        currentHp = Math.min(currentHp + amount, maxHp);
    }

    public void addBlock(int amount)  { this.block += amount; }
    public void resetBlock()          { this.block = 0; }
    public void useMana(int amount)   { this.currentMana -= amount; }
    public void restoreMana()         { this.currentMana = this.maxMana; }
    public boolean isAlive()          { return currentHp > 0; }

    public String getName()           { return name; }
    public int getCurrentHp()         { return currentHp; }
    public int getMaxHp()             { return maxHp; }
    public int getCurrentMana()       { return currentMana; }
    public int getMaxMana()           { return maxMana; }
    public int getBlock()             { return block; }

    public void setMaxHp(int newMaxHp) {
        int bonus      = newMaxHp - this.maxHp;
        this.maxHp     = newMaxHp;
        this.currentHp = Math.min(this.currentHp + bonus, this.maxHp);
    }

    public void setMaxMana(int newMaxMana) {
        this.maxMana = newMaxMana;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = Math.max(0, Math.min(currentHp, this.maxHp));
    }

    public void setCurrentMana(int currentMana) {
        this.currentMana = Math.max(0, Math.min(currentMana, this.maxMana));
    }
}
