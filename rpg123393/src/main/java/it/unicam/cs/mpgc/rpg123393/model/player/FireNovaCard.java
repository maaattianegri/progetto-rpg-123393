package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Nova di Fuoco — base: 18 dmg, 3 mana; upgraded: 24 dmg, 3 mana. */
public class FireNovaCard implements ICard {
    private final boolean upgraded;

    public FireNovaCard()                  { this(false); }
    public FireNovaCard(boolean upgraded)  { this.upgraded = upgraded; }

    @Override public String getName()      { return upgraded ? "Nova di Fuoco+" : "Nova di Fuoco"; }
    @Override public int    getManaCost()  { return 3; }
    @Override public String getImagePath() { return null; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            target.takeDamage(upgraded ? 24 : 18);
        }
    }
}
