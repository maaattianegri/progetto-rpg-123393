package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/**
 * 🔴 GUERRIERO — Furia Berserker
 * Costo: 2 mana
 * Effetto: 7 danni al nemico. Se i tuoi HP sono sotto il 50% del massimo, infligge +5 danni bonus.
 * Stats: ATK 7 (12 se HP bassi)
 */
public class BerserkerRageCard implements ICard {
    @Override public String getName()      { return "Furia Berserker"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return null; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            int dmg = 7;
            if (user.getCurrentHp() < user.getMaxHp() / 2) dmg += 5;
            target.takeDamage(dmg);
        }
    }
}
