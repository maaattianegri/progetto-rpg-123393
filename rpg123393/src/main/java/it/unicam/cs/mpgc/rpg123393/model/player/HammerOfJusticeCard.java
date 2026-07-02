package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Paladino — Martello della Giustizia: 14 danni. Costo 3. */
public class HammerOfJusticeCard implements ICard {
    @Override public String getName()      { return "Martello della Giustizia"; }
    @Override public int    getManaCost()  { return 3; }
    @Override public String getImagePath() { return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        target.takeDamage(14);
    }
}
