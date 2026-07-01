package it.unicam.cs.mpgc.rpg123393.model;

public class FireballCard implements ICard {
    @Override
    public String getName() { return "Palla di Fuoco"; }

    @Override
    public int getManaCost() { return 2; } // Costa più mana!

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            target.takeDamage(14); // Fa un sacco di danno
            System.out.println(user.getName() + " lancia una Palla di Fuoco!");
        }
    }
    @Override
    public String getImagePath() { return "/images/fireball.jpg"; }
}