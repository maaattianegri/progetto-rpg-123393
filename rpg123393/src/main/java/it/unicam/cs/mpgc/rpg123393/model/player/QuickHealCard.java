package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Neutra — Pozione Rapida: cura 10 HP. Costo 1. Disponibile a tutte le classi. */
public class QuickHealCard implements ICard {
    @Override public String getName()     { return "Pozione Rapida"; }
    @Override public int    getManaCost() { return 1; }
    @Override public String getImagePath(){ return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        user.heal(10);
    }
}
