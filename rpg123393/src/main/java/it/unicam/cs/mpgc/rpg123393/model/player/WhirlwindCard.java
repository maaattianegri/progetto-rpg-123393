package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Guerriero — Mulinello: 5 danni, poi cura 5 HP. Costo 2. */
public class WhirlwindCard implements ICard {
    @Override public String getName()     { return "Mulinello"; }
    @Override public int    getManaCost() { return 2; }
    @Override public String getImagePath(){ return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        target.takeDamage(5);
        user.heal(5);
    }
}
