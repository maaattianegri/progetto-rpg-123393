package it.unicam.cs.mpgc.rpg123393.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Rappresenta la mappa a nodi di una run.
 * Contiene tutti i nodi, le loro connessioni e il nodo corrente.
 */
public class GameMap {

    private List<MapNode> nodes         = new ArrayList<>();
    private String        currentNodeId = null;

    public GameMap() {}

    // -------------------------------------------------------
    // Gestione nodi
    // -------------------------------------------------------

    public void addNode(MapNode node) {
        nodes.add(node);
    }

    public Optional<MapNode> getNodeById(String id) {
        return nodes.stream().filter(n -> n.getId().equals(id)).findFirst();
    }

    public List<MapNode> getAllNodes() {
        return nodes;
    }

    // -------------------------------------------------------
    // Navigazione
    // -------------------------------------------------------

    public String getCurrentNodeId() {
        return currentNodeId;
    }

    public void setCurrentNodeId(String id) {
        this.currentNodeId = id;
    }

    public Optional<MapNode> getCurrentNode() {
        if (currentNodeId == null) return Optional.empty();
        return getNodeById(currentNodeId);
    }

    /**
     * Restituisce i nodi raggiungibili dal nodo corrente.
     * Un nodo è raggiungibile se è collegato al nodo corrente
     * e non è ancora stato completato (cleared).
     */
    public List<MapNode> getReachableNodes() {
        Optional<MapNode> current = getCurrentNode();
        if (current.isEmpty()) return List.of();

        List<MapNode> reachable = new ArrayList<>();
        for (String nextId : current.get().getNextNodeIds()) {
            getNodeById(nextId)
                .filter(n -> !n.isCleared())
                .ifPresent(reachable::add);
        }
        return reachable;
    }

    /**
     * Sposta il giocatore verso il nodo con l'id dato, se raggiungibile.
     * Marca il nodo come visitato.
     *
     * @return true se lo spostamento è avvenuto, false se il nodo non è raggiungibile.
     */
    public boolean moveTo(String nodeId) {
        boolean reachable = getReachableNodes().stream()
                .anyMatch(n -> n.getId().equals(nodeId));
        if (!reachable) return false;

        currentNodeId = nodeId;
        getNodeById(nodeId).ifPresent(n -> n.setVisited(true));
        return true;
    }

    /**
     * Marca il nodo corrente come cleared (incontro completato).
     */
    public void clearCurrentNode() {
        getCurrentNode().ifPresent(n -> n.setCleared(true));
    }

    public void setNodes(List<MapNode> nodes) {
        this.nodes = nodes != null ? nodes : new ArrayList<>();
    }
}
