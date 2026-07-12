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
 * Ogni nodo della mappa ha un nemico dedicato, scelto tramite createForNode(nodeId, level).
 * I nodi "pool" (trunk e rami con più nemici possibili) usano nemici fissi per nodo
 * in modo che il restore del salvataggio non alteri il nemico atteso.
 *
 * Trunk:   n00 → Goblin, n01 → Ratto Gigante
 *
 * Ramo A:  nA1 → Goblin, nA5 → Scheletro Arcano,
 *          nA3 → Orco Berserker (ELITE), nA6 → Scheletro Arcano (ELITE), nAB → Negromante (BOSS)
 *
 * Ramo B:  nB1 → Troll Rigenerante, nB2 → Scheletro Arcano,
 *          nB4 → Cavaliere Vampiro, nB5 → Sentinella Cremisi,
 *          nBB1 → Vampiro Lord, nBB2 → Drago Antico
 *
 * Ramo C:  nC2 → Sentinella Abissale, nC4 → Ratto Gigante,
 *          nC5 → Custode delle Ombre,
 *          nCB1 → Cuore dell'Abisso, nCB2 → Re Ombra
 *
 * Ramo HK: nHK1 → Frammenti del Vuoto, nHKB → Cavaliere Vacuo
 */
public class EnemyFactory {

    // ---------------------------------------------------------------------------
    // Metodo principale: nemico per nodo specifico
    // ---------------------------------------------------------------------------

    public GameCharacter createForNode(String nodeId, int playerLevel) {
        return switch (nodeId) {
            // --- TRUNK ---
            case "n00"  -> new GameCharacter("Goblin",           35 + playerLevel * 4, 3);
            case "n01"  -> new GameCharacter("Ratto Gigante",    28 + playerLevel * 3, 2);

            // --- RAMO A ---
            case "nA1"  -> new GameCharacter("Goblin",           35 + playerLevel * 4, 3);
            case "nA5"  -> new GameCharacter("Scheletro Arcano", 45 + playerLevel * 5, 3);
            case "nA3"  -> new GameCharacter("Orco Berserker",   70 + playerLevel * 8, 4);
            case "nA6"  -> new GameCharacter("Scheletro Arcano", 58 + playerLevel * 7, 5);
            case "nAB"  -> new GameCharacter("Negromante",      130 + playerLevel * 14, 6);

            // --- RAMO B ---
            case "nB1"  -> new GameCharacter("Troll Rigenerante",   80 + playerLevel * 10, 5);
            case "nB2"  -> new GameCharacter("Scheletro Arcano",    58 + playerLevel * 7,  5);
            case "nB4"  -> new GameCharacter("Cavaliere Vampiro",   72 + playerLevel * 9,  4);
            case "nB5"  -> new GameCharacter("Sentinella Cremisi",  68 + playerLevel * 8,  4);
            case "nBB1" -> new GameCharacter("Vampiro Lord",       140 + playerLevel * 15, 6);
            case "nBB2" -> new GameCharacter("Drago Antico",       160 + playerLevel * 18, 7);

            // --- RAMO C ---
            case "nC2"  -> new GameCharacter("Sentinella Abissale", 75 + playerLevel * 9,  5);
            case "nC4"  -> new GameCharacter("Ratto Gigante",       28 + playerLevel * 3,  2);
            case "nC5"  -> new GameCharacter("Custode delle Ombre", 78 + playerLevel * 9,  5);
            case "nCB1" -> new GameCharacter("Cuore dell'Abisso",  145 + playerLevel * 15, 6);
            case "nCB2" -> new GameCharacter("Re Ombra",           155 + playerLevel * 16, 7);

            // --- RAMO HK ---
            case "nHK1" -> createVoidEnemy(playerLevel);
            case "nHKB" -> createVoidBoss(playerLevel);

            // fallback
            default -> new GameCharacter("Goblin", 35 + playerLevel * 4, 3);
        };
    }

    // ---------------------------------------------------------------------------
    // Compatibilità con chiamate che passano solo EncounterType (es. test)
    // ---------------------------------------------------------------------------

