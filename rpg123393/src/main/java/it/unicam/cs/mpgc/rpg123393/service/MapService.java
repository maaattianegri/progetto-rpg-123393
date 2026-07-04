package it.unicam.cs.mpgc.rpg123393.service;

import it.unicam.cs.mpgc.rpg123393.model.EncounterType;
import it.unicam.cs.mpgc.rpg123393.model.GameMap;
import it.unicam.cs.mpgc.rpg123393.model.MapNode;
import it.unicam.cs.mpgc.rpg123393.model.NodeType;

import java.util.List;
import java.util.Optional;

/**
 * Struttura mappa ampliata con sviluppo verticale.
 *
 * ATTO 1
 *   n00 → n01 → n02 → [n03a ELITE | n03b REST | n03c EVENT] → n04 SHOP
 *
 * ATTO 2
 *   n04 → n05 BATTLE → n06 ELITE → [n07a ELITE | n07b EVENT | n07c REST] → n08 SHOP
 *                                                                               |
 *                                                          [n09a ELITE | n09b EVENT | n09c REST] → n10 SHOP
 *
 * ATTO 3 — tre percorsi separati (non convergenti)
 *   Percorso A (Normale):  n10 → n11a BATTLE → n12a ELITE → n13a BOSS (Negromante)  → n14a BOSS (Lich)
 *   Percorso B (Eroico):   n10 → n11b ELITE  → n12b ELITE → n13b BOSS (Vampiro Lord) → n14b BOSS (Drago Antico)
 *   Percorso C (Oscuro):   n10 → n11c EVENT  → n12c ELITE → n13c BOSS (Cuore Abisso) → n14c BOSS (Re Ombra)
 *
 * Nodi condizionali (requiredClass):
 *   n09c REST  → solo Guerriero / Paladino  ("Santuario del Guerriero")
 *   n11c EVENT → solo Dracomante / Assassino ("Cripta Proibita")
 */
public class MapService {

    private final GameMap map;

    public MapService() {
        this.map = buildExpandedMap();
    }

