package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Paladino — Castigo Sacro+: 11 danni + rimuove tutto lo scudo del nemico. Costo 2. */
public class SmitePlusCard implements ICard {
    @Override public String getName()      { return "Castigo Sacro+"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            target.takeDamage(11);
            target.resetBlock();
        }
    }
}
