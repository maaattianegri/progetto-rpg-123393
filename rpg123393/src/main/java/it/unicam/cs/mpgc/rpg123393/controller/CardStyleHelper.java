package it.unicam.cs.mpgc.rpg123393.controller;

/**
 * Utility statica condivisa tra HelloController, CardRewardController e CollectionController
 * per colori, simboli e descrizioni delle carte.
 */
public class CardStyleHelper {

    public static String borderColor(String name) {
        String n = name.toLowerCase();
        if (n.contains("veleno") || n.contains("ombra") || n.contains("letale") || n.contains("lama"))
            return "#27ae60";
        if (n.contains("scudo") || n.contains("luce") || n.contains("divina") || n.contains("mana") || n.contains("armatura") || n.contains("scaglie") || n.contains("sacro"))
            return "#3498db";
        if (n.contains("fuoco") || n.contains("fireball") || n.contains("tempesta") || n.contains("artiglio") || n.contains("soffio") || n.contains("ghiaccio"))
            return "#e67e22";
        if (n.contains("pozione") || n.contains("cura") || n.contains("mulinello") || n.contains("grida") || n.contains("punizione"))
            return "#c77dff";
        return "#e74c3c";
    }

    public static String symbol(String name) {
        String color = borderColor(name);
        return switch (color) {
            case "#27ae60" -> "~~~";
            case "#3498db" -> "[ ]";
            case "#e67e22" -> "***";
            case "#c77dff" -> "(+)";
            default        -> "/ \\";
        };
    }

    public static String description(String name) {
        return switch (name) {
            case "Colpo"                -> "6 danni al nemico";
            case "Difesa"               -> "+6 scudo";
            case "Fireball"             -> "8 danni al nemico";
            case "Colpo Devastante"     -> "12 danni al nemico";
            case "Tempesta Arcana"      -> "10 + 2\u00d7veleno nemico danni";
            case "Artiglio del Drago"   -> "6 danni + 4 scudo";
            case "Scudo Sacro"          -> "12 scudo + 4 HP";
            case "Lama Avvelenata"      -> "3 danni + 3 stack veleno";
            case "Grida di Battaglia"   -> "8 danni + 6 scudo";
            case "Mulinello"            -> "5 danni + cura 5 HP";
            case "Dardo di Ghiaccio"    -> "7 danni + 2 veleno";
            case "Scudo di Mana"        -> "+10 scudo, +1 mana";
            case "Soffio del Drago"     -> "9 danni + 3 veleno";
            case "Armatura di Scaglie"  -> "+8 scudo + cura 4 HP";
            case "Luce Divina"          -> "Cura 12 HP + 4 scudo";
            case "Punizione Divina"     -> "10 danni (+4 se hai scudo)";
            case "Passo nell'Ombra"     -> "4 danni + 5 veleno";
            case "Colpo Letale"         -> "6 + veleno nemico danni";
            case "Pozione Rapida"       -> "Cura 10 HP";
            default                     -> "";
        };
    }
}
