package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Mago — Tempesta Arcana+: 13 danni + 3 stack veleno. Costo 2. */
public class ArcaneStormPlusCard implements ICard {
    @Override public String getName()     { return "Tempesta Arcana+"; }
    @Override public int    getManaCost() { return 2; }
    @Override public String getImagePath(){ return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        int poisonDmg = target.getPoison() * 3;
        target.takeDamage(13 + poisonDmg);
    }
}
