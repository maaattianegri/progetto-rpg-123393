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
 * Fase 1 — mappa lineare mascherata:
 * La struttura tecnica è un grafo, ma la sequenza è fissa e identica
 * a quella del RunManager originale. Questo garantisce zero regressioni.
 *
 * Sequenza Fase 1:
 *   ingresso -> BATTLE -> BATTLE -> SHOP -> ELITE -> ELITE -> SHOP
 *            -> ELITE -> ELITE -> SHOP -> BOSS -> BOSS
 */
public class MapService {

    private GameMap map;

    public MapService() {
        this.map = buildLinearMap();
    }

    // -------------------------------------------------------
    // Generazione mappa
    // -------------------------------------------------------

    /**
     * Costruisce la mappa lineare mascherata (Fase 1).
     * Ogni nodo ha esattamente un successore, replicando la sequenza del RunManager.
     */
    private GameMap buildLinearMap() {
        GameMap gameMap = new GameMap();

        MapNode ingresso  = new MapNode("n00", "Ingresso",          "Inizio della run.",              NodeType.BATTLE);
        MapNode battle1   = new MapNode("n01", "Goblin",            "Un Goblin ti sbarra la strada.", NodeType.BATTLE);
        MapNode battle2   = new MapNode("n02", "Ratto Gigante",     "Un Ratto Gigante ti attacca.",   NodeType.BATTLE);
        MapNode shop1     = new MapNode("n03", "Mercante",          "Un mercante ti offre i suoi prodotti.", NodeType.SHOP);
        MapNode elite1    = new MapNode("n04", "Orco Berserker",    "L'Orco Berserker ruggisce.",     NodeType.ELITE);
        MapNode elite2    = new MapNode("n05", "Scheletro Arcano",  "Lo Scheletro Arcano ti fissa.",  NodeType.ELITE);
        MapNode shop2     = new MapNode("n06", "Mercante",          "Il mercante ha nuova merce.",    NodeType.SHOP);
        MapNode elite3    = new MapNode("n07", "Troll Rigenerante", "Il Troll si rigenera!",          NodeType.ELITE);
        MapNode elite4    = new MapNode("n08", "Banshee",           "La Banshee urla nel buio.",      NodeType.ELITE);
        MapNode shop3     = new MapNode("n09", "Mercante",          "Ultima chance prima del boss.",  NodeType.SHOP);
        MapNode boss1     = new MapNode("n10", "Negromante",        "Il Negromante evoca non-morti.", NodeType.BOSS);
        MapNode boss2     = new MapNode("n11", "Drago Antico",      "Il Drago Antico scuote la terra.", NodeType.BOSS);

        ingresso.addNextNode("n01");
        battle1.addNextNode("n02");
        battle2.addNextNode("n03");
        shop1.addNextNode("n04");
        elite1.addNextNode("n05");
        elite2.addNextNode("n06");
        shop2.addNextNode("n07");
        elite3.addNextNode("n08");
        elite4.addNextNode("n09");
        shop3.addNextNode("n10");
        boss1.addNextNode("n11");

        gameMap.addNode(ingresso);
        gameMap.addNode(battle1);
        gameMap.addNode(battle2);
        gameMap.addNode(shop1);
        gameMap.addNode(elite1);
        gameMap.addNode(elite2);
        gameMap.addNode(shop2);
        gameMap.addNode(elite3);
        gameMap.addNode(elite4);
        gameMap.addNode(shop3);
        gameMap.addNode(boss1);
        gameMap.addNode(boss2);

        // Posiziona il giocatore al nodo iniziale
        gameMap.setCurrentNodeId("n00");
        ingresso.setVisited(true);

        return gameMap;
    }

    // -------------------------------------------------------
    // Navigazione
    // -------------------------------------------------------

    public GameMap getMap() {
        return map;
    }

    /**
     * Restituisce il tipo di incontro del nodo corrente,
     * compatibile con la logica esistente di GameService/BattleService.
     */
    public EncounterType currentEncounterType() {
        return map.getCurrentNode()
                .map(MapNode::toEncounterType)
                .orElse(EncounterType.NORMAL);
    }

    /**
     * Avanza verso il prossimo nodo (in Fase 1 ce n'è sempre uno solo).
     * Marca il nodo corrente come cleared prima di spostarsi.
     *
     * @return il nodo verso cui ci si è spostati, oppure empty se non ci sono successori.
     */
    public Optional<MapNode> advance() {
        map.clearCurrentNode();
        List<MapNode> reachable = map.getReachableNodes();
        if (reachable.isEmpty()) return Optional.empty();
        // Fase 1: prende sempre il primo (unico) successore
        MapNode next = reachable.get(0);
        map.moveTo(next.getId());
        return Optional.of(next);
    }

    /**
     * Sposta il giocatore verso un nodo specifico (per Fase 2 con bivi).
     *
     * @return true se lo spostamento è valido.
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
        Optional<MapNode> current = map.getCurrentNode();
        if (current.isEmpty()) return false;
        MapNode node = current.get();
        return node.getType() == NodeType.BOSS && node.getNextNodeIds().isEmpty();
    }

    /**
     * Numero di shop completati fino ad ora. Usato da ShopPool per scalare i prezzi.
     */
    public int completedShopCount() {
        return (int) map.getAllNodes().stream()
                .filter(n -> n.getType() == NodeType.SHOP && n.isCleared())
                .count();
    }

    public int shopRound() {
        int count = completedShopCount();
        return Math.max(1, count + 1);
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
