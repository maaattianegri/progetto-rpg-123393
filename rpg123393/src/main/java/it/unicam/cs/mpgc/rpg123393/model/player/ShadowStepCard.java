package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Passo nell'Ombra — base: 4 dmg + 5 veleno, 2 mana; upgraded: 5 dmg + 7 veleno, 2 mana. */
public class ShadowStepCard implements ICard {
    private final boolean upgraded;

    public ShadowStepCard()                  { this(false); }
    public ShadowStepCard(boolean upgraded)  { this.upgraded = upgraded; }

    @Override public String getName()      { return upgraded ? "Passo nell'Ombra+" : "Passo nell'Ombra"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return null; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            target.takeDamage(upgraded ? 5 : 4);
            target.addPoison(upgraded ? 7 : 5);
        }
    }
}
