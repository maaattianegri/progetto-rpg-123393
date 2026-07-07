package it.unicam.cs.mpgc.rpg123393.model.achievement;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Registro centralizzato di tutti gli achievement del gioco.
 * Tutti i 30 achievement sono definiti qui con id, nome, descrizione, icona e categoria.
 */
public final class AchievementRegistry {

    private static final List<Achievement> ALL = new ArrayList<>();
    private static final Map<String, Achievement> BY_ID = new LinkedHashMap<>();

    static {
        // -------------------------------------------------------
        // COMBATTIMENTO
        // -------------------------------------------------------
        register(new Achievement(
                "first_steps", "Primi Passi",
                "Inizia la tua prima run.",
                "\u2694\uFE0F", AchievementCategory.COMBATTIMENTO));

        register(new Achievement(
                "no_damage_fight", "Intoccabile",
                "Vinci un combattimento senza subire danni.",
                "\uD83D\uDEE1\uFE0F", AchievementCategory.COMBATTIMENTO));

        register(new Achievement(
                "boss_slayer", "Cacciatore di Boss",
                "Sconfiggi 10 boss in totale tra tutte le run.",
                "\uD83D\uDC80", AchievementCategory.COMBATTIMENTO));

        register(new Achievement(
                "hundred_enemies", "Carneficina",
                "Sconfiggi 100 nemici in totale tra tutte le run.",
                "\u2620\uFE0F", AchievementCategory.COMBATTIMENTO));

        register(new Achievement(
                "poison_master", "Maestro del Veleno",
                "Uccidi un nemico con il danno da veleno.",
                "\uD83D\uDC0D", AchievementCategory.COMBATTIMENTO));

        register(new Achievement(
                "survivor", "Sopravvissuto per un pelo",
                "Vinci un combattimento con meno di 10 HP rimanenti.",
                "\uD83E\uDE78", AchievementCategory.COMBATTIMENTO));

        register(new Achievement(
                "full_hp_boss", "Senza un graffio",
                "Sconfiggi il boss finale di una run senza aver perso HP.",
                "\uD83D\uDCAA", AchievementCategory.COMBATTIMENTO));

        // -------------------------------------------------------
        // RUN
        // -------------------------------------------------------
        register(new Achievement(
                "first_run", "Il Viaggio Comincia",
                "Completa la tua prima run.",
                "\uD83C\uDFC1", AchievementCategory.RUN));

        register(new Achievement(
                "ten_runs", "Veterano dell'Arena",
                "Completa 10 run.",
                "\uD83C\uDFC5", AchievementCategory.RUN));

        register(new Achievement(
                "speed_run", "Come un Fulmine",
                "Completa una run visitando meno di 15 nodi.",
                "\u26A1", AchievementCategory.RUN));

        register(new Achievement(
                "no_forge", "Lama Grezza",
                "Completa una run senza mai usare la fucina.",
                "\uD83D\uDD27", AchievementCategory.RUN));

        register(new Achievement(
                "no_shop", "Il Prezzo della Gloria",
                "Completa una run senza acquistare nessuna carta al negozio.",
                "\uD83D\uDEAB", AchievementCategory.RUN));

        register(new Achievement(
                "pacifist_shop", "Solo Reliquie",
                "Completa una run acquistando solo reliquie al negozio (nessuna carta).",
                "\uD83D\uDCFF", AchievementCategory.RUN));

        register(new Achievement(
                "golden_run", "Run d'Oro",
                "Termina una run con pi\u00f9 di 300 monete d'oro.",
                "\uD83D\uDCB0", AchievementCategory.RUN));

        // -------------------------------------------------------
        // CLASSI
        // -------------------------------------------------------
        register(new Achievement(
                "cavaliere_run", "Il Cavaliere",
                "Completa una run con il Cavaliere.",
                "\uD83D\uDEE1\uFE0F", AchievementCategory.CLASSI));

        register(new Achievement(
                "mago_run", "Il Mago",
                "Completa una run con il Mago.",
                "\uD83E\uDDD9", AchievementCategory.CLASSI));

        register(new Achievement(
                "paladino_run", "Il Paladino",
                "Completa una run con il Paladino.",
                "\u2728", AchievementCategory.CLASSI));

        register(new Achievement(
                "assassino_run", "L'Assassino",
                "Completa una run con l'Assassino.",
                "\uD83D\uDDE1\uFE0F", AchievementCategory.CLASSI));

        register(new Achievement(
                "dracomante_run", "Il Dracomante",
                "Completa una run con il Dracomante.",
                "\uD83D\uDC09", AchievementCategory.CLASSI));

        register(new Achievement(
                "all_classes", "Maestro delle Arti",
                "Completa almeno una run con ogni classe disponibile.",
                "\uD83C\uDF1F", AchievementCategory.CLASSI));

        // -------------------------------------------------------
        // PROGRESSIONE
        // -------------------------------------------------------
        register(new Achievement(
                "rich", "Il Grande Mercante",
                "Accumula 500 monete d'oro totali tra tutte le run.",
                "\uD83D\uDCB3", AchievementCategory.PROGRESSIONE));

        register(new Achievement(
                "twenty_cards", "Collezionista",
                "Sblocca 20 carte nella collezione.",
                "\uD83C\uDCCF", AchievementCategory.PROGRESSIONE));

        register(new Achievement(
                "unlock_all_cards", "Archivista Supremo",
                "Sblocca tutte le carte della collezione.",
                "\uD83D\uDCDA", AchievementCategory.PROGRESSIONE));

        register(new Achievement(
                "upgrade_addict", "Forgiatore Ossessivo",
                "Usa la fucina 20 volte in totale tra tutte le run.",
                "\uD83D\uDD28", AchievementCategory.PROGRESSIONE));

        // -------------------------------------------------------
        // SEGRETI
        // -------------------------------------------------------
        register(new Achievement(
                "void_heart",
                "Cuore di Vuoto",
                "Hai ottenuto il Cuore di Vuoto nel percorso del Cavaliere.",
                true,
                "Un percorso nascosto attende chi osa esplorare...",
                "\uD83D\uDDA4",
                AchievementCategory.SEGRETI));

        register(new Achievement(
                "hollow_knight",
                "Il Cavaliere Vacuo",
                "Hai sconfitto il Cavaliere Vacuo nel percorso nascosto.",
                true,
                "Un boss leggendario si cela nell'oscurit\u00e0...",
                "\u26AB",
                AchievementCategory.SEGRETI));

        register(new Achievement(
                "void_rejected",
                "Rifiuto del Vuoto",
                "Hai rifiutato il Cuore di Vuoto e scelto la tua strada.",
                true,
                "Non tutto ci\u00f2 che luccica va preso...",
                "\uD83D\uDEAB",
                AchievementCategory.SEGRETI));

        register(new Achievement(
                "die_first",
                "Vita Breve",
                "Muori al primo nodo della run.",
                true,
                "Come si pu\u00f2 perdere cos\u00ec in fretta...?",
                "\uD83D\uDC80",
                AchievementCategory.SEGRETI));

        register(new Achievement(
                "zero_mana",
                "Riserve Vuote",
                "Vinci un combattimento con 0 mana e nessuna carta giocabile in mano.",
                true,
                "C'\u00e8 un modo per vincere senza risorse?",
                "\uD83D\uDCA8",
                AchievementCategory.SEGRETI));

        register(new Achievement(
                "dracomante_run2",
                "Il Vero Dracomante",
                "Sconfiggi il boss finale con una carta Drago giocata nell'ultimo turno.",
                true,
                "Solo chi domina i draghi pu\u00f2 dirsi tale...",
                "\uD83D\uDD25",
                AchievementCategory.SEGRETI));
    }

    private AchievementRegistry() {}

    private static void register(Achievement a) {
        ALL.add(a);
        BY_ID.put(a.getId(), a);
    }

    public static List<Achievement> getAll() {
        return Collections.unmodifiableList(ALL);
    }

    public static Achievement getById(String id) {
        return BY_ID.get(id);
    }

    /** Restituisce tutti gli achievement di una determinata categoria. */
    public static List<Achievement> getByCategory(AchievementCategory category) {
        return ALL.stream()
                .filter(a -> a.getCategory() == category)
                .collect(Collectors.toList());
    }

    public static int count() {
        return ALL.size();
    }
}
