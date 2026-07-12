package it.unicam.cs.mpgc.rpg123393;

import it.unicam.cs.mpgc.rpg123393.model.DefendCard;
import it.unicam.cs.mpgc.rpg123393.model.FireballCard;
import it.unicam.cs.mpgc.rpg123393.model.GameCharacter;
import it.unicam.cs.mpgc.rpg123393.model.StrikeCard;
import it.unicam.cs.mpgc.rpg123393.model.player.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitari per tutte le carte (base e versione upgraded).
 * Verifica che ogni carta applichi i valori corretti di danno/scudo/cura/veleno
 * e che il flag 'upgraded' produca i valori potenziati attesi.
 */
class CardTest {

    private GameCharacter player;
    private GameCharacter enemy;

    @BeforeEach
    void setUp() {
        player = new GameCharacter("Giocatore", 100, 10);
        enemy  = new GameCharacter("Nemico",    100, 10);
    }

    // =====================
    // CARTE UNIVERSALI
    // =====================

    @Test @DisplayName("StrikeCard base infligge 6 danni")
    void strikeBase() {
        new StrikeCard().play(player, enemy);
        assertEquals(94, enemy.getCurrentHp());
    }

    @Test @DisplayName("StrikeCard upgraded infligge 9 danni")
    void strikeUpgraded() {
        new StrikeCard(true).play(player, enemy);
        assertEquals(91, enemy.getCurrentHp());
    }

    @Test @DisplayName("DefendCard base aggiunge 6 scudo")
    void defendBase() {
        new DefendCard().play(player, enemy);
        assertEquals(6, player.getBlock());
    }

    @Test @DisplayName("DefendCard upgraded aggiunge 9 scudo")
    void defendUpgraded() {
        new DefendCard(true).play(player, enemy);
        assertEquals(9, player.getBlock());
    }

    @Test @DisplayName("FireballCard base infligge 14 danni")
    void fireballBase() {
        new FireballCard().play(player, enemy);
        assertEquals(86, enemy.getCurrentHp());
    }

    @Test @DisplayName("FireballCard upgraded infligge 18 danni")
    void fireballUpgraded() {
        new FireballCard(true).play(player, enemy);
        assertEquals(82, enemy.getCurrentHp());
    }

    // =====================
    // GUERRIERO
    // =====================

    @Test @DisplayName("BerserkerRageCard base: 7 dmg + 5 scudo")
    void berserkerRageBase() {
        new BerserkerRageCard().play(player, enemy);
        assertEquals(93, enemy.getCurrentHp());
        assertEquals(5, player.getBlock());
    }

    @Test @DisplayName("BerserkerRageCard upgraded: 9 dmg + 6 scudo")
    void berserkerRageUpgraded() {
        new BerserkerRageCard(true).play(player, enemy);
        assertEquals(91, enemy.getCurrentHp());
        assertEquals(6, player.getBlock());
    }

    @Test @DisplayName("DevastatingStrikeCard base: 12 dmg")
    void devastatingStrikeBase() {
        new DevastatingStrikeCard().play(player, enemy);
        assertEquals(88, enemy.getCurrentHp());
    }

    @Test @DisplayName("DevastatingStrikeCard upgraded: 16 dmg")
    void devastatingStrikeUpgraded() {
        new DevastatingStrikeCard(true).play(player, enemy);
        assertEquals(84, enemy.getCurrentHp());
    }

    @Test @DisplayName("TauntCard base: +8 scudo")
    void tauntBase() {
        new TauntCard().play(player, enemy);
        assertEquals(8, player.getBlock());
    }

    @Test @DisplayName("TauntCard upgraded: +12 scudo")
    void tauntUpgraded() {
        new TauntCard(true).play(player, enemy);
        assertEquals(12, player.getBlock());
    }

    @Test @DisplayName("WhirlwindCard base: 5 dmg + 5 cura")
    void whirlwindBase() {
        player.takeDamage(20); // HP = 80
        new WhirlwindCard().play(player, enemy);
        assertEquals(95, enemy.getCurrentHp());
        assertEquals(85, player.getCurrentHp()); // 80 + 5
    }

    @Test @DisplayName("WhirlwindCard upgraded: 7 dmg + 7 cura")
    void whirlwindUpgraded() {
        player.takeDamage(20);
        new WhirlwindCard(true).play(player, enemy);
        assertEquals(93, enemy.getCurrentHp());
        assertEquals(87, player.getCurrentHp());
    }

    @Test @DisplayName("BattleCryCard base: 8 dmg + 6 scudo")
    void battleCryBase() {
        new BattleCryCard().play(player, enemy);
        assertEquals(92, enemy.getCurrentHp());
        assertEquals(6, player.getBlock());
    }

