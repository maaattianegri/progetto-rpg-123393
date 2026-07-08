package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Colpo Devastante — base: 12 dmg/2 mana; upgraded: 16 dmg/2 mana. */
public class DevastatingStrikeCard implements ICard {
    private final boolean upgraded;

    public DevastatingStrikeCard()                  { this(false); }
    public DevastatingStrikeCard(boolean upgraded)  { this.upgraded = upgraded; }

    @Override public String getName()      { return upgraded ? "Colpo Devastante+" : "Colpo Devastante"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return "/images/player/devastating_strike.svg"; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            target.takeDamage(upgraded ? 16 : 12);
        }
    }
}
