package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Colpo Letale — base: 6 + veleno nemico dmg, 2 mana; upgraded: 8 + veleno nemico dmg, 2 mana. */
public class DeadlyStrikeCard implements ICard {
    private final boolean upgraded;

    public DeadlyStrikeCard()                  { this(false); }
    public DeadlyStrikeCard(boolean upgraded)  { this.upgraded = upgraded; }

    @Override public String getName()      { return upgraded ? "Colpo Letale+" : "Colpo Letale"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return null; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            target.takeDamage((upgraded ? 8 : 6) + target.getPoison());
        }
    }
}
