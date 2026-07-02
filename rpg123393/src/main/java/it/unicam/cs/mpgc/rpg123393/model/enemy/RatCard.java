package it.unicam.cs.mpgc.rpg123393.model.enemy;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Ratto Gigante — Morso: 4 danni + 1 veleno. */
public class RatCard implements ICard {
    @Override public String getName()      { return "Morso"; }
    @Override public int    getManaCost()  { return 1; }
    @Override public String getImagePath() { return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        target.takeDamage(4);
        target.addPoison(1);
    }
}
