package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Assassino — Passo nell'Ombra+: 5 danni + 7 veleno. Costo 2. */
public class ShadowStepPlusCard implements ICard {
    @Override public String getName()     { return "Passo nell'Ombra+"; }
    @Override public int    getManaCost() { return 2; }
    @Override public String getImagePath(){ return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        user.useMana(getManaCost());
        target.takeDamage(5);
        target.addPoison(7);
    }
}
