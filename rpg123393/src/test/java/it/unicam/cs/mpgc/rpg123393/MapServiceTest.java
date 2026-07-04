package it.unicam.cs.mpgc.rpg123393;

import it.unicam.cs.mpgc.rpg123393.model.EncounterType;
import it.unicam.cs.mpgc.rpg123393.model.MapNode;
import it.unicam.cs.mpgc.rpg123393.model.NodeType;
import it.unicam.cs.mpgc.rpg123393.service.MapService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test di unità per MapService (Fase 2 — mappa a bivi).
 *
 * Struttura mappa:
 *   n00 -> n01 -> n02 -+-> n03a (ELITE)
 *                      +-> n03b (REST)
 *                      +-> n03c (EVENT)
 *                      |
 *              n03a/b/c --> n04 (SHOP) -> n05 -> n06 -> n07 -+-> n08a (ELITE)
 *                                                             +-> n08b (REST)
 *                                                   n08a/b --> n09 -> n10 -> n11
 */
class MapServiceTest {

    private MapService mapService;

    @BeforeEach
    void setUp() {
        mapService = new MapService();
    }

    // -----------------------------------------------------------
    // Struttura della mappa
    // -----------------------------------------------------------

    @Test
    @DisplayName("La mappa deve contenere esattamente 15 nodi")
    void mapHasFifteenNodes() {
        assertEquals(15, mapService.getMap().getAllNodes().size());
    }

    @Test
    @DisplayName("Il nodo iniziale è n00 ed è già visited")
    void initialNodeIsN00AndVisited() {
        Optional<MapNode> current = mapService.getMap().getCurrentNode();
        assertTrue(current.isPresent());
        assertEquals("n00", current.get().getId());
        assertTrue(current.get().isVisited());
    }

    @Test
    @DisplayName("Il nodo n11 (boss finale) non ha successori")
    void lastBossHasNoSuccessors() {
        assertTrue(nodeById("n11").getNextNodeIds().isEmpty());
    }

    @Test
    @DisplayName("I nodi hanno il NodeType corretto")
    void nodeTypesAreCorrect() {
        assertEquals(NodeType.SHOP,   nodeById("n04").getType());
        assertEquals(NodeType.ELITE,  nodeById("n03a").getType());
        assertEquals(NodeType.REST,   nodeById("n03b").getType());
        assertEquals(NodeType.EVENT,  nodeById("n03c").getType());
        assertEquals(NodeType.BOSS,   nodeById("n10").getType());
        assertEquals(NodeType.BOSS,   nodeById("n11").getType());
        assertEquals(NodeType.BATTLE, nodeById("n01").getType());
    }

    // -----------------------------------------------------------
    // Tipo di incontro
    // -----------------------------------------------------------

    @Test
    @DisplayName("currentEncounterType() restituisce il tipo corretto su n00")
    void currentEncounterTypeOnStart() {
        EncounterType type = mapService.currentEncounterType();
        assertNotNull(type);
    }

    // -----------------------------------------------------------
    // Primo bivio — n02 ha 3 successori
    // -----------------------------------------------------------

    @Test
    @DisplayName("n02 ha esattamente 3 successori (bivio)")
    void n02HasThreeSuccessors() {
        assertEquals(3, nodeById("n02").getNextNodeIds().size());
    }

    @Test
    @DisplayName("I successori di n02 sono n03a, n03b, n03c")
    void n02SuccessorsAreCorrect() {
        List<String> nexts = nodeById("n02").getNextNodeIds();
        assertTrue(nexts.contains("n03a"));
        assertTrue(nexts.contains("n03b"));
        assertTrue(nexts.contains("n03c"));
    }

    @Test
    @DisplayName("advance() su n02 restituisce empty (bivio — il player deve scegliere)")
    void advanceOnBivioReturnsEmpty() {
        // Sposta il cursore su n02
        mapService.getMap().setCurrentNodeId("n02");
        Optional<MapNode> result = mapService.advance();
        assertTrue(result.isEmpty(),
            "advance() su un bivio non deve spostarsi automaticamente");
    }

    @Test
    @DisplayName("getReachableNodes() su n02 restituisce 3 nodi")
    void reachableNodesOnBivioAreThree() {
        mapService.getMap().setCurrentNodeId("n02");
        // Per leggere i reachable usiamo il GameMap direttamente
        List<MapNode> reachable = mapService.getMap().getReachableNodes();
        assertEquals(3, reachable.size());
    }

    // -----------------------------------------------------------
    // Secondo bivio — n07 ha 2 successori
    // -----------------------------------------------------------

