package it.unicam.cs.mpgc.rpg123393.model.enemy;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/**
 * Rigenerazione del Troll: recupera 6 HP (non supera il massimo grazie a heal()).
 * Il target passato è il giocatore, ma il Troll si auto-cura — usa "user".
 */
public class TrollRegenCard implements ICard {
    @Override public String getName()     { return "Rigenerazione"; }
    @Override public int    getManaCost() { return 0; }
    @Override public String getImagePath(){ return "/images/enemy/troll_regen.svg"; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        user.heal(6);
    }
}
