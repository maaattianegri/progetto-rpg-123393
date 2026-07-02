package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Colpo di Spada+ — 9 danni. Versione potenziata. */
public class StrikePlusCard implements ICard {
    @Override public String getName()      { return "Colpo di Spada+"; }
    @Override public int    getManaCost()  { return 1; }
    @Override public String getImagePath() { return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            target.takeDamage(9);
        }
    }
}
