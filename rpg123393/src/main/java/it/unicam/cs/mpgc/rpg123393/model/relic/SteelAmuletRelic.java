package it.unicam.cs.mpgc.rpg123393.model.relic;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.Relic;

/** Amuleto d'Acciaio — inizi ogni battaglia con 5 scudo. */
public class SteelAmuletRelic implements Relic {
    @Override public String getName()        { return "Amuleto d'Acciaio"; }
    @Override public String getDescription() { return "Inizi ogni battaglia con 5 scudo"; }
    @Override public void onBattleStart(GameCharacter player) { player.addBlock(5); }
}
