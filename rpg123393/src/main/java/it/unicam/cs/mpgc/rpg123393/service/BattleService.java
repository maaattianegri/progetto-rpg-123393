package it.unicam.cs.mpgc.rpg123393.service;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

import java.util.List;
import java.util.Random;

/**
 * Gestisce la logica del combattimento a turni.
 * Non conosce JavaFX né la persistenza: si occupa solo delle regole di gioco.
 */
public class BattleService {

    private final Random random = new Random();

    // --- Turno del giocatore ---

    /**
     * Il giocatore gioca una carta contro il nemico.
     * Restituisce un messaggio descrittivo dell'azione.
     */
    public String playCard(ICard card, GameCharacter player, GameCharacter enemy) {
        if (player.getCurrentMana() < card.getManaCost()) {
            return "Mana insufficiente per giocare " + card.getName() + "!";
        }
        card.play(player, enemy);
        return player.getName() + " usa " + card.getName() + "!";
    }

    /**
     * Controlla se il giocatore ha abbastanza mana per giocare una carta.
     */
    public boolean canPlayCard(ICard card, GameCharacter player) {
        return player.getCurrentMana() >= card.getManaCost();
    }

    // --- Turno del nemico ---

    /**
     * Il nemico esegue un attacco base casuale tra minDamage e maxDamage.
     * Restituisce un messaggio descrittivo.
     */
    public String enemyAttack(GameCharacter enemy, GameCharacter player, int minDamage, int maxDamage) {
        int damage = minDamage + random.nextInt(maxDamage - minDamage + 1);
        player.takeDamage(damage);
        return enemy.getName() + " attacca per " + damage + " danni!";
    }

    /**
     * Il nemico gioca una carta casuale dalla propria lista.
     * Se la lista è vuota, esegue un attacco base da 5 a 10 danni.
     */
    public String enemyPlayRandomCard(GameCharacter enemy, GameCharacter player, List<ICard> enemyCards) {
        if (enemyCards == null || enemyCards.isEmpty()) {
            return enemyAttack(enemy, player, 5, 10);
        }
        ICard chosen = enemyCards.get(random.nextInt(enemyCards.size()));
        chosen.play(enemy, player);
        return enemy.getName() + " usa " + chosen.getName() + "!";
    }

    // --- Gestione turno ---

    /**
     * Prepara l'inizio di un nuovo turno: resetta il block e ripristina il mana.
     */
    public void startTurn(GameCharacter character) {
        character.resetBlock();
        character.restoreMana();
    }

    // --- Esito battaglia ---

    /**
     * Controlla se la battaglia è terminata.
     */
    public boolean isBattleOver(GameCharacter player, GameCharacter enemy) {
        return !player.isAlive() || !enemy.isAlive();
    }

    /**
     * Restituisce true se il giocatore ha vinto (nemico morto, giocatore vivo).
     */
    public boolean isPlayerVictory(GameCharacter player, GameCharacter enemy) {
        return !enemy.isAlive() && player.isAlive();
    }

    /**
     * Restituisce un messaggio di esito finale.
     */
    public String getBattleResultMessage(GameCharacter player, GameCharacter enemy) {
        if (isPlayerVictory(player, enemy)) {
            return "Hai sconfitto " + enemy.getName() + "! Vittoria!";
        } else {
            return "Sei stato sconfitto da " + enemy.getName() + "...";
        }
    }
}
