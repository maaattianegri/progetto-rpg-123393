package it.unicam.cs.mpgc.rpg123393.model;

import it.unicam.cs.mpgc.rpg123393.model.player.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Pool statico di carte disponibili per la scelta post-vittoria.
 * Ogni classe ha il suo pool + c'è un pool neutro accessibile a tutti.
 */
public class CardPool {

    private static final Random RANDOM = new Random();

    /** Restituisce 3 carte casuali per la scelta post-vittoria. */
    public static List<ICard> getRewardOptions(String className) {
        List<ICard> pool = new ArrayList<>();
        pool.addAll(getClassPool(className));
        pool.addAll(getNeutralPool());
        Collections.shuffle(pool, RANDOM);
        // Ritorna al massimo 3 carte, evitando duplicati di nome
        List<ICard> result = new ArrayList<>();
        List<String> seen  = new ArrayList<>();
        for (ICard c : pool) {
            if (!seen.contains(c.getName())) {
                result.add(c);
                seen.add(c.getName());
            }
            if (result.size() == 3) break;
        }
        return result;
    }

    /** Pool specifico per classe (esclusive + secondarie). */
    public static List<ICard> getClassPool(String className) {
        if (className == null) return new ArrayList<>();
        return switch (className) {
            case "Guerriero" -> List.of(
                    new DevastatingStrikeCard(),
                    new BattleCryCard(),
                    new WhirlwindCard(),
                    new StrikeCard(),
                    new DefendCard()
            );
            case "Mago" -> List.of(
                    new ArcaneStormCard(),
                    new FrostboltCard(),
                    new ManaShieldCard(),
                    new FireballCard(),
                    new DefendCard()
            );
            case "Dracomante" -> List.of(
                    new DragonClawCard(),
                    new DragonBreathCard(),
                    new ScaleArmorCard(),
                    new FireballCard(),
                    new StrikeCard()
            );
            case "Paladino" -> List.of(
                    new HolyShieldCard(),
                    new DivineLightCard(),
                    new RetributionCard(),
                    new DefendCard(),
                    new StrikeCard()
            );
            case "Assassino" -> List.of(
                    new PoisonBladeCard(),
                    new ShadowStepCard(),
                    new DeadlyStrikeCard(),
                    new StrikeCard(),
                    new DefendCard()
            );
            default -> new ArrayList<>();
        };
    }

    /** Pool neutro: accessibile a tutte le classi. */
    public static List<ICard> getNeutralPool() {
        return List.of(
                new QuickHealCard(),
                new StrikeCard(),
                new DefendCard()
        );
    }

    /** Tutte le carte del gioco (per la Collezione). */
    public static List<ICard> getAllCards() {
        List<ICard> all = new ArrayList<>();
        for (String cls : List.of("Guerriero","Mago","Dracomante","Paladino","Assassino"))
            all.addAll(getClassPool(cls));
        all.addAll(getNeutralPool());
        return all;
    }
}
