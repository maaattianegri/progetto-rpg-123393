package it.unicam.cs.mpgc.rpg123393.service;

import it.unicam.cs.mpgc.rpg123393.model.EncounterType;
import it.unicam.cs.mpgc.rpg123393.model.GameMap;
import it.unicam.cs.mpgc.rpg123393.model.MapNode;
import it.unicam.cs.mpgc.rpg123393.model.NodeType;

import java.util.List;
import java.util.Optional;

/**
 * Struttura mappa — trunk corto + bivio precoce verso 4 percorsi finali.
 *
 * TRUNK
 *   n00 BATTLE (Ingresso) → n01 BATTLE (Corridoio Oscuro) → [BIVIO]
 *
 * RAMO A — Normale (6 nodi + boss finale)
 *   nA1 BATTLE → nA2 SHOP → nA3 ELITE → nA4 REST → nA5 BATTLE → nA6 ELITE → BOSS_A (Negromante)
 *
 * RAMO B — Eroico (5 nodi + doppio boss)
 *   nB1 ELITE → nB2 BATTLE → nB3 SHOP → nB4 ELITE → nB5 ELITE → BOSS_B1 (Vampiro Lord) → BOSS_B2 (Drago Antico)
 *
 * RAMO C — Oscuro (2 nodi condivisi poi si biforca)
 *   nC1 EVENT (condizionale: Dracomante|Assassino|Cavaliere) → nC2 ELITE → [BIVIO C]
 *         ├─ RAMO C1 (Proibito):  nC3 EVENT → BOSS_C1 (Cuore dell'Abisso)
 *         └─ RAMO C2 (Abissale):  nC4 BATTLE → nC5 ELITE → BOSS_C2 (Re Ombra)
 *                                              └─ [solo Cavaliere] nHK0 VOID (Ingresso Abisso)
 *                                                       → nHK1 BATTLE → nHK2 BATTLE → nHK3 BATTLE
 *                                                       → nHK4 EVENT (Velo tra i Mondi)
 *                                                              ├─ [accetta] nHK_BOSS VOID_BOSS (Cavaliere Vacuo)
 *                                                              └─ [rifiuta] nBB2 (Drago Antico) ← ricollega Ramo B
 */
public class MapService {

    private final GameMap map;

    public MapService() {
        this.map = buildMap();
    }

