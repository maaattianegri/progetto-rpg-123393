package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Assassino — Colpo Letale+: 8 danni + veleno nemico. Costo 2. */
public class DeadlyStrikePlusCard implements ICard {
    @Override public String getName()     { return "Colpo Letale+"; }
    @Override public int    getManaCost() { return 2; }
    @Override public String getImagePath(){ return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        target.takeDamage(8 + target.getPoison());
    }
}
