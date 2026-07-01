package it.unicam.cs.mpgc.rpg123393.persistence;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

/**
 * Implementazione di SaveRepository che salva e carica
 * lo stato della partita in un file JSON.
 *
 * La serializzazione viene fatta manualmente (senza librerie esterne)
 * per evitare dipendenze aggiuntive nel build.gradle.
 *
 * Il file viene salvato nella home dell'utente:
 *   Windows: C:\Users\<nome>\.eldoria\savegame.json
 *   Linux/Mac: ~/.eldoria/savegame.json
 */
public class JsonSaveRepository implements SaveRepository {

    private static final String SAVE_DIR  = System.getProperty("user.home") + File.separator + ".eldoria";
    private static final String SAVE_FILE = SAVE_DIR + File.separator + "savegame.json";

    @Override
    public void save(GameState state) throws IOException {
        Files.createDirectories(Paths.get(SAVE_DIR));
        String json = toJson(state);
        Files.writeString(Paths.get(SAVE_FILE), json, StandardCharsets.UTF_8);
    }

    @Override
    public GameState load() throws IOException {
        if (!saveExists()) return null;
        String json = Files.readString(Paths.get(SAVE_FILE), StandardCharsets.UTF_8);
        return fromJson(json);
    }

    @Override
    public boolean saveExists() {
        return Files.exists(Paths.get(SAVE_FILE));
    }

    // -------------------------------------------------------
    // Serializzazione manuale JSON
    // -------------------------------------------------------

    /**
     * Converte un GameState in stringa JSON.
     * Formato semplice senza librerie esterne.
     */
    private String toJson(GameState s) {
        return "{\n" +
            "  \"playerName\": \""       + escape(s.getPlayerName())   + "\",\n" +
            "  \"playerMaxHp\": "        + s.getPlayerMaxHp()          + ",\n" +
            "  \"playerCurrentHp\": "    + s.getPlayerCurrentHp()      + ",\n" +
            "  \"playerMaxMana\": "      + s.getPlayerMaxMana()        + ",\n" +
            "  \"playerCurrentMana\": "  + s.getPlayerCurrentMana()    + ",\n" +
            "  \"playerLevel\": "        + s.getPlayerLevel()          + ",\n" +
            "  \"playerXp\": "           + s.getPlayerXp()             + ",\n" +
            "  \"saveDate\": \""         + escape(s.getSaveDate())     + "\",\n" +
            "  \"className\": \""        + escape(s.getClassName())    + "\",\n" +
            "  \"imagePath\": \""        + escape(s.getImagePath())    + "\"\n" +
            "}";
    }

    /**
     * Converte una stringa JSON in GameState.
     * Parsing riga per riga: robusto per il formato prodotto da toJson().
     */
    private GameState fromJson(String json) {
        GameState s = new GameState();
        for (String line : json.split("\n")) {
            line = line.trim().replaceAll(",?$", ""); // rimuovi virgola finale
            if (line.startsWith("\"playerName\""))       s.setPlayerName(stringValue(line));
            else if (line.startsWith("\"playerMaxHp\""))      s.setPlayerMaxHp(intValue(line));
            else if (line.startsWith("\"playerCurrentHp\""))  s.setPlayerCurrentHp(intValue(line));
            else if (line.startsWith("\"playerMaxMana\""))    s.setPlayerMaxMana(intValue(line));
            else if (line.startsWith("\"playerCurrentMana\""))s.setPlayerCurrentMana(intValue(line));
            else if (line.startsWith("\"playerLevel\""))      s.setPlayerLevel(intValue(line));
            else if (line.startsWith("\"playerXp\""))         s.setPlayerXp(intValue(line));
            else if (line.startsWith("\"saveDate\""))         s.setSaveDate(stringValue(line));
            else if (line.startsWith("\"className\""))        s.setClassName(stringValue(line));
            else if (line.startsWith("\"imagePath\""))        s.setImagePath(stringValue(line));
        }
        return s;
    }

    // --- Utility parsing ---

    /** Estrae il valore intero da una riga tipo: "campo": 42 */
    private int intValue(String line) {
        String[] parts = line.split(":", 2);
        return Integer.parseInt(parts[1].trim());
    }

    /** Estrae il valore stringa da una riga tipo: "campo": "valore" */
    private String stringValue(String line) {
        int first = line.indexOf('"', line.indexOf(':'));
        int last  = line.lastIndexOf('"');
        if (first < 0 || first == last) return "";
        return line.substring(first + 1, last);
    }

    /** Escapa i caratteri speciali JSON nelle stringhe. */
    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
