package it.unicam.cs.mpgc.rpg123393.service;

/**
 * Gestisce la progressione del personaggio: esperienza e salita di livello.
 * Non conosce JavaFX né la persistenza.
 */
public class LevelService {

    // Esperienza base richiesta per salire al livello 2.
    // Ogni livello successivo richiede BASE * livello corrente.
    private static final int BASE_XP = 100;

    /** Mana massimo assoluto raggiungibile durante la run. */
    public static final int MAX_MANA_CAP = 9;

    /**
     * Calcola quanta esperienza serve per salire al livello successivo.
     * Formula: BASE_XP * livello corrente.
     * Es: livello 1 → 100 XP, livello 2 → 200 XP, livello 3 → 300 XP.
     */
    public int xpRequiredForNextLevel(int currentLevel) {
        return BASE_XP * currentLevel;
    }

    /**
     * Controlla se il personaggio deve salire di livello.
     */
    public boolean shouldLevelUp(int currentXp, int currentLevel) {
        return currentXp >= xpRequiredForNextLevel(currentLevel);
    }

    /**
     * Esegue il level up: restituisce la XP residua dopo il salto di livello.
     * Da chiamare in loop finché shouldLevelUp() è true (gestisce level up multipli).
     */
    public int consumeXpForLevelUp(int currentXp, int currentLevel) {
        return currentXp - xpRequiredForNextLevel(currentLevel);
    }

    /**
     * Calcola il bonus di HP massimi guadagnato salendo di livello.
     * Formula: 10 + (livello nuovo / 2), arrotondato per difetto.
     */
    public int hpBonusOnLevelUp(int newLevel) {
        return 10 + (newLevel / 2);
    }

    /**
     * Calcola il bonus di mana massimo guadagnato salendo di livello.
     * +1 mana ogni 3 livelli, con cap assoluto a MAX_MANA_CAP.
     * Esempi: lv3 → +1, lv6 → +1, lv9 → +1. Tutti gli altri livelli → 0.
     */
    public int manaBonusOnLevelUp(int newLevel) {
        return newLevel % 3 == 0 ? 1 : 0;
    }

    /**
     * Restituisce un messaggio descrittivo del level up.
     */
    public String levelUpMessage(String characterName, int newLevel) {
        int manaBonus = manaBonusOnLevelUp(newLevel);
        String manaStr = manaBonus > 0 ? ", +" + manaBonus + " Mana" : "";
        return characterName + " è salito al livello " + newLevel + "! +"
                + hpBonusOnLevelUp(newLevel) + " HP" + manaStr + ".";
    }
}
