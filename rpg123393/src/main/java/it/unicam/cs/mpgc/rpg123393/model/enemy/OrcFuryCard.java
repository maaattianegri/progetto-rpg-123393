package it.unicam.cs.mpgc.rpg123393.model.enemy;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/**
 * Furia dell'Orco: attacca due volte nello stesso turno (4 + 4 danni).
 * Costo mana 0 — i nemici non hanno un vero sistema mana, il costo viene ignorato.
 */
public class OrcFuryCard implements ICard {
    @Override public String getName()     { return "Furia Doppia"; }
    @Override public int    getManaCost() { return 0; }
    @Override public String getImagePath(){ return "/images/enemy/orc_fury.svg"; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        target.takeDamage(4);
        target.takeDamage(4);
    }
}
