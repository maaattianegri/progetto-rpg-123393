package it.unicam.cs.mpgc.rpg123393.persistence;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistenza dedicata agli achievement, separata dal savegame di run.
 * Scrive/legge ~/.eldoria/achievements.json con un formato JSON minimale.
 *
 * Usare questo store invece di integrare i dati achievement nel GameState
 * elimina qualsiasi rischio di sovrascrittura reciproca tra il save di gioco
 * e i progressi achievement.
 */
public class AchievementStore {

    private static final String SAVE_DIR  = System.getProperty("user.home") + File.separator + ".eldoria";
    private static final String FILE_PATH = SAVE_DIR + File.separator + "achievements.json";

    public boolean exists() {
        return Files.exists(Paths.get(FILE_PATH));
    }

    public AchievementData load() throws IOException {
        if (!exists()) return new AchievementData();
        String json = Files.readString(Paths.get(FILE_PATH), StandardCharsets.UTF_8);
        return fromJson(json);
    }

    public void save(AchievementData data) throws IOException {
        Files.createDirectories(Paths.get(SAVE_DIR));
        Files.writeString(Paths.get(FILE_PATH), toJson(data), StandardCharsets.UTF_8);
    }

    // -------------------------------------------------------
    // Serializzazione
    // -------------------------------------------------------

    private String toJson(AchievementData d) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"unlocked\": ").append(arrayToJson(d.unlocked)).append(",\n");
        sb.append("  \"classesCompleted\": ").append(arrayToJson(d.classesCompleted)).append(",\n");
        sb.append("  \"totalEnemiesDefeated\": ").append(d.totalEnemiesDefeated).append(",\n");
        sb.append("  \"totalBossesDefeated\": ").append(d.totalBossesDefeated).append(",\n");
        sb.append("  \"totalRunsCompleted\": ").append(d.totalRunsCompleted).append(",\n");
        sb.append("  \"totalGoldEarned\": ").append(d.totalGoldEarned).append(",\n");
        sb.append("  \"totalUpgradesUsed\": ").append(d.totalUpgradesUsed).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private AchievementData fromJson(String json) {
        AchievementData d = new AchievementData();
        // Leggi campi scalari riga per riga
        for (String rawLine : json.split("\n")) {
            String line = rawLine.trim().replaceAll(",?$", "");
            if      (line.startsWith("\"totalEnemiesDefeated\"")) d.totalEnemiesDefeated = intVal(line);
            else if (line.startsWith("\"totalBossesDefeated\""))  d.totalBossesDefeated  = intVal(line);
            else if (line.startsWith("\"totalRunsCompleted\""))   d.totalRunsCompleted   = intVal(line);
            else if (line.startsWith("\"totalGoldEarned\""))      d.totalGoldEarned      = intVal(line);
            else if (line.startsWith("\"totalUpgradesUsed\""))    d.totalUpgradesUsed    = intVal(line);
        }
        // Leggi array
        d.unlocked        = parseArray(json, "unlocked");
        d.classesCompleted = parseArray(json, "classesCompleted");
        return d;
    }

    private String arrayToJson(List<String> list) {
        if (list == null || list.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append("\"").append(escape(list.get(i))).append("\"");
            if (i < list.size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    private List<String> parseArray(String json, String field) {
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

    private int intVal(String line) {
        try { return Integer.parseInt(line.split(":", 2)[1].trim()); }
        catch (Exception e) { return 0; }
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // -------------------------------------------------------
    // DTO
    // -------------------------------------------------------

    public static class AchievementData {
        public List<String> unlocked         = new ArrayList<>();
        public List<String> classesCompleted = new ArrayList<>();
        public int totalEnemiesDefeated = 0;
        public int totalBossesDefeated  = 0;
        public int totalRunsCompleted   = 0;
        public int totalGoldEarned      = 0;
        public int totalUpgradesUsed    = 0;
    }
}
