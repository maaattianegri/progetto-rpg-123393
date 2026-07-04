package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/**
 * 🟠 DRACOMANTE — Soffio del Drago+
 * Costo: 3 mana
 * Effetto: 13 danni + 5 veleno al nemico.
 * Stats: ATK 13 | VELENO 5
 */
public class DragonBreathPlusCard implements ICard {
    @Override public String getName()      { return "Soffio del Drago+"; }
    @Override public int    getManaCost()  { return 3; }
    @Override public String getImagePath() { return null; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            target.takeDamage(13);
            target.addPoison(5);
        }
    }
}
