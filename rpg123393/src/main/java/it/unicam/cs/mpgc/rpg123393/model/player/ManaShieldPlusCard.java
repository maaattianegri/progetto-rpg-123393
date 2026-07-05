package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Mago — Scudo di Mana+: +14 scudo + 1 mana. Costo 2. */
public class ManaShieldPlusCard implements ICard {
    @Override public String getName()     { return "Scudo di Mana+"; }
    @Override public int    getManaCost() { return 2; }
    @Override public String getImagePath(){ return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        user.useMana(getManaCost());
        user.addBlock(14);
        user.addMana(1);
    }
}
