package it.unicam.cs.mpgc.rpg123393.model;

public class GameCharacter {
    private String name;
    private int maxHp;
    private int currentHp;
    private int currentMana;
    private int maxMana;
    private int block;

    public GameCharacter(String name, int maxHp, int maxMana) {
        this.name = name;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.maxMana = maxMana;
        this.currentMana = maxMana;
        this.block = 0;
    }

    public void takeDamage(int damage) {
        if (block >= damage) {
            block -= damage;
        } else {
            int remainingDamage = damage - block;
            block = 0;
            currentHp -= remainingDamage;
        }
        if (currentHp < 0) currentHp = 0;
    }

    public void addBlock(int amount) {
        this.block += amount;
    }

    public void useMana(int amount) {
        this.currentMana -= amount;
    }

    public void resetBlock() {
        this.block = 0;
    }

    public void restoreMana() {
        this.currentMana = this.maxMana;
    }

    public boolean isAlive() { return currentHp > 0; }
    public String getName() { return name; }
    public int getCurrentHp() { return currentHp; }
    public int getMaxHp() { return maxHp; }
    public int getCurrentMana() { return currentMana; }
    public int getBlock() { return block; }
}
