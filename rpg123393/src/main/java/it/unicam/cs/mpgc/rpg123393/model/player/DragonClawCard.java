package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Artiglio del Drago — base: 6 dmg + 4 scudo, 2 mana; upgraded: 9 dmg + 6 scudo, 2 mana. */
public class DragonClawCard implements ICard {
    private final boolean upgraded;

    public DragonClawCard()                  { this(false); }
    public DragonClawCard(boolean upgraded)  { this.upgraded = upgraded; }

    @Override public String getName()      { return upgraded ? "Artiglio del Drago+" : "Artiglio del Drago"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return "/images/player/dragon_claw.svg"; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            target.takeDamage(upgraded ? 9 : 6);
            user.addBlock(upgraded ? 6 : 4);
        }
    }
}
