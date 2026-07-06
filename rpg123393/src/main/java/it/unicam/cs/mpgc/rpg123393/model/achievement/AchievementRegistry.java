package it.unicam.cs.mpgc.rpg123393.model.achievement;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Registro statico di tutti gli achievement del gioco.
 *
 * <p>Gli achievement sono suddivisi in:
 * <ul>
 *   <li><b>Progressione run</b> — legati al numero di run avviate/completate e situazioni di sopravvivenza</li>
 *   <li><b>Classe</b> — legati al completamento di run con classi specifiche</li>
 *   <li><b>Combattimento</b> — legati a statistiche di battaglia accumulate nel tempo</li>
 *   <li><b>Collezione e progressione globale</b> — carte sbloccate, oro accumulato, fucina usata</li>
 *   <li><b>Sfide per singola run</b> — comportamenti particolari durante una singola run</li>
 *   <li><b>Segreti</b> — nascosti finche' non sbloccati, legati a percorsi o situazioni speciali</li>
 * </ul>
 */
public final class AchievementRegistry {

    private static final Map<String, Achievement> BY_ID;
    private static final List<Achievement>        ALL;

    static {
        Map<String, Achievement> map = new LinkedHashMap<>();

        // -------------------------------------------------------
        // PROGRESSIONE RUN
        // -------------------------------------------------------
        reg(map, new Achievement("first_steps",
                "Iniziazione",
                "Inizia la tua prima run.",
                "\uD83D\uDEAA")); // 🚪

        reg(map, new Achievement("first_run",
                "Primo Passo",
                "Completa la tua prima run.",
                "\uD83C\uDFC6")); // 🏆

        reg(map, new Achievement("ten_runs",
                "Veterano del Dungeon",
                "Completa 10 run.",
                "\uD83D\uDD31")); // 🔱

        reg(map, new Achievement("survivor",
                "Sopravvissuto",
                "Sopravvivi con meno di 10 HP alla fine di un combattimento.",
                "\u2764\uFE0F")); // ❤️

        // -------------------------------------------------------
        // CLASSE
        // -------------------------------------------------------
        reg(map, new Achievement("cavaliere_run",
                "Lama d'Acciaio",
                "Completa una run con il Cavaliere.",
                "\u2694\uFE0F")); // ⚔️

        reg(map, new Achievement("mago_run",
                "Artefice dell'Arcano",
                "Completa una run con il Mago.",
                "\uD83E\uDDD9")); // 🧙

        reg(map, new Achievement("paladino_run",
                "Scudo della Fede",
                "Completa una run con il Paladino.",
                "\uD83D\uDEE1\uFE0F")); // 🛡️

        reg(map, new Achievement("assassino_run",
                "Ombra Silente",
                "Completa una run con l'Assassino.",
                "\uD83D\uDDE1\uFE0F")); // 🗡️

        reg(map, new Achievement("dracomante_run",
                "Sangue del Drago",
                "Completa una run con il Dracomante.",
                "\uD83D\uDC09")); // 🐉

        reg(map, new Achievement("all_classes",
                "Maestro delle Arti",
                "Completa almeno una run con ogni classe.",
                "\uD83C\uDF1F")); // 🌟

        // -------------------------------------------------------
        // COMBATTIMENTO
        // -------------------------------------------------------
        reg(map, new Achievement("no_damage_fight",
                "Intoccabile",
                "Vinci un combattimento senza subire danni.",
                "\uD83D\uDCA8")); // 💨

        reg(map, new Achievement("boss_slayer",
                "Cacciatore di Boss",
                "Sconfiggi 10 boss in totale.",
                "\uD83D\uDC80")); // 💀

        reg(map, new Achievement("hundred_enemies",
                "Sterminatore",
                "Sconfiggi 100 nemici in totale.",
                "\u2694\uFE0F")); // ⚔️

        reg(map, new Achievement("poison_master",
                "Maestro del Veleno",
                "Uccidi un nemico con il danno da veleno.",
                "\uD83D\uDC22")); // 🐢

        // -------------------------------------------------------
        // COLLEZIONE E PROGRESSIONE GLOBALE
        // -------------------------------------------------------
        reg(map, new Achievement("twenty_cards",
                "Collezionista",
                "Sblocca 20 carte diverse.",
                "\uD83C\uDCCF")); // 🃏

        reg(map, new Achievement("unlock_all_cards",
                "Biblioteca Completa",
                "Sblocca tutte le carte del gioco.",
                "\uD83D\uDCDA")); // 📚

        reg(map, new Achievement("rich",
                "Mercante di Fortuna",
                "Accumula 500 oro in totale tra tutte le run.",
                "\uD83E\uDE99")); // 🪙

        reg(map, new Achievement("upgrade_addict",
                "Artigiano",
                "Usa la fucina 20 volte in totale.",
                "\uD83D\uDD28")); // 🔨

        // -------------------------------------------------------
        // SFIDE PER SINGOLA RUN
        // -------------------------------------------------------
        reg(map, new Achievement("no_shop",
                "Ascetico",
                "Completa una run senza acquistare nulla al negozio.",
                "\uD83D\uDEAB")); // 🚫

        reg(map, new Achievement("no_forge",
                "Purista",
                "Completa una run senza usare mai la fucina.",
                "\u2B50")); // ⭐

        reg(map, new Achievement("pacifist_shop",
                "Solo Reliquie",
                "Completa una run acquistando solo reliquie (nessuna carta al negozio).",
                "\uD83D\uDC8E")); // 💎

        reg(map, new Achievement("full_hp_boss",
                "Impermeabile",
                "Affronta il boss finale con gli HP al massimo.",
                "\uD83D\uDC9A")); // 💚

        reg(map, new Achievement("speed_run",
                "Fulmine",
                "Completa una run visitando meno di 15 nodi.",
                "\u26A1")); // ⚡

        // -------------------------------------------------------
        // SEGRETI
        // -------------------------------------------------------
        reg(map, new Achievement("void_heart",
                "Cuore di Vuoto",
                "Hai ottenuto il Void Heart nell'Abisso.",
                true,
                "\u2728 Qualcosa pulsa nell'oscurit\u00e0\u2026",
                "\uD83D\uDDA4")); // 🖤

        reg(map, new Achievement("hollow_knight",
                "Il Cavaliere Vacuo",
                "Hai sconfitto il tuo riflesso nell'Abisso.",
                true,
                "\uD83D\uDD2E Uno specchio non mente mai.",
                "\u26AB")); // ⚫

        reg(map, new Achievement("void_rejected",
                "Rifiuto dell'Abisso",
                "Hai guardato l'Abisso negli occhi\u2026 e hai scelto di voltarti.",
                true,
                "\uD83C\uDF00 Non tutto ci\u00f2 che brilla va raccolto.",
                "\uD83D\uDD34")); // 🔴

        reg(map, new Achievement("die_first",
                "Umiliazione Totale",
                "Sei morto contro il primo nemico della run. Forse non sei tagliato per questo.",
                true,
                "\uD83E\uDD14 Forse non sei tagliato per questo.",
                "\uD83D\uDCA9")); // 💩

        reg(map, new Achievement("golden_run",
                "Re del Dungeon",
                "Hai finito una run con pi\u00f9 di 300 oro in tasca.",
                true,
                "\uD83D\uDCB0 L'oro non basta mai.",
                "\uD83D\uDC51")); // 👑

        reg(map, new Achievement("zero_mana",
                "A Mani Nude",
                "Hai vinto un combattimento finendo con 0 mana e nessuna carta giocabile.",
                true,
                "\uD83D\uDCAA La forza non viene dalle carte.",
                "\uD83D\uDD25")); // 🔥

        BY_ID = Collections.unmodifiableMap(map);
        ALL   = Collections.unmodifiableList(List.copyOf(map.values()));
    }

    private AchievementRegistry() {}

    private static void reg(Map<String, Achievement> map, Achievement a) {
        map.put(a.getId(), a);
    }

    /** Restituisce tutti gli achievement nell'ordine di registrazione. */
    public static List<Achievement> getAll() { return ALL; }

    /** Restituisce l'achievement con l'id dato, o {@code null} se non esiste. */
    public static Achievement getById(String id) { return BY_ID.get(id); }

    /** Numero totale di achievement. */
    public static int count() { return ALL.size(); }
}
