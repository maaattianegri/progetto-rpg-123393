package it.unicam.cs.mpgc.rpg123393.persistence;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class JsonSaveRepository implements SaveRepository {

    private static final String SAVE_DIR  = System.getProperty("user.home") + File.separator + ".eldoria";
    private static final String SAVE_FILE = SAVE_DIR + File.separator + "savegame.json";

    @Override
    public void save(GameState state) throws IOException {
        Files.createDirectories(Paths.get(SAVE_DIR));
        Files.writeString(Paths.get(SAVE_FILE), toJson(state), StandardCharsets.UTF_8);
    }

    @Override
    public GameState load() throws IOException {
        if (!saveExists()) return null;
        return fromJson(Files.readString(Paths.get(SAVE_FILE), StandardCharsets.UTF_8));
    }

    @Override
    public boolean saveExists() {
        return Files.exists(Paths.get(SAVE_FILE));
    }

    @Override
    public void deleteSave() {
        try {
            Files.deleteIfExists(Paths.get(SAVE_FILE));
        } catch (IOException e) {
            System.err.println("[WARN] Impossibile cancellare il save: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // Serializzazione
    // -------------------------------------------------------

    private String toJson(GameState s) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"playerName\": \"").append(escape(s.getPlayerName())).append("\",\n");
        sb.append("  \"playerMaxHp\": ").append(s.getPlayerMaxHp()).append(",\n");
        sb.append("  \"playerCurrentHp\": ").append(s.getPlayerCurrentHp()).append(",\n");
        sb.append("  \"playerMaxMana\": ").append(s.getPlayerMaxMana()).append(",\n");
        sb.append("  \"playerCurrentMana\": ").append(s.getPlayerCurrentMana()).append(",\n");
        sb.append("  \"playerLevel\": ").append(s.getPlayerLevel()).append(",\n");
        sb.append("  \"playerXp\": ").append(s.getPlayerXp()).append(",\n");
        sb.append("  \"saveDate\": \"").append(escape(s.getSaveDate())).append("\",\n");
        sb.append("  \"className\": \"").append(escape(s.getClassName())).append("\",\n");
        sb.append("  \"imagePath\": \"").append(escape(s.getImagePath())).append("\",\n");
        sb.append("  \"unlockedCards\": [");
        List<String> cards = s.getUnlockedCards();
        for (int i = 0; i < cards.size(); i++) {
            sb.append("\"").append(escape(cards.get(i))).append("\"");
            if (i < cards.size() - 1) sb.append(", ");
        }
        sb.append("]\n}");
        return sb.toString();
    }

    private GameState fromJson(String json) {
        GameState s = new GameState();
        for (String line : json.split("\n")) {
            line = line.trim().replaceAll(",?$", "");
            if      (line.startsWith("\"playerName\""))       s.setPlayerName(stringValue(line));
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
        s.setUnlockedCards(parseStringArray(json, "unlockedCards"));
        return s;
    }

    private List<String> parseStringArray(String json, String field) {
        List<String> result = new ArrayList<>();
        String marker = "\"" + field + "\": [";
        int start = json.indexOf(marker);
        if (start < 0) return result;
        int arrStart = json.indexOf('[', start);
        int arrEnd   = json.indexOf(']', arrStart);
        if (arrStart < 0 || arrEnd < 0) return result;
        String content = json.substring(arrStart + 1, arrEnd).trim();
        if (content.isEmpty()) return result;
        for (String token : content.split(",")) {
            token = token.trim();
            if (token.startsWith("\"") && token.endsWith("\""))
                result.add(token.substring(1, token.length() - 1));
        }
        return result;
    }

    private int intValue(String line) {
        return Integer.parseInt(line.split(":", 2)[1].trim());
    }

    private String stringValue(String line) {
        int first = line.indexOf('"', line.indexOf(':'));
        int last  = line.lastIndexOf('"');
        if (first < 0 || first == last) return "";
        return line.substring(first + 1, last);
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
