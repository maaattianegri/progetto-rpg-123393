package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Paladino — Scudo Sacro: 12 scudo + cura 4 HP, 2 mana. Tank difensivo. */
public class HolyShieldCard implements ICard {
    @Override public String getName()      { return "Scudo Sacro"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return "/images/player/holy_shield.svg"; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            user.addBlock(12);
            user.heal(4);
        }
    }
}
