package it.unicam.cs.mpgc.rpg123393.model.enemy;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/**
 * Fiato di Fuoco del Drago Antico: infligge 14 danni che bypassano completamente
 * lo scudo del bersaglio (danno diretto agli HP).
 */
public class DragonBreathCard implements ICard {
    @Override public String getName()     { return "Fiato di Fuoco"; }
    @Override public int    getManaCost() { return 0; }
    @Override public String getImagePath(){ return "/images/enemy/dragon_breath.svg"; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        // Danno diretto: azzera temporaneamente il block, poi lo ripristina
        int savedBlock = target.getBlock();
        target.resetBlock();
        target.takeDamage(14);
        target.addBlock(savedBlock);
    }
}
