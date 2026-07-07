package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Guerriero — Mulinello+: 7 danni + 7 cura. Costo 2. */
public class WhirlwindPlusCard implements ICard {
    @Override public String getName()     { return "Mulinello+"; }
    @Override public int    getManaCost() { return 2; }
    @Override public String getImagePath(){ return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        user.useMana(getManaCost());
        target.takeDamage(7);
        user.heal(7);
    }
}
