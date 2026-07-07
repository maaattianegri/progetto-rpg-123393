package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Voto di Ferro — base: 12 scudo + 8 dmg, 3 mana; upgraded: 15 scudo + 10 dmg, 3 mana. */
public class IronVowCard implements ICard {
    private final boolean upgraded;

    public IronVowCard()                  { this(false); }
    public IronVowCard(boolean upgraded)  { this.upgraded = upgraded; }

    @Override public String getName()      { return upgraded ? "Voto di Ferro+" : "Voto di Ferro"; }
    @Override public int    getManaCost()  { return 3; }
    @Override public String getImagePath() { return null; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            user.addBlock(upgraded ? 15 : 12);
            target.takeDamage(upgraded ? 10 : 8);
        }
    }
}
