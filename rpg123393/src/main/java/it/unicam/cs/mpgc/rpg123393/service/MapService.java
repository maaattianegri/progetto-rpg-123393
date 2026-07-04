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
 * Fase 2 — mappa a percorsi multipli:
 * Alcuni nodi hanno più successori, il player sceglie il percorso dalla mappa.
 *
 * Struttura:
 *
 *   n00 (BATTLE) -> n01 (BATTLE) -> n02 (BATTLE) -+-> n03a (ELITE)
 *                                                  +-> n03b (REST)
 *                                                  +-> n03c (EVENT)
 *                                                  |
 *                                       n03a/b/c --+--> n04 (SHOP)
 *                                                       |
 *                                                  n05 (ELITE) -> n06 (ELITE)
 *                                                       |
 *                                                  n07 (SHOP) -+-> n08a (ELITE)
 *                                                              +-> n08b (REST)
 *                                                              |
 *                                              n08a/b --------+--> n09 (SHOP)
 *                                                                  |
 *                                                            n10 (BOSS) -> n11 (BOSS)
 */
public class MapService {

    private final GameMap map;

    public MapService() {
        this.map = buildBranchingMap();
    }

    // -------------------------------------------------------
    // Generazione mappa (Fase 2 — bivi reali)
    // -------------------------------------------------------

    private GameMap buildBranchingMap() {
        GameMap gameMap = new GameMap();

        // --- Corridoio iniziale (lineare) ---
        MapNode n00 = new MapNode("n00", "Ingresso",         "Inizio della run.",                  NodeType.BATTLE);
        MapNode n01 = new MapNode("n01", "Goblin",           "Un Goblin ti sbarra la strada.",      NodeType.BATTLE);
        MapNode n02 = new MapNode("n02", "Ratto Gigante",    "Un Ratto Gigante ti attacca.",        NodeType.BATTLE);

        // --- Primo bivio (3 scelte dopo n02) ---
        MapNode n03a = new MapNode("n03a", "Orco Berserker", "L'Orco Berserker ruggisce.",          NodeType.ELITE);
        MapNode n03b = new MapNode("n03b", "Fal\u00f2",           "Un fuoco crepitante ti aspetta.",     NodeType.REST);
        MapNode n03c = new MapNode("n03c", "Presenza Oscura","Qualcosa nell'ombra ti osserva.",    NodeType.EVENT);

        // --- Convergenza al primo shop ---
        MapNode n04  = new MapNode("n04",  "Mercante",        "Un mercante ti offre i suoi prodotti.", NodeType.SHOP);

        // --- Tratto centrale (lineare) ---
        MapNode n05  = new MapNode("n05",  "Scheletro Arcano","Lo Scheletro Arcano ti fissa.",       NodeType.ELITE);
        MapNode n06  = new MapNode("n06",  "Troll Rigenerante","Il Troll si rigenera!",              NodeType.ELITE);
        MapNode n07  = new MapNode("n07",  "Mercante",        "Il mercante ha nuova merce.",          NodeType.SHOP);

        // --- Secondo bivio (2 scelte dopo n07) ---
        MapNode n08a = new MapNode("n08a", "Banshee",         "La Banshee urla nel buio.",           NodeType.ELITE);
        MapNode n08b = new MapNode("n08b", "Rifugio",         "Un angolo tranquillo per riposare.",  NodeType.REST);

        // --- Convergenza al terzo shop ---
        MapNode n09  = new MapNode("n09",  "Mercante",        "Ultima chance prima del boss.",       NodeType.SHOP);

        // --- Boss finali ---
        MapNode n10  = new MapNode("n10",  "Negromante",      "Il Negromante evoca non-morti.",      NodeType.BOSS);
        MapNode n11  = new MapNode("n11",  "Drago Antico",    "Il Drago Antico scuote la terra.",    NodeType.BOSS);

        // ---- Connessioni ----

        // Corridoio iniziale
        n00.addNextNode("n01");
        n01.addNextNode("n02");

        // Primo bivio
        n02.addNextNode("n03a");
        n02.addNextNode("n03b");
        n02.addNextNode("n03c");

        // Convergenza al primo shop
        n03a.addNextNode("n04");
        n03b.addNextNode("n04");
        n03c.addNextNode("n04");

        // Tratto centrale
        n04.addNextNode("n05");
        n05.addNextNode("n06");
        n06.addNextNode("n07");

        // Secondo bivio
        n07.addNextNode("n08a");
        n07.addNextNode("n08b");

        // Convergenza al terzo shop
        n08a.addNextNode("n09");
        n08b.addNextNode("n09");

        // Boss
        n09.addNextNode("n10");
        n10.addNextNode("n11");
        // n11 non ha successori — fine della run

        // ---- Aggiungi tutti i nodi ----
        for (MapNode n : List.of(n00, n01, n02, n03a, n03b, n03c,
                                  n04, n05, n06, n07, n08a, n08b,
                                  n09, n10, n11)) {
            gameMap.addNode(n);
        }

        // Posiziona il giocatore al nodo iniziale
        gameMap.setCurrentNodeId("n00");
        n00.setVisited(true);

        return gameMap;
    }

    // -------------------------------------------------------
    // Navigazione
    // -------------------------------------------------------

    public GameMap getMap() { return map; }

    /**
     * Tipo di incontro del nodo corrente.
     */
    public EncounterType currentEncounterType() {
        return map.getCurrentNode()
                .map(MapNode::toEncounterType)
                .orElse(EncounterType.NORMAL);
    }

    /**
     * Marca il nodo corrente come cleared (incontro completato).
     * NON sposta il cursore: la navigazione al nodo successivo è sempre
     * delegata all'utente tramite click sulla mappa (moveToNode).
     * Questo vale sia per tratti lineari che per bivi.
     */
    public Optional<MapNode> advance() {
        map.clearCurrentNode();
        return Optional.empty();
    }

    /**
     * Sposta il giocatore verso un nodo specifico scelto dalla mappa.
     *
     * @return true se lo spostamento è valido (il nodo è tra i successori del corrente).
     */
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

    /**
     * Numero di shop completati. Usato da ShopPool per scalare i prezzi.
     */
    public int completedShopCount() {
        return (int) map.getAllNodes().stream()
                .filter(n -> n.getType() == NodeType.SHOP && n.isCleared())
                .count();
    }

    public int shopRound() {
        return Math.max(1, completedShopCount() + 1);
    }

    /** Ripristina la mappa da uno stato salvato. */
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