    @Test @DisplayName("BattleCryCard upgraded: 10 dmg + 8 scudo")
    void battleCryUpgraded() {
        new BattleCryCard(true).play(player, enemy);
        assertEquals(90, enemy.getCurrentHp());
        assertEquals(8, player.getBlock());
    }

    // =====================
    // PALADINO
    // =====================

    @Test @DisplayName("HolyShieldCard base: 4 dmg + 12 scudo + 4 cura")
    void holyShieldBase() {
        player.takeDamage(20);
        new HolyShieldCard().play(player, enemy);
        assertEquals(96, enemy.getCurrentHp());
        assertEquals(12, player.getBlock());
        assertEquals(84, player.getCurrentHp()); // 80 + 4
    }

    @Test @DisplayName("HolyShieldCard upgraded: 5 dmg + 14 scudo + 5 cura")
    void holyShieldUpgraded() {
        player.takeDamage(20);
        new HolyShieldCard(true).play(player, enemy);
        assertEquals(95, enemy.getCurrentHp());
        assertEquals(14, player.getBlock());
        assertEquals(85, player.getCurrentHp());
    }

    @Test @DisplayName("DivineLightCard base: 12 cura + 4 scudo")
    void divineLightBase() {
        player.takeDamage(30);
        new DivineLightCard().play(player, enemy);
        assertEquals(82, player.getCurrentHp()); // 70 + 12
        assertEquals(4, player.getBlock());
    }

    @Test @DisplayName("DivineLightCard upgraded: 15 cura + 6 scudo")
    void divineLightUpgraded() {
        player.takeDamage(30);
        new DivineLightCard(true).play(player, enemy);
        assertEquals(85, player.getCurrentHp());
        assertEquals(6, player.getBlock());
    }

    @Test @DisplayName("BlessingCard base: 8 cura + 6 scudo")
    void blessingBase() {
        player.takeDamage(20);
        new BlessingCard().play(player, enemy);
        assertEquals(88, player.getCurrentHp());
        assertEquals(6, player.getBlock());
    }

    @Test @DisplayName("BlessingCard upgraded: 10 cura + 8 scudo")
    void blessingUpgraded() {
        player.takeDamage(20);
        new BlessingCard(true).play(player, enemy);
        assertEquals(90, player.getCurrentHp());
        assertEquals(8, player.getBlock());
    }

    @Test @DisplayName("ConsecrationCard base: 8 dmg + 6 scudo")
    void consecrationBase() {
        new ConsecrationCard().play(player, enemy);
        assertEquals(92, enemy.getCurrentHp());
        assertEquals(6, player.getBlock());
    }

    @Test @DisplayName("ConsecrationCard upgraded: 10 dmg + 8 scudo")
    void consecrationUpgraded() {
        new ConsecrationCard(true).play(player, enemy);
        assertEquals(90, enemy.getCurrentHp());
        assertEquals(8, player.getBlock());
    }

    @Test @DisplayName("SmiteCard base: 8 dmg + resetBlock nemico (costo 2 mana)")
    void smiteBase() {
        // SmiteCard costa 2 mana. Player ha 10 mana di default → la carta si gioca.
        // resetBlock() viene chiamato prima di takeDamage, quindi lo scudo nemico non assorbe.
        enemy.addBlock(20);
        new SmiteCard().play(player, enemy);
        assertEquals(92, enemy.getCurrentHp());  // 100 - 8, lo scudo è già stato azzerato
        assertEquals(0, enemy.getBlock());
        assertEquals(8, player.getCurrentMana()); // 10 - 2
    }

    @Test @DisplayName("SmiteCard upgraded: 11 dmg + resetBlock nemico (costo 2 mana)")
    void smiteUpgraded() {
        enemy.addBlock(20);
        new SmiteCard(true).play(player, enemy);
        assertEquals(89, enemy.getCurrentHp());  // 100 - 11
        assertEquals(0, enemy.getBlock());
        assertEquals(8, player.getCurrentMana()); // 10 - 2
    }

    @Test @DisplayName("HammerOfJusticeCard base: 14 dmg")
    void hammerBase() {
        new HammerOfJusticeCard().play(player, enemy);
        assertEquals(86, enemy.getCurrentHp());
    }

    @Test @DisplayName("HammerOfJusticeCard upgraded: 18 dmg")
    void hammerUpgraded() {
        new HammerOfJusticeCard(true).play(player, enemy);
        assertEquals(82, enemy.getCurrentHp());
    }

    @Test @DisplayName("IronVowCard base: 12 scudo + 8 dmg")
    void ironVowBase() {
        new IronVowCard().play(player, enemy);
        assertEquals(12, player.getBlock());
        assertEquals(92, enemy.getCurrentHp());
    }

