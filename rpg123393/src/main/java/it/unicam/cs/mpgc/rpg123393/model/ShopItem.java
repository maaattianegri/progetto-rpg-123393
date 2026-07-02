package it.unicam.cs.mpgc.rpg123393.model;

/** Rappresenta un oggetto in vendita nello shop. */
public class ShopItem {

    public enum ItemType { CARD, CONSUMABLE, RELIC, UPGRADE }

    private final String   name;
    private final String   description;
    private final int      price;
    private final ItemType type;
    private final Object   payload; // ICard, Relic, o null per consumabili

    public ShopItem(String name, String description, int price, ItemType type, Object payload) {
        this.name        = name;
        this.description = description;
        this.price       = price;
        this.type        = type;
        this.payload     = payload;
    }

    public String   getName()       { return name; }
    public String   getDescription(){ return description; }
    public int      getPrice()      { return price; }
    public ItemType getType()       { return type; }
    public Object   getPayload()    { return payload; }
}
