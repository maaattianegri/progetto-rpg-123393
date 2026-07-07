package it.unicam.cs.mpgc.rpg123393.model.enemy;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Cavaliere Vampiro — Lancia Oscura: 9 danni + drena 3 HP. */
public class VampireKnightCard implements ICard {
    @Override public String getName()      { return "Lancia Oscura"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        target.takeDamage(9);
        user.heal(3);
    }
}
