package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Dardo di Ghiaccio — base: 7 dmg + 2 veleno, 2 mana; upgraded: 9 dmg + 3 veleno, 2 mana. */
public class FrostboltCard implements ICard {
    private final boolean upgraded;

    public FrostboltCard()                  { this(false); }
    public FrostboltCard(boolean upgraded)  { this.upgraded = upgraded; }

    @Override public String getName()      { return upgraded ? "Dardo di Ghiaccio+" : "Dardo di Ghiaccio"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return null; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            target.takeDamage(upgraded ? 9 : 7);
            target.addPoison(upgraded ? 3 : 2);
        }
    }
}
