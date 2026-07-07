package it.unicam.cs.mpgc.rpg123393.model.enemy;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Vampiro Lord — Morso Vampirico: 10 danni + si cura di 5 HP. */
public class VampireDrainCard implements ICard {
    @Override public String getName()      { return "Morso Vampirico"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        target.takeDamage(10);
        user.heal(5);
    }
}
