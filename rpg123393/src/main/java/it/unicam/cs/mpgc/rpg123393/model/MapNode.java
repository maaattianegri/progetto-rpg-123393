package it.unicam.cs.mpgc.rpg123393.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta un singolo nodo della mappa.
 * POJO serializzabile via JSON.
 *
 * Il campo requiredClass (null = aperto a tutti) limita l'accesso
 * a una specifica classe del player. Il MapController usa questo
 * campo per mostrare il nodo come bloccato (icona 🔒, colore desaturato)
 * e non cliccabile se la classe non corrisponde.
 */
public class MapNode {

    private String   id;
    private String   name;
    private String   description;
    private NodeType type;
    private boolean  visited;
    private boolean  cleared;
    /** null = accessibile a tutti; altrimenti nome esatto della classe richiesta. */
    private String   requiredClass;

    private List<String> nextNodeIds = new ArrayList<>();

    public MapNode() {}

    public MapNode(String id, String name, String description, NodeType type) {
        this.id          = id;
        this.name        = name;
        this.description = description;
        this.type        = type;
    }

    public MapNode(String id, String name, String description, NodeType type, String requiredClass) {
        this(id, name, description, type);
        this.requiredClass = requiredClass;
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
    public String   getRequiredClass()               { return requiredClass; }
    public void     setRequiredClass(String rc)      { this.requiredClass = rc; }
    public List<String> getNextNodeIds()             { return nextNodeIds; }
    public void     setNextNodeIds(List<String> ids) { this.nextNodeIds = ids != null ? ids : new ArrayList<>(); }

    public boolean isLockedFor(String playerClass) {
        return requiredClass != null && !requiredClass.equals(playerClass);
    }

    public void addNextNode(String nodeId) {
        if (!nextNodeIds.contains(nodeId)) nextNodeIds.add(nodeId);
    }

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
