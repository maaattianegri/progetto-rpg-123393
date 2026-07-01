package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Dracomante — Artiglio del Drago: 6 danni + 4 scudo, 2 mana. Ibrido attacco/difesa. */
public class DragonClawCard implements ICard {
    @Override public String getName()      { return "Artiglio del Drago"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return "/images/player/dragon_claw.svg"; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            target.takeDamage(6);
            user.addBlock(4);
        }
    }
}
