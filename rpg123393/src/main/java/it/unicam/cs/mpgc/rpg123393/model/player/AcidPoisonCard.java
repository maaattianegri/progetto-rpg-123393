package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/**
 * 🟢 ASSASSINO — Veleno Acido
 * Costo: 1 mana
 * Effetto: Applica 4 stack di veleno al nemico (nessun danno diretto).
 *          Il veleno infligge danno ogni turno nemico e scala con gli stack.
 * Stats: VELENO +4
 */
public class AcidPoisonCard implements ICard {
    @Override public String getName()      { return "Veleno Acido"; }
    @Override public int    getManaCost()  { return 1; }
    @Override public String getImagePath() { return null; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            target.addPoison(4);
        }
    }
}
