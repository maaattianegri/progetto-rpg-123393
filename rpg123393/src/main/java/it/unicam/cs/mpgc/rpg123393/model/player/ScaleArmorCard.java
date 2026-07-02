package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Dracomante — Armatura di Scaglie: +8 scudo + cura 4 HP. Costo 2. */
public class ScaleArmorCard implements ICard {
    @Override public String getName()     { return "Armatura di Scaglie"; }
    @Override public int    getManaCost() { return 2; }
    @Override public String getImagePath(){ return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        user.addBlock(8);
        user.heal(4);
    }
}
