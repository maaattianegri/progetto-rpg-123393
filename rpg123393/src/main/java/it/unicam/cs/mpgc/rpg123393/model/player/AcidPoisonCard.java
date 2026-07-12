package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Veleno Acido — base: 4 veleno, 1 mana; upgraded: 6 veleno, 1 mana. */
public class AcidPoisonCard implements ICard {
    private final boolean upgraded;

    public AcidPoisonCard()                  { this(false); }
    public AcidPoisonCard(boolean upgraded)  { this.upgraded = upgraded; }

    @Override public String getName()      { return upgraded ? "Veleno Acido+" : "Veleno Acido"; }
    @Override public int    getManaCost()  { return 1; }
    @Override public String getImagePath() { return null; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            target.addPoison(upgraded ? 6 : 4);
        }
    }
}
