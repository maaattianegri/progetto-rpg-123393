package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/**
 * Scudo di Mana
 * base:     +10 scudo + 1 mana recuperato, costo 1 mana
 * upgraded: +14 scudo + 1 mana recuperato, costo 2 mana
 */
public class ManaShieldCard implements ICard {
    private final boolean upgraded;

    public ManaShieldCard()                  { this(false); }
    public ManaShieldCard(boolean upgraded)  { this.upgraded = upgraded; }

    @Override public String getName()      { return upgraded ? "Scudo di Mana+" : "Scudo di Mana"; }
    @Override public int    getManaCost()  { return upgraded ? 2 : 1; }
    @Override public String getImagePath() { return null; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            user.addBlock(upgraded ? 14 : 10);
            user.addMana(1);
        }
    }
}
