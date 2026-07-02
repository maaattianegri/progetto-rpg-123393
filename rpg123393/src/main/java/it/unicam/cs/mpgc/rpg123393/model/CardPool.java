package it.unicam.cs.mpgc.rpg123393.model;

import it.unicam.cs.mpgc.rpg123393.model.player.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardPool {

    public static List<ICard> getRewardOptions(String className) {
        List<ICard> pool = new ArrayList<>(getClassPool(className));
        pool.addAll(getNeutralPool());
        Collections.shuffle(pool);
        return pool.subList(0, Math.min(3, pool.size()));
    }

    public static List<ICard> getClassPool(String className) {
        if (className == null) return new ArrayList<>();
        return switch (className) {
            case "Guerriero" -> List.of(
                    new DevastatingStrikeCard(),
                    new BattleCryCard(),
                    new WhirlwindCard());
            case "Mago" -> List.of(
                    new ArcaneStormCard(),
                    new FrostboltCard(),
                    new ManaShieldCard());
            case "Dracomante" -> List.of(
                    new DragonClawCard(),
                    new DragonBreathCard2(),
                    new ScaleArmorCard());
            case "Paladino" -> List.of(
                    new HolyShieldCard(),
                    new DivineLightCard(),
                    new RetributionCard());
            case "Assassino" -> List.of(
                    new PoisonBladeCard(),
                    new ShadowStepCard(),
                    new DeadlyStrikeCard());
            default -> new ArrayList<>();
        };
    }

    public static List<ICard> getNeutralPool() {
        return List.of(new QuickHealCard());
    }

    public static List<ICard> getAllCards() {
        List<ICard> all = new ArrayList<>();
        for (String cls : List.of("Guerriero", "Mago", "Dracomante", "Paladino", "Assassino"))
            all.addAll(getClassPool(cls));
        all.addAll(getNeutralPool());
        all.add(new StrikeCard());
        all.add(new DefendCard());
        all.add(new FireballCard());
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
            case "Colpo"            -> new StrikePlusCard();
            case "Difesa"           -> new DefendPlusCard();
            case "Fireball"         -> new FireballPlusCard();
            case "Colpo Devastante" -> new DevastatingStrikePlusCard();
            case "Lama Avvelenata"  -> new PoisonBladePlusCard();
            default                 -> null;
        };
    }
}
