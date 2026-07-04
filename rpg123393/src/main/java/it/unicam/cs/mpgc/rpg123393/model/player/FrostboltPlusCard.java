package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Mago — Dardo di Ghiaccio+: 9 danni + 3 veleno. Costo 2. */
public class FrostboltPlusCard implements ICard {
    @Override public String getName()     { return "Dardo di Ghiaccio+"; }
    @Override public int    getManaCost() { return 2; }
    @Override public String getImagePath(){ return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        target.takeDamage(9);
        target.addPoison(3);
    }
}
