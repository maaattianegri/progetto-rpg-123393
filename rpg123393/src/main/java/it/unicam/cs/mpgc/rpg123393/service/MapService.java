package it.unicam.cs.mpgc.rpg123393.service;

import it.unicam.cs.mpgc.rpg123393.model.EncounterType;
import it.unicam.cs.mpgc.rpg123393.model.GameMap;
import it.unicam.cs.mpgc.rpg123393.model.MapNode;
import it.unicam.cs.mpgc.rpg123393.model.NodeType;

import java.util.List;
import java.util.Optional;

/**
 * Struttura mappa.
 *
 * TRUNK
 *   n00 BATTLE → n01 BATTLE → [BIVIO]
 *
 * RAMO A — Normale
 *   nA1 BATTLE → nA2 SHOP → nA3 ELITE → nA4 REST → nA5 BATTLE → nA6 ELITE → BOSS_A
 *
 * RAMO B — Eroico
 *   nB1 ELITE → nB2 BATTLE → nB3 SHOP → nB4 ELITE → nB5 ELITE → BOSS_B1 → BOSS_B2
 *
 * RAMO C — Oscuro
 *   nC1 EVENT (Dracomante|Assassino|Cavaliere) → nC2 ELITE → [BIVIO C]
 *     ├─ C1: nC3 EVENT → BOSS_C1
 *     └─ C2: nC4 BATTLE → nC5 ELITE → BOSS_C2
 *                          └─ [solo Cavaliere] nHK0 VOID (Ingresso Abisso)
 *                                → nHK1 BATTLE (Frammenti del Vuoto — nemico: Maskfly)
 *                                → nHK4 EVENT  (Il Velo tra i Mondi)
 *                                      ├─ [accetta] nHKB VOID_BOSS (Cavaliere Vacuo)
 *                                      └─ [rifiuta] nBB2 (Drago Antico)
 */
public class MapService {

    private final GameMap map;

    public MapService() {
        this.map = buildMap();
    }

    private GameMap buildMap() {
        GameMap gm = new GameMap();

        // --- TRUNK ---
        MapNode n00 = node("n00", "Ingresso del Dungeon",
                "Le torce tremolano. Qualcosa si muove nel buio.", NodeType.BATTLE);
        MapNode n01 = node("n01", "Corridoio Oscuro",
                "Ombre si agitano tra le pareti umide del corridoio.", NodeType.BATTLE);

        // --- RAMO A ---
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
                "Il Negromante evoca legioni di non-morti.", NodeType.BOSS);

        // --- RAMO B ---
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

        // --- RAMO C ---
        MapNode nC1 = node("nC1", "Cripta Proibita",
                "Solo chi conosce l'oscurit\u00e0 pu\u00f2 entrare.", NodeType.EVENT, "Dracomante|Assassino|Cavaliere");
        MapNode nC2 = node("nC2", "Sentinella Abissale",
                "Una creatura di pura tenebra ti sfida.", NodeType.ELITE);
        MapNode nC3 = node("nC3", "Rituale Proibito",
                "Un potere senza nome palpita oltre il velo del reale.", NodeType.EVENT);
        MapNode nCB1 = node("nCB1", "Cuore dell'Abisso",
                "L'entit\u00e0 primordiale si risveglia.", NodeType.BOSS);
        MapNode nC4 = node("nC4", "Torre degli Echi",
                "Le urla di chi ha fallito risuonano tra le mura.", NodeType.BATTLE);
        MapNode nC5 = node("nC5", "Custode delle Ombre",
                "Il guardiano del Re Ombra non concede passaggio.", NodeType.ELITE);
        MapNode nCB2 = node("nCB2", "Re Ombra",
                "Il signore segreto del dungeon emerge dall'oscurit\u00e0.", NodeType.BOSS);

        // --- RAMO HK (accorciato: nHK0 → nHK1 → nHK4 → nHKB / nBB2) ---
        MapNode nHK0 = node("nHK0", "Ingresso dell'Abisso",
                "Il buio oltre la soglia pulsa di vita propria. Non \u00e8 buio normale.",
                NodeType.VOID, "Cavaliere");
        MapNode nHK1 = node("nHK1", "Frammenti del Vuoto",
                "Creature fatte di tenebra pura, senza occhi n\u00e9 voce. Ti attaccano in silenzio.",
                NodeType.BATTLE, "Cavaliere");
        MapNode nHK4 = node("nHK4", "Il Velo tra i Mondi",
                "Davanti a te galleggia qualcosa di oscuro e pulsante. Il Cuore di Vuoto ti chiama.",
                NodeType.EVENT, "Cavaliere");
        MapNode nHKB = node("nHKB", "Cavaliere Vacuo",
                "Un riflesso di te stesso, svuotato. Combatte con la tua stessa tecnica, senza esitazione.",
                NodeType.VOID_BOSS, "Cavaliere");

        // ---- Connessioni ----
        n00.addNextNode("n01");
        n01.addNextNode("nA1");
        n01.addNextNode("nB1");
        n01.addNextNode("nC1");

        nA1.addNextNode("nA2"); nA2.addNextNode("nA3"); nA3.addNextNode("nA4");
        nA4.addNextNode("nA5"); nA5.addNextNode("nA6"); nA6.addNextNode("nAB");

        nB1.addNextNode("nB2"); nB2.addNextNode("nB3"); nB3.addNextNode("nB4");
        nB4.addNextNode("nB5"); nB5.addNextNode("nBB1"); nBB1.addNextNode("nBB2");

        nC1.addNextNode("nC2");
        nC2.addNextNode("nC3"); nC2.addNextNode("nC4");
        nC3.addNextNode("nCB1");
        nC4.addNextNode("nC5");
        nC5.addNextNode("nCB2");
        nC5.addNextNode("nHK0");   // solo Cavaliere vede nHK0

        nHK0.addNextNode("nHK1");
        nHK1.addNextNode("nHK4");
        nHK4.addNextNode("nHKB");  // accetta → boss segreto
        nHK4.addNextNode("nBB2");  // rifiuta → Drago Antico

        for (MapNode n : List.of(
                n00, n01,
                nA1, nA2, nA3, nA4, nA5, nA6, nAB,
                nB1, nB2, nB3, nB4, nB5, nBB1, nBB2,
                nC1, nC2, nC3, nCB1, nC4, nC5, nCB2,
                nHK0, nHK1, nHK4, nHKB)) {
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
