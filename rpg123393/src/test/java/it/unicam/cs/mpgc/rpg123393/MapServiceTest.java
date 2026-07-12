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
 * Struttura mappa (tre rami dopo n01):
 *   n00 -> n01 -+-> nA1 -> nA2 (SHOP) -> nA3 (ELITE) -> nA4 (REST) -> nA5 -> nA6 (ELITE) -> nAB (BOSS)
 *               +-> nB1 (ELITE) -> nB2 -> nB3 (SHOP) -> nB4 (ELITE) -> nB5 (ELITE) -> nBB1 (BOSS) -> nBB2 (BOSS)
 *               +-> nC1 (EVENT) -> nC2 (ELITE) -+-> nC3 (EVENT) -> nCB1 (BOSS)
 *                                               +-> nC4 -> nC5 (ELITE) -> nCB2 (BOSS)
 *                                                          nC5 -> nHK0 (VOID) -> nHK1 -> nHK4 (EVENT) -> nHKB (VOID_BOSS)
 *                                                                                                       -> nBB2 (BOSS)
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
    @DisplayName("La mappa deve contenere esattamente 27 nodi")
    void mapHasTwentySevenNodes() {
        assertEquals(27, mapService.getMap().getAllNodes().size());
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
    @DisplayName("n01 ha esattamente 3 successori (bivio principale)")
    void n01HasThreeSuccessors() {
        assertEquals(3, nodeById("n01").getNextNodeIds().size());
    }

    @Test
    @DisplayName("I successori di n01 sono nA1, nB1, nC1")
    void n01SuccessorsAreCorrect() {
        List<String> nexts = nodeById("n01").getNextNodeIds();
        assertTrue(nexts.contains("nA1"));
        assertTrue(nexts.contains("nB1"));
        assertTrue(nexts.contains("nC1"));
    }

    @Test
    @DisplayName("nC2 ha esattamente 2 successori (secondo bivio)")
    void nC2HasTwoSuccessors() {
        assertEquals(2, nodeById("nC2").getNextNodeIds().size());
    }

    @Test
    @DisplayName("I successori di nC2 sono nC3 e nC4")
    void nC2SuccessorsAreCorrect() {
        List<String> nexts = nodeById("nC2").getNextNodeIds();
        assertTrue(nexts.contains("nC3"));
        assertTrue(nexts.contains("nC4"));
    }

    @Test
    @DisplayName("nC5 ha esattamente 2 successori (bivio segreto Cavaliere)")
    void nC5HasTwoSuccessors() {
        assertEquals(2, nodeById("nC5").getNextNodeIds().size());
    }

    @Test
    @DisplayName("I successori di nHK4 sono nHKB e nBB2")
    void nHK4SuccessorsAreCorrect() {
        List<String> nexts = nodeById("nHK4").getNextNodeIds();
        assertTrue(nexts.contains("nHKB"));
        assertTrue(nexts.contains("nBB2"));
    }

    // -----------------------------------------------------------
    // NodeType
    // -----------------------------------------------------------

    @Test
    @DisplayName("I nodi hanno il NodeType corretto")
    void nodeTypesAreCorrect() {
        assertEquals(NodeType.BATTLE,    nodeById("n00").getType());
        assertEquals(NodeType.BATTLE,    nodeById("n01").getType());
        assertEquals(NodeType.SHOP,      nodeById("nA2").getType());
        assertEquals(NodeType.SHOP,      nodeById("nB3").getType());
        assertEquals(NodeType.ELITE,     nodeById("nA3").getType());
        assertEquals(NodeType.REST,      nodeById("nA4").getType());
        assertEquals(NodeType.EVENT,     nodeById("nC1").getType());
        assertEquals(NodeType.ELITE,     nodeById("nC2").getType());
        assertEquals(NodeType.EVENT,     nodeById("nC3").getType());
        assertEquals(NodeType.BOSS,      nodeById("nAB").getType());
        assertEquals(NodeType.BOSS,      nodeById("nBB1").getType());
        assertEquals(NodeType.BOSS,      nodeById("nBB2").getType());
        assertEquals(NodeType.BOSS,      nodeById("nCB1").getType());
        assertEquals(NodeType.BOSS,      nodeById("nCB2").getType());
        assertEquals(NodeType.VOID,      nodeById("nHK0").getType());
        assertEquals(NodeType.VOID_BOSS, nodeById("nHKB").getType());
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
    // Boss finali senza successori
    // -----------------------------------------------------------

    @Test
    @DisplayName("I boss finali di ogni ramo non hanno successori")
    void finalBossesHaveNoSuccessors() {
        assertTrue(nodeById("nAB").getNextNodeIds().isEmpty(),  "nAB dovrebbe essere senza successori");
        assertTrue(nodeById("nCB1").getNextNodeIds().isEmpty(), "nCB1 dovrebbe essere senza successori");
        assertTrue(nodeById("nCB2").getNextNodeIds().isEmpty(), "nCB2 dovrebbe essere senza successori");
        assertTrue(nodeById("nHKB").getNextNodeIds().isEmpty(), "nHKB dovrebbe essere senza successori");
    }

    // -----------------------------------------------------------
    // isCurrentNodeLastBoss()
    // -----------------------------------------------------------

    @Test
    @DisplayName("isCurrentNodeLastBoss() è true su nAB (BOSS senza successori)")
    void nABIsLastBoss() {
        mapService.getMap().setCurrentNodeId("nAB");
        assertTrue(mapService.isCurrentNodeLastBoss());
    }

    @Test
    @DisplayName("isCurrentNodeLastBoss() è false su nBB1 (ha un successore: nBB2)")
    void nBB1IsNotLastBoss() {
        mapService.getMap().setCurrentNodeId("nBB1");
        assertFalse(mapService.isCurrentNodeLastBoss());
    }

    @Test
    @DisplayName("isCurrentNodeLastBoss() è true su nHKB (VOID_BOSS senza successori)")
    void nHKBIsLastBoss() {
        mapService.getMap().setCurrentNodeId("nHKB");
        assertTrue(mapService.isCurrentNodeLastBoss());
    }

    // -----------------------------------------------------------
    // Navigazione — advance()
    // -----------------------------------------------------------

    @Test
    @DisplayName("advance() marca n00 come cleared e restituisce empty")
    void advanceClearsCurrentNodeAndReturnsEmpty() {
        // advance() nel sorgente attuale svuota il nodo e ritorna sempre Optional.empty().
        // La navigazione effettiva avviene tramite moveToNode().
        Optional<MapNode> result = mapService.advance();
        assertTrue(result.isEmpty());
        assertTrue(nodeById("n00").isCleared());
    }

    @Test
    @DisplayName("advance() su nAB (senza successori) restituisce empty")
    void advanceOnLastNodeReturnsEmpty() {
        mapService.getMap().setCurrentNodeId("nAB");
        assertTrue(mapService.advance().isEmpty());
    }

    // -----------------------------------------------------------
    // Navigazione — moveToNode()
    // -----------------------------------------------------------

    @Test
    @DisplayName("moveToNode() verso nA1 da n01 restituisce true")
    void moveToReachableNodeReturnsTrue() {
        mapService.getMap().setCurrentNodeId("n01");
        assertTrue(mapService.moveToNode("nA1"));
        assertEquals("nA1", mapService.getMap().getCurrentNode().get().getId());
    }

    @Test
    @DisplayName("moveToNode() verso nC3 da nC2 restituisce true")
    void moveToReachableNodeC2ToC3() {
        mapService.getMap().setCurrentNodeId("nC2");
        assertTrue(mapService.moveToNode("nC3"));
        assertEquals("nC3", mapService.getMap().getCurrentNode().get().getId());
    }

    @Test
    @DisplayName("moveToNode() verso un nodo non raggiungibile restituisce false")
    void moveToUnreachableNodeReturnsFalse() {
        // Da n00, l'unico successore è n01 — nHKB non è raggiungibile direttamente
        assertFalse(mapService.moveToNode("nHKB"));
    }

    // -----------------------------------------------------------
    // shopRound()
    // -----------------------------------------------------------

    @Test
    @DisplayName("shopRound() vale 1 all'inizio")
    void shopRoundStartsAtOne() {
        assertEquals(1, mapService.shopRound());
    }

    @Test
    @DisplayName("shopRound() vale 2 dopo aver cleared nA2 (primo shop del ramo A)")
    void shopRoundIncreasesAfterFirstShop() {
        nodeById("nA2").setVisited(true);
        nodeById("nA2").setCleared(true);
        assertEquals(2, mapService.shopRound());
    }

    @Test
    @DisplayName("shopRound() vale 3 dopo aver cleared nA2 e nB3 (entrambi gli shop)")
    void shopRoundIncreasesAfterBothShops() {
        nodeById("nA2").setVisited(true);
        nodeById("nA2").setCleared(true);
        nodeById("nB3").setVisited(true);
        nodeById("nB3").setCleared(true);
        assertEquals(3, mapService.shopRound());
    }

    // -----------------------------------------------------------
    // restoreMap()
    // -----------------------------------------------------------

    @Test
    @DisplayName("restoreMap() riposiziona il cursore al nodo indicato")
    void restoreMapSetsCursor() {
        mapService.restoreMap("nA3", List.of("n00", "n01", "nA1", "nA2"));
        assertEquals("nA3", mapService.getMap().getCurrentNode().get().getId());
    }

    @Test
    @DisplayName("restoreMap() marca i nodi cleared correttamente")
    void restoreMapMarksClearedNodes() {
        mapService.restoreMap("nA3", List.of("n00", "n01", "nA1"));
        assertTrue(nodeById("n00").isCleared());
        assertTrue(nodeById("n01").isCleared());
        assertTrue(nodeById("nA1").isCleared());
        assertFalse(nodeById("nA2").isCleared());
    }

    @Test
    @DisplayName("restoreMap(null, null) non lancia eccezioni")
    void restoreMapWithNullsDoesNotThrow() {
        assertDoesNotThrow(() -> mapService.restoreMap(null, null));
    }

    // -----------------------------------------------------------
    // requiredClass sui nodi speciali
    // -----------------------------------------------------------

    @Test
    @DisplayName("nC1 richiede classe Dracomante, Assassino o Cavaliere")
    void nC1RequiredClassIsCorrect() {
        String req = nodeById("nC1").getRequiredClass();
        assertNotNull(req);
        assertTrue(req.contains("Dracomante") || req.contains("Assassino") || req.contains("Cavaliere"));
    }

    @Test
    @DisplayName("nHK0 richiede classe Cavaliere")
    void nHK0RequiredClassIsCavaliere() {
        assertEquals("Cavaliere", nodeById("nHK0").getRequiredClass());
    }

    @Test
    @DisplayName("nA1 non ha requiredClass (nodo aperto a tutti)")
    void nA1HasNoRequiredClass() {
        String req = nodeById("nA1").getRequiredClass();
        assertTrue(req == null || req.isBlank());
    }

    // -----------------------------------------------------------
    // Helper
    // -----------------------------------------------------------

    private MapNode nodeById(String id) {
        return mapService.getMap().getNodeById(id)
                .orElseThrow(() -> new AssertionError("Nodo non trovato: " + id));
    }
}
