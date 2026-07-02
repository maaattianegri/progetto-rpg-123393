package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Fireball+ — 12 danni. Versione potenziata di Fireball. */
public class FireballPlusCard implements ICard {
    @Override public String getName()      { return "Fireball+"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return null; }
    @Override public void play(GameCharacter user, GameCharacter target) { target.takeDamage(12); }
}