    @Test @DisplayName("IronVowCard upgraded: 15 scudo + 10 dmg")
    void ironVowUpgraded() {
        new IronVowCard(true).play(player, enemy);
        assertEquals(15, player.getBlock());
        assertEquals(90, enemy.getCurrentHp());
    }

    @Test @DisplayName("RetributionCard base: 10 dmg, +6 bonus se player ha scudo")
    void retributionBaseWithBlock() {
        player.addBlock(5);
        new RetributionCard().play(player, enemy);
        assertEquals(84, enemy.getCurrentHp()); // 10 + 6
    }

    @Test @DisplayName("RetributionCard base: solo 10 dmg senza scudo")
    void retributionBaseNoBlock() {
        new RetributionCard().play(player, enemy);
        assertEquals(90, enemy.getCurrentHp());
    }

    @Test @DisplayName("RetributionCard upgraded: 11 dmg, +8 bonus se player ha scudo")
    void retributionUpgradedWithBlock() {
        player.addBlock(5);
        new RetributionCard(true).play(player, enemy);
        assertEquals(81, enemy.getCurrentHp()); // 11 + 8
    }

    // =====================
    // MAGO
    // =====================

    @Test @DisplayName("FrostboltCard base: 7 dmg + 2 veleno")
    void frostboltBase() {
        new FrostboltCard().play(player, enemy);
        assertEquals(93, enemy.getCurrentHp());
        assertEquals(2, enemy.getPoison());
    }

    @Test @DisplayName("FrostboltCard upgraded: 9 dmg + 3 veleno")
    void frostboltUpgraded() {
        new FrostboltCard(true).play(player, enemy);
        assertEquals(91, enemy.getCurrentHp());
        assertEquals(3, enemy.getPoison());
    }

    @Test @DisplayName("FireNovaCard base: 18 dmg")
    void fireNovaBase() {
        new FireNovaCard().play(player, enemy);
        assertEquals(82, enemy.getCurrentHp());
    }

    @Test @DisplayName("FireNovaCard upgraded: 24 dmg")
    void fireNovaUpgraded() {
        new FireNovaCard(true).play(player, enemy);
        assertEquals(76, enemy.getCurrentHp());
    }

    @Test @DisplayName("ManaShieldCard base: +10 scudo, costa 1 mana")
    void manaShieldBase() {
        // ManaShieldCard costa 1 mana, aggiunge 10 scudo. Nessun recupero mana.
        new ManaShieldCard().play(player, enemy);
        assertEquals(10, player.getBlock());
        assertEquals(9, player.getCurrentMana()); // 10 - 1
    }

    @Test @DisplayName("ManaShieldCard upgraded: +14 scudo, costa 2 mana")
    void manaShieldUpgraded() {
        new ManaShieldCard(true).play(player, enemy);
        assertEquals(14, player.getBlock());
        assertEquals(8, player.getCurrentMana()); // 10 - 2
    }

    @Test @DisplayName("ArcaneStormCard base: 10 + veleno*2 dmg (0 stack)")
    void arcaneStormBaseNoPoison() {
        new ArcaneStormCard().play(player, enemy);
        assertEquals(90, enemy.getCurrentHp()); // 10 + 0*2
    }

    @Test @DisplayName("ArcaneStormCard base: 10 + veleno*2 dmg (3 stack veleno)")
    void arcaneStormBaseWithPoison() {
        enemy.addPoison(3);
        new ArcaneStormCard().play(player, enemy);
        assertEquals(84, enemy.getCurrentHp()); // 10 + 3*2
    }

    @Test @DisplayName("ArcaneStormCard upgraded: 13 + veleno*3 dmg (3 stack veleno)")
    void arcaneStormUpgradedWithPoison() {
        enemy.addPoison(3);
        new ArcaneStormCard(true).play(player, enemy);
        assertEquals(78, enemy.getCurrentHp()); // 13 + 3*3
    }

    // =====================
    // DRACOMANTE
    // =====================

    @Test @DisplayName("DragonClawCard base: 6 dmg + 4 scudo")
    void dragonClawBase() {
        new DragonClawCard().play(player, enemy);
        assertEquals(94, enemy.getCurrentHp());
        assertEquals(4, player.getBlock());
    }

    @Test @DisplayName("DragonClawCard upgraded: 9 dmg + 6 scudo")
    void dragonClawUpgraded() {
        new DragonClawCard(true).play(player, enemy);
        assertEquals(91, enemy.getCurrentHp());
        assertEquals(6, player.getBlock());
    }

    @Test @DisplayName("DragonBreathCard base: 9 dmg + 3 veleno")
    void dragonBreathBase() {
        new DragonBreathCard().play(player, enemy);
        assertEquals(91, enemy.getCurrentHp());
        assertEquals(3, enemy.getPoison());
    }

