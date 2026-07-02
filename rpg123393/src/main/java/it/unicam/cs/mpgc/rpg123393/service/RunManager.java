package it.unicam.cs.mpgc.rpg123393.service;

import it.unicam.cs.mpgc.rpg123393.model.EncounterType;

import java.util.List;

/**
 * Gestisce la sequenza fissa di incontri per una run.
 *
 * Sequenza:
 *   0 NORMAL  (Goblin)
 *   1 NORMAL  (Ratto Gigante)
 *   2 SHOP    <- round 1
 *   3 ELITE   (Orco Berserker)
 *   4 ELITE   (Scheletro Arcano)
 *   5 SHOP    <- round 2
 *   6 ELITE   (Troll Rigenerante)
 *   7 ELITE   (Banshee)
 *   8 SHOP    <- round 3
 *   9 BOSS    (Negromante)
 *  10 BOSS    (Drago Antico)
 */
public class RunManager {

    private static final List<EncounterType> SEQUENCE = List.of(
            EncounterType.NORMAL,
            EncounterType.NORMAL,
            EncounterType.SHOP,
            EncounterType.ELITE,
            EncounterType.ELITE,
            EncounterType.SHOP,
            EncounterType.ELITE,
            EncounterType.ELITE,
            EncounterType.SHOP,
            EncounterType.BOSS,
            EncounterType.BOSS
    );

    private int index = 0;

    public EncounterType current() {
        if (index >= SEQUENCE.size()) return EncounterType.BOSS;
        return SEQUENCE.get(index);
    }

    public EncounterType advance() {
        EncounterType t = current();
        index++;
        return t;
    }

    public int  getIndex()       { return index; }
    public int  getTotal()       { return SEQUENCE.size(); }
    public boolean isLastBoss()  { return index >= SEQUENCE.size() - 1; }

    /**
     * Ritorna il numero dello shop corrente (1, 2 o 3) in base all'indice.
     * Usato da ShopPool per scalare i prezzi.
     *   index <= 2  -> shop 1 (dopo 2 normali)
     *   index <= 5  -> shop 2 (dopo 2 elite)
     *   index >= 8  -> shop 3 (dopo 4 elite)
     */
    public int shopRound() {
        if (index <= 2)  return 1;
        if (index <= 5)  return 2;
        return 3;
    }

    /** Oro drop in base al tipo di incontro. Valori alzati per rendere lo shop accessibile. */
    public static int goldDrop(EncounterType type) {
        return switch (type) {
            case NORMAL -> 20 + (int)(Math.random() * 16); // 20-35
            case ELITE  -> 40 + (int)(Math.random() * 21); // 40-60
            case BOSS   -> 80 + (int)(Math.random() * 41); // 80-120
            default     -> 0;
        };
    }

    /** Oro bonus fisso all'inizio della run. */
    public static int startingGold() { return 30; }
}
