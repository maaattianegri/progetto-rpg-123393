package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/**
 * PALADINO — Consacrazione
 * Costo: 2 mana
 * Effetto: 8 danni al nemico + 6 scudo a te stesso.
 */
public class ConsecrationCard implements ICard {
    @Override public String getName()      { return "Consacrazione"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return null; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            target.takeDamage(8);
            user.addBlock(6);
        }
    }
}
