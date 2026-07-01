package it.unicam.cs.mpgc.rpg123393.model.enemy;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/**
 * Maledizione dello Scheletro Arcano: drena 2 mana al bersaglio.
 * Se il bersaglio ha meno di 2 mana, lo porta a 0 senza andare negativo.
 */
public class SkeletonCurseCard implements ICard {
    @Override public String getName()     { return "Maledizione"; }
    @Override public int    getManaCost() { return 0; }
    @Override public String getImagePath(){ return "/images/enemy/skeleton_curse.svg"; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        int drain = Math.min(2, target.getCurrentMana());
        target.useMana(drain);
    }
}
