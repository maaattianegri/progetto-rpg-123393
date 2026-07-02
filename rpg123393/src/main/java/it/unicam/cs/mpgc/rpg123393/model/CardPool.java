package it.unicam.cs.mpgc.rpg123393.model;

import it.unicam.cs.mpgc.rpg123393.model.player.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Definisce i pool di carte per classe.
 *
 * Logica colore/classe:
 *   🔴 Rosso       → Guerriero   (danni fisici, qualche scudo)
 *   🔵 Blu         → Paladino    (scudo, cura, danno condizionale, smite)
 *   🟠 Arancione   → Mago / Dracomante  (danni magici/fuoco, veleno magico)
 *   🟢 Verde       → Assassino   (veleno, doppi colpi, danni scalanti)
 *   🟣 Viola       → Neutrale    (utilità, cure, disponibile a tutti)
 *   ⚪ Argento     → Starter     (multiclasse: Colpo di Spada, Scudo di Legno)
 */
public class CardPool {

    // -------------------------------------------------------
    // Pool per classe
    // -------------------------------------------------------

    public static List<ICard> getClassPool(String className) {
        if (className == null) return new ArrayList<>();
        return switch (className) {
            case "Guerriero" -> List.of(
                    new DevastatingStrikeCard(),        // 2 mana | 12 danni
                    new BattleCryCard(),                // 3 mana | 8 danni + 6 scudo
                    new WhirlwindCard(),                // 2 mana | 5 danni + 5 cura
                    new BerserkerRageCard(),            // 2 mana | 7 danni (12 se HP<50%)
                    new TauntCard()                     // 1 mana | +8 scudo
            );
            case "Paladino" -> List.of(
                    new HolyShieldCard(),               // 2 mana | +12 scudo + 4 cura
                    new DivineLightCard(),              // 2 mana | cura 12 + 4 scudo
                    new RetributionCard(),              // 2 mana | 10 danni (+4 se scudo>0)
                    new ConsecrationCard(),             // 2 mana | 6 danni + 6 scudo
                    new IronVowCard(),                  // 3 mana | +20 scudo
                    new SmiteCard(),                    // 2 mana | 8 danni + azzera scudo nemico
                    new BlessingCard(),                 // 2 mana | cura 8 + 6 scudo
                    new HammerOfJusticeCard()           // 3 mana | 14 danni puri
            );
            case "Mago" -> List.of(
                    new ArcaneStormCard(),              // 2 mana | 10 + 2×veleno danni
                    new FrostboltCard(),                // 2 mana | 7 danni + 2 veleno
                    new ManaShieldCard(),               // 2 mana | +10 scudo + 1 mana
                    new FireNovaCard()                  // 3 mana | 18 danni
            );
            case "Dracomante" -> List.of(
                    new DragonClawCard(),               // 2 mana | 6 danni + 4 scudo
                    new DragonBreathCard2(),            // 2 mana | 9 danni + 3 veleno
                    new ScaleArmorCard(),               // 2 mana | +8 scudo + 4 cura
                    new DragonFangCard()                // 1 mana | 4 danni + 2 scudo
            );
            case "Assassino" -> List.of(
                    new PoisonBladeCard(),              // 1 mana | 3 danni + 3 veleno
                    new ShadowStepCard(),               // 2 mana | 4 danni + 5 veleno
                    new DeadlyStrikeCard(),             // 2 mana | 6 + veleno nemico danni
                    new AcidPoisonCard(),               // 1 mana | +4 veleno (no danno)
                    new DoubleBladCard()                // 2 mana | 4+4 danni
            );
            default -> new ArrayList<>();
        };
    }

    /** Carte neutrali: disponibili nello shop per tutte le classi. */
    public static List<ICard> getNeutralPool() {
        return List.of(new QuickHealCard());  // 1 mana | cura 10 HP
    }

    // -------------------------------------------------------
    // Premi battaglia (3 opzioni casuali dal pool della classe)
    // -------------------------------------------------------

    public static List<ICard> getRewardOptions(String className) {
        List<ICard> pool = new ArrayList<>(getClassPool(className));
        pool.addAll(getNeutralPool());
        Collections.shuffle(pool);
        return pool.subList(0, Math.min(3, pool.size()));
    }

    // -------------------------------------------------------
    // Tutte le carte (usato da collezione e debug unlock)
    // -------------------------------------------------------

    public static List<ICard> getAllCards() {
        List<ICard> all = new ArrayList<>();
        // ⚪ Starter condivise (multiclasse)
        all.add(new StrikeCard());
        all.add(new DefendCard());
        // Pool per classe
        for (String cls : List.of("Guerriero", "Paladino", "Mago", "Dracomante", "Assassino"))
            all.addAll(getClassPool(cls));
        // Neutrale
        all.addAll(getNeutralPool());
        // Versioni potenziate (Fucina)
        all.addAll(getUpgradedPool());
        return all;
    }

    public static List<ICard> getUpgradedPool() {
        return List.of(
                new StrikePlusCard(),             // 🔴 1 mana | 9 danni
                new DefendPlusCard(),             // ⚪ 1 mana | +9 scudo
                new FireballPlusCard(),           // 🟠 2 mana | 18 danni
                new DevastatingStrikePlusCard(),  // 🔴 2 mana | 16 danni
                new PoisonBladePlusCard()         // 🟢 1 mana | 3 danni + 5 veleno
        );
    }

    /**
     * Versione potenziata di una carta dato il suo getName().
     * Usato dalla Fucina (UpgradeController).
     */
    public static ICard getUpgradedCard(String name) {
        return switch (name) {
            case "Colpo di Spada"    -> new StrikePlusCard();
            case "Scudo di Legno"    -> new DefendPlusCard();
            case "Palla di Fuoco"    -> new FireballPlusCard();
            case "Colpo Devastante"  -> new DevastatingStrikePlusCard();
            case "Lama Avvelenata"   -> new PoisonBladePlusCard();
            default                  -> null;
        };
    }
}
