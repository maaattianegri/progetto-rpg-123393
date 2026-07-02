package it.unicam.cs.mpgc.rpg123393.model;

/**
 * Interfaccia per le reliquie — effetti passivi che durano tutta la run.
 */
public interface Relic {
    String getName();
    String getDescription();
    /** Chiamato all'inizio di ogni battaglia. */
    void onBattleStart(GameCharacter player);
}
