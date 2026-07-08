package it.unicam.cs.mpgc.rpg123393.model;

/** Scudo di Legno — base: +6 scudo/1 mana; upgraded: +9 scudo/1 mana. */
public class DefendCard implements ICard {
    private final boolean upgraded;

    public DefendCard()                  { this(false); }
    public DefendCard(boolean upgraded)  { this.upgraded = upgraded; }

    @Override public String getName()      { return upgraded ? "Scudo di Legno+" : "Scudo di Legno"; }
    @Override public int    getManaCost()  { return 1; }
    @Override public String getImagePath() { return "/images/defend.svg"; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            user.addBlock(upgraded ? 9 : 6);
        }
    }
}
