package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Guerriero — Grida di Battaglia: 8 danni + 6 scudo. Costo 3. */
public class BattleCryCard implements ICard {
    @Override public String getName()     { return "Grida di Battaglia"; }
    @Override public int    getManaCost() { return 3; }
    @Override public String getImagePath(){ return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        target.takeDamage(8);
        user.addBlock(6);
    }
}
