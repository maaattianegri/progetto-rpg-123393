package it.unicam.cs.mpgc.rpg123393.service;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.ICard;

import java.util.List;
import java.util.Random;

/**
 * Gestisce la logica del combattimento a turni.
 * Applica il veleno in startTurn() e restituisce messaggi descrittivi per il log.
 */
public class BattleService {

    private final Random random = new Random();

    public String playCard(ICard card, GameCharacter player, GameCharacter enemy) {
        if (player.getCurrentMana() < card.getManaCost()) {
            return "Mana insufficiente per giocare " + card.getName() + "!";
        }
        // NON chiamare useMana qui: ogni carta lo gestisce internamente in play()
        int hpBefore    = enemy.getCurrentHp();
        int blockBefore = enemy.getBlock();
        card.play(player, enemy);

        int dmgDealt = hpBefore - enemy.getCurrentHp();
        StringBuilder msg = new StringBuilder();
        msg.append(player.getName()).append(" usa ").append(card.getName()).append("!");
        if (dmgDealt > 0)    msg.append(" [Danno: ").append(dmgDealt).append("]");
        if (enemy.getPoison() > blockBefore)
            msg.append(" [Avvelena: ").append(enemy.getPoison()).append(" stack]");
        return msg.toString();
    }

    public boolean canPlayCard(ICard card, GameCharacter player) {
        return player.getCurrentMana() >= card.getManaCost();
    }

    public String enemyAttack(GameCharacter enemy, GameCharacter player, int minDamage, int maxDamage) {
        int damage = minDamage + random.nextInt(maxDamage - minDamage + 1);
        player.takeDamage(damage);
        return enemy.getName() + " attacca per " + damage + " danni!";
    }

    public String enemyPlayRandomCard(GameCharacter enemy, GameCharacter player, List<ICard> enemyCards) {
        if (enemyCards == null || enemyCards.isEmpty()) {
            return enemyAttack(enemy, player, 5, 10);
        }
        int hpBefore = player.getCurrentHp();
        ICard chosen = enemyCards.get(random.nextInt(enemyCards.size()));
        chosen.play(enemy, player);
        int dmgDealt = hpBefore - player.getCurrentHp();

        StringBuilder msg = new StringBuilder();
        msg.append(enemy.getName()).append(" usa ").append(chosen.getName()).append("!");
        if (dmgDealt > 0)          msg.append(" [Danno: ").append(dmgDealt).append("]");
        if (enemy.getCurrentHp() > hpBefore)
            msg.append(" [Cura: ").append(enemy.getCurrentHp() - hpBefore).append(" HP]");
        if (player.getPoison() > 0)
            msg.append(" [Veleno: ").append(player.getPoison()).append(" stack]");
        return msg.toString();
    }

    /**
     * Prepara l'inizio di un nuovo turno: applica veleno, resetta block, ripristina mana.
     * Restituisce un messaggio se il veleno ha fatto danno, stringa vuota altrimenti.
     */
    public String startTurn(GameCharacter character) {
        int poisonDmg = character.applyPoison();
        character.resetBlock();
        character.restoreMana();
        if (poisonDmg > 0) {
            return character.getName() + " subisce " + poisonDmg + " danni da veleno! ("
                    + character.getPoison() + " stack rimanenti)";
        }
        return "";
    }

    public boolean isBattleOver(GameCharacter player, GameCharacter enemy) {
        return !player.isAlive() || !enemy.isAlive();
    }

    public boolean isPlayerVictory(GameCharacter player, GameCharacter enemy) {
        return !enemy.isAlive() && player.isAlive();
    }

    public String getBattleResultMessage(GameCharacter player, GameCharacter enemy) {
        if (isPlayerVictory(player, enemy)) {
            return "Hai sconfitto " + enemy.getName() + "! Vittoria!";
        } else {
            return "Sei stato sconfitto da " + enemy.getName() + "...";
        }
    }
}
