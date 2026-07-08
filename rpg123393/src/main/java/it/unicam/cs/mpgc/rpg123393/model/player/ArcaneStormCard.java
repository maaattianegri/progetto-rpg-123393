package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/**
 * Tempesta Arcana
 * base:     10 dmg + veleno×2, 3 mana
 * upgraded: 13 dmg + veleno×3, 2 mana
 */
public class ArcaneStormCard implements ICard {
    private final boolean upgraded;

    public ArcaneStormCard()                  { this(false); }
    public ArcaneStormCard(boolean upgraded)  { this.upgraded = upgraded; }

    @Override public String getName()      { return upgraded ? "Tempesta Arcana+" : "Tempesta Arcana"; }
    @Override public int    getManaCost()  { return upgraded ? 2 : 3; }
    @Override public String getImagePath() { return "/images/player/arcane_storm.svg"; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            int poisonMult = upgraded ? 3 : 2;
            int baseDmg    = upgraded ? 13 : 10;
            target.takeDamage(baseDmg + target.getPoison() * poisonMult);
        }
    }
}
