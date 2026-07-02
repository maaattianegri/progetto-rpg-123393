package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/**
 * 🟠 MAGO — Nova di Fuoco
 * Costo: 3 mana
 * Effetto: 18 danni al nemico.
 * Stats: ATK 18
 */
public class FireNovaCard implements ICard {
    @Override public String getName()      { return "Nova di Fuoco"; }
    @Override public int    getManaCost()  { return 3; }
    @Override public String getImagePath() { return null; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            target.takeDamage(18);
        }
    }
}
