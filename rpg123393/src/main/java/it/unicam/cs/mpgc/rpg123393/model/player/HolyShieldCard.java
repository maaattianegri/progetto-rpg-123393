package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Scudo Sacro — base: 4 dmg + 12 scudo + 4 cura, 2 mana; upgraded: 5 dmg + 14 scudo + 5 cura, 2 mana. */
public class HolyShieldCard implements ICard {
    private final boolean upgraded;

    public HolyShieldCard()                  { this(false); }
    public HolyShieldCard(boolean upgraded)  { this.upgraded = upgraded; }

    @Override public String getName()      { return upgraded ? "Scudo Sacro+" : "Scudo Sacro"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return "/images/player/holy_shield.svg"; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            target.takeDamage(upgraded ? 5 : 4);
            user.addBlock(upgraded ? 14 : 12);
            user.heal(upgraded ? 5 : 4);
        }
    }
}
