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
            System.out.println(user.getName() + " usa " + getName() + " e infligge 6 danni a " + target.getName() + "!");
        } else {
            System.out.println("Mana insufficiente per usare " + getName() + "!");
        }
    }

    @Override
    public String getImagePath() { return "/images/strike.jpg"; }
}