    public GameCharacter createForEncounter(EncounterType type, int playerLevel) {
        return switch (type) {
            case NORMAL -> new GameCharacter("Goblin",      35 + playerLevel * 4,  3);
            case ELITE  -> new GameCharacter("Orco Berserker", 70 + playerLevel * 8, 4);
            case BOSS   -> new GameCharacter("Negromante",  130 + playerLevel * 14, 6);
            default     -> new GameCharacter("Goblin",      35 + playerLevel * 4,  3);
        };
    }

    public GameCharacter createForLevel(int playerLevel) {
        return createForEncounter(EncounterType.NORMAL, playerLevel);
    }

    // ---------------------------------------------------------------------------
    // Easter egg Hollow Knight
    // ---------------------------------------------------------------------------

    public GameCharacter createVoidEnemy(int playerLevel) {
        return new GameCharacter("Frammenti del Vuoto", 40 + playerLevel * 5, 3);
    }

    public GameCharacter createVoidBoss(int playerLevel) {
        return new GameCharacter("Cavaliere Vacuo", 200 + playerLevel * 20, 8);
    }

    // ---------------------------------------------------------------------------
    // Carte per nemico
    // ---------------------------------------------------------------------------

    public List<ICard> getCardsForEnemy(GameCharacter enemy) {
        List<ICard> cards = new ArrayList<>();
        switch (enemy.getName()) {
            case "Goblin"               -> { cards.add(new StrikeCard()); cards.add(new StrikeCard()); }
            case "Ratto Gigante"        -> { cards.add(new RatCard()); cards.add(new RatCard()); cards.add(new StrikeCard()); }
            case "Orco Berserker"       -> { cards.add(new OrcFuryCard()); cards.add(new StrikeCard()); }
            case "Scheletro Arcano"     -> { cards.add(new SkeletonCurseCard()); cards.add(new SkeletonCurseCard()); cards.add(new StrikeCard()); cards.add(new DefendCard()); }
            case "Troll Rigenerante"    -> { cards.add(new TrollRegenCard()); cards.add(new TrollRegenCard()); cards.add(new StrikeCard()); cards.add(new StrikeCard()); }
            case "Cavaliere Vampiro"    -> { cards.add(new VampireKnightCard()); cards.add(new VampireKnightCard()); cards.add(new StrikeCard()); cards.add(new DefendCard()); }
            case "Sentinella Cremisi"   -> { cards.add(new CrimsonSentinelCard()); cards.add(new CrimsonSentinelCard()); cards.add(new StrikeCard()); }
            case "Sentinella Abissale"  -> { cards.add(new AbyssalSentinelCard()); cards.add(new AbyssalSentinelCard()); cards.add(new StrikeCard()); }
            case "Custode delle Ombre"  -> { cards.add(new ShadowKeeperCard()); cards.add(new ShadowKeeperCard()); cards.add(new StrikeCard()); cards.add(new DefendCard()); }
            case "Negromante"           -> { cards.add(new NecromancerCard()); cards.add(new NecromancerCard()); cards.add(new StrikeCard()); cards.add(new FireballCard()); }
            case "Vampiro Lord"         -> { cards.add(new VampireDrainCard()); cards.add(new VampireDrainCard()); cards.add(new StrikeCard()); cards.add(new DefendCard()); }
            case "Drago Antico"         -> { cards.add(new DragonBreathCard()); cards.add(new DragonBreathCard()); cards.add(new StrikeCard()); cards.add(new FireballCard()); }
            case "Cuore dell'Abisso"    -> { cards.add(new AbyssHeartPulseCard()); cards.add(new AbyssHeartPulseCard()); cards.add(new StrikeCard()); cards.add(new AbyssHeartPulseCard()); }
            case "Re Ombra"             -> { cards.add(new ShadowKingStrikeCard()); cards.add(new ShadowKingStrikeCard()); cards.add(new StrikeCard()); cards.add(new DefendCard()); cards.add(new ShadowKingStrikeCard()); }
            case "Frammenti del Vuoto"  -> { cards.add(new StrikeCard()); cards.add(new StrikeCard()); cards.add(new StrikeCard()); }
            case "Cavaliere Vacuo"      -> { cards.add(new StrikeCard()); cards.add(new StrikeCard()); cards.add(new DefendCard()); cards.add(new StrikeCard()); cards.add(new FireballCard()); }
            default                     -> cards.add(new StrikeCard());
        }
        return cards;
    }
}
