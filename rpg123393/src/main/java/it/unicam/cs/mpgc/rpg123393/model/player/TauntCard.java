package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Sfida — base: +8 scudo, 1 mana; upgraded: +12 scudo, 1 mana. */
public class TauntCard implements ICard {
    private final boolean upgraded;

    public TauntCard()                  { this(false); }
    public TauntCard(boolean upgraded)  { this.upgraded = upgraded; }

    @Override public String getName()      { return upgraded ? "Sfida+" : "Sfida"; }
    @Override public int    getManaCost()  { return 1; }
    @Override public String getImagePath() { return null; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            user.addBlock(upgraded ? 12 : 8);
        }
    }
}
