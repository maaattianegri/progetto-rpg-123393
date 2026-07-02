package it.unicam.cs.mpgc.rpg123393.service;

import it.unicam.cs.mpgc.rpg123393.model.EncounterType;

import java.util.List;

/**
 * Gestisce la sequenza fissa di incontri per una run.
 *
 * Sequenza:
 *   0 NORMAL  (Goblin)
 *   1 NORMAL  (Ratto Gigante)
 *   2 SHOP
 *   3 ELITE   (Orco Berserker)
 *   4 ELITE   (Scheletro Arcano)
 *   5 SHOP
 *   6 ELITE   (Troll Rigenerante)
 *   7 ELITE   (Banshee)
 *   8 SHOP
 *   9 BOSS    (Negromante) -> scelta carta gratis
 *  10 BOSS    (Drago Antico) -> scelta carta gratis
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

    /** Restituisce il tipo di incontro corrente senza avanzare. */
    public EncounterType current() {
        if (index >= SEQUENCE.size()) return EncounterType.BOSS;
        return SEQUENCE.get(index);
    }

    /** Avanza al prossimo incontro e restituisce il tipo corrente PRIMA dell'avanzamento. */
    public EncounterType advance() {
        EncounterType t = current();
        index++;
        return t;
    }

    public int getIndex()      { return index; }
    public int getTotal()      { return SEQUENCE.size(); }
    public boolean isLastBoss(){ return index >= SEQUENCE.size() - 1; }

    /** Oro drop in base al tipo di incontro corrente. */
    public static int goldDrop(EncounterType type) {
        return switch (type) {
            case NORMAL -> 10 + (int)(Math.random() * 11); // 10-20
            case ELITE  -> 25 + (int)(Math.random() * 16); // 25-40
            case BOSS   -> 60 + (int)(Math.random() * 41); // 60-100
            default     -> 0;
        };
    }
}
