package it.unicam.cs.mpgc.rpg123393.model.enemy;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Custode delle Ombre — Manto d'Ombra: 10 danni + 4 blocco self. */
public class ShadowKeeperCard implements ICard {
    @Override public String getName()      { return "Manto d'Ombra"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        target.takeDamage(10);
        user.addBlock(4);
    }
}
