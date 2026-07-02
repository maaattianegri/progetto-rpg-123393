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

    public static List<ICard> getClassPool(String className) {
        if (className == null) return new ArrayList<>();
        return switch (className) {
            case "Guerriero" -> List.of(
                    new DevastatingStrikeCard(),
                    new BattleCryCard(),
                    new WhirlwindCard(),
                    new BerserkerRageCard(),
                    new TauntCard()
            );
            case "Paladino" -> List.of(
                    new HolyShieldCard(),
                    new DivineLightCard(),
                    new RetributionCard(),
                    new ConsecrationCard(),
                    new IronVowCard(),
                    new SmiteCard(),
                    new BlessingCard(),
                    new HammerOfJusticeCard()
            );
            case "Mago" -> List.of(
                    new ArcaneStormCard(),
                    new FrostboltCard(),
                    new ManaShieldCard(),
                    new FireNovaCard()
            );
            case "Dracomante" -> List.of(
                    new DragonClawCard(),
                    new DragonBreathCard2(),
                    new ScaleArmorCard(),
                    new DragonFangCard()
            );
            case "Assassino" -> List.of(
                    new PoisonBladeCard(),
                    new ShadowStepCard(),
                    new DeadlyStrikeCard(),
                    new AcidPoisonCard(),
                    new DoubleBladCard()
            );
            default -> new ArrayList<>();
        };
    }

    public static List<ICard> getNeutralPool() {
        return List.of(new QuickHealCard());
    }

    public static List<ICard> getRewardOptions(String className) {
        List<ICard> pool = new ArrayList<>(getClassPool(className));
        pool.addAll(getNeutralPool());
        Collections.shuffle(pool);
        return pool.subList(0, Math.min(3, pool.size()));
    }

    public static List<ICard> getAllCards() {
        List<ICard> all = new ArrayList<>();
        all.add(new StrikeCard());
        all.add(new DefendCard());
        for (String cls : List.of("Guerriero", "Paladino", "Mago", "Dracomante", "Assassino"))
            all.addAll(getClassPool(cls));
        all.addAll(getNeutralPool());
        all.addAll(getUpgradedPool());
        return all;
    }

    public static List<ICard> getUpgradedPool() {
        return List.of(
                new StrikePlusCard(),
                new DefendPlusCard(),
                new FireballPlusCard(),
                new DevastatingStrikePlusCard(),
                new PoisonBladePlusCard()
        );
    }

    public static ICard getUpgradedCard(String name) {
        return switch (name) {
            case "Colpo di Spada"   -> new StrikePlusCard();
            case "Scudo di Legno"   -> new DefendPlusCard();
            case "Palla di Fuoco"   -> new FireballPlusCard();
            case "Colpo Devastante" -> new DevastatingStrikePlusCard();
            case "Lama Avvelenata"  -> new PoisonBladePlusCard();
            default                 -> null;
        };
    }
}
