package it.unicam.cs.mpgc.rpg123393.model.player;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

/** Luce Divina — base: 12 cura + 4 scudo, 2 mana; upgraded: 15 cura + 6 scudo, 2 mana. */
public class DivineLightCard implements ICard {
    private final boolean upgraded;

    public DivineLightCard()                  { this(false); }
    public DivineLightCard(boolean upgraded)  { this.upgraded = upgraded; }

    @Override public String getName()      { return upgraded ? "Luce Divina+" : "Luce Divina"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return null; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            user.heal(upgraded ? 15 : 12);
            user.addBlock(upgraded ? 6 : 4);
        }
    }
}
