package it.unicam.cs.mpgc.rpg123393.service;

import it.unicam.cs.mpgc.rpg123393.model.NodeType;
import it.unicam.cs.mpgc.rpg123393.model.achievement.Achievement;
import it.unicam.cs.mpgc.rpg123393.model.achievement.AchievementRegistry;
import it.unicam.cs.mpgc.rpg123393.persistence.GameState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Gestisce la logica di unlock degli achievement.
 *
 * <p>Il servizio opera su un {@link GameState} condiviso con {@link GameService};
 * tutti i metodi {@code on*()} vanno chiamati nei punti opportuni del codice
 * (GameService, BattleService, ShopController, RestController, EventController).
 *
 * <p>Quando un achievement viene sbloccato, viene notificato tramite il
 * listener registrato con {@link #setOnUnlocked(Consumer)}, che la UI può
 * usare per mostrare un toast/popup.
 */
public class AchievementService {

    private final GameState state;

    /** Callback invocata ogni volta che un achievement viene sbloccato (può essere null). */
    private Consumer<Achievement> onUnlocked;

    public AchievementService(GameState state) {
        this.state = state;
    }

    public void setOnUnlocked(Consumer<Achievement> listener) {
        this.onUnlocked = listener;
    }

    // -------------------------------------------------------
    // API pubblica — hook da chiamare nei punti giusti
    // -------------------------------------------------------

    /** Da chiamare all'inizio di ogni nuova run (dopo aver resettato i flag). */
    public void onRunStarted() {
        state.incrementRunsStarted();
        tryUnlock("first_steps");
    }

    /**
     * Da chiamare quando il giocatore completa una run con successo
     * (boss finale sconfitto).
     *
     * @param className   nome della classe usata nella run
     * @param currentHp   HP del giocatore al momento della vittoria
     * @param maxHp       HP massimi del giocatore
     * @param gold        oro in tasca al termine della run
     * @param nodesVisited nodi visitati nella run
     */
    public void onRunCompleted(String className, int currentHp, int maxHp,
                               int gold, int nodesVisited) {
        state.incrementRunsCompleted();
        tryUnlock("first_run");
        if (state.getTotalRunsCompleted() >= 10) tryUnlock("ten_runs");

        // Achievement per classe
        if (className != null) {
            switch (className) {
                case "Cavaliere"  -> tryUnlock("cavaliere_run");
                case "Mago"       -> tryUnlock("mago_run");
                case "Paladino"   -> tryUnlock("paladino_run");
                case "Assassino"  -> tryUnlock("assassino_run");
                case "Dracomante" -> tryUnlock("dracomante_run");
            }
            checkAllClasses();
        }

        // Sfide singola run
        if (state.getRunForgeUses() == 0)        tryUnlock("no_forge");
        if (state.getRunShopCardsBought() == 0)  tryUnlock("no_shop");
        if (state.getRunShopCardsBought() == 0
                && state.getRunShopRelicsBought() > 0) tryUnlock("pacifist_shop");
        if (currentHp == maxHp)                  tryUnlock("full_hp_boss");
        if (nodesVisited < 15)                   tryUnlock("speed_run");
        if (gold > 300)                          tryUnlock("golden_run");
    }

    /**
     * Da chiamare dopo ogni vittoria in combattimento.
     *
     * @param isBoss       true se il nemico sconfitto era un boss
     * @param damagesTaken danni subiti durante il combattimento
     * @param playerHp     HP rimanenti del giocatore dopo il combattimento
     * @param wasFirstNode true se era il primo nodo della run
     */
    public void onEnemyDefeated(boolean isBoss, int damagesTaken,
                                int playerHp, boolean wasFirstNode) {
        state.incrementEnemiesKilled();
        if (isBoss) state.incrementBossesKilled();

        if (damagesTaken == 0)                         tryUnlock("no_damage_fight");
        if (state.getTotalBossesKilled() >= 10)        tryUnlock("boss_slayer");
        if (state.getTotalEnemiesKilled() >= 100)      tryUnlock("hundred_enemies");
        if (playerHp < 10 && playerHp > 0)            tryUnlock("survivor");
    }

    /**
     * Da chiamare quando un nemico muore per danno da veleno.
     */
    public void onPoisonKill() {
        tryUnlock("poison_master");
    }

    /**
     * Da chiamare quando il giocatore muore al primo nodo della run.
     */
    public void onDiedAtFirstNode() {
        tryUnlock("die_first");
    }

    /**
     * Da chiamare quando il giocatore usa la fucina.
     */
    public void onForgeUsed() {
        state.incrementForgeUses();
        state.incrementRunForgeUses();
        if (state.getTotalForgeUses() >= 20) tryUnlock("upgrade_addict");
    }

    /**
     * Da chiamare quando il giocatore acquista una carta al negozio.
     */
    public void onShopCardBought() {
        state.incrementRunShopCardsBought();
    }

    /**
     * Da chiamare quando il giocatore acquista una reliquia al negozio.
     */
    public void onShopRelicBought() {
        state.incrementRunShopRelicsBought();
    }

    /**
     * Da chiamare ogni volta che il giocatore visita un nuovo nodo.
     */
    public void onNodeVisited() {
        state.incrementRunNodesVisited();
    }

    /**
     * Da chiamare ogni volta che si sblocca una nuova carta nella collezione.
     *
     * @param totalUnlocked numero totale di carte sbloccate dopo questo unlock
     * @param totalCards    numero totale di carte nel gioco
     */
    public void onCardUnlocked(int totalUnlocked, int totalCards) {
        if (totalUnlocked >= 20)          tryUnlock("twenty_cards");
        if (totalUnlocked >= totalCards)  tryUnlock("unlock_all_cards");
    }

    /**
     * Da chiamare ogni volta che il giocatore guadagna oro.
     *
     * @param amount oro guadagnato
     */
    public void onGoldEarned(int amount) {
        state.addGoldEarned(amount);
        if (state.getTotalGoldEarned() >= 500) tryUnlock("rich");
    }

    /**
     * Da chiamare quando il giocatore ottiene il Void Heart nel percorso HK.
     */
    public void onVoidHeartObtained() {
        tryUnlock("void_heart");
    }

    /**
     * Da chiamare quando il giocatore sconfigge il Cavaliere Vacuo (VOID_BOSS).
     */
    public void onHollowKnightDefeated() {
        tryUnlock("hollow_knight");
    }

    /**
     * Da chiamare quando il giocatore rifiuta il Void Heart in nHK4
     * e si dirige verso il Drago Antico.
     */
    public void onVoidRejected() {
        tryUnlock("void_rejected");
    }

    /**
     * Da chiamare quando il giocatore vince un combattimento con 0 mana
     * e nessuna carta giocabile rimasta in mano.
     */
    public void onWinWithZeroMana() {
        tryUnlock("zero_mana");
    }

    // -------------------------------------------------------
    // Query
    // -------------------------------------------------------

    /** Restituisce la lista degli achievement sbloccati. */
    public List<String> getUnlockedIds() {
        return state.getUnlockedAchievements();
    }

    public boolean isUnlocked(String id) {
        return state.isAchievementUnlocked(id);
    }

    /** Numero di achievement sbloccati sul totale. */
    public int getUnlockedCount() {
        return state.getUnlockedAchievements().size();
    }

    public int getTotalCount() {
        return AchievementRegistry.count();
    }

    // -------------------------------------------------------
    // Internals
    // -------------------------------------------------------

    /**
     * Tenta di sbloccare l'achievement con l'id dato.
     * Se già sbloccato o id inesistente, non fa nulla.
     * Notifica il listener se presente.
     */
    private void tryUnlock(String id) {
        if (state.isAchievementUnlocked(id)) return;
        Achievement a = AchievementRegistry.getById(id);
        if (a == null) return;
        state.unlockAchievement(id);
        if (onUnlocked != null) onUnlocked.accept(a);
    }

    /**
     * Controlla se tutte e 5 le classi hanno una run completata
     * e in quel caso sblocca "Maestro delle Arti".
     */
    private void checkAllClasses() {
        boolean all = state.isAchievementUnlocked("cavaliere_run")
                && state.isAchievementUnlocked("mago_run")
                && state.isAchievementUnlocked("paladino_run")
                && state.isAchievementUnlocked("assassino_run")
                && state.isAchievementUnlocked("dracomante_run");
        if (all) tryUnlock("all_classes");
    }
}
