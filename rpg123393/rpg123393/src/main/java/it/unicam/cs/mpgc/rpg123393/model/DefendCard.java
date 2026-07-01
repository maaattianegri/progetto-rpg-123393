package it.unicam.cs.mpgc.rpg123393.model;

public class DefendCard implements ICard {
    @Override
    public String getName() { return "Scudo di Legno"; }

    @Override
    public int getManaCost() { return 1; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            user.addBlock(6); // Aggiunge 6 punti armatura
            System.out.println(user.getName() + " ottiene 6 di Armatura!");
        }
    }
    @Override
    public String getImagePath() { return "/images/defend.jpg"; }
}