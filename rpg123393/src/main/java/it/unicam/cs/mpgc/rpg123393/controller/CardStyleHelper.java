package it.unicam.cs.mpgc.rpg123393.controller;

/**
 * Colori canonici per classe — allineati a class-select-view.fxml:
 *
 *   #e74c3c  🔴 Guerriero
 *   #f1c40f  🟡 Paladino
 *   #9b59b6  🟣 Mago
 *   #e67e22  🟠 Dracomante
 *   #27ae60  🟢 Assassino
 *   #c77dff  ⚡ Neutrale
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
            // ⚡ Neutrale
            case "Pozione Rapida"                              -> "#c77dff";
            default                                            -> "#888888";
        };
    }

    public static String symbol(String name) {
        return switch (borderColor(name)) {
            case "#9aaaba" -> "★";
            case "#e74c3c" -> "⚔";
            case "#f1c40f" -> "🛡";
            case "#9b59b6" -> "🔮";
            case "#e67e22" -> "🐉";
            case "#27ae60" -> "☠";
            case "#c77dff" -> "+";
            default        -> "?";
        };
    }

    public static String description(String name) {
        return switch (name) {
            // ── ⚪ Starter ──
            case "Colpo di Spada"           -> "⚔ 6 danni  |  1 mana";
            case "Colpo di Spada+"          -> "⚔ 9 danni  |  1 mana";
            case "Scudo di Legno"           -> "🛡 +6 scudo  |  1 mana";
            case "Scudo di Legno+"          -> "🛡 +9 scudo  |  1 mana";
            // ── 🔴 Guerriero base ──
            case "Colpo Devastante"         -> "⚔ 12 danni  |  2 mana";
            case "Colpo Devastante+"        -> "⚔ 16 danni  |  2 mana";
            case "Grida di Battaglia"       -> "⚔ 8 danni + 🛡 6 scudo  |  3 mana";
            case "Grida di Battaglia+"      -> "⚔ 10 danni + 🛡 8 scudo  |  3 mana";
            case "Mulinello"                -> "⚔ 5 danni + ❤ 5 cura  |  2 mana";
            case "Mulinello+"               -> "⚔ 7 danni + ❤ 7 cura  |  2 mana";
            case "Furia Berserker"          -> "⚔ 7 danni (12 se HP<50%)  |  2 mana";
            case "Furia Berserker+"         -> "⚔ 9 danni (15 se HP<50%)  |  2 mana";
            case "Sfida"                    -> "🛡 +8 scudo  |  1 mana";
            case "Sfida+"                   -> "🛡 +12 scudo  |  1 mana";
            // ── 🟡 Paladino base ──
            case "Scudo Sacro"              -> "⚔ 4 danni + 🛡 +12 scudo + ❤ 4 cura  |  2 mana";
            case "Scudo Sacro+"             -> "⚔ 5 danni + 🛡 +14 scudo + ❤ 5 cura  |  2 mana";
            case "Luce Divina"              -> "❤ cura 12 + 🛡 +4 scudo  |  2 mana";
            case "Luce Divina+"             -> "❤ cura 15 + 🛡 +6 scudo  |  2 mana";
            case "Punizione Divina"         -> "⚔ 10 danni (+6 se scudo>0)  |  2 mana";
            case "Punizione Divina+"        -> "⚔ 11 danni (+8 se scudo>0)  |  2 mana";
            case "Consacrazione"            -> "⚔ 8 danni + 🛡 +6 scudo  |  2 mana";
            case "Consacrazione+"           -> "⚔ 10 danni + 🛡 +8 scudo  |  2 mana";
            case "Voto di Ferro"            -> "🛡 +12 scudo + ⚔ 8 danni  |  3 mana";
            case "Voto di Ferro+"           -> "🛡 +15 scudo + ⚔ 10 danni  |  3 mana";
            case "Castigo Sacro"            -> "⚔ 8 danni + azzera scudo nemico  |  2 mana";
            case "Castigo Sacro+"           -> "⚔ 11 danni + azzera scudo nemico  |  2 mana";
            case "Benedizione"              -> "❤ cura 8 + 🛡 +6 scudo  |  2 mana";
            case "Benedizione+"             -> "❤ cura 10 + 🛡 +8 scudo  |  2 mana";
            case "Martello della Giustizia" -> "⚔ 14 danni  |  3 mana";
            case "Martello della Giustizia+"-> "⚔ 18 danni  |  3 mana";
            // ── 🟣 Mago base + Plus ──
            case "Palla di Fuoco"           -> "🔮 14 danni  |  2 mana";
            case "Palla di Fuoco+"          -> "🔮 18 danni  |  2 mana";
            case "Nova di Fuoco"            -> "🔮 18 danni  |  3 mana";
            case "Nova di Fuoco+"           -> "🔮 24 danni  |  3 mana";
            case "Tempesta Arcana"          -> "🔮 10 + 2×veleno danni  |  2 mana";
            case "Tempesta Arcana+"         -> "🔮 13 + 3×veleno danni  |  2 mana";
            case "Dardo di Ghiaccio"        -> "🔮 7 danni + ☠ 2 veleno  |  2 mana";
            case "Dardo di Ghiaccio+"       -> "🔮 9 danni + ☠ 3 veleno  |  2 mana";
            case "Scudo di Mana"            -> "🛡 +10 scudo + ✨ 1 mana  |  2 mana";
            case "Scudo di Mana+"           -> "🛡 +14 scudo + ✨ 1 mana  |  2 mana";
            // ── 🟠 Dracomante base + Plus ──
            case "Zanna di Drago"           -> "🐉 4 danni + 🛡 2 scudo  |  1 mana";
            case "Zanna di Drago+"          -> "🐉 6 danni + 🛡 4 scudo  |  1 mana";
            case "Artiglio del Drago"       -> "🐉 6 danni + 🛡 4 scudo  |  2 mana";
            case "Artiglio del Drago+"      -> "🐉 9 danni + 🛡 6 scudo  |  2 mana";
            case "Soffio del Drago"         -> "🐉 9 danni + ☠ 3 veleno  |  2 mana";
            case "Soffio del Drago+"        -> "🐉 12 danni + ☠ 5 veleno  |  2 mana";
            case "Armatura di Scaglie"      -> "🛡 +8 scudo + ❤ 4 cura  |  2 mana";
            case "Armatura di Scaglie+"     -> "🛡 +12 scudo + ❤ 6 cura  |  2 mana";
            // ── 🟢 Assassino base + Plus ──
            case "Lama Avvelenata"          -> "⚔ 3 danni + ☠ 3 veleno  |  1 mana";
            case "Lama Avvelenata+"         -> "⚔ 3 danni + ☠ 5 veleno  |  1 mana";
            case "Passo nell'Ombra"         -> "⚔ 4 danni + ☠ 5 veleno  |  2 mana";
            case "Passo nell'Ombra+"        -> "⚔ 5 danni + ☠ 7 veleno  |  2 mana";
            case "Colpo Letale"             -> "⚔ 6 + veleno nemico danni  |  2 mana";
            case "Colpo Letale+"            -> "⚔ 8 + veleno nemico danni  |  2 mana";
            case "Veleno Acido"             -> "☠ +4 veleno (no danno diretto)  |  1 mana";
            case "Veleno Acido+"            -> "☠ +6 veleno (no danno diretto)  |  1 mana";
            case "Doppia Lama"              -> "⚔ 4+4 danni (2 colpi)  |  2 mana";
            case "Doppia Lama+"             -> "⚔ 6+6 danni (2 colpi)  |  2 mana";
            // ── ⚡ Neutrale ──
            case "Pozione Rapida"           -> "❤ cura 10 HP  |  1 mana";
            default                         -> "";
        };
    }
}
