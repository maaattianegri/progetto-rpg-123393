package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Difesa+ — +9 scudo. Versione potenziata di Difesa. */
public class DefendPlusCard implements ICard {
    @Override public String getName()      { return "Difesa+"; }
    @Override public int    getManaCost()  { return 1; }
    @Override public String getImagePath() { return null; }
    @Override public void play(GameCharacter user, GameCharacter target) { user.addBlock(9); }
}
