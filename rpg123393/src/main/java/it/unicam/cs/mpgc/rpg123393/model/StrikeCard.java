package it.unicam.cs.mpgc.rpg123393.model;

/** Colpo di Spada — base: 6 dmg/1 mana; upgraded: 9 dmg/1 mana. */
public class StrikeCard implements ICard {
    private final boolean upgraded;

    public StrikeCard()              { this(false); }
    public StrikeCard(boolean upgraded) { this.upgraded = upgraded; }

    @Override public String getName()      { return upgraded ? "Colpo di Spada+" : "Colpo di Spada"; }
    @Override public int    getManaCost()  { return 1; }
    @Override public String getImagePath() { return "/images/strike.svg"; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            target.takeDamage(upgraded ? 9 : 6);
        }
    }
}
