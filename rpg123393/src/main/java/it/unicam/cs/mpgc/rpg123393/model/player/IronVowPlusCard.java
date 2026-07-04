package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/**
 * PALADINO — Voto di Ferro+
 * Costo: 3 mana
 * Effetto: 15 scudo + 10 danni al nemico.
 */
public class IronVowPlusCard implements ICard {
    @Override public String getName()      { return "Voto di Ferro+"; }
    @Override public int    getManaCost()  { return 3; }
    @Override public String getImagePath() { return null; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            user.addBlock(15);
            target.takeDamage(10);
        }
    }
}
