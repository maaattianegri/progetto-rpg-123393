package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/**
 * 🟠 DRACOMANTE — Zanna di Drago+
 * Costo: 1 mana
 * Effetto: 6 danni al nemico + 3 scudo a te stesso.
 * Stats: ATK 6 | SCUDO +3
 */
public class DragonFangPlusCard implements ICard {
    @Override public String getName()      { return "Zanna di Drago+"; }
    @Override public int    getManaCost()  { return 1; }
    @Override public String getImagePath() { return null; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            target.takeDamage(6);
            user.addBlock(3);
        }
    }
}
