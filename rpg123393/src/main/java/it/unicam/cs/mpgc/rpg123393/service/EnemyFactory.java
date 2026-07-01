package it.unicam.cs.mpgc.rpg123393.service;

import it.unicam.cs.mpgc.rpg123393.model.DefendCard;
import it.unicam.cs.mpgc.rpg123393.model.FireballCard;
import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;
import it.unicam.cs.mpgc.rpg123393.model.StrikeCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Factory per la creazione dei nemici.
 * Aggiungere un nuovo tipo di nemico richiede solo un nuovo metodo privato
 * e registrarlo in createForLevel() — nessuna altra classe va modificata.
 */
public class EnemyFactory {

    private final Random random = new Random();

    /**
     * Crea un nemico casuale scalato sul livello corrente del giocatore.
     * Livelli 1-2  → Goblin
     * Livelli 3-4  → Scheletro
     * Livello 5+   → Troll (o uno dei tre a caso per varietà)
     */
    public GameCharacter createForLevel(int playerLevel) {
        if (playerLevel <= 2) {
            return createGoblin(playerLevel);
        } else if (playerLevel <= 4) {
            return createSkeleton(playerLevel);
        } else {
            // Dalla livello 5 in su: nemico casuale tra tutti e tre
            int pick = random.nextInt(3);
            return switch (pick) {
                case 0 -> createGoblin(playerLevel);
                case 1 -> createSkeleton(playerLevel);
                default -> createTroll(playerLevel);
            };
        }
    }

    /**
     * Restituisce la lista di carte che il nemico può usare in battaglia.
     * Usata da BattleService.enemyPlayRandomCard().
     */
    public List<ICard> getCardsForEnemy(GameCharacter enemy) {
        List<ICard> cards = new ArrayList<>();
        switch (enemy.getName()) {
            case "Goblin" -> {
                cards.add(new StrikeCard());
            }
            case "Scheletro" -> {
                cards.add(new StrikeCard());
                cards.add(new DefendCard());
            }
            case "Troll" -> {
                cards.add(new StrikeCard());
                cards.add(new StrikeCard()); // doppia probabilità di attaccare
                cards.add(new DefendCard());
                cards.add(new FireballCard());
            }
            default -> cards.add(new StrikeCard()); // fallback generico
        }
        return cards;
    }

    // --- Nemici concreti ---

    private GameCharacter createGoblin(int playerLevel) {
        int hp   = 30 + (playerLevel * 5);
        int mana = 3;
        return new GameCharacter("Goblin", hp, mana);
    }

    private GameCharacter createSkeleton(int playerLevel) {
        int hp   = 50 + (playerLevel * 8);
        int mana = 4;
        return new GameCharacter("Scheletro", hp, mana);
    }

    private GameCharacter createTroll(int playerLevel) {
        int hp   = 80 + (playerLevel * 12);
        int mana = 5;
        return new GameCharacter("Troll", hp, mana);
    }
}
