package it.unicam.cs.mpgc.rpg123393.model;

import it.unicam.cs.mpgc.rpg123393.model.relic.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Genera gli slot dello shop per ogni visita (4 slot casuali). */
public class ShopPool {

    public static List<ShopItem> generateShopItems(String className) {
        List<ShopItem> all = new ArrayList<>();

        // --- Carte (2 slot) ---
        List<ICard> cards = CardPool.getRewardOptions(className);
        Collections.shuffle(cards);
        for (int i = 0; i < Math.min(2, cards.size()); i++) {
            ICard c = cards.get(i);
            all.add(new ShopItem(c.getName(),
                    describeCard(c.getName()) + "  (Mana: " + c.getManaCost() + ")",
                    55 + c.getManaCost() * 10,
                    ShopItem.ItemType.CARD, c));
        }

        // --- Consumabile (1 slot) ---
        List<ShopItem> consumables = new ArrayList<>();
        consumables.add(new ShopItem("Pozione di Cura",    "Cura 30 HP istantaneamente",    25, ShopItem.ItemType.CONSUMABLE, "HEAL_30"));
        consumables.add(new ShopItem("Antidoto",           "Rimuove tutto il veleno",        20, ShopItem.ItemType.CONSUMABLE, "CURE_POISON"));
        consumables.add(new ShopItem("Elisir di Scudo",    "Applica 10 scudo istantaneo",    22, ShopItem.ItemType.CONSUMABLE, "SHIELD_10"));
        Collections.shuffle(consumables);
        all.add(consumables.get(0));

        // --- Reliquia (1 slot) ---
        List<Relic> relics = new ArrayList<>();
        relics.add(new BloodStoneRelic());
        relics.add(new SteelAmuletRelic());
        relics.add(new ArcaneCrystalRelic());
        relics.add(new PoisonRingRelic());
        Collections.shuffle(relics);
        Relic r = relics.get(0);
        all.add(new ShopItem(r.getName(), r.getDescription(), 95, ShopItem.ItemType.RELIC, r));

        // --- Upgrade deck (1 slot fisso) ---
        all.add(new ShopItem("Fucina dell'Eroe",
                "Potenzia una carta del tuo deck (sceglila nella prossima schermata)",
                75, ShopItem.ItemType.UPGRADE, null));

        Collections.shuffle(all);
        return all;
    }

    private static String describeCard(String name) {
        return switch (name) {
            case "Colpo"               -> "6 danni";
            case "Fireball"            -> "8 danni";
            case "Difesa"              -> "+6 scudo";
            case "Grida di Battaglia"  -> "8 danni+6 scudo";
            case "Mulinello"           -> "5 danni+cura 5";
            case "Dardo di Ghiaccio"   -> "7 danni+2 veleno";
            case "Scudo di Mana"       -> "+10 scudo+1 mana";
            case "Soffio del Drago"    -> "9 danni+3 veleno";
            case "Armatura di Scaglie" -> "+8 scudo+cura 4";
            case "Luce Divina"         -> "cura 12+4 scudo";
            case "Punizione Divina"    -> "10 danni cond.";
            case "Passo nell'Ombra"    -> "4 danni+5 veleno";
            case "Colpo Letale"        -> "6+veleno danni";
            case "Pozione Rapida"      -> "cura 10 HP";
            default                    -> "";
        };
    }
}