    @Test @DisplayName("DragonBreathCard upgraded: 13 dmg + 5 veleno")
    void dragonBreathUpgraded() {
        new DragonBreathCard(true).play(player, enemy);
        assertEquals(87, enemy.getCurrentHp());
        assertEquals(5, enemy.getPoison());
    }

    @Test @DisplayName("DragonFangCard base: 4 dmg + 2 scudo")
    void dragonFangBase() {
        new DragonFangCard().play(player, enemy);
        assertEquals(96, enemy.getCurrentHp());
        assertEquals(2, player.getBlock());
    }

    @Test @DisplayName("DragonFangCard upgraded: 6 dmg + 3 scudo")
    void dragonFangUpgraded() {
        new DragonFangCard(true).play(player, enemy);
        assertEquals(94, enemy.getCurrentHp());
        assertEquals(3, player.getBlock());
    }

    @Test @DisplayName("ScaleArmorCard base: +8 scudo + 4 cura")
    void scaleArmorBase() {
        player.takeDamage(20);
        new ScaleArmorCard().play(player, enemy);
        assertEquals(8, player.getBlock());
        assertEquals(84, player.getCurrentHp()); // 80 + 4
    }

    @Test @DisplayName("ScaleArmorCard upgraded: +12 scudo + 8 cura")
    void scaleArmorUpgraded() {
        player.takeDamage(20);
        new ScaleArmorCard(true).play(player, enemy);
        assertEquals(12, player.getBlock());
        assertEquals(88, player.getCurrentHp());
    }

    // =====================
    // ASSASSINO
    // =====================

    @Test @DisplayName("AcidPoisonCard base: 4 stack veleno senza danno diretto")
    void acidPoisonBase() {
        new AcidPoisonCard().play(player, enemy);
        assertEquals(4, enemy.getPoison());
        assertEquals(100, enemy.getCurrentHp());
    }

    @Test @DisplayName("AcidPoisonCard upgraded: 6 stack veleno")
    void acidPoisonUpgraded() {
        new AcidPoisonCard(true).play(player, enemy);
        assertEquals(6, enemy.getPoison());
    }

    @Test @DisplayName("DoubleBladCard base: 2 colpi da 4 (tot 8 dmg)")
    void doubleBladBase() {
        new DoubleBladCard().play(player, enemy);
        assertEquals(92, enemy.getCurrentHp());
    }

    @Test @DisplayName("DoubleBladCard upgraded: 2 colpi da 6 (tot 12 dmg)")
    void doubleBladUpgraded() {
        new DoubleBladCard(true).play(player, enemy);
        assertEquals(88, enemy.getCurrentHp());
    }

    @Test @DisplayName("PoisonBladeCard base: 3 dmg + 3 veleno")
    void poisonBladeBase() {
        new PoisonBladeCard().play(player, enemy);
        assertEquals(97, enemy.getCurrentHp());
        assertEquals(3, enemy.getPoison());
    }

    @Test @DisplayName("PoisonBladeCard upgraded: 3 dmg + 5 veleno")
    void poisonBladeUpgraded() {
        new PoisonBladeCard(true).play(player, enemy);
        assertEquals(97, enemy.getCurrentHp());
        assertEquals(5, enemy.getPoison());
    }

    @Test @DisplayName("DeadlyStrikeCard base: 6 + stack veleno dmg (3 stack)")
    void deadlyStrikeBase() {
        enemy.addPoison(3);
        new DeadlyStrikeCard().play(player, enemy);
        assertEquals(91, enemy.getCurrentHp()); // 100 - (6+3)
    }

    @Test @DisplayName("DeadlyStrikeCard upgraded: 8 + stack veleno dmg (3 stack)")
    void deadlyStrikeUpgraded() {
        enemy.addPoison(3);
        new DeadlyStrikeCard(true).play(player, enemy);
        assertEquals(89, enemy.getCurrentHp()); // 100 - (8+3)
    }

    @Test @DisplayName("ShadowStepCard base: 4 dmg + 5 veleno")
    void shadowStepBase() {
        new ShadowStepCard().play(player, enemy);
        assertEquals(96, enemy.getCurrentHp());
        assertEquals(5, enemy.getPoison());
    }

    @Test @DisplayName("ShadowStepCard upgraded: 5 dmg + 7 veleno")
    void shadowStepUpgraded() {
        new ShadowStepCard(true).play(player, enemy);
        assertEquals(95, enemy.getCurrentHp());
        assertEquals(7, enemy.getPoison());
    }

    // =====================
    // NESSUN MANA — carta non giocata
    // =====================

    @Test @DisplayName("Carta non giocata se mana insufficiente")
    void cardNotPlayedWithInsufficientMana() {
        GameCharacter broke = new GameCharacter("Broke", 100, 0);
        new StrikeCard().play(broke, enemy);
        assertEquals(100, enemy.getCurrentHp()); // nessun danno
    }
}
