package it.unicam.cs.mpgc.rpg123393.model.relic;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.Relic;

/** Cristallo Arcano — +1 mana massimo per la run (applicato una volta all'acquisto). */
public class ArcaneCrystalRelic implements Relic {
    private boolean applied = false;
    @Override public String getName()        { return "Cristallo Arcano"; }
    @Override public String getDescription() { return "+1 mana massimo per la run"; }
    @Override public void onBattleStart(GameCharacter player) {
        if (!applied) { player.setMaxMana(player.getMaxMana() + 1); applied = true; }
    }
}
