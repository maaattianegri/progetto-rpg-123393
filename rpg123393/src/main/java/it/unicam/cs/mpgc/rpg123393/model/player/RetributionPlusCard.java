package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Paladino — Punizione Divina+: 11 danni. Se hai scudo > 0, danni +8. Costo 2. */
public class RetributionPlusCard implements ICard {
    @Override public String getName()     { return "Punizione Divina+"; }
    @Override public int    getManaCost() { return 2; }
    @Override public String getImagePath(){ return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            int dmg = 11 + (user.getBlock() > 0 ? 8 : 0);
            target.takeDamage(dmg);
        }
    }
}
