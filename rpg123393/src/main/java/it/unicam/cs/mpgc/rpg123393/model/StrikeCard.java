package it.unicam.cs.mpgc.rpg123393.model;

public class StrikeCard implements ICard {
    @Override
    public String getName() { return "Colpo di Spada"; }

    @Override
    public int getManaCost() { return 1; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            target.takeDamage(6);
        }
    }

    @Override
    public String getImagePath() { return "/images/strike.svg"; }
}