    private GameMap buildExpandedMap() {
        GameMap gm = new GameMap();

        // --- Atto 1 ---
        MapNode n00  = node("n00",  "Ingresso",           "Inizio della run.",                      NodeType.BATTLE);
        MapNode n01  = node("n01",  "Goblin",             "Un Goblin ti sbarra la strada.",          NodeType.BATTLE);
        MapNode n02  = node("n02",  "Ratto Gigante",      "Un Ratto Gigante ti attacca.",            NodeType.BATTLE);
        MapNode n03a = node("n03a", "Orco Berserker",     "L'Orco Berserker ruggisce.",              NodeType.ELITE);
        MapNode n03b = node("n03b", "Fal\u00f2",              "Un fuoco crepitante ti aspetta.",         NodeType.REST);
        MapNode n03c = node("n03c", "Presenza Oscura",    "Qualcosa nell'ombra ti osserva.",        NodeType.EVENT);
        MapNode n04  = node("n04",  "Mercante",           "Un mercante ti offre i suoi prodotti.",   NodeType.SHOP);

        // --- Atto 2 — primo segmento ---
        MapNode n05  = node("n05",  "Scheletro Arcano",   "Lo Scheletro Arcano ti fissa.",           NodeType.BATTLE);
        MapNode n06  = node("n06",  "Troll Rigenerante",  "Il Troll si rigenera!",                  NodeType.ELITE);
        MapNode n07a = node("n07a", "Banshee",            "La Banshee urla nel buio.",               NodeType.ELITE);
        MapNode n07b = node("n07b", "Cappella Infranta",  "Un luogo sospeso tra quiete e rovina.",  NodeType.EVENT);
        MapNode n07c = node("n07c", "Rifugio",            "Un angolo tranquillo per riposare.",      NodeType.REST);
        MapNode n08  = node("n08",  "Mercante",           "Il mercante ha nuova merce.",              NodeType.SHOP);

        // --- Atto 2 — secondo segmento (secondo bivio) ---
        MapNode n09a = node("n09a", "Mastino d'Ossa",     "Il guardiano ringhia tra le tombe.",      NodeType.ELITE);
        MapNode n09b = node("n09b", "Voce nell'Ombra",    "Una voce ti sussurra dal buio.",          NodeType.EVENT);
        // Nodo condizionale: solo Guerriero o Paladino
        MapNode n09c = node("n09c", "Santuario del Guerriero", "Un antico altare riconosce il tuo valore.", NodeType.REST, "Guerriero|Paladino");
        MapNode n10  = node("n10",  "Mercato Nero",       "Ultima occasione per prepararti.",        NodeType.SHOP);

        // --- Atto 3 — Percorso A: Normale ---
        MapNode n11a = node("n11a", "Corridoio dei Morti", "Scheletri ti sbarrano il passo.",        NodeType.BATTLE);
        MapNode n12a = node("n12a", "Guardia del Negromante", "Un campione non-morto ti sfida.",     NodeType.ELITE);
        MapNode n13a = node("n13a", "Negromante",         "Il Negromante evoca non-morti.",          NodeType.BOSS);
        MapNode n14a = node("n14a", "Lich",               "Il Lich custodisce la soglia finale.",    NodeType.BOSS);

        // --- Atto 3 — Percorso B: Eroico ---
        MapNode n11b = node("n11b", "Sala del Sangue",    "I muri trasudano oscurit\u00e0.",              NodeType.ELITE);
        MapNode n12b = node("n12b", "Cavaliere Vampiro",  "Un cavaliere caduto al servizio del buio.", NodeType.ELITE);
        MapNode n13b = node("n13b", "Vampiro Lord",       "Un signore della notte ti attende.",      NodeType.BOSS);
        MapNode n14b = node("n14b", "Drago Antico",       "Il Drago Antico scuote la terra.",        NodeType.BOSS);

        // --- Atto 3 — Percorso C: Oscuro (n11c condizionale: Dracomante o Assassino) ---
        MapNode n11c = node("n11c", "Cripta Proibita",    "Solo chi conosce l'oscurit\u00e0 pu\u00f2 entrare.", NodeType.EVENT, "Dracomante|Assassino");
        MapNode n12c = node("n12c", "Sentinella Abissale", "Una creatura di pura tenebra ti sfida.", NodeType.ELITE);
        MapNode n13c = node("n13c", "Cuore dell'Abisso",  "Un potere proibito palpita oltre il velo.", NodeType.BOSS);
        MapNode n14c = node("n14c", "Re Ombra",           "Il signore segreto del dungeon emerge.",  NodeType.BOSS);

        // ---- Connessioni ----
        n00.addNextNode("n01"); n01.addNextNode("n02");
        n02.addNextNode("n03a"); n02.addNextNode("n03b"); n02.addNextNode("n03c");
        n03a.addNextNode("n04"); n03b.addNextNode("n04"); n03c.addNextNode("n04");

        n04.addNextNode("n05"); n05.addNextNode("n06");
        n06.addNextNode("n07a"); n06.addNextNode("n07b"); n06.addNextNode("n07c");
        n07a.addNextNode("n08"); n07b.addNextNode("n08"); n07c.addNextNode("n08");

        n08.addNextNode("n09a"); n08.addNextNode("n09b"); n08.addNextNode("n09c");
        n09a.addNextNode("n10"); n09b.addNextNode("n10"); n09c.addNextNode("n10");

        n10.addNextNode("n11a"); n10.addNextNode("n11b"); n10.addNextNode("n11c");

        n11a.addNextNode("n12a"); n12a.addNextNode("n13a"); n13a.addNextNode("n14a");
        n11b.addNextNode("n12b"); n12b.addNextNode("n13b"); n13b.addNextNode("n14b");
        n11c.addNextNode("n12c"); n12c.addNextNode("n13c"); n13c.addNextNode("n14c");

        for (MapNode n : List.of(
                n00, n01, n02, n03a, n03b, n03c, n04,
                n05, n06, n07a, n07b, n07c, n08,
                n09a, n09b, n09c, n10,
                n11a, n12a, n13a, n14a,
                n11b, n12b, n13b, n14b,
                n11c, n12c, n13c, n14c)) {
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
        return map.getCurrentNode().map(n -> n.getType() == NodeType.BOSS).orElse(false);
    }

    public boolean isCurrentNodeLastBoss() {
        return map.getCurrentNode()
                .map(n -> n.getType() == NodeType.BOSS && n.getNextNodeIds().isEmpty())
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
