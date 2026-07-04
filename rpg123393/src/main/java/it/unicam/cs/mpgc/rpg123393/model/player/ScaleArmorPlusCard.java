package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/**
 * 🟠 DRACOMANTE — Armatura di Scaglie+
 * Costo: 2 mana
 * Effetto: +12 scudo + cura 8 HP.
 * Stats: SCUDO +12 | CURA 8
 */
public class ScaleArmorPlusCard implements ICard {
    @Override public String getName()      { return "Armatura di Scaglie+"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return null; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            user.addBlock(12);
            user.heal(8);
        }
    }
}
