package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/**
 * 🟠 DRACOMANTE — Zanna di Drago
 * Costo: 1 mana
 * Effetto: 4 danni al nemico + 2 scudo a te stesso.
 * Stats: ATK 4 | SCUDO +2
 */
public class DragonFangCard implements ICard {
    @Override public String getName()      { return "Zanna di Drago"; }
    @Override public int    getManaCost()  { return 1; }
    @Override public String getImagePath() { return null; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            target.takeDamage(4);
            user.addBlock(2);
        }
    }
}
