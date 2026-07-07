package it.unicam.cs.mpgc.rpg123393.model.enemy;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Sentinella Cremisi — Scudo Cremisi: 7 danni + 5 blocco self. */
public class CrimsonSentinelCard implements ICard {
    @Override public String getName()      { return "Scudo Cremisi"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        target.takeDamage(7);
        user.addBlock(5);
    }
}
