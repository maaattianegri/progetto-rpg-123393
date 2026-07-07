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
 * I nodi NORMAL e ELITE condividono pool per ramo, ma ogni boss è univoco.
 *
 * Ramo A:  nA1/nA5 NORMAL, nA3 ELITE (Orco Berserker), nA6 ELITE (Scheletro Arcano), nAB BOSS (Negromante)
 * Ramo B:  nB1/nB2 ELITE, nB4 ELITE (Vampiro), nB5 ELITE (Banshee), nBB1 BOSS (Vampiro Lord), nBB2 BOSS (Drago Antico)
 * Ramo C:  nC2/nC5 ELITE, nCB1 BOSS (Cuore dell'Abisso), nCB2 BOSS (Re Ombra)
 * Trunk:   n00/n01 NORMAL
 * Ramo HK: nHK1 BATTLE (Frammenti del Vuoto), nHKB VOID_BOSS (Cavaliere Vacuo)
 */
public class EnemyFactory {

    // Indici pool per nodi generici (trunk e ramo C normal/elite condivisi)
    private int normalIndex = 0;
    private int eliteIndex  = 0;

    // ---------------------------------------------------------------------------
    // Metodo principale: nemico per nodo specifico
    // ---------------------------------------------------------------------------

    public GameCharacter createForNode(String nodeId, int playerLevel) {
        return switch (nodeId) {
            // --- TRUNK ---
            case "n00", "n01"  -> roundRobinNormal(playerLevel);

            // --- RAMO A ---
            case "nA1", "nA5" -> roundRobinNormal(playerLevel);
            case "nA3"         -> new GameCharacter("Orco Berserker",    70 + playerLevel * 8,  4);
            case "nA6"         -> new GameCharacter("Scheletro Arcano",  58 + playerLevel * 7,  5);
            case "nAB"         -> new GameCharacter("Negromante",       130 + playerLevel * 14, 6);

            // --- RAMO B ---
            case "nB1"         -> new GameCharacter("Troll Rigenerante", 80 + playerLevel * 10, 5);
            case "nB2"         -> new GameCharacter("Scheletro Arcano",  58 + playerLevel * 7,  5);
            case "nB4"         -> new GameCharacter("Orco Berserker",    70 + playerLevel * 8,  4);
            case "nB5"         -> new GameCharacter("Banshee",           65 + playerLevel * 8,  4);
            case "nBB1"        -> new GameCharacter("Vampiro Lord",     140 + playerLevel * 15, 6);
            case "nBB2"        -> new GameCharacter("Drago Antico",     160 + playerLevel * 18, 7);

            // --- RAMO C ---
            case "nC2", "nC5" -> new GameCharacter("Banshee",           65 + playerLevel * 8,  4);
            case "nC4"         -> roundRobinNormal(playerLevel);
            case "nCB1"        -> new GameCharacter("Cuore dell'Abisso", 145 + playerLevel * 15, 6);
            case "nCB2"        -> new GameCharacter("Re Ombra",          155 + playerLevel * 16, 7);

            // --- RAMO HK ---
            case "nHK1"        -> createVoidEnemy(playerLevel);
            case "nHKB"        -> createVoidBoss(playerLevel);

            // fallback
            default            -> roundRobinNormal(playerLevel);
        };
    }

    // ---------------------------------------------------------------------------
    // Compatibilità con chiamate che passano solo EncounterType (es. test)
    // ---------------------------------------------------------------------------

    public GameCharacter createForEncounter(EncounterType type, int playerLevel) {
        return switch (type) {
            case NORMAL    -> roundRobinNormal(playerLevel);
            case ELITE     -> roundRobinElite(playerLevel);
            case BOSS      -> new GameCharacter("Negromante", 130 + playerLevel * 14, 6);
            default        -> roundRobinNormal(playerLevel);
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
    // Pool generiche (round-robin)
    // ---------------------------------------------------------------------------

    private GameCharacter roundRobinNormal(int level) {
        return switch (normalIndex++ % 2) {
            case 0 -> new GameCharacter("Goblin",        35 + level * 4, 3);
            default-> new GameCharacter("Ratto Gigante", 28 + level * 3, 2);
        };
    }

    private GameCharacter roundRobinElite(int level) {
        return switch (eliteIndex++ % 4) {
            case 0 -> new GameCharacter("Orco Berserker",    70 + level * 8,  4);
            case 1 -> new GameCharacter("Scheletro Arcano",  58 + level * 7,  5);
            case 2 -> new GameCharacter("Troll Rigenerante", 80 + level * 10, 5);
            default-> new GameCharacter("Banshee",           65 + level * 8,  4);
        };
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
            case "Banshee"              -> { cards.add(new BansheeWailCard()); cards.add(new BansheeWailCard()); cards.add(new StrikeCard()); }
            case "Negromante"           -> { cards.add(new NecromancerCard()); cards.add(new NecromancerCard()); cards.add(new StrikeCard()); cards.add(new FireballCard()); }
            case "Vampiro Lord"         -> { cards.add(new VampireDrainCard()); cards.add(new VampireDrainCard()); cards.add(new StrikeCard()); cards.add(new DefendCard()); }
            case "Drago Antico"         -> { cards.add(new DragonBreathCard()); cards.add(new DragonBreathCard()); cards.add(new StrikeCard()); cards.add(new FireballCard()); }
            case "Cuore dell'Abisso"    -> { cards.add(new AbyssHeartPulseCard()); cards.add(new AbyssHeartPulseCard()); cards.add(new StrikeCard()); cards.add(new AbyssHeartPulseCard()); }
            case "Re Ombra"             -> { cards.add(new ShadowKingStrikeCard()); cards.add(new ShadowKingStrikeCard()); cards.add(new StrikeCard()); cards.add(new DefendCard()); cards.add(new ShadowKingStrikeCard()); }
            // Ramo HK
            case "Frammenti del Vuoto"  -> { cards.add(new StrikeCard()); cards.add(new StrikeCard()); cards.add(new StrikeCard()); }
            case "Cavaliere Vacuo"      -> { cards.add(new StrikeCard()); cards.add(new StrikeCard()); cards.add(new DefendCard()); cards.add(new StrikeCard()); cards.add(new FireballCard()); }
            default                     -> cards.add(new StrikeCard());
        }
        return cards;
    }
}
