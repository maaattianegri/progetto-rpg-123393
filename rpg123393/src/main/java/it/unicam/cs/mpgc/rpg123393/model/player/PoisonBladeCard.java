package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Lama Avvelenata — base: 3 dmg + 3 veleno, 1 mana; upgraded: 3 dmg + 5 veleno, 1 mana. */
public class PoisonBladeCard implements ICard {
    private final boolean upgraded;

    public PoisonBladeCard()                  { this(false); }
    public PoisonBladeCard(boolean upgraded)  { this.upgraded = upgraded; }

    @Override public String getName()      { return upgraded ? "Lama Avvelenata+" : "Lama Avvelenata"; }
    @Override public int    getManaCost()  { return 1; }
    @Override public String getImagePath() { return "/images/player/poison_blade.svg"; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            target.takeDamage(3);
            target.addPoison(upgraded ? 5 : 3);
        }
    }
}
