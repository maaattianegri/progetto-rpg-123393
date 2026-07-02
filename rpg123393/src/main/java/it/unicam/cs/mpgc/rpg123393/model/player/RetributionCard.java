package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Paladino — Punizione Divina: 10 danni. Se hai scudo > 0, danni +4. Costo 2. */
public class RetributionCard implements ICard {
    @Override public String getName()     { return "Punizione Divina"; }
    @Override public int    getManaCost() { return 2; }
    @Override public String getImagePath(){ return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        int dmg = 10 + (user.getBlock() > 0 ? 4 : 0);
        target.takeDamage(dmg);
    }
}
