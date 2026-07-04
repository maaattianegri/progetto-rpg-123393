package it.unicam.cs.mpgc.rpg123393.service;

import it.unicam.cs.mpgc.rpg123393.model.EncounterType;
import it.unicam.cs.mpgc.rpg123393.model.GameMap;
import it.unicam.cs.mpgc.rpg123393.model.MapNode;
import it.unicam.cs.mpgc.rpg123393.model.NodeType;

import java.util.List;
import java.util.Optional;

/**
 * Gestisce la generazione e la navigazione della mappa a nodi.
 *
 * Struttura ampliata:
 * - Atto 1: corridoio iniziale + primo bivio a 3 rami
 * - Atto 2: tratto centrale + doppio bivio a 3 rami
 * - Atto 3: tre rami finali separati con boss multipli
 */
public class MapService {

    private final GameMap map;

    public MapService() {
        this.map = buildExpandedMap();
    }

    // -------------------------------------------------------
    // Generazione mappa ampliata
    // -------------------------------------------------------

    private GameMap buildExpandedMap() {
        GameMap gameMap = new GameMap();

        // --- Atto 1 ---
        MapNode n00 = new MapNode("n00", "Ingresso", "Inizio della run.", NodeType.BATTLE);
        MapNode n01 = new MapNode("n01", "Goblin", "Un Goblin ti sbarra la strada.", NodeType.BATTLE);
        MapNode n02 = new MapNode("n02", "Ratto Gigante", "Un Ratto Gigante ti attacca.", NodeType.BATTLE);

        MapNode n03a = new MapNode("n03a", "Orco Berserker", "L'Orco Berserker ruggisce.", NodeType.ELITE);
        MapNode n03b = new MapNode("n03b", "Falò", "Un fuoco crepitante ti aspetta.", NodeType.REST);
        MapNode n03c = new MapNode("n03c", "Presenza Oscura", "Qualcosa nell'ombra ti osserva.", NodeType.EVENT);

        MapNode n04 = new MapNode("n04", "Mercante", "Un mercante ti offre i suoi prodotti.", NodeType.SHOP);

        // --- Atto 2 ---
        MapNode n05 = new MapNode("n05", "Scheletro Arcano", "Lo Scheletro Arcano ti fissa.", NodeType.BATTLE);
        MapNode n06 = new MapNode("n06", "Troll Rigenerante", "Il Troll si rigenera!", NodeType.ELITE);

        MapNode n07a = new MapNode("n07a", "Banshee", "La Banshee urla nel buio.", NodeType.ELITE);
        MapNode n07b = new MapNode("n07b", "Cappella Infranta", "Un luogo sospeso tra quiete e rovina.", NodeType.EVENT);
        MapNode n07c = new MapNode("n07c", "Rifugio", "Un angolo tranquillo per riposare.", NodeType.REST);

        MapNode n08 = new MapNode("n08", "Mercante", "Il mercante ha nuova merce.", NodeType.SHOP);

        MapNode n09a = new MapNode("n09a", "Mastino d'Ossa", "Il guardiano ringhia tra le tombe.", NodeType.ELITE);
        MapNode n09b = new MapNode("n09b", "Voce nell'Ombra", "Una voce ti sussurra dal buio.", NodeType.EVENT);
        MapNode n09c = new MapNode("n09c", "Sentiero Sepolto", "Un passaggio segreto sembra aprirsi oltre la roccia.", NodeType.EVENT);

        MapNode n10 = new MapNode("n10", "Mercato Nero", "Ultima occasione per prepararti.", NodeType.SHOP);

        // --- Atto 3: rami finali separati ---
        MapNode n11a = new MapNode("n11a", "Negromante", "Il Negromante evoca non-morti.", NodeType.BOSS);
        MapNode n11b = new MapNode("n11b", "Vampiro Lord", "Un signore della notte ti attende.", NodeType.BOSS);
        MapNode n11c = new MapNode("n11c", "Cuore dell'Abisso", "Un potere proibito palpita oltre il velo.", NodeType.BOSS);

        MapNode n12a = new MapNode("n12a", "Lich", "Il Lich custodisce la soglia finale.", NodeType.BOSS);
        MapNode n12b = new MapNode("n12b", "Drago Antico", "Il Drago Antico scuote la terra.", NodeType.BOSS);
        MapNode n12c = new MapNode("n12c", "Re Ombra", "Il signore segreto del dungeon emerge dal nulla.", NodeType.BOSS);

        // ---- Connessioni ----
        n00.addNextNode("n01");
        n01.addNextNode("n02");

        n02.addNextNode("n03a");
        n02.addNextNode("n03b");
        n02.addNextNode("n03c");

        n03a.addNextNode("n04");
        n03b.addNextNode("n04");
        n03c.addNextNode("n04");

        n04.addNextNode("n05");
        n05.addNextNode("n06");

        n06.addNextNode("n07a");
        n06.addNextNode("n07b");
        n06.addNextNode("n07c");

        n07a.addNextNode("n08");
        n07b.addNextNode("n08");
        n07c.addNextNode("n08");

        n08.addNextNode("n09a");
        n08.addNextNode("n09b");
        n08.addNextNode("n09c");

        n09a.addNextNode("n10");
        n09b.addNextNode("n10");
        n09c.addNextNode("n10");

        n10.addNextNode("n11a");
        n10.addNextNode("n11b");
        n10.addNextNode("n11c");

        n11a.addNextNode("n12a");
        n11b.addNextNode("n12b");
        n11c.addNextNode("n12c");

        for (MapNode n : List.of(
                n00, n01, n02,
                n03a, n03b, n03c,
                n04,
                n05, n06,
                n07a, n07b, n07c,
                n08,
                n09a, n09b, n09c,
                n10,
                n11a, n11b, n11c,
                n12a, n12b, n12c)) {
            gameMap.addNode(n);
        }

        gameMap.setCurrentNodeId("n00");
        n00.setVisited(true);
        return gameMap;
    }

    // -------------------------------------------------------
    // Navigazione
    // -------------------------------------------------------

    public GameMap getMap() { return map; }

    public EncounterType currentEncounterType() {
        return map.getCurrentNode()
                .map(MapNode::toEncounterType)
                .orElse(EncounterType.NORMAL);
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
        return map.getCurrentNode()
                .map(n -> n.getType() == NodeType.BOSS)
                .orElse(false);
    }

    public boolean isCurrentNodeLastBoss() {
        return map.getCurrentNode()
                .map(n -> n.getType() == NodeType.BOSS && n.getNextNodeIds().isEmpty())
                .orElse(false);
    }

    public int completedShopCount() {
        return (int) map.getAllNodes().stream()
                .filter(n -> n.getType() == NodeType.SHOP && n.isCleared())
                .count();
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
            for (String id : clearedNodeIds) {
                map.getNodeById(id).ifPresent(n -> {
                    n.setVisited(true);
                    n.setCleared(true);
                });
            }
        }
    }
}
