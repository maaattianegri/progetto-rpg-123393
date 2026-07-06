package it.unicam.cs.mpgc.rpg123393.service;

import it.unicam.cs.mpgc.rpg123393.model.achievement.Achievement;
import it.unicam.cs.mpgc.rpg123393.model.achievement.AchievementRegistry;
import it.unicam.cs.mpgc.rpg123393.persistence.GameState;
import it.unicam.cs.mpgc.rpg123393.persistence.JsonSaveRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestisce lo sblocco degli achievement durante la run.
 *
 * Viene istanziato da GameService e riceve notifiche di eventi tramite i metodi on*.
 * Ogni metodo controlla le condizioni rilevanti e chiama {@link #unlock(String)}
 * che persiste lo sblocco nel save file.
 *
 * I contatori cross-run (nemici uccisi totali, oro accumulato, ecc.) vengono
 * letti e scritti direttamente nel GameState persistito.
 */
public class AchievementService {

    private final JsonSaveRepository repo = new JsonSaveRepository();

    // Stato runtime della run corrente
    private int  enemiesDefeatedThisRun  = 0;
    private int  bossesDefeatedThisRun   = 0;
    private boolean noForgeUsedThisRun   = true;
    private boolean noShopCardThisRun    = true;
    private int  nodesVisitedThisRun     = 0;
    private boolean fullHpBossVictory    = false;

    // Stato cross-run (caricato da save)
    private List<String> unlocked       = new ArrayList<>();
    private int  totalEnemiesDefeated   = 0;
    private int  totalBossesDefeated    = 0;
    private int  totalRunsCompleted     = 0;
    private int  totalGoldEarned        = 0;
    private int  totalUpgradesUsed      = 0;
    private List<String> classesCompleted = new ArrayList<>();

    public AchievementService() {
        loadFromSave();
    }

    // -------------------------------------------------------
    // Caricamento stato persistito
    // -------------------------------------------------------

    private void loadFromSave() {
        try {
            if (!repo.saveExists()) return;
            GameState s = repo.load();
            if (s == null) return;
            if (s.getUnlockedAchievements() != null)
                unlocked = new ArrayList<>(s.getUnlockedAchievements());
            totalEnemiesDefeated = s.getTotalEnemiesDefeated();
            totalBossesDefeated  = s.getTotalBossesDefeated();
            totalRunsCompleted   = s.getTotalRunsCompleted();
            totalGoldEarned      = s.getTotalGoldEarned();
            totalUpgradesUsed    = s.getTotalUpgradesUsed();
            if (s.getClassesCompleted() != null)
                classesCompleted = new ArrayList<>(s.getClassesCompleted());
        } catch (IOException ignored) {}
    }

    // -------------------------------------------------------
    // Hook eventi
    // -------------------------------------------------------

    /** Chiamato all'inizio di ogni nuova run (createPlayer). */
    public void onRunStarted() {
        unlock("first_steps");
        nodesVisitedThisRun  = 0;
        noForgeUsedThisRun   = true;
        noShopCardThisRun    = true;
        fullHpBossVictory    = false;
    }

    /** Chiamato dopo ogni battaglia vinta (in handleBattleEnd di HelloController). */
    public void onEnemyDefeated(String enemyName, boolean playerTookNoDamage,
                                 boolean isBoss, boolean playerAtFullHp,
                                 boolean killedByPoison, boolean playerBelowTenHp,
                                 int playerCurrentHp, int playerMaxHp,
                                 int playerCurrentMana, int playerHandMana) {
        enemiesDefeatedThisRun++;
        totalEnemiesDefeated++;

        if (playerTookNoDamage)  unlock("no_damage_fight");
        if (killedByPoison)      unlock("poison_master");
        if (playerBelowTenHp)    unlock("survivor");
        if (playerCurrentMana == 0) unlock("zero_mana");

        if (totalEnemiesDefeated >= 100) unlock("hundred_enemies");

        if (isBoss) {
            bossesDefeatedThisRun++;
            totalBossesDefeated++;
            if (playerAtFullHp) { fullHpBossVictory = true; unlock("full_hp_boss"); }
            if (totalBossesDefeated >= 10) unlock("boss_slayer");
        }

        persistCounters();
    }

    /** Chiamato quando il Cavaliere Vacuo viene sconfitto. */
    public void onHollowKnightDefeated() {
        unlock("hollow_knight");
        persistCounters();
    }

    /** Chiamato quando il Void Heart viene ottenuto nell'evento. */
    public void onVoidHeartObtained() {
        unlock("void_heart");
        persistCounters();
    }

    /** Chiamato quando il Void Heart viene rifiutato nell'evento. */
    public void onVoidHeartRejected() {
        unlock("void_rejected");
        persistCounters();
    }

    /** Chiamato quando il giocatore muore al primo nodo. */
    public void onDiedAtFirstNode() {
        unlock("die_first");
        persistCounters();
    }

    /** Chiamato ogni volta che si usa la fucina (upgrade carta). */
    public void onForgeUsed() {
        noForgeUsedThisRun = false;
        totalUpgradesUsed++;
        if (totalUpgradesUsed >= 20) unlock("upgrade_addict");
        persistCounters();
    }

    /** Chiamato quando si acquista una carta al negozio. */
    public void onShopCardBought() {
        noShopCardThisRun = false;
    }

    /** Chiamato quando si visita un nodo della mappa. */
    public void onNodeVisited() {
        nodesVisitedThisRun++;
    }

    /** Chiamato al completamento della run (boss finale sconfitto). */
    public void onRunCompleted(String className, int nodesVisited,
                               int currentGold, int totalGoldEarnedRun,
                               int unlockedCardsCount, boolean fullHpFinalBoss) {
        totalRunsCompleted++;

        unlock("first_run");
        if (totalRunsCompleted >= 10) unlock("ten_runs");

        if (nodesVisited < 15)   unlock("speed_run");
        if (noForgeUsedThisRun)  unlock("no_forge");
        if (noShopCardThisRun)   unlock("no_shop");
        if (currentGold > 300)   unlock("golden_run");
        if (fullHpFinalBoss)     unlock("full_hp_boss");

        totalGoldEarned += totalGoldEarnedRun;
        if (totalGoldEarned >= 500) unlock("rich");

        if (unlockedCardsCount >= 20) unlock("twenty_cards");

        // Achievement classe
        if (className != null && !classesCompleted.contains(className)) {
            classesCompleted.add(className);
        }
        switch (className != null ? className : "") {
            case "Cavaliere" -> unlock("cavaliere_run");
            case "Mago"      -> unlock("mago_run");
            case "Paladino"  -> unlock("paladino_run");
            case "Assassino" -> unlock("assassino_run");
            case "Dracomante"-> unlock("dracomante_run");
        }
        if (classesCompleted.containsAll(List.of("Cavaliere","Mago","Paladino","Assassino","Dracomante")))
            unlock("all_classes");

        persistCounters();
    }

    // -------------------------------------------------------
    // Unlock + persist
    // -------------------------------------------------------

    public List<String> getUnlocked() { return unlocked; }

    private void unlock(String id) {
        if (unlocked.contains(id)) return;
        Achievement a = AchievementRegistry.getById(id);
        if (a == null) return;
        unlocked.add(id);
        System.out.println("[Achievement sbloccato] " + a.getName());
    }

    /**
     * Scrive i contatori cross-run nel GameState attuale (se esiste un save)
     * senza sovrascrivere i dati di run (deck, nodo corrente, ecc.).
     */
    private void persistCounters() {
        try {
            GameState s;
            if (repo.saveExists()) {
                s = repo.load();
                if (s == null) s = new GameState();
            } else {
                s = new GameState();
            }
            s.setUnlockedAchievements(new ArrayList<>(unlocked));
            s.setTotalEnemiesDefeated(totalEnemiesDefeated);
            s.setTotalBossesDefeated(totalBossesDefeated);
            s.setTotalRunsCompleted(totalRunsCompleted);
            s.setTotalGoldEarned(totalGoldEarned);
            s.setTotalUpgradesUsed(totalUpgradesUsed);
            s.setClassesCompleted(new ArrayList<>(classesCompleted));
            repo.save(s);
        } catch (IOException ignored) {}
    }
}
