package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Mulinello — base: 5 dmg + 5 cura, 2 mana; upgraded: 7 dmg + 7 cura, 2 mana. */
public class WhirlwindCard implements ICard {
    private final boolean upgraded;

    public WhirlwindCard()                  { this(false); }
    public WhirlwindCard(boolean upgraded)  { this.upgraded = upgraded; }

    @Override public String getName()      { return upgraded ? "Mulinello+" : "Mulinello"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return null; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            target.takeDamage(upgraded ? 7 : 5);
            user.heal(upgraded ? 7 : 5);
        }
    }
}
