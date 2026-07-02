package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Lama Avvelenata+ — 3 danni + 5 veleno. Versione potenziata. */
public class PoisonBladePlusCard implements ICard {
    @Override public String getName()      { return "Lama Avvelenata+"; }
    @Override public int    getManaCost()  { return 1; }
    @Override public String getImagePath() { return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            target.takeDamage(3);
            target.addPoison(5);
        }
    }
}
