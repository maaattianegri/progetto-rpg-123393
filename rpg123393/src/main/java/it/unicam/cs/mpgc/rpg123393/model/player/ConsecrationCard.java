package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Consacrazione — base: 8 dmg + 6 scudo, 2 mana; upgraded: 10 dmg + 8 scudo, 2 mana. */
public class ConsecrationCard implements ICard {
    private final boolean upgraded;

    public ConsecrationCard()                  { this(false); }
    public ConsecrationCard(boolean upgraded)  { this.upgraded = upgraded; }

    @Override public String getName()      { return upgraded ? "Consacrazione+" : "Consacrazione"; }
    @Override public int    getManaCost()  { return 2; }
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
