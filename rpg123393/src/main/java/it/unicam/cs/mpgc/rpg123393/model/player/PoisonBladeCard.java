package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/**
 * Assassino — Lama Avvelenata: 3 danni immediati + aggiunge 3 stack veleno al nemico, 1 mana.
 * Il veleno infligge danno ogni turno nemico e scala con gli stack (si riducono di 1/turno).
 */
public class PoisonBladeCard implements ICard {
    @Override public String getName()      { return "Lama Avvelenata"; }
    @Override public int    getManaCost()  { return 1; }
    @Override public String getImagePath() { return "/images/player/poison_blade.svg"; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            target.takeDamage(3);
            target.addPoison(3);
        }
    }
}
