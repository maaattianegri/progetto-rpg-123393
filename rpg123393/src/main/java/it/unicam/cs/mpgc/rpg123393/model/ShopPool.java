package it.unicam.cs.mpgc.rpg123393.model;

import it.unicam.cs.mpgc.rpg123393.model.relic.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShopPool {

    public static List<ShopItem> generateShopItems(String className, int shopRound,
                                                    boolean upgradeAvailable) {
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

        List<ICard> cards = new ArrayList<>(CardPool.getRewardOptions(className));
        Collections.shuffle(cards);
        for (int i = 0; i < Math.min(2, cards.size()); i++) {
            ICard c = cards.get(i);
            int price = cardBase + c.getManaCost() * cardMult;
            all.add(new ShopItem(c.getName(),
                    describeCard(c.getName()) + "  (Mana: " + c.getManaCost() + ")",
                    price, ShopItem.ItemType.CARD, c));
        }

        List<ShopItem> consumables = new ArrayList<>();
        consumables.add(new ShopItem("Pozione di Cura",  "Cura 30 HP istantaneamente",  20, ShopItem.ItemType.CONSUMABLE, "HEAL_30"));
        consumables.add(new ShopItem("Antidoto",          "Rimuove tutto il veleno",      15, ShopItem.ItemType.CONSUMABLE, "CURE_POISON"));
        consumables.add(new ShopItem("Elisir di Scudo",   "Applica 10 scudo istantaneo", 18, ShopItem.ItemType.CONSUMABLE, "SHIELD_10"));
        Collections.shuffle(consumables);
        all.add(consumables.get(0));

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

        if (upgradeAvailable) {
            int upgradePrice = switch (shopRound) {
                case 1  -> 40;
                case 2  -> 55;
                default -> 70;
            };
            all.add(new ShopItem("Fucina dell'Eroe",
                    "Potenzia una carta del tuo deck",
                    upgradePrice, ShopItem.ItemType.UPGRADE, null));
        }

        Collections.shuffle(all);
        return all;
    }

    public static List<ShopItem> generateShopItems(String className, int shopRound) {
        return generateShopItems(className, shopRound, true);
    }

    public static List<ShopItem> generateShopItems(String className) {
        return generateShopItems(className, 1, true);
    }

    private static String describeCard(String name) {
        return switch (name) {
            // Starter
            case "Colpo di Spada"           -> "6 danni";
            case "Colpo di Spada+"          -> "9 danni";
            case "Scudo di Legno"           -> "+6 scudo";
            case "Scudo di Legno+"          -> "+9 scudo";
            case "Pozione Rapida"           -> "cura 10 HP";
            // Guerriero
            case "Colpo Devastante"         -> "12 danni";
            case "Colpo Devastante+"        -> "16 danni";
            case "Grida di Battaglia"       -> "8 danni+6 scudo";
            case "Grida di Battaglia+"      -> "10 danni+8 scudo";
            case "Mulinello"                -> "5 danni+cura 5";
            case "Mulinello+"               -> "7 danni+cura 7";
            case "Furia Berserker"          -> "7 danni (12 se HP bassi)";
            case "Furia Berserker+"         -> "9 danni (15 se HP bassi)";
            case "Sfida"                    -> "+8 scudo";
            case "Sfida+"                   -> "+12 scudo";
            // Paladino
            case "Scudo Sacro"              -> "4 danni+12 scudo+cura 4";
            case "Scudo Sacro+"             -> "5 danni+14 scudo+cura 5";
            case "Luce Divina"              -> "cura 12+4 scudo";
            case "Luce Divina+"             -> "cura 15+6 scudo";
            case "Punizione Divina"         -> "10 danni (+6 se scudo>0)";
            case "Punizione Divina+"        -> "11 danni (+8 se scudo>0)";
            case "Consacrazione"            -> "8 danni+6 scudo";
            case "Consacrazione+"           -> "10 danni+8 scudo";
            case "Voto di Ferro"            -> "12 scudo+8 danni";
            case "Voto di Ferro+"           -> "15 scudo+10 danni";
            case "Castigo Sacro"            -> "8 danni+azzera scudo nemico";
            case "Castigo Sacro+"           -> "11 danni+azzera scudo nemico";
            case "Benedizione"              -> "cura 8+6 scudo";
            case "Benedizione+"             -> "cura 10+8 scudo";
            case "Martello della Giustizia" -> "14 danni";
            case "Martello della Giustizia+"-> "18 danni";
            // Mago
            case "Palla di Fuoco"           -> "14 danni";
            case "Palla di Fuoco+"          -> "18 danni";
            case "Nova di Fuoco"            -> "18 danni";
            case "Nova di Fuoco+"           -> "24 danni";
            case "Tempesta Arcana"          -> "10+2×veleno danni";
            case "Tempesta Arcana+"         -> "13+3×veleno danni";
            case "Dardo di Ghiaccio"        -> "7 danni+2 veleno";
            case "Dardo di Ghiaccio+"       -> "9 danni+3 veleno";
            case "Scudo di Mana"            -> "+10 scudo+1 mana";
            case "Scudo di Mana+"           -> "+14 scudo+1 mana";
            // Dracomante
            case "Zanna di Drago"           -> "4 danni+2 scudo";
            case "Zanna di Drago+"          -> "6 danni+4 scudo";
            case "Artiglio del Drago"       -> "6 danni+4 scudo";
            case "Artiglio del Drago+"      -> "9 danni+6 scudo";
            case "Soffio del Drago"         -> "9 danni+3 veleno";
            case "Soffio del Drago+"        -> "12 danni+5 veleno";
            case "Armatura di Scaglie"      -> "+8 scudo+cura 4";
            case "Armatura di Scaglie+"     -> "+12 scudo+cura 6";
            // Assassino
            case "Lama Avvelenata"          -> "3 danni+3 veleno";
            case "Lama Avvelenata+"         -> "3 danni+5 veleno";
            case "Passo nell'Ombra"         -> "4 danni+5 veleno";
            case "Passo nell'Ombra+"        -> "5 danni+7 veleno";
            case "Colpo Letale"             -> "6+veleno danni";
            case "Colpo Letale+"            -> "8+veleno danni";
            case "Veleno Acido"             -> "4 veleno";
            case "Veleno Acido+"            -> "6 veleno";
            case "Doppia Lama"              -> "4+4 danni";
            case "Doppia Lama+"             -> "6+6 danni";
            default                         -> "";
        };
    }
}
