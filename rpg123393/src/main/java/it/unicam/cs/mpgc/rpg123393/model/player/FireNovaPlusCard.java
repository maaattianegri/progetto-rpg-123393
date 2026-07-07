package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Mago — Nova di Fuoco+: 24 danni. Costo 3. */
public class FireNovaPlusCard implements ICard {
    @Override public String getName()     { return "Nova di Fuoco+"; }
    @Override public int    getManaCost() { return 3; }
    @Override public String getImagePath(){ return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        user.useMana(getManaCost());
        target.takeDamage(24);
    }
}
