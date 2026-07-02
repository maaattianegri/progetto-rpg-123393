package it.unicam.cs.mpgc.rpg123393.model;

import it.unicam.cs.mpgc.rpg123393.model.relic.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Genera gli slot dello shop per ogni visita (5 slot).
 *
 * Prezzi carte per round:
 *   Round 1: costo base 25 + manaCost*8  (es. 1-mana = 33, 2-mana = 41)
 *   Round 2: costo base 40 + manaCost*10 (es. 1-mana = 50, 2-mana = 60)
 *   Round 3: costo base 60 + manaCost*12 (es. 1-mana = 72, 2-mana = 84)
 */
public class ShopPool {

    public static List<ShopItem> generateShopItems(String className, int shopRound) {
        List<ShopItem> all = new ArrayList<>();

        int cardBase = switch (shopRound) {
            case 1  -> 25;
            case 2  -> 40;
            default -> 60;
        };
        int cardMult = switch (shopRound) {
            case 1  -> 8;
            case 2  -> 10;
            default -> 12;
        };

        // --- 2 carte dalla pool della classe ---
        List<ICard> cards = new ArrayList<>(CardPool.getClassPool(className));
        cards.addAll(CardPool.getNeutralPool());
        Collections.shuffle(cards);
        for (int i = 0; i < Math.min(2, cards.size()); i++) {
            ICard c = cards.get(i);
            int price = cardBase + c.getManaCost() * cardMult;
            all.add(new ShopItem(c.getName(),
                    describeCard(c.getName()) + "  (Mana: " + c.getManaCost() + ")",
                    price, ShopItem.ItemType.CARD, c));
        }

        // --- 1 consumabile ---
        List<ShopItem> consumables = new ArrayList<>();
        consumables.add(new ShopItem("Pozione di Cura",  "Cura 30 HP istantaneamente",  20, ShopItem.ItemType.CONSUMABLE, "HEAL_30"));
        consumables.add(new ShopItem("Antidoto",          "Rimuove tutto il veleno",      15, ShopItem.ItemType.CONSUMABLE, "CURE_POISON"));
        consumables.add(new ShopItem("Elisir di Scudo",   "Applica 10 scudo istantaneo", 18, ShopItem.ItemType.CONSUMABLE, "SHIELD_10"));
        Collections.shuffle(consumables);
        all.add(consumables.get(0));

        // --- 1 reliquia ---
        List<Relic> relics = new ArrayList<>();
        relics.add(new BloodStoneRelic());
        relics.add(new SteelAmuletRelic());
        relics.add(new ArcaneCrystalRelic());
        relics.add(new PoisonRingRelic());
        Collections.shuffle(relics);
        Relic r = relics.get(0);
        int relicPrice = switch (shopRound) {
            case 1  -> 60;
            case 2  -> 80;
            default -> 100;
        };
        all.add(new ShopItem(r.getName(), r.getDescription(), relicPrice, ShopItem.ItemType.RELIC, r));

        // --- 1 upgrade deck (Fucina) ---
        int upgradePrice = switch (shopRound) {
            case 1  -> 40;
            case 2  -> 55;
            default -> 70;
        };
        all.add(new ShopItem("Fucina dell'Eroe",
                "Potenzia una carta del tuo deck",
                upgradePrice, ShopItem.ItemType.UPGRADE, null));

        Collections.shuffle(all);
        return all;
    }

    /** Compatibilità con vecchie chiamate senza shopRound. */
    public static List<ShopItem> generateShopItems(String className) {
        return generateShopItems(className, 1);
    }

    private static String describeCard(String name) {
        return switch (name) {
            case "Tempesta Arcana"      -> "10+2×veleno danni";
            case "Dardo Gelato"         -> "7 danni+2 veleno";
            case "Scudo di Mana"        -> "+10 scudo, +1 mana";
            case "Colpo Devastante"     -> "12 danni";
            case "Grida di Battaglia"   -> "8 danni+6 scudo";
            case "Mulinello"            -> "5 danni+cura 5";
            case "Artiglio del Drago"   -> "6 danni+4 scudo";
            case "Soffio del Drago"     -> "9 danni+3 veleno";
            case "Armatura di Scaglie"  -> "+8 scudo+cura 4";
            case "Scudo Sacro"          -> "+12 scudo+cura 4";
            case "Luce Divina"          -> "cura 12+4 scudo";
            case "Retribuzione"         -> "10 danni condiz.";
            case "Lama Avvelenata"      -> "3 danni+3 veleno";
            case "Passo nell'Ombra"     -> "4 danni+5 veleno";
            case "Colpo Letale"         -> "6+veleno danni";
            case "Pozione Rapida"       -> "cura 10 HP";
            default                     -> "";
        };
    }
}
