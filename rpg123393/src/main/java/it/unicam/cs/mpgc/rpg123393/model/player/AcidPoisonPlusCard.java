package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Assassino — Veleno Acido+: 6 veleno (nessun danno diretto). Costo 1. */
public class AcidPoisonPlusCard implements ICard {
    @Override public String getName()     { return "Veleno Acido+"; }
    @Override public int    getManaCost() { return 1; }
    @Override public String getImagePath(){ return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        target.addPoison(6);
    }
}
