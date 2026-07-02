package it.unicam.cs.mpgc.rpg123393.model.enemy;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Banshee — Urlo Spettrale: 5 danni + azzera lo scudo del bersaglio. */
public class BansheeWailCard implements ICard {
    @Override public String getName()      { return "Urlo Spettrale"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        target.resetBlock();
        target.takeDamage(5);
    }
}
