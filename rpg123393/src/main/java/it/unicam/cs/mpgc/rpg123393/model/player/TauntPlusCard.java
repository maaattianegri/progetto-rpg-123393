package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Guerriero — Sfida+: +12 scudo. Costo 1. */
public class TauntPlusCard implements ICard {
    @Override public String getName()     { return "Sfida+"; }
    @Override public int    getManaCost() { return 1; }
    @Override public String getImagePath(){ return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        user.useMana(getManaCost());
        user.addBlock(12);
    }
}
