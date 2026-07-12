package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Armatura di Scaglie — base: +8 scudo + 4 cura, 2 mana; upgraded: +12 scudo + 8 cura, 2 mana. */
public class ScaleArmorCard implements ICard {
    private final boolean upgraded;

    public ScaleArmorCard()                  { this(false); }
    public ScaleArmorCard(boolean upgraded)  { this.upgraded = upgraded; }

    @Override public String getName()      { return upgraded ? "Armatura di Scaglie+" : "Armatura di Scaglie"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return null; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            user.addBlock(upgraded ? 12 : 8);
            user.heal(upgraded ? 8 : 4);
        }
    }
}
