package it.unicam.cs.mpgc.rpg123393;

import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitari per GameCharacter.
 * Coprono: danno, scudo, cura, mana, veleno, morte.
 */
class GameCharacterTest {

    private GameCharacter character;

    @BeforeEach
    void setUp() {
        character = new GameCharacter("Eroe", 100, 3);
    }

    // -------------------------
    // takeDamage
    // -------------------------

    @Test
    @DisplayName("takeDamage riduce gli HP correttamente senza scudo")
    void takeDamageReducesHp() {
        character.takeDamage(20);
        assertEquals(80, character.getCurrentHp());
    }

    @Test
    @DisplayName("takeDamage non porta gli HP sotto zero")
    void takeDamageDoesNotGoBelowZero() {
        character.takeDamage(999);
        assertEquals(0, character.getCurrentHp());
    }

    @Test
    @DisplayName("takeDamage consuma prima lo scudo")
    void takeDamageConsumesBlockFirst() {
        character.addBlock(10);
        character.takeDamage(6);
        assertEquals(4, character.getBlock());
        assertEquals(100, character.getCurrentHp());
    }

    @Test
    @DisplayName("takeDamage con danno > scudo riduce HP per la differenza")
    void takeDamageExceedingBlock() {
        character.addBlock(5);
        character.takeDamage(15);
        assertEquals(0, character.getBlock());
        assertEquals(90, character.getCurrentHp());
    }

    @Test
    @DisplayName("takeDamage con danno esattamente uguale allo scudo azzera lo scudo senza danni HP")
    void takeDamageExactBlock() {
        character.addBlock(10);
        character.takeDamage(10);
        assertEquals(0, character.getBlock());
        assertEquals(100, character.getCurrentHp());
    }

    // -------------------------
    // heal
    // -------------------------

    @Test
    @DisplayName("heal recupera HP correttamente")
    void healRestoresHp() {
        character.takeDamage(30);
        character.heal(10);
        assertEquals(80, character.getCurrentHp());
    }

    @Test
    @DisplayName("heal non supera il massimo degli HP")
    void healDoesNotExceedMaxHp() {
        character.heal(50);
        assertEquals(100, character.getCurrentHp());
    }

    // -------------------------
    // addBlock / resetBlock
    // -------------------------

    @Test
    @DisplayName("addBlock accumula lo scudo correttamente")
    void addBlockAccumulates() {
        character.addBlock(5);
        character.addBlock(8);
        assertEquals(13, character.getBlock());
    }

    @Test
    @DisplayName("resetBlock azzera lo scudo")
    void resetBlockClearsBlock() {
        character.addBlock(20);
        character.resetBlock();
        assertEquals(0, character.getBlock());
    }

    // -------------------------
    // mana
    // -------------------------

    @Test
    @DisplayName("useMana riduce il mana corrente")
    void useManaReducesMana() {
        character.useMana(2);
        assertEquals(1, character.getCurrentMana());
    }

    @Test
    @DisplayName("useMana non porta il mana sotto zero")
    void useManaDoesNotGoBelowZero() {
        character.useMana(999);
        assertEquals(0, character.getCurrentMana());
    }

    @Test
    @DisplayName("restoreMana ripristina il mana al massimo")
    void restoreManaSetsToMax() {
        character.useMana(3);
        character.restoreMana();
        assertEquals(3, character.getCurrentMana());
    }

    @Test
    @DisplayName("addMana non supera il mana massimo")
    void addManaDoesNotExceedMax() {
        character.addMana(999);
        assertEquals(3, character.getCurrentMana());
    }

    // -------------------------
    // veleno
    // -------------------------

    @Test
    @DisplayName("addPoison accumula gli stack di veleno")
    void addPoisonAccumulates() {
        character.addPoison(3);
        character.addPoison(2);
        assertEquals(5, character.getPoison());
    }

    @Test
    @DisplayName("applyPoison infligge danni pari agli stack e riduce gli stack di 1")
    void applyPoisonDealtsDamageAndDecrementsStack() {
        character.addPoison(4);
        int dmg = character.applyPoison();
        assertEquals(4, dmg);
        assertEquals(96, character.getCurrentHp());
        assertEquals(3, character.getPoison());
    }

    @Test
    @DisplayName("applyPoison con 0 stack non fa danni")
    void applyPoisonWithNoStackDoesNothing() {
        int dmg = character.applyPoison();
        assertEquals(0, dmg);
        assertEquals(100, character.getCurrentHp());
    }

    @Test
    @DisplayName("applyPoison con 1 stack azzera gli stack")
    void applyPoisonWithOneStackClearsPoison() {
        character.addPoison(1);
        character.applyPoison();
        assertEquals(0, character.getPoison());
    }

    // -------------------------
    // isAlive
    // -------------------------

    @Test
    @DisplayName("isAlive è true con HP > 0")
    void isAliveWithHp() {
        assertTrue(character.isAlive());
    }

    @Test
    @DisplayName("isAlive è false con HP = 0")
    void isDeadWithZeroHp() {
        character.takeDamage(100);
        assertFalse(character.isAlive());
    }

    // -------------------------
    // setMaxHp (level up)
    // -------------------------

    @Test
    @DisplayName("setMaxHp aumenta anche gli HP correnti proporzionalmente")
    void setMaxHpIncreasesCurrentHp() {
        character.takeDamage(10); // HP = 90
        character.setMaxHp(120); // bonus = +20
        assertEquals(120, character.getMaxHp());
        assertEquals(110, character.getCurrentHp()); // 90 + 20
    }

    @Test
    @DisplayName("setMaxHp non porta gli HP correnti sopra il nuovo massimo")
    void setMaxHpCappsCurrentHp() {
        character.setMaxHp(80);
        assertEquals(80, character.getCurrentHp()); // era 100, cappato a 80
    }
}
