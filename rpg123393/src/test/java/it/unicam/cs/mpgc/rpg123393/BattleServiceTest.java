package it.unicam.cs.mpgc.rpg123393;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.StrikeCard;
import it.unicam.cs.mpgc.rpg123393.model.DefendCard;
import it.unicam.cs.mpgc.rpg123393.model.player.AcidPoisonCard;
import it.unicam.cs.mpgc.rpg123393.service.BattleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitari per BattleService.
 * Coprono: playCard, canPlayCard, startTurn, isBattleOver, isPlayerVictory, getBattleResultMessage, enemyAttack.
 */
class BattleServiceTest {

    private BattleService battleService;
    private GameCharacter player;
    private GameCharacter enemy;

    @BeforeEach
    void setUp() {
        battleService = new BattleService();
        player = new GameCharacter("Eroe",   100, 3);
        enemy  = new GameCharacter("Goblin",  50, 3);
    }

    // -------------------------
    // canPlayCard
    // -------------------------

    @Test
    @DisplayName("canPlayCard restituisce true se il mana è sufficiente")
    void canPlayCardWithSufficientMana() {
        assertTrue(battleService.canPlayCard(new StrikeCard(), player));
    }

    @Test
    @DisplayName("canPlayCard restituisce false se il mana è insufficiente")
    void canPlayCardWithInsufficientMana() {
        player.useMana(3); // mana = 0
        assertFalse(battleService.canPlayCard(new StrikeCard(), player));
    }

    // -------------------------
    // playCard
    // -------------------------

    @Test
    @DisplayName("playCard applica danno al nemico e restituisce messaggio")
    void playCardDealsDamage() {
        String msg = battleService.playCard(new StrikeCard(), player, enemy);
        assertEquals(44, enemy.getCurrentHp()); // 50 - 6
        assertTrue(msg.contains("Colpo di Spada"));
        assertTrue(msg.contains("Danno: 6"));
    }

    @Test
    @DisplayName("playCard con mana insufficiente restituisce messaggio di errore e non applica danno")
    void playCardInsufficientMana() {
        player.useMana(3);
        String msg = battleService.playCard(new StrikeCard(), player, enemy);
        assertEquals(50, enemy.getCurrentHp());
        assertTrue(msg.contains("Mana insufficiente"));
    }

    @Test
    @DisplayName("playCard con carta difensiva aggiunge scudo al player")
    void playCardAddsBlock() {
        battleService.playCard(new DefendCard(), player, enemy);
        assertEquals(6, player.getBlock());
    }

    // -------------------------
    // startTurn
    // -------------------------

    @Test
    @DisplayName("startTurn resetta lo scudo a 0")
    void startTurnResetsBlock() {
        player.addBlock(10);
        battleService.startTurn(player);
        assertEquals(0, player.getBlock());
    }

    @Test
    @DisplayName("startTurn ripristina il mana al massimo")
    void startTurnRestoresMana() {
        player.useMana(3);
        battleService.startTurn(player);
        assertEquals(3, player.getCurrentMana());
    }

    @Test
    @DisplayName("startTurn applica il veleno e restituisce messaggio se stack > 0")
    void startTurnAppliesPoison() {
        player.addPoison(4);
        String msg = battleService.startTurn(player);
        assertEquals(96, player.getCurrentHp());
        assertEquals(3, player.getPoison());
        assertTrue(msg.contains("veleno"));
    }

    @Test
    @DisplayName("startTurn senza veleno restituisce stringa vuota")
    void startTurnNoPoison() {
        String msg = battleService.startTurn(player);
        assertEquals("", msg);
    }

    // -------------------------
    // isBattleOver / isPlayerVictory
    // -------------------------

    @Test
    @DisplayName("isBattleOver è false se entrambi sono vivi")
    void battleNotOverWhenBothAlive() {
        assertFalse(battleService.isBattleOver(player, enemy));
    }

    @Test
    @DisplayName("isBattleOver è true se il nemico è morto")
    void battleOverWhenEnemyDead() {
        enemy.takeDamage(50);
        assertTrue(battleService.isBattleOver(player, enemy));
    }

    @Test
    @DisplayName("isBattleOver è true se il player è morto")
    void battleOverWhenPlayerDead() {
        player.takeDamage(100);
        assertTrue(battleService.isBattleOver(player, enemy));
    }

    @Test
    @DisplayName("isPlayerVictory è true solo se il nemico è morto e il player è vivo")
    void playerVictoryCondition() {
        enemy.takeDamage(50);
        assertTrue(battleService.isPlayerVictory(player, enemy));
    }

    @Test
    @DisplayName("isPlayerVictory è false se il player è morto")
    void noVictoryIfPlayerDead() {
        player.takeDamage(100);
        enemy.takeDamage(50);
        assertFalse(battleService.isPlayerVictory(player, enemy));
    }

    // -------------------------
    // getBattleResultMessage
    // -------------------------

    @Test
    @DisplayName("getBattleResultMessage restituisce messaggio vittoria se nemico è morto")
    void victoryMessage() {
        enemy.takeDamage(50);
        String msg = battleService.getBattleResultMessage(player, enemy);
        assertTrue(msg.contains("Vittoria") || msg.contains("sconfitto"));
    }

    @Test
    @DisplayName("getBattleResultMessage restituisce messaggio sconfitta se player è morto")
    void defeatMessage() {
        player.takeDamage(100);
        String msg = battleService.getBattleResultMessage(player, enemy);
        assertTrue(msg.contains("sconfitto") || msg.contains("Goblin"));
    }

    // -------------------------
    // enemyAttack
    // -------------------------

    @Test
    @DisplayName("enemyAttack riduce gli HP del player entro il range specificato")
    void enemyAttackDealsCorrectRange() {
        int hpBefore = player.getCurrentHp();
        battleService.enemyAttack(enemy, player, 5, 10);
        int dmg = hpBefore - player.getCurrentHp();
        assertTrue(dmg >= 5 && dmg <= 10, "Danno fuori range: " + dmg);
    }

    @Test
    @DisplayName("enemyPlayRandomCard con lista vuota usa fallback enemyAttack")
    void enemyPlayRandomCardEmptyList() {
        int hpBefore = player.getCurrentHp();
        String msg = battleService.enemyPlayRandomCard(enemy, player, List.of());
        assertTrue(player.getCurrentHp() < hpBefore);
        assertNotNull(msg);
    }

    @Test
    @DisplayName("enemyPlayRandomCard con carte usa una carta dalla lista")
    void enemyPlayRandomCardFromList() {
        String msg = battleService.enemyPlayRandomCard(enemy, player, List.of(new AcidPoisonCard()));
        assertNotNull(msg);
    }
}
