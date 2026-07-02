package it.unicam.cs.mpgc.rpg123393.controller;

/**
 * Colori e descrizioni per tutte le carte del gioco.
 *
 * Logica colori:
 *   #e74c3c  🔴 Rosso     → Guerriero (danni fisici)
 *   #3498db  🔵 Blu       → Paladino  (scudo/cura)
 *   #e67e22  🟠 Arancione → Mago / Dracomante (fuoco/magia)
 *   #27ae60  🟢 Verde     → Assassino (veleno)
 *   #c77dff  🟣 Viola     → Neutrale  (utilità)
 */
public class CardStyleHelper {

    public static String borderColor(String name) {
        return switch (name) {
            // 🔴 Guerriero
            case "Colpo di Spada", "Colpo di Spada+",
                 "Colpo Devastante", "Colpo Devastante+",
                 "Grida di Battaglia", "Mulinello",
                 "Furia Berserker", "Sfida" -> "#e74c3c";
            // 🔵 Paladino
            case "Scudo di Legno", "Scudo di Legno+",
                 "Scudo Sacro", "Luce Divina",
                 "Punizione Divina", "Consacrazione", "Voto di Ferro" -> "#3498db";
            // 🟠 Mago / Dracomante
            case "Palla di Fuoco", "Palla di Fuoco+",
                 "Tempesta Arcana", "Dardo di Ghiaccio", "Scudo di Mana", "Nova di Fuoco",
                 "Artiglio del Drago", "Soffio del Drago",
                 "Armatura di Scaglie", "Zanna di Drago" -> "#e67e22";
            // 🟢 Assassino
            case "Lama Avvelenata", "Lama Avvelenata+",
                 "Passo nell'Ombra", "Colpo Letale",
                 "Veleno Acido", "Doppia Lama" -> "#27ae60";
            // 🟣 Neutrale
            case "Pozione Rapida" -> "#c77dff";
            default -> "#888888";
        };
    }

    public static String symbol(String name) {
        return switch (borderColor(name)) {
            case "#e74c3c" -> "⚔";
            case "#3498db" -> "🛡";
            case "#e67e22" -> "🔥";
            case "#27ae60" -> "☠";
            case "#c77dff" -> "+";
            default        -> "?";
        };
    }

    public static String description(String name) {
        return switch (name) {
            // ── 🔴 Guerriero ──
            case "Colpo di Spada"       -> "⚔ 6 danni  |  1 mana";
            case "Colpo di Spada+"      -> "⚔ 9 danni  |  1 mana";
            case "Colpo Devastante"     -> "⚔ 12 danni  |  2 mana";
            case "Colpo Devastante+"    -> "⚔ 16 danni  |  2 mana";
            case "Grida di Battaglia"   -> "⚔ 8 danni + 🛡 6 scudo  |  3 mana";
            case "Mulinello"            -> "⚔ 5 danni + ❤ 5 cura  |  2 mana";
            case "Furia Berserker"      -> "⚔ 7 danni (12 se HP<50%)  |  2 mana";
            case "Sfida"                -> "🛡 +8 scudo  |  1 mana";
            // ── 🔵 Paladino ──
            case "Scudo di Legno"       -> "🛡 +6 scudo  |  1 mana";
            case "Scudo di Legno+"      -> "🛡 +9 scudo  |  1 mana";
            case "Scudo Sacro"          -> "🛡 +12 scudo + ❤ 4 cura  |  2 mana";
            case "Luce Divina"          -> "❤ cura 12 + 🛡 4 scudo  |  2 mana";
            case "Punizione Divina"     -> "⚔ 10 danni (+4 se scudo>0)  |  2 mana";
            case "Consacrazione"        -> "⚔ 6 danni + 🛡 6 scudo  |  2 mana";
            case "Voto di Ferro"        -> "🛡 +20 scudo  |  3 mana";
            // ── 🟠 Mago ──
            case "Palla di Fuoco"       -> "🔥 14 danni  |  2 mana";
            case "Palla di Fuoco+"      -> "🔥 18 danni  |  2 mana";
            case "Tempesta Arcana"      -> "🔥 10 + 2×veleno danni  |  2 mana";
            case "Dardo di Ghiaccio"    -> "🔥 7 danni + ☠ 2 veleno  |  2 mana";
            case "Scudo di Mana"        -> "🛡 +10 scudo + ✨ 1 mana  |  2 mana";
            case "Nova di Fuoco"        -> "🔥 18 danni  |  3 mana";
            // ── 🟠 Dracomante ──
            case "Artiglio del Drago"   -> "🔥 6 danni + 🛡 4 scudo  |  2 mana";
            case "Soffio del Drago"     -> "🔥 9 danni + ☠ 3 veleno  |  2 mana";
            case "Armatura di Scaglie"  -> "🛡 +8 scudo + ❤ 4 cura  |  2 mana";
            case "Zanna di Drago"       -> "🔥 4 danni + 🛡 2 scudo  |  1 mana";
            // ── 🟢 Assassino ──
            case "Lama Avvelenata"      -> "⚔ 3 danni + ☠ 3 veleno  |  1 mana";
            case "Lama Avvelenata+"     -> "⚔ 3 danni + ☠ 5 veleno  |  1 mana";
            case "Passo nell'Ombra"     -> "⚔ 4 danni + ☠ 5 veleno  |  2 mana";
            case "Colpo Letale"         -> "⚔ 6 + veleno nemico danni  |  2 mana";
            case "Veleno Acido"         -> "☠ +4 veleno (no danno diretto)  |  1 mana";
            case "Doppia Lama"          -> "⚔ 4+4 danni (2 colpi)  |  2 mana";
            // ── 🟣 Neutrale ──
            case "Pozione Rapida"       -> "❤ cura 10 HP  |  1 mana";
            default                     -> "";
        };
    }
}
