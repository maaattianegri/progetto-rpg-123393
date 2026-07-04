package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/**
 * 🟠 DRACOMANTE — Artiglio del Drago+
 * Costo: 2 mana
 * Effetto: 9 danni al nemico + 6 scudo a te stesso.
 * Stats: ATK 9 | SCUDO +6
 */
public class DragonClawPlusCard implements ICard {
    @Override public String getName()      { return "Artiglio del Drago+"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return "/images/player/dragon_claw.svg"; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            target.takeDamage(9);
            user.addBlock(6);
        }
    }
}