    private GameMap buildMap() {
        GameMap gm = new GameMap();

        // --- TRUNK (2 nodi) ---
        MapNode n00 = node("n00", "Ingresso del Dungeon",
                "Le torce tremolano. Qualcosa si muove nel buio.", NodeType.BATTLE);
        MapNode n01 = node("n01", "Corridoio Oscuro",
                "Ombre si agitano tra le pareti umide del corridoio.", NodeType.BATTLE);

        // --- RAMO A: Normale ---
        MapNode nA1 = node("nA1", "Corridoio Infestato",
                "Scheletri ambulanti pattugliano il corridoio.", NodeType.BATTLE);
        MapNode nA2 = node("nA2", "Mercante Errante",
                "Un mercante ha allestito il suo banchetto.", NodeType.SHOP);
        MapNode nA3 = node("nA3", "Orco Berserker",
                "L'Orco ruggisce e si avventa su di te.", NodeType.ELITE);
        MapNode nA4 = node("nA4", "Fal\u00f2 Antico",
                "Le fiamme danzano sul pietrisco consumato dal tempo.", NodeType.REST);
        MapNode nA5 = node("nA5", "Guardiani della Soglia",
                "Due soldati non-morti proteggono il passaggio.", NodeType.BATTLE);
        MapNode nA6 = node("nA6", "Guardia del Negromante",
                "Un campione non-morto sbarra la porta del boss.", NodeType.ELITE);
        MapNode nAB = node("nAB", "Negromante",
                "Il Negromante evoca legioni di non-morti. Nessuno ha mai visto il suo volto.", NodeType.BOSS);

        // --- RAMO B: Eroico ---
        MapNode nB1 = node("nB1", "Sala del Sangue",
                "I muri trasudano un'oscurit\u00e0 antica.", NodeType.ELITE);
        MapNode nB2 = node("nB2", "Cripta dei Caduti",
                "I guerrieri qui sepolti si rifiutano di restare morti.", NodeType.BATTLE);
        MapNode nB3 = node("nB3", "Mercante Oscuro",
                "Vende oggetti proibiti a chi sopravvive fin qui.", NodeType.SHOP);
        MapNode nB4 = node("nB4", "Cavaliere Vampiro",
                "Un cavaliere caduto al servizio del buio.", NodeType.ELITE);
        MapNode nB5 = node("nB5", "Sentinella Cremisi",
                "L'ultima protezione prima del trono.", NodeType.ELITE);
        MapNode nBB1 = node("nBB1", "Vampiro Lord",
                "Il Signore della Notte ti attende sul suo trono di ossa.", NodeType.BOSS);
        MapNode nBB2 = node("nBB2", "Drago Antico",
                "Il Drago Antico scuote la terra con ogni passo.", NodeType.BOSS);

        // --- RAMO C: Oscuro (nC1 aperto anche al Cavaliere) ---
        MapNode nC1 = node("nC1", "Cripta Proibita",
                "Solo chi conosce l'oscurit\u00e0 pu\u00f2 entrare.", NodeType.EVENT, "Dracomante|Assassino|Cavaliere");
        MapNode nC2 = node("nC2", "Sentinella Abissale",
                "Una creatura di pura tenebra ti sfida.", NodeType.ELITE);

        // Ramo C1 — corto
        MapNode nC3 = node("nC3", "Rituale Proibito",
                "Un potere senza nome palpita oltre il velo del reale.", NodeType.EVENT);
        MapNode nCB1 = node("nCB1", "Cuore dell'Abisso",
                "L'entit\u00e0 primordiale si risveglia.", NodeType.BOSS);

        // Ramo C2 — lungo
        MapNode nC4 = node("nC4", "Torre degli Echi",
                "Le urla di chi ha fallito risuonano tra le mura.", NodeType.BATTLE);
        MapNode nC5 = node("nC5", "Custode delle Ombre",
                "Il guardiano del Re Ombra non concede passaggio.", NodeType.ELITE);
        MapNode nCB2 = node("nCB2", "Re Ombra",
                "Il signore segreto del dungeon emerge dall'oscurit\u00e0.", NodeType.BOSS);

        // --- RAMO HK: Easter egg Hollow Knight (solo Cavaliere) ---
        MapNode nHK0 = node("nHK0", "Ingresso dell'Abisso",
                "Il buio oltre la soglia è diverso da qualsiasi oscurit\u00e0 tu abbia mai visto. Pulsa.",
                NodeType.VOID, "Cavaliere");
        MapNode nHK1 = node("nHK1", "Frammenti del Vuoto",
                "Creature fatte di tenebra pura, senza occhi n\u00e9 voce. Ti attaccano in silenzio.",
                NodeType.BATTLE, "Cavaliere");
        MapNode nHK2 = node("nHK2", "Antro della Larva",
                "Larve dell'Abisso sciamano tra le fessure delle pietre. Sono pi\u00f9 pericolose di quanto sembrino.",
                NodeType.BATTLE, "Cavaliere");
        MapNode nHK3 = node("nHK3", "Guscio Vuoto",
                "Un guerriero antico, svuotato di ogni volont\u00e0. Combatte per istinto puro.",
                NodeType.BATTLE, "Cavaliere");
        MapNode nHK4 = node("nHK4", "Il Velo tra i Mondi",
                "Davanti a te galleggia qualcosa di oscuro e pulsante. Il Cuore di Vuoto ti chiama.",
                NodeType.EVENT, "Cavaliere");
        MapNode nHKB = node("nHKB", "Cavaliere Vacuo",
                "Un riflesso di te stesso, svuotato. Combatte con la tua stessa tecnica, ma senza esitazione.",
                NodeType.VOID_BOSS, "Cavaliere");

        // ---- Connessioni ----

        // Trunk
        n00.addNextNode("n01");
        // Bivio principale dopo n01
        n01.addNextNode("nA1");
        n01.addNextNode("nB1");
        n01.addNextNode("nC1");

        // Ramo A
        nA1.addNextNode("nA2");
        nA2.addNextNode("nA3");
        nA3.addNextNode("nA4");
        nA4.addNextNode("nA5");
        nA5.addNextNode("nA6");
        nA6.addNextNode("nAB");

        // Ramo B
        nB1.addNextNode("nB2");
        nB2.addNextNode("nB3");
        nB3.addNextNode("nB4");
        nB4.addNextNode("nB5");
        nB5.addNextNode("nBB1");
        nBB1.addNextNode("nBB2");

        // Ramo C — tronco comune
        nC1.addNextNode("nC2");
        // Bivio secondario
        nC2.addNextNode("nC3");
        nC2.addNextNode("nC4");
        // C1
        nC3.addNextNode("nCB1");
        // C2
        nC4.addNextNode("nC5");
        nC5.addNextNode("nCB2");
        // Biforcazione HK da nC5 (solo Cavaliere vede nHK0)
        nC5.addNextNode("nHK0");

        // Ramo HK
        nHK0.addNextNode("nHK1");
        nHK1.addNextNode("nHK2");
        nHK2.addNextNode("nHK3");
        nHK3.addNextNode("nHK4");
        // Biforcazione EVENT: accetta → boss segreto, rifiuta → Drago Antico
        nHK4.addNextNode("nHKB");
        nHK4.addNextNode("nBB2");

        // Aggiunta alla mappa
        for (MapNode n : List.of(
                n00, n01,
                nA1, nA2, nA3, nA4, nA5, nA6, nAB,
                nB1, nB2, nB3, nB4, nB5, nBB1, nBB2,
                nC1, nC2, nC3, nCB1, nC4, nC5, nCB2,
                nHK0, nHK1, nHK2, nHK3, nHK4, nHKB)) {
            gm.addNode(n);
        }

        gm.setCurrentNodeId("n00");
        n00.setVisited(true);
        return gm;
    }

