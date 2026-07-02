package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/**
 * 🔴 GUERRIERO — Sfida
 * Costo: 1 mana
 * Effetto: Applica 8 scudo a te stesso.
 * Stats: SCUDO +8
 */
public class TauntCard implements ICard {
    @Override public String getName()      { return "Sfida"; }
    @Override public int    getManaCost()  { return 1; }
    @Override public String getImagePath() { return null; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            user.addBlock(8);
        }
    }
}
