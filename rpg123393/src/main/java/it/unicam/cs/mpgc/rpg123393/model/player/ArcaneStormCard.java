package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Mago — Tempesta Arcana: 10 danni + 2 danni extra per ogni stack veleno sul nemico, 3 mana. */
public class ArcaneStormCard implements ICard {
    @Override public String getName()      { return "Tempesta Arcana"; }
    @Override public int    getManaCost()  { return 3; }
    @Override public String getImagePath() { return "/images/player/arcane_storm.svg"; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            int bonus = target.getPoison() * 2; // sinergia con veleno
            target.takeDamage(10 + bonus);
        }
    }
}