    private static MapNode node(String id, String name, String desc, NodeType type) {
        return new MapNode(id, name, desc, type);
    }

    private static MapNode node(String id, String name, String desc, NodeType type, String requiredClass) {
        return new MapNode(id, name, desc, type, requiredClass);
    }

    // -------------------------------------------------------
    // Navigazione
    // -------------------------------------------------------

    public GameMap getMap() { return map; }

    public EncounterType currentEncounterType() {
        return map.getCurrentNode().map(MapNode::toEncounterType).orElse(EncounterType.NORMAL);
    }

    public Optional<MapNode> advance() {
        map.clearCurrentNode();
        return Optional.empty();
    }

    public boolean moveToNode(String nodeId) {
        map.clearCurrentNode();
        return map.moveTo(nodeId);
    }

    public boolean isCurrentNodeBoss() {
        return map.getCurrentNode().map(n ->
                n.getType() == NodeType.BOSS || n.getType() == NodeType.VOID_BOSS
        ).orElse(false);
    }

    public boolean isCurrentNodeLastBoss() {
        return map.getCurrentNode()
                .map(n -> (n.getType() == NodeType.BOSS || n.getType() == NodeType.VOID_BOSS)
                        && n.getNextNodeIds().isEmpty())
                .orElse(false);
    }

    public int completedShopCount() {
        return (int) map.getAllNodes().stream()
                .filter(n -> n.getType() == NodeType.SHOP && n.isCleared()).count();
    }

    public int shopRound() {
        return Math.max(1, completedShopCount() + 1);
    }

    public void restoreMap(String currentNodeId, List<String> clearedNodeIds) {
        if (currentNodeId != null) {
            map.setCurrentNodeId(currentNodeId);
            map.getNodeById(currentNodeId).ifPresent(n -> n.setVisited(true));
        }
        if (clearedNodeIds != null) {
            for (String id : clearedNodeIds)
                map.getNodeById(id).ifPresent(n -> { n.setVisited(true); n.setCleared(true); });
        }
    }
}
