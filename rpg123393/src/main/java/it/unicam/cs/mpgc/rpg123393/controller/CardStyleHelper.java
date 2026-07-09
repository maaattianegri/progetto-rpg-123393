package it.unicam.cs.mpgc.rpg123393.controller;

/**
 * Colori canonici per classe — allineati a class-select-view.fxml:
 *
 *   #e74c3c  🔴 Guerriero
 *   #f1c40f  🟡 Paladino
 *   #9b59b6  🟣 Mago
 *   #e67e22  🟠 Dracomante
 *   #27ae60  🟢 Assassino
 *   #00bcd4  🟦 Neutrale (ciano)
 *   #9aaaba  ⚪ Starter multiclasse
 */
public class CardStyleHelper {

    public static String borderColor(String name) {
        return switch (name) {
            // ⚪ Starter multiclasse
            case "Colpo di Spada", "Colpo di Spada+",
                 "Scudo di Legno",  "Scudo di Legno+"          -> "#9aaaba";
            // 🔴 Guerriero (base + Plus)
            case "Colpo Devastante",  "Colpo Devastante+",
                 "Grida di Battaglia","Grida di Battaglia+",
                 "Mulinello",          "Mulinello+",
                 "Furia Berserker",    "Furia Berserker+",
                 "Sfida",              "Sfida+"                 -> "#e74c3c";
            // 🟡 Paladino (base + Plus)
            case "Scudo Sacro",    "Scudo Sacro+",
                 "Luce Divina",     "Luce Divina+",
                 "Punizione Divina","Punizione Divina+",
                 "Consacrazione",   "Consacrazione+",
                 "Voto di Ferro",   "Voto di Ferro+",
                 "Castigo Sacro",   "Castigo Sacro+",
                 "Benedizione",     "Benedizione+",
                 "Martello della Giustizia", "Martello della Giustizia+" -> "#f1c40f";
            // 🟣 Mago (base + Plus)
            case "Palla di Fuoco",    "Palla di Fuoco+",
                 "Nova di Fuoco",      "Nova di Fuoco+",
                 "Tempesta Arcana",    "Tempesta Arcana+",
                 "Dardo di Ghiaccio",  "Dardo di Ghiaccio+",
                 "Scudo di Mana",      "Scudo di Mana+"         -> "#9b59b6";
            // 🟠 Dracomante (base + Plus)
            case "Zanna di Drago",   "Zanna di Drago+",
                 "Artiglio del Drago", "Artiglio del Drago+",
                 "Soffio del Drago",  "Soffio del Drago+",
                 "Armatura di Scaglie", "Armatura di Scaglie+" -> "#e67e22";
            // 🟢 Assassino (base + Plus)
            case "Lama Avvelenata",   "Lama Avvelenata+",
                 "Passo nell'Ombra",  "Passo nell'Ombra+",
                 "Colpo Letale",      "Colpo Letale+",
                 "Veleno Acido",      "Veleno Acido+",
                 "Doppia Lama",       "Doppia Lama+"            -> "#27ae60";
            // 🟦 Neutrale (ciano)
            case "Pozione Rapida"                              -> "#00bcd4";
            default                                            -> "#888888";
        };
    }

    public static String symbol(String name) {
        return switch (borderColor(name)) {
            case "#9aaaba" -> "\u2605";
            case "#e74c3c" -> "\u2694";
            case "#f1c40f" -> "\ud83d\udee1";
            case "#9b59b6" -> "\ud83d\udd2e";
            case "#e67e22" -> "\ud83d\udc09";
            case "#27ae60" -> "\u2620";
            case "#00bcd4" -> "\u2665";
            default        -> "?";
        };
    }

