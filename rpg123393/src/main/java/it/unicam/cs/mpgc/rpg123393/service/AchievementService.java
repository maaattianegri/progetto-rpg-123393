package it.unicam.cs.mpgc.rpg123393.service;

import it.unicam.cs.mpgc.rpg123393.controller.AchievementToastManager;
import it.unicam.cs.mpgc.rpg123393.model.achievement.Achievement;
import it.unicam.cs.mpgc.rpg123393.model.achievement.AchievementRegistry;
import it.unicam.cs.mpgc.rpg123393.persistence.AchievementStore;
import it.unicam.cs.mpgc.rpg123393.persistence.AchievementStore.AchievementData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestisce lo sblocco degli achievement durante la run.
 * Usa {@link AchievementStore} per la persistenza su file separato.
 * Usa {@link AchievementToastManager} per mostrare il banner in-game.
 */
public class AchievementService {

    private final AchievementStore store = new AchievementStore();

    private boolean noForgeUsedThisRun = true;
    private boolean noShopCardThisRun  = true;
    private int     nodesVisitedThisRun = 0;

    private List<String> unlocked          = new ArrayList<>();
    private List<String> classesCompleted  = new ArrayList<>();
    private int totalEnemiesDefeated = 0;
    private int totalBossesDefeated  = 0;
    private int totalRunsCompleted   = 0;
    private int totalGoldEarned      = 0;
    private int totalUpgradesUsed    = 0;

    public AchievementService() {
        loadFromStore();
    }

    private void loadFromStore() {
        try {
            AchievementData d = store.load();
            unlocked           = new ArrayList<>(d.unlocked);
            classesCompleted   = new ArrayList<>(d.classesCompleted);
            totalEnemiesDefeated = d.totalEnemiesDefeated;
            totalBossesDefeated  = d.totalBossesDefeated;
            totalRunsCompleted   = d.totalRunsCompleted;
            totalGoldEarned      = d.totalGoldEarned;
            totalUpgradesUsed    = d.totalUpgradesUsed;
        } catch (IOException ignored) {}
    }

    // -------------------------------------------------------
    // Hook eventi
    // -------------------------------------------------------

    public void onRunStarted() {
        nodesVisitedThisRun = 0;
        noForgeUsedThisRun  = true;
        noShopCardThisRun   = true;
        unlock("first_steps");
        persist();
    }

    public void onEnemyDefeated(String enemyName, boolean playerTookNoDamage,
                                 boolean isBoss, boolean playerAtFullHp,
                                 boolean killedByPoison, boolean playerBelowTenHp,
                                 int playerCurrentHp, int playerMaxHp,
                                 int playerCurrentMana, int playerHandMana) {
        totalEnemiesDefeated++;

        if (playerTookNoDamage)     unlock("no_damage_fight");
        if (killedByPoison)         unlock("poison_master");
        if (playerBelowTenHp)       unlock("survivor");
        if (playerCurrentMana == 0) unlock("zero_mana");
        if (totalEnemiesDefeated >= 100) unlock("hundred_enemies");

        if (isBoss) {
            totalBossesDefeated++;
            if (playerAtFullHp)            unlock("full_hp_boss");
            if (totalBossesDefeated >= 10) unlock("boss_slayer");
        }
        persist();
    }

    public void onHollowKnightDefeated() {
        unlock("hollow_knight");
        persist();
    }

    public void onVoidHeartObtained() {
        unlock("void_heart");
        persist();
    }

    public void onVoidHeartRejected() {
        unlock("void_rejected");
        persist();
    }

    public void onDiedAtFirstNode() {
        unlock("die_first");
        persist();
    }

    public void onForgeUsed() {
        noForgeUsedThisRun = false;
        totalUpgradesUsed++;
        if (totalUpgradesUsed >= 20) unlock("upgrade_addict");
        persist();
    }

    public void onShopCardBought() {
        noShopCardThisRun = false;
    }

    public void onNodeVisited() {
        nodesVisitedThisRun++;
    }

    public void onRunCompleted(String className, int nodesVisited,
                               int currentGold, int totalGoldEarnedRun,
                               int unlockedCardsCount, boolean fullHpFinalBoss) {
        totalRunsCompleted++;
        unlock("first_run");
        if (totalRunsCompleted >= 10) unlock("ten_runs");
        if (nodesVisitedThisRun < 15) unlock("speed_run");
        if (noForgeUsedThisRun)       unlock("no_forge");
        if (noShopCardThisRun)        unlock("no_shop");
        if (currentGold > 300)        unlock("golden_run");
        if (fullHpFinalBoss)          unlock("full_hp_boss");

        totalGoldEarned += totalGoldEarnedRun;
        if (totalGoldEarned >= 500)        unlock("rich");
        if (unlockedCardsCount >= 20)      unlock("twenty_cards");

        if (className != null && !classesCompleted.contains(className))
            classesCompleted.add(className);

        switch (className != null ? className : "") {
            case "Cavaliere"  -> unlock("cavaliere_run");
            case "Mago"       -> unlock("mago_run");
            case "Paladino"   -> unlock("paladino_run");
            case "Assassino"  -> unlock("assassino_run");
            case "Dracomante" -> unlock("dracomante_run");
        }
        if (classesCompleted.containsAll(List.of("Cavaliere","Mago","Paladino","Assassino","Dracomante")))
            unlock("all_classes");

        persist();
    }

    public List<String> getUnlocked() { return unlocked; }

    // -------------------------------------------------------
    // Unlock + toast
    // -------------------------------------------------------

    private void unlock(String id) {
        if (unlocked.contains(id)) return;
        Achievement a = AchievementRegistry.getById(id);
        if (a == null) return;
        unlocked.add(id);
        System.out.println("[Achievement sbloccato] " + a.getName());
        AchievementToastManager.getInstance().show(id);
    }

    private void persist() {
        try {
            AchievementData d = new AchievementData();
            d.unlocked           = new ArrayList<>(unlocked);
            d.classesCompleted   = new ArrayList<>(classesCompleted);
            d.totalEnemiesDefeated = totalEnemiesDefeated;
            d.totalBossesDefeated  = totalBossesDefeated;
            d.totalRunsCompleted   = totalRunsCompleted;
            d.totalGoldEarned      = totalGoldEarned;
            d.totalUpgradesUsed    = totalUpgradesUsed;
            store.save(d);
        } catch (IOException e) {
            System.err.println("[WARN] Impossibile salvare achievement: " + e.getMessage());
        }
    }
}
