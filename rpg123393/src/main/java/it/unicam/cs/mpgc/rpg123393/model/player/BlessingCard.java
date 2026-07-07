package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Benedizione — base: 8 cura + 6 scudo, 2 mana; upgraded: 10 cura + 8 scudo, 2 mana. */
public class BlessingCard implements ICard {
    private final boolean upgraded;

    public BlessingCard()                  { this(false); }
    public BlessingCard(boolean upgraded)  { this.upgraded = upgraded; }

    @Override public String getName()      { return upgraded ? "Benedizione+" : "Benedizione"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return null; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            user.heal(upgraded ? 10 : 8);
            user.addBlock(upgraded ? 8 : 6);
        }
    }
}
