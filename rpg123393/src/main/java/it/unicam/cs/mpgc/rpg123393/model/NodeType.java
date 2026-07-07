package it.unicam.cs.mpgc.rpg123393.model;

/**
 * Tipi di nodo disponibili nella mappa a nodi.
 * Separato da EncounterType per non alterare la logica esistente.
 */
public enum NodeType {
    BATTLE,    // combattimento normale
    ELITE,     // nemico intermedio
    SHOP,      // negozio
    REST,      // riposo / cura
    EVENT,     // evento casuale
    BOSS,      // boss finale
    VOID,      // nodo speciale Hollow Knight — ingresso del Vuoto (solo Cavaliere)
    VOID_BOSS  // boss segreto Cavaliere Vacuo — accessibile solo con voidHeartObtained
}