    public static String description(String name) {
        return switch (name) {
            // ── ⚪ Starter ──
            case "Colpo di Spada"           -> "\u2694 6 danni  |  1 mana";
            case "Colpo di Spada+"          -> "\u2694 9 danni  |  1 mana";
            case "Scudo di Legno"           -> "\ud83d\udee1 +6 scudo  |  1 mana";
            case "Scudo di Legno+"          -> "\ud83d\udee1 +9 scudo  |  1 mana";
            // ── 🔴 Guerriero base ──
            case "Colpo Devastante"         -> "\u2694 12 danni  |  2 mana";
            case "Colpo Devastante+"        -> "\u2694 16 danni  |  2 mana";
            case "Grida di Battaglia"       -> "\u2694 8 danni + \ud83d\udee1 6 scudo  |  3 mana";
            case "Grida di Battaglia+"      -> "\u2694 10 danni + \ud83d\udee1 8 scudo  |  3 mana";
            case "Mulinello"                -> "\u2694 5 danni + \u2764 5 cura  |  2 mana";
            case "Mulinello+"               -> "\u2694 7 danni + \u2764 7 cura  |  2 mana";
            case "Furia Berserker"          -> "\u2694 7 danni (12 se HP<50%)  |  2 mana";
            case "Furia Berserker+"         -> "\u2694 9 danni (15 se HP<50%)  |  2 mana";
            case "Sfida"                    -> "\ud83d\udee1 +8 scudo  |  1 mana";
            case "Sfida+"                   -> "\ud83d\udee1 +12 scudo  |  1 mana";
            // ── 🟡 Paladino base ──
            case "Scudo Sacro"              -> "\u2694 4 danni + \ud83d\udee1 +12 scudo + \u2764 4 cura  |  2 mana";
            case "Scudo Sacro+"             -> "\u2694 5 danni + \ud83d\udee1 +14 scudo + \u2764 5 cura  |  2 mana";
            case "Luce Divina"              -> "\u2764 cura 12 + \ud83d\udee1 +4 scudo  |  2 mana";
            case "Luce Divina+"             -> "\u2764 cura 15 + \ud83d\udee1 +6 scudo  |  2 mana";
            case "Punizione Divina"         -> "\u2694 10 danni (+6 se scudo>0)  |  2 mana";
            case "Punizione Divina+"        -> "\u2694 11 danni (+8 se scudo>0)  |  2 mana";
            case "Consacrazione"            -> "\u2694 8 danni + \ud83d\udee1 +6 scudo  |  2 mana";
            case "Consacrazione+"           -> "\u2694 10 danni + \ud83d\udee1 +8 scudo  |  2 mana";
            case "Voto di Ferro"            -> "\ud83d\udee1 +12 scudo + \u2694 8 danni  |  3 mana";
            case "Voto di Ferro+"           -> "\ud83d\udee1 +15 scudo + \u2694 10 danni  |  3 mana";
            case "Castigo Sacro"            -> "\u2694 8 danni + azzera scudo nemico  |  2 mana";
            case "Castigo Sacro+"           -> "\u2694 11 danni + azzera scudo nemico  |  2 mana";
            case "Benedizione"              -> "\u2764 cura 8 + \ud83d\udee1 +6 scudo  |  2 mana";
            case "Benedizione+"             -> "\u2764 cura 10 + \ud83d\udee1 +8 scudo  |  2 mana";
            case "Martello della Giustizia" -> "\u2694 14 danni  |  3 mana";
            case "Martello della Giustizia+"-> "\u2694 18 danni  |  3 mana";
            // ── 🟣 Mago base + Plus ──
            case "Palla di Fuoco"           -> "\ud83d\udd2e 14 danni  |  2 mana";
            case "Palla di Fuoco+"          -> "\ud83d\udd2e 18 danni  |  2 mana";
            case "Nova di Fuoco"            -> "\ud83d\udd2e 18 danni  |  3 mana";
            case "Nova di Fuoco+"           -> "\ud83d\udd2e 24 danni  |  3 mana";
            case "Tempesta Arcana"          -> "\ud83d\udd2e 10 + 2\u00d7veleno danni  |  2 mana";
            case "Tempesta Arcana+"         -> "\ud83d\udd2e 13 + 3\u00d7veleno danni  |  2 mana";
            case "Dardo di Ghiaccio"        -> "\ud83d\udd2e 7 danni + \u2620 2 veleno  |  2 mana";
            case "Dardo di Ghiaccio+"       -> "\ud83d\udd2e 9 danni + \u2620 3 veleno  |  2 mana";
            case "Scudo di Mana"            -> "\ud83d\udee1 +10 scudo  |  1 mana";
            case "Scudo di Mana+"           -> "\ud83d\udee1 +14 scudo  |  2 mana";
            // ── 🟠 Dracomante base + Plus ──
            case "Zanna di Drago"           -> "\ud83d\udc09 4 danni + \ud83d\udee1 2 scudo  |  1 mana";
            case "Zanna di Drago+"          -> "\ud83d\udc09 6 danni + \ud83d\udee1 4 scudo  |  1 mana";
            case "Artiglio del Drago"       -> "\ud83d\udc09 6 danni + \ud83d\udee1 4 scudo  |  2 mana";
            case "Artiglio del Drago+"      -> "\ud83d\udc09 9 danni + \ud83d\udee1 6 scudo  |  2 mana";
            case "Soffio del Drago"         -> "\ud83d\udc09 9 danni + \u2620 3 veleno  |  2 mana";
            case "Soffio del Drago+"        -> "\ud83d\udc09 12 danni + \u2620 5 veleno  |  2 mana";
            case "Armatura di Scaglie"      -> "\ud83d\udee1 +8 scudo + \u2764 4 cura  |  2 mana";
            case "Armatura di Scaglie+"     -> "\ud83d\udee1 +12 scudo + \u2764 6 cura  |  2 mana";
            // ── 🟢 Assassino base + Plus ──
            case "Lama Avvelenata"          -> "\u2694 3 danni + \u2620 3 veleno  |  1 mana";
            case "Lama Avvelenata+"         -> "\u2694 3 danni + \u2620 5 veleno  |  1 mana";
            case "Passo nell'Ombra"         -> "\u2694 4 danni + \u2620 5 veleno  |  2 mana";
            case "Passo nell'Ombra+"        -> "\u2694 5 danni + \u2620 7 veleno  |  2 mana";
            case "Colpo Letale"             -> "\u2694 6 + veleno nemico danni  |  2 mana";
            case "Colpo Letale+"            -> "\u2694 8 + veleno nemico danni  |  2 mana";
            case "Veleno Acido"             -> "\u2620 +4 veleno (no danno diretto)  |  1 mana";
            case "Veleno Acido+"            -> "\u2620 +6 veleno (no danno diretto)  |  1 mana";
            case "Doppia Lama"              -> "\u2694 4+4 danni (2 colpi)  |  2 mana";
            case "Doppia Lama+"             -> "\u2694 6+6 danni (2 colpi)  |  2 mana";
            // ── 🟦 Neutrale ──
            case "Pozione Rapida"           -> "\u2764 cura 10 HP  |  1 mana";
            default                         -> "";
        };
    }
}
