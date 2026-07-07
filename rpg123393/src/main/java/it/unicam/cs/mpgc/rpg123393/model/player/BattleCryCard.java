package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Grida di Battaglia — base: 8 dmg + 6 scudo, 3 mana; upgraded: 10 dmg + 8 scudo, 3 mana. */
public class BattleCryCard implements ICard {
    private final boolean upgraded;

    public BattleCryCard()                  { this(false); }
    public BattleCryCard(boolean upgraded)  { this.upgraded = upgraded; }

    @Override public String getName()      { return upgraded ? "Grida di Battaglia+" : "Grida di Battaglia"; }
    @Override public int    getManaCost()  { return 3; }
    @Override public String getImagePath() { return null; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            target.takeDamage(upgraded ? 10 : 8);
            user.addBlock(upgraded ? 8 : 6);
        }
    }
}
