package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Assassino — Doppia Lama+: 6+6 danni (2 colpi). Costo 2. */
public class DoubleBladPlusCard implements ICard {
    @Override public String getName()     { return "Doppia Lama+"; }
    @Override public int    getManaCost() { return 2; }
    @Override public String getImagePath(){ return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        target.takeDamage(6);
        target.takeDamage(6);
    }
}
