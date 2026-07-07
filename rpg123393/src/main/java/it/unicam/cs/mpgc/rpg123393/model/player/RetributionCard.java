package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Punizione Divina — base: 10 dmg (+6 se scudo>0), 2 mana; upgraded: 11 dmg (+8 se scudo>0), 2 mana. */
public class RetributionCard implements ICard {
    private final boolean upgraded;

    public RetributionCard()                  { this(false); }
    public RetributionCard(boolean upgraded)  { this.upgraded = upgraded; }

    @Override public String getName()      { return upgraded ? "Punizione Divina+" : "Punizione Divina"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return null; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            int bonus = user.getBlock() > 0 ? (upgraded ? 8 : 6) : 0;
            target.takeDamage((upgraded ? 11 : 10) + bonus);
        }
    }
}
