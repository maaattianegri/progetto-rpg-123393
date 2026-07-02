package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Paladino — Luce Divina: cura 12 HP + 4 scudo. Costo 2. */
public class DivineLightCard implements ICard {
    @Override public String getName()     { return "Luce Divina"; }
    @Override public int    getManaCost() { return 2; }
    @Override public String getImagePath(){ return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        user.heal(12);
        user.addBlock(4);
    }
}
