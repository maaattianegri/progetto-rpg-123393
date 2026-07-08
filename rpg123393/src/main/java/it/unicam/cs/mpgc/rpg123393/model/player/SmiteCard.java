package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Castigo Sacro — base: 8 dmg + resetBlock nemico, 2 mana; upgraded: 11 dmg + resetBlock nemico, 2 mana. */
public class SmiteCard implements ICard {
    private final boolean upgraded;

    public SmiteCard()                  { this(false); }
    public SmiteCard(boolean upgraded)  { this.upgraded = upgraded; }

    @Override public String getName()      { return upgraded ? "Castigo Sacro+" : "Castigo Sacro"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return null; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            target.takeDamage(upgraded ? 11 : 8);
            target.resetBlock();
        }
    }
}
