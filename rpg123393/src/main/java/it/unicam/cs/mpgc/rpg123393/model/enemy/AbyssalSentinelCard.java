package it.unicam.cs.mpgc.rpg123393.model.enemy;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Sentinella Abissale — Urlo del Vuoto: 8 danni + 1 veleno. */
public class AbyssalSentinelCard implements ICard {
    @Override public String getName()      { return "Urlo del Vuoto"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        target.takeDamage(8);
        target.addPoison(1);
    }
}
