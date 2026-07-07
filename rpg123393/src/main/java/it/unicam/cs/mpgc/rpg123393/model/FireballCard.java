package it.unicam.cs.mpgc.rpg123393.model;

/** Palla di Fuoco — base: 14 dmg/2 mana; upgraded: 18 dmg/2 mana. */
public class FireballCard implements ICard {
    private final boolean upgraded;

    public FireballCard()                  { this(false); }
    public FireballCard(boolean upgraded)  { this.upgraded = upgraded; }

    @Override public String getName()      { return upgraded ? "Palla di Fuoco+" : "Palla di Fuoco"; }
    @Override public int    getManaCost()  { return 2; }
    @Override public String getImagePath() { return "/images/fireball.svg"; }

    @Override
    public void play(GameCharacter user, GameCharacter target) {
        if (user.getCurrentMana() >= getManaCost()) {
            user.useMana(getManaCost());
            target.takeDamage(upgraded ? 18 : 14);
        }
    }
}
