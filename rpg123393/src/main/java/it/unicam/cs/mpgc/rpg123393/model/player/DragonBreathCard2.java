package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Dracomante — Soffio del Drago: 9 danni + 3 veleno. Costo 3. */
public class DragonBreathCard2 implements ICard {
    @Override public String getName()      { return "Soffio del Drago"; }
    @Override public int    getManaCost()  { return 3; }
    @Override public String getImagePath() { return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        user.useMana(getManaCost());
        target.takeDamage(9);
        target.addPoison(3);
    }
}
