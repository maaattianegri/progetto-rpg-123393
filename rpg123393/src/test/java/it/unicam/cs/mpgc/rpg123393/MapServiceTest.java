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
 * Test di unità per MapService.
 *
 * Verifica:
 *   - struttura della mappa generata
 *   - navigazione (advance, moveToNode)
 *   - stati dei nodi (visited, cleared)
 *   - metodi di supporto (shopRound, isCurrentNodeLastBoss)
 *   - ripristino da salvataggio (restoreMap)
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
    @DisplayName("La mappa deve contenere esattamente 12 nodi")
    void mapHasTwelveNodes() {
        assertEquals(12, mapService.getMap().getAllNodes().size());
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
        Optional<MapNode> boss2 = mapService.getMap().getNodeById("n11");
        assertTrue(boss2.isPresent());
        assertTrue(boss2.get().getNextNodeIds().isEmpty());
    }

    @Test
    @DisplayName("I nodi hanno il NodeType corretto")
    void nodeTypesAreCorrect() {
        assertEquals(NodeType.SHOP,  nodeById("n03").getType());
        assertEquals(NodeType.ELITE, nodeById("n04").getType());
        assertEquals(NodeType.BOSS,  nodeById("n10").getType());
        assertEquals(NodeType.BOSS,  nodeById("n11").getType());
        assertEquals(NodeType.BATTLE, nodeById("n01").getType());
    }

    // -----------------------------------------------------------
    // Tipo di incontro
    // -----------------------------------------------------------

    @Test
    @DisplayName("currentEncounterType() restituisce BATTLE sul nodo n00")
    void currentEncounterTypeOnStart() {
        assertEquals(EncounterType.NORMAL, mapService.currentEncounterType(),
                "n00 è NodeType.BATTLE che mappa a NORMAL/BATTLE; vedi toEncounterType()");
    }

    // -----------------------------------------------------------
    // Navigazione — advance()
    // -----------------------------------------------------------

    @Test
    @DisplayName("advance() sposta il cursore da n00 a n01")
    void advanceMovesCursorToN01() {
        Optional<MapNode> next = mapService.advance();
        assertTrue(next.isPresent());
        assertEquals("n01", next.get().getId());
        assertEquals("n01", mapService.getMap().getCurrentNode().get().getId());
    }

    @Test
    @DisplayName("advance() marca n00 come cleared")
    void advanceClearsCurrentNode() {
        mapService.advance();
        Optional<MapNode> n00 = mapService.getMap().getNodeById("n00");
        assertTrue(n00.isPresent());
        assertTrue(n00.get().isCleared());
    }

    @Test
    @DisplayName("advance() marca n01 come visited")
    void advanceVisitsNextNode() {
        mapService.advance();
        Optional<MapNode> n01 = mapService.getMap().getNodeById("n01");
        assertTrue(n01.isPresent());
        assertTrue(n01.get().isVisited());
    }

    @Test
    @DisplayName("advance() su n11 (nessun successore) restituisce Optional.empty()")
    void advanceOnLastNodeReturnsEmpty() {
        // Naviga manualmente fino a n11
        mapService.getMap().setCurrentNodeId("n11");
        Optional<MapNode> result = mapService.advance();
        assertTrue(result.isEmpty());
    }

    // -----------------------------------------------------------
    // Navigazione — moveToNode()
    // -----------------------------------------------------------

    @Test
    @DisplayName("moveToNode() verso un nodo raggiungibile restituisce true")
    void moveToReachableNodeReturnsTrue() {
        // n00 -> n01 è connesso
        boolean result = mapService.moveToNode("n01");
        assertTrue(result);
        assertEquals("n01", mapService.getMap().getCurrentNode().get().getId());
    }

    @Test
    @DisplayName("moveToNode() verso un nodo non raggiungibile restituisce false")
    void moveToUnreachableNodeReturnsFalse() {
        // n00 -> n05 non è collegato direttamente
        boolean result = mapService.moveToNode("n05");
        assertFalse(result);
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
    @DisplayName("isCurrentNodeLastBoss() è true su n11 (nessun successore)")
    void n11IsLastBoss() {
        mapService.getMap().setCurrentNodeId("n11");
        assertTrue(mapService.isCurrentNodeLastBoss());
    }

    // -----------------------------------------------------------
    // shopRound() e completedShopCount()
    // -----------------------------------------------------------

    @Test
    @DisplayName("shopRound() vale 1 all'inizio (nessuno shop completato)")
    void shopRoundStartsAtOne() {
        assertEquals(1, mapService.shopRound());
    }

    @Test
    @DisplayName("shopRound() vale 2 dopo aver completato n03 (primo SHOP)")
    void shopRoundIncrementsAfterShopCleared() {
        // Marca n03 come cleared manualmente
        mapService.getMap().getNodeById("n03").ifPresent(n -> {
            n.setVisited(true);
            n.setCleared(true);
        });
        assertEquals(2, mapService.shopRound());
    }

    // -----------------------------------------------------------
    // restoreMap()
    // -----------------------------------------------------------

    @Test
    @DisplayName("restoreMap() riposiziona il cursore al nodo indicato")
    void restoreMapSetsCursor() {
        mapService.restoreMap("n05", List.of("n00", "n01", "n02", "n03", "n04"));
        assertEquals("n05", mapService.getMap().getCurrentNode().get().getId());
    }

    @Test
    @DisplayName("restoreMap() marca i nodi cleared correttamente")
    void restoreMapMarksClearedNodes() {
        mapService.restoreMap("n05", List.of("n00", "n01", "n02"));
        assertTrue(nodeById("n00").isCleared());
        assertTrue(nodeById("n01").isCleared());
        assertTrue(nodeById("n02").isCleared());
        assertFalse(nodeById("n03").isCleared());
    }

    @Test
    @DisplayName("restoreMap(null, null) non lancia eccezioni (retrocompatibilità)")
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
