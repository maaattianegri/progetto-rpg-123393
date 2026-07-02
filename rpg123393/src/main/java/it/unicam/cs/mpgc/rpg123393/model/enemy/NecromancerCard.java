package it.unicam.cs.mpgc.rpg123393.model.enemy;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Negromante — Drenaggio: 8 danni + si cura di 4 HP. */
public class NecromancerCard implements ICard {
    @Override public String getName()      { return "Drenaggio Vitale"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        target.takeDamage(8);
        user.heal(4);
    }
}
