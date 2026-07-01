package it.unicam.cs.mpgc.rpg123393.service;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;
import it.unicam.cs.mpgc.rpg123393.model.StrikeCard;
import it.unicam.cs.mpgc.rpg123393.model.DefendCard;
import it.unicam.cs.mpgc.rpg123393.model.FireballCard;
import it.unicam.cs.mpgc.rpg123393.model.enemy.DragonBreathCard;
import it.unicam.cs.mpgc.rpg123393.model.enemy.OrcFuryCard;
import it.unicam.cs.mpgc.rpg123393.model.enemy.SkeletonCurseCard;
import it.unicam.cs.mpgc.rpg123393.model.enemy.TrollRegenCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Factory per la creazione dei nemici.
 *
 * Progressione:
 *   Lv. 1  -> Goblin
 *   Lv. 2  -> Orco Berserker  (Furia Doppia)
 *   Lv. 3  -> Scheletro Arcano (Maledizione mana)
 *   Lv. 4  -> Troll Rigenerante (si auto-cura ogni turno)
 *   Lv. 5+ -> Drago Antico (Fiato di Fuoco che bypassa lo scudo)
 *
 * Aggiungere un nuovo nemico: creare il metodo createXxx() privato,
 * registrarlo in createForLevel() e in getCardsForEnemy().
 */
public class EnemyFactory {

    private final Random random = new Random();

    public GameCharacter createForLevel(int playerLevel) {
        return switch (playerLevel) {
            case 1  -> createGoblin(playerLevel);
            case 2  -> createOrc(playerLevel);
            case 3  -> createSkeletonArcane(playerLevel);
            case 4  -> createTroll(playerLevel);
            default -> createDragon(playerLevel);  // Lv.5+
        };
    }

    public List<ICard> getCardsForEnemy(GameCharacter enemy) {
        List<ICard> cards = new ArrayList<>();
        switch (enemy.getName()) {

            case "Goblin" -> {
                // Solo attacco base: semplice e prevedibile al Lv.1
                cards.add(new StrikeCard());
                cards.add(new StrikeCard());
            }

            case "Orco Berserker" -> {
                // 50% Furia Doppia, 50% attacco singolo
                cards.add(new OrcFuryCard());
                cards.add(new StrikeCard());
            }

            case "Scheletro Arcano" -> {
                // 50% Maledizione (drena mana), 33% attacco, 17% difesa
                cards.add(new SkeletonCurseCard());
                cards.add(new SkeletonCurseCard());
                cards.add(new StrikeCard());
                cards.add(new DefendCard());
            }

            case "Troll Rigenerante" -> {
                // Attacca forte e si rigenera spesso
                cards.add(new TrollRegenCard());
                cards.add(new TrollRegenCard());
                cards.add(new StrikeCard());
                cards.add(new StrikeCard());
            }

            case "Drago Antico" -> {
                // Fiato di Fuoco bypassa scudo, attacco e Fireball come bonus
                cards.add(new DragonBreathCard());
                cards.add(new DragonBreathCard());
                cards.add(new StrikeCard());
                cards.add(new FireballCard());
            }

            default -> cards.add(new StrikeCard());
        }
        return cards;
    }

    // -------------------------------------------------------
    // Nemici concreti — HP e mana scalano con il livello
    // -------------------------------------------------------

    private GameCharacter createGoblin(int level) {
        // HP: 35 + 5 per livello | danno base: StrikeCard (6)
        return new GameCharacter("Goblin", 35 + level * 5, 3);
    }

    private GameCharacter createOrc(int level) {
        // HP: 60 + 8 per livello | abilità: Furia Doppia (4+4)
        return new GameCharacter("Orco Berserker", 60 + level * 8, 4);
    }

    private GameCharacter createSkeletonArcane(int level) {
        // HP: 50 + 7 per livello | abilità: Maledizione (drena 2 mana)
        return new GameCharacter("Scheletro Arcano", 50 + level * 7, 5);
    }

    private GameCharacter createTroll(int level) {
        // HP: 85 + 12 per livello | abilità: Rigenerazione (+6 HP/turno)
        return new GameCharacter("Troll Rigenerante", 85 + level * 12, 5);
    }

    private GameCharacter createDragon(int level) {
        // HP: 120 + 15 per livello | abilità: Fiato di Fuoco (bypassa scudo)
        return new GameCharacter("Drago Antico", 120 + level * 15, 6);
    }
}
