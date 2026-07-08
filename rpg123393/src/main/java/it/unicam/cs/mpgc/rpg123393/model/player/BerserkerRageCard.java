package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Furia Berserk — base: 7 dmg + 5 scudo, 2 mana; upgraded: 9 dmg + 6 scudo, 2 mana. */
public class BerserkerRageCard implements ICard {
    private final boolean upgraded;

    public BerserkerRageCard()                  { this(false); }
    public BerserkerRageCard(boolean upgraded)  { this.upgraded = upgraded; }

    @Override public String getName()      { return upgraded ? "Furia Berserk+" : "Furia Berserk"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return "/images/player/berserker_rage.svg"; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            target.takeDamage(upgraded ? 9 : 7);
            user.addBlock(upgraded ? 6 : 5);
        }
    }
}
