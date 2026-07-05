package it.unicam.cs.mpgc.rpg123393.service;

import it.unicam.cs.mpgc.rpg123393.model.EncounterType;
import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;
import it.unicam.cs.mpgc.rpg123393.model.StrikeCard;
import it.unicam.cs.mpgc.rpg123393.model.DefendCard;
import it.unicam.cs.mpgc.rpg123393.model.FireballCard;
import it.unicam.cs.mpgc.rpg123393.model.enemy.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory nemici.
 *
 * Ramo HK (easter egg Cavaliere):
 *   VOID_NORMAL : "Frammenti del Vuoto" (nHK1)
 *   VOID_BOSS   : "Cavaliere Vacuo"     (nHKB)
 */
public class EnemyFactory {

    private int normalIndex = 0;
    private int eliteIndex  = 0;
    private int bossIndex   = 0;

    public GameCharacter createForEncounter(EncounterType type, int playerLevel) {
        return switch (type) {
            case NORMAL -> createNormal(playerLevel);
            case ELITE  -> createElite(playerLevel);
            case BOSS   -> createBoss(playerLevel);
            default     -> createNormal(playerLevel);
        };
    }

    public GameCharacter createForLevel(int playerLevel) {
        return createForEncounter(EncounterType.NORMAL, playerLevel);
    }

    /**
     * Nemico del nodo nHK1.
     * Mostrato come "Frammenti del Vuoto" in battaglia.
     */
    public GameCharacter createVoidEnemy(int playerLevel) {
        return new GameCharacter("Frammenti del Vuoto", 40 + playerLevel * 5, 3);
    }

    /**
     * Boss segreto: Cavaliere Vacuo (nHKB).
     */
    public GameCharacter createVoidBoss(int playerLevel) {
        return new GameCharacter("Cavaliere Vacuo", 200 + playerLevel * 20, 8);
    }

    private GameCharacter createNormal(int level) {
        return switch (normalIndex++ % 2) {
            case 0 -> new GameCharacter("Goblin",        35 + level * 4, 3);
            default-> new GameCharacter("Ratto Gigante", 28 + level * 3, 2);
        };
    }

    private GameCharacter createElite(int level) {
        return switch (eliteIndex++ % 4) {
            case 0 -> new GameCharacter("Orco Berserker",    70 + level * 8,  4);
            case 1 -> new GameCharacter("Scheletro Arcano",  58 + level * 7,  5);
            case 2 -> new GameCharacter("Troll Rigenerante", 80 + level * 10, 5);
            default-> new GameCharacter("Banshee",           65 + level * 8,  4);
        };
    }

    private GameCharacter createBoss(int level) {
        return switch (bossIndex++ % 2) {
            case 0 -> new GameCharacter("Negromante",   130 + level * 14, 6);
            default-> new GameCharacter("Drago Antico", 160 + level * 18, 7);
        };
    }

    public List<ICard> getCardsForEnemy(GameCharacter enemy) {
        List<ICard> cards = new ArrayList<>();
        switch (enemy.getName()) {
            case "Goblin"               -> { cards.add(new StrikeCard()); cards.add(new StrikeCard()); }
            case "Ratto Gigante"        -> { cards.add(new RatCard()); cards.add(new RatCard()); cards.add(new StrikeCard()); }
            case "Orco Berserker"       -> { cards.add(new OrcFuryCard()); cards.add(new StrikeCard()); }
            case "Scheletro Arcano"     -> { cards.add(new SkeletonCurseCard()); cards.add(new SkeletonCurseCard()); cards.add(new StrikeCard()); cards.add(new DefendCard()); }
            case "Troll Rigenerante"    -> { cards.add(new TrollRegenCard()); cards.add(new TrollRegenCard()); cards.add(new StrikeCard()); cards.add(new StrikeCard()); }
            case "Banshee"              -> { cards.add(new BansheeWailCard()); cards.add(new BansheeWailCard()); cards.add(new StrikeCard()); }
            case "Negromante"           -> { cards.add(new NecromancerCard()); cards.add(new NecromancerCard()); cards.add(new StrikeCard()); cards.add(new FireballCard()); }
            case "Drago Antico"         -> { cards.add(new DragonBreathCard()); cards.add(new DragonBreathCard()); cards.add(new StrikeCard()); cards.add(new FireballCard()); }
            // Ramo HK
            case "Frammenti del Vuoto" -> { cards.add(new StrikeCard()); cards.add(new StrikeCard()); cards.add(new StrikeCard()); }
            case "Cavaliere Vacuo"      -> { cards.add(new StrikeCard()); cards.add(new StrikeCard()); cards.add(new DefendCard()); cards.add(new StrikeCard()); cards.add(new FireballCard()); }
            default                     -> cards.add(new StrikeCard());
        }
        return cards;
    }
}
