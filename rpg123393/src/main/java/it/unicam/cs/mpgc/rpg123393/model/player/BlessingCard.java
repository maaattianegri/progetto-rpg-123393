package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Paladino — Benedizione: cura 8 HP + guadagna 6 scudo. Costo 2. */
public class BlessingCard implements ICard {
    @Override public String getName()      { return "Benedizione"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        user.useMana(getManaCost());
        user.heal(8);
        user.addBlock(6);
    }
}
