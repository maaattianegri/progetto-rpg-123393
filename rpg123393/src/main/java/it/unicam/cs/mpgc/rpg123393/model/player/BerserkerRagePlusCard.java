package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Guerriero — Furia Berserker+: 9 danni (15 se HP < 50%). Costo 2. */
public class BerserkerRagePlusCard implements ICard {
    @Override public String getName()     { return "Furia Berserker+"; }
    @Override public int    getManaCost() { return 2; }
    @Override public String getImagePath(){ return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        int dmg = 9;
        if (user.getCurrentHp() < user.getMaxHp() / 2) dmg += 6;
        target.takeDamage(dmg);
    }
}
