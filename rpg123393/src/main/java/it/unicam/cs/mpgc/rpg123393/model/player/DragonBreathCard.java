package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Soffio del Drago — base: 9 dmg + 3 veleno, 3 mana; upgraded: 13 dmg + 5 veleno, 3 mana. */
public class DragonBreathCard implements ICard {
    private final boolean upgraded;

    public DragonBreathCard()                  { this(false); }
    public DragonBreathCard(boolean upgraded)  { this.upgraded = upgraded; }

    @Override public String getName()      { return upgraded ? "Soffio del Drago+" : "Soffio del Drago"; }
    @Override public int    getManaCost()  { return 3; }
    @Override public String getImagePath() { return null; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            target.takeDamage(upgraded ? 13 : 9);
            target.addPoison(upgraded ? 5 : 3);
        }
    }
}
