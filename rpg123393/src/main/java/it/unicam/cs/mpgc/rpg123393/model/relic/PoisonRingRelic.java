package it.unicam.cs.mpgc.rpg123393.model.relic;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.Relic;

/**
 * Anello del Veleno — passivo: le carte veleno applicano +1 stack extra.
 * Questo effetto viene gestito direttamente in BattleService controllando le reliquie.
 * onBattleStart non fa nulla — l'effetto è hook-based.
 */
public class PoisonRingRelic implements Relic {
    @Override public String getName()        { return "Anello del Veleno"; }
    @Override public String getDescription() { return "Le carte veleno applicano +1 stack extra"; }
    @Override public void onBattleStart(GameCharacter player) { /* hook passivo */ }
}
