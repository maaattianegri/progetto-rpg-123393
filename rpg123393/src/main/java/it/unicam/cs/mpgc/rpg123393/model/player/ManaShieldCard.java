package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Mago — Scudo di Mana: +10 scudo + recupera 1 mana. Costo 1. */
public class ManaShieldCard implements ICard {
    @Override public String getName()     { return "Scudo di Mana"; }
    @Override public int    getManaCost() { return 1; }
    @Override public String getImagePath(){ return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        user.useMana(getManaCost());
        user.addBlock(10);
        user.addMana(1);
    }
}
