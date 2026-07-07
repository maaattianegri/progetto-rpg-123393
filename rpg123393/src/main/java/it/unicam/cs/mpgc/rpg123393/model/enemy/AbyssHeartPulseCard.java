package it.unicam.cs.mpgc.rpg123393.model.enemy;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Cuore dell'Abisso — Impulso Abissale: 9 danni + applica 2 veleno. */
public class AbyssHeartPulseCard implements ICard {
    @Override public String getName()      { return "Impulso Abissale"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        target.takeDamage(9);
        target.addPoison(2);
    }
}
