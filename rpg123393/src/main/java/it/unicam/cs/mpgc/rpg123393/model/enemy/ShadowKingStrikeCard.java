package it.unicam.cs.mpgc.rpg123393.model.enemy;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Re Ombra — Lama d'Ombra: 12 danni + si aggiunge 3 blocco. */
public class ShadowKingStrikeCard implements ICard {
    @Override public String getName()      { return "Lama d'Ombra"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return null; }
    @Override public void play(GameCharacter user, GameCharacter target) {
        target.takeDamage(12);
        user.addBlock(3);
    }
}
