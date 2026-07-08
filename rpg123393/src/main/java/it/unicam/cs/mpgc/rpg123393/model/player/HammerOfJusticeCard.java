package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Martello della Giustizia — base: 14 dmg, 3 mana; upgraded: 18 dmg, 3 mana. */
public class HammerOfJusticeCard implements ICard {
    private final boolean upgraded;

    public HammerOfJusticeCard()                  { this(false); }
    public HammerOfJusticeCard(boolean upgraded)  { this.upgraded = upgraded; }

    @Override public String getName()      { return upgraded ? "Martello della Giustizia+" : "Martello della Giustizia"; }
    @Override public int    getManaCost()  { return 3; }
    @Override public String getImagePath() { return null; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            target.takeDamage(upgraded ? 18 : 14);
        }
    }
}
