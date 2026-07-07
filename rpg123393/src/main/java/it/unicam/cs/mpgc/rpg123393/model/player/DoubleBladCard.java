package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Doppia Lama — base: 4+4 dmg (2 colpi), 2 mana; upgraded: 6+6 dmg (2 colpi), 2 mana. */
public class DoubleBladCard implements ICard {
    private final boolean upgraded;

    public DoubleBladCard()                  { this(false); }
    public DoubleBladCard(boolean upgraded)  { this.upgraded = upgraded; }

    @Override public String getName()      { return upgraded ? "Doppia Lama+" : "Doppia Lama"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return null; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            int hit = upgraded ? 6 : 4;
            target.takeDamage(hit);
            target.takeDamage(hit);
        }
    }
}
