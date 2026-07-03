package it.unicam.cs.mpgc.rpg123393.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta un singolo nodo della mappa.
 * POJO serializzabile via JSON (compatibile con JsonSaveRepository).
 */
public class MapNode {

    private String   id;
    private String   name;
    private String   description;
    private NodeType type;
    private boolean  visited;
    private boolean  cleared;

    /** ID dei nodi raggiungibili da questo nodo. */
    private List<String> nextNodeIds = new ArrayList<>();

    public MapNode() {}

    public MapNode(String id, String name, String description, NodeType type) {
        this.id          = id;
        this.name        = name;
        this.description = description;
        this.type        = type;
        this.visited     = false;
        this.cleared     = false;
    }

    public String   getId()                          { return id; }
    public void     setId(String id)                 { this.id = id; }
    public String   getName()                        { return name; }
    public void     setName(String name)             { this.name = name; }
    public String   getDescription()                 { return description; }
    public void     setDescription(String desc)      { this.description = desc; }
    public NodeType getType()                        { return type; }
    public void     setType(NodeType type)           { this.type = type; }
    public boolean  isVisited()                      { return visited; }
    public void     setVisited(boolean visited)      { this.visited = visited; }
    public boolean  isCleared()                      { return cleared; }
    public void     setCleared(boolean cleared)      { this.cleared = cleared; }
    public List<String> getNextNodeIds()             { return nextNodeIds; }
    public void     setNextNodeIds(List<String> ids) { this.nextNodeIds = ids != null ? ids : new ArrayList<>(); }

    public void addNextNode(String nodeId) {
        if (!nextNodeIds.contains(nodeId)) nextNodeIds.add(nodeId);
    }

    /** Mappa NodeType -> EncounterType per compatibilità con la logica battaglia esistente. */
    public EncounterType toEncounterType() {
        return switch (type) {
            case BATTLE -> EncounterType.NORMAL;
            case ELITE  -> EncounterType.ELITE;
            case BOSS   -> EncounterType.BOSS;
            case SHOP   -> EncounterType.SHOP;
            case REST   -> EncounterType.REST;
            case EVENT  -> EncounterType.EVENT;
        };
    }

    @Override
    public String toString() {
        return "MapNode{id='" + id + "', type=" + type + ", visited=" + visited + ", cleared=" + cleared + "}";
    }
}
