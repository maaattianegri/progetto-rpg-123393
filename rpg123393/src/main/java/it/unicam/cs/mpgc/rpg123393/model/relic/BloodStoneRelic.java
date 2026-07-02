package it.unicam.cs.mpgc.rpg123393.model.relic;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.Relic;

/** Pietra di Sangue — inizi ogni battaglia con +3 HP temporanei (bonus una tantum). */
public class BloodStoneRelic implements Relic {
    @Override public String getName()        { return "Pietra di Sangue"; }
    @Override public String getDescription() { return "Inizi ogni battaglia con +3 HP"; }
    @Override public void onBattleStart(GameCharacter player) { player.heal(3); }
}