    @Test
    @DisplayName("n07 ha esattamente 2 successori (bivio)")
    void n07HasTwoSuccessors() {
        assertEquals(2, nodeById("n07").getNextNodeIds().size());
    }

    @Test
    @DisplayName("I successori di n07 sono n08a e n08b")
    void n07SuccessorsAreCorrect() {
        List<String> nexts = nodeById("n07").getNextNodeIds();
        assertTrue(nexts.contains("n08a"));
        assertTrue(nexts.contains("n08b"));
    }

    // -----------------------------------------------------------
    // Navigazione — advance() su tratto lineare
    // -----------------------------------------------------------

    @Test
    @DisplayName("advance() su n00 sposta il cursore a n01")
    void advanceMovesCursorToN01() {
        Optional<MapNode> next = mapService.advance();
        assertTrue(next.isPresent());
        assertEquals("n01", next.get().getId());
    }

    @Test
    @DisplayName("advance() su n00 marca n00 come cleared")
    void advanceClearsCurrentNode() {
        mapService.advance();
        assertTrue(nodeById("n00").isCleared());
    }

    @Test
    @DisplayName("advance() su n11 restituisce empty (nessun successore)")
    void advanceOnLastNodeReturnsEmpty() {
        mapService.getMap().setCurrentNodeId("n11");
        assertTrue(mapService.advance().isEmpty());
    }

    // -----------------------------------------------------------
    // Navigazione — moveToNode()
    // -----------------------------------------------------------

    @Test
    @DisplayName("moveToNode() verso n03a da n02 restituisce true")
    void moveToReachableNodeReturnsTrue() {
        mapService.getMap().setCurrentNodeId("n02");
        assertTrue(mapService.moveToNode("n03a"));
        assertEquals("n03a", mapService.getMap().getCurrentNode().get().getId());
    }

    @Test
    @DisplayName("moveToNode() verso un nodo non raggiungibile restituisce false")
    void moveToUnreachableNodeReturnsFalse() {
        // Da n00 non si può raggiungere n09 direttamente
        assertFalse(mapService.moveToNode("n09"));
    }

    // -----------------------------------------------------------
    // isCurrentNodeLastBoss()
    // -----------------------------------------------------------

    @Test
    @DisplayName("isCurrentNodeLastBoss() è false su n10 (ha un successore)")
    void n10IsNotLastBoss() {
        mapService.getMap().setCurrentNodeId("n10");
        assertFalse(mapService.isCurrentNodeLastBoss());
    }

    @Test
    @DisplayName("isCurrentNodeLastBoss() è true su n11")
    void n11IsLastBoss() {
        mapService.getMap().setCurrentNodeId("n11");
        assertTrue(mapService.isCurrentNodeLastBoss());
    }

    // -----------------------------------------------------------
    // shopRound() — conta gli shop cleared
    // -----------------------------------------------------------

    @Test
    @DisplayName("shopRound() vale 1 all'inizio")
    void shopRoundStartsAtOne() {
        assertEquals(1, mapService.shopRound());
    }

    @Test
    @DisplayName("shopRound() vale 2 dopo aver completato il primo shop (n04)")
    void shopRoundIncreasesAfterFirstShop() {
        nodeById("n04").setVisited(true);
        nodeById("n04").setCleared(true);
        assertEquals(2, mapService.shopRound());
    }

    // -----------------------------------------------------------
    // restoreMap()
    // -----------------------------------------------------------

    @Test
    @DisplayName("restoreMap() riposiziona il cursore al nodo indicato")
    void restoreMapSetsCursor() {
        mapService.restoreMap("n05", List.of("n00", "n01", "n02", "n03a", "n04"));
        assertEquals("n05", mapService.getMap().getCurrentNode().get().getId());
    }

    @Test
    @DisplayName("restoreMap() marca i nodi cleared correttamente")
    void restoreMapMarksClearedNodes() {
        mapService.restoreMap("n05", List.of("n00", "n01", "n02"));
        assertTrue(nodeById("n00").isCleared());
        assertTrue(nodeById("n01").isCleared());
        assertTrue(nodeById("n02").isCleared());
        assertFalse(nodeById("n03a").isCleared());
    }

    @Test
    @DisplayName("restoreMap(null, null) non lancia eccezioni")
    void restoreMapWithNullsDoesNotThrow() {
        assertDoesNotThrow(() -> mapService.restoreMap(null, null));
    }

    // -----------------------------------------------------------
    // Helper
    // -----------------------------------------------------------

    private MapNode nodeById(String id) {
        return mapService.getMap().getNodeById(id)
                .orElseThrow(() -> new AssertionError("Nodo non trovato: " + id));
    }
}
