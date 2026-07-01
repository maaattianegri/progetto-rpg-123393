package it.unicam.cs.mpgc.rpg123393.model;

public interface ICard {
    String getName();
    int getManaCost();

    void play(GameCharacter user, GameCharacter target);
    String getImagePath();
}
