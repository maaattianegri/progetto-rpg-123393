package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/**
 * 🟢 ASSASSINO — Doppia Lama
 * Costo: 2 mana
 * Effetto: Infligge 2 colpi da 4 danni ciascuno (totale 8 danni).
 * Stats: ATK 4+4 = 8 totali
 */
public class DoubleBladCard implements ICard {
    @Override public String getName()      { return "Doppia Lama"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return null; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            target.takeDamage(4);
            target.takeDamage(4);
        }
    }
}
