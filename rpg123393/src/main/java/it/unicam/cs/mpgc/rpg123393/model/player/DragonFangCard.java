package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Zanna di Drago — base: 4 dmg + 2 scudo, 1 mana; upgraded: 6 dmg + 3 scudo, 1 mana. */
public class DragonFangCard implements ICard {
    private final boolean upgraded;

    public DragonFangCard()                  { this(false); }
    public DragonFangCard(boolean upgraded)  { this.upgraded = upgraded; }

    @Override public String getName()      { return upgraded ? "Zanna di Drago+" : "Zanna di Drago"; }
    @Override public int    getManaCost()  { return 1; }
    @Override public String getImagePath() { return null; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            target.takeDamage(upgraded ? 6 : 4);
            user.addBlock(upgraded ? 3 : 2);
        }
    }
}
