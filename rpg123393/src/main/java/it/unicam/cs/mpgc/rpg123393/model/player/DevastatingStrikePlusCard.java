package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Colpo Devastante+ — 16 danni. */
public class DevastatingStrikePlusCard implements ICard {
    @Override public String getName()      { return "Colpo Devastante+"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return null; }
    @Override public void play(GameCharacter user, GameCharacter target) { target.takeDamage(16); }
}
