package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Paladino — Luce Divina+: cura 15 HP + 6 scudo. Costo 2. */
public class DivineLightPlusCard implements ICard {
    @Override public String getName()     { return "Luce Divina+"; }
    @Override public int    getManaCost() { return 2; }
    @Override public String getImagePath(){ return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            user.heal(15);
            user.addBlock(6);
        }
    }
}
