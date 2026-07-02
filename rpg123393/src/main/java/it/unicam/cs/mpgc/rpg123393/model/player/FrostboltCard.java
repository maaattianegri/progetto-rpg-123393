package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Mago — Dardo di Ghiaccio: 7 danni + 2 stack veleno ("congelamento"). Costo 2. */
public class FrostboltCard implements ICard {
    @Override public String getName()     { return "Dardo di Ghiaccio"; }
    @Override public int    getManaCost() { return 2; }
    @Override public String getImagePath(){ return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        target.takeDamage(7);
        target.addPoison(2);
    }
}
