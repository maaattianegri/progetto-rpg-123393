package it.unicam.cs.mpgc.rpg123393.model;

import it.unicam.cs.mpgc.rpg123393.model.player.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardPool {

    public static List<ICard> getStarterPool() {
        return List.of(
                new StrikeCard(),
                new StrikeCard(true),
                new DefendCard(),
                new DefendCard(true)
        );
    }

    public static List<ICard> getClassPool(String className) {
        if (className == null) return new ArrayList<>();
        return switch (className) {
            case "Cavaliere" -> List.of(
                    new DevastatingStrikeCard(),
                    new DevastatingStrikeCard(true),
                    new BattleCryCard(),
                    new BattleCryCard(true),
                    new WhirlwindCard(),
                    new WhirlwindCard(true),
                    new BerserkerRageCard(),
                    new BerserkerRageCard(true),
                    new TauntCard(),
                    new TauntCard(true)
            );
            case "Paladino" -> List.of(
                    new HolyShieldCard(),
                    new HolyShieldCard(true),
                    new DivineLightCard(),
                    new DivineLightCard(true),
                    new RetributionCard(),
                    new RetributionCard(true),
                    new ConsecrationCard(),
                    new ConsecrationCard(true),
                    new IronVowCard(),
                    new IronVowCard(true),
                    new SmiteCard(),
                    new SmiteCard(true),
                    new BlessingCard(),
                    new BlessingCard(true),
                    new HammerOfJusticeCard(),
                    new HammerOfJusticeCard(true)
            );
            case "Mago" -> List.of(
                    new FireNovaCard(),
                    new FireNovaCard(true),
                    new ArcaneStormCard(),
                    new ArcaneStormCard(true),
                    new FrostboltCard(),
                    new FrostboltCard(true),
                    new ManaShieldCard(),
                    new ManaShieldCard(true)
            );
            case "Dracomante" -> List.of(
                    new DragonClawCard(),
                    new DragonClawCard(true),
                    new DragonBreathCard(),
                    new DragonBreathCard(true),
                    new ScaleArmorCard(),
                    new ScaleArmorCard(true),
                    new DragonFangCard(),
                    new DragonFangCard(true)
            );
            case "Assassino" -> List.of(
                    new PoisonBladeCard(),
                    new PoisonBladeCard(true),
                    new ShadowStepCard(),
                    new ShadowStepCard(true),
                    new DeadlyStrikeCard(),
                    new DeadlyStrikeCard(true),
                    new AcidPoisonCard(),
                    new AcidPoisonCard(true),
                    new DoubleBladCard(),
                    new DoubleBladCard(true)
            );
            default -> new ArrayList<>();
        };
    }

    public static List<ICard> getNeutralPool() {
        return List.of(new QuickHealCard());
    }

    public static List<ICard> getRewardOptions(String className) {
        List<ICard> pool = new ArrayList<>(getClassPool(className));
        pool.removeIf(c -> c.getName().endsWith("+"));
        pool.addAll(getNeutralPool());
        Collections.shuffle(pool);
        return pool.subList(0, Math.min(3, pool.size()));
    }

    public static List<ICard> getAllCards() {
        List<ICard> all = new ArrayList<>();
        all.addAll(getStarterPool());
        all.add(new FireballCard());
        all.add(new FireballCard(true));
        for (String cls : List.of("Cavaliere", "Paladino", "Mago", "Dracomante", "Assassino"))
            all.addAll(getClassPool(cls));
        all.addAll(getNeutralPool());
        return all;
    }

    public static List<ICard> getUpgradedPool() {
        List<ICard> all = new ArrayList<>();
        all.add(new StrikeCard(true));
        all.add(new DefendCard(true));
        all.add(new FireballCard(true));
        // Cavaliere
        all.add(new DevastatingStrikeCard(true));
        all.add(new BattleCryCard(true));
        all.add(new WhirlwindCard(true));
        all.add(new BerserkerRageCard(true));
        all.add(new TauntCard(true));
        // Paladino
        all.add(new HolyShieldCard(true));
        all.add(new DivineLightCard(true));
        all.add(new RetributionCard(true));
        all.add(new ConsecrationCard(true));
        all.add(new IronVowCard(true));
        all.add(new SmiteCard(true));
        all.add(new BlessingCard(true));
        all.add(new HammerOfJusticeCard(true));
        // Mago
        all.add(new FireNovaCard(true));
        all.add(new ArcaneStormCard(true));
        all.add(new FrostboltCard(true));
        all.add(new ManaShieldCard(true));
        // Dracomante
        all.add(new DragonClawCard(true));
        all.add(new DragonBreathCard(true));
        all.add(new ScaleArmorCard(true));
        all.add(new DragonFangCard(true));
        // Assassino
        all.add(new PoisonBladeCard(true));
        all.add(new ShadowStepCard(true));
        all.add(new DeadlyStrikeCard(true));
        all.add(new AcidPoisonCard(true));
        all.add(new DoubleBladCard(true));
        return all;
    }

    public static ICard getUpgradedCard(String name) {
        return switch (name) {
            case "Colpo di Spada"           -> new StrikeCard(true);
            case "Scudo di Legno"           -> new DefendCard(true);
            case "Palla di Fuoco"           -> new FireballCard(true);
            // Cavaliere
            case "Colpo Devastante"         -> new DevastatingStrikeCard(true);
            case "Grida di Battaglia"       -> new BattleCryCard(true);
            case "Mulinello"                -> new WhirlwindCard(true);
            case "Furia Berserker"          -> new BerserkerRageCard(true);
            case "Sfida"                    -> new TauntCard(true);
            // Paladino
            case "Scudo Sacro"              -> new HolyShieldCard(true);
            case "Luce Divina"              -> new DivineLightCard(true);
            case "Punizione Divina"         -> new RetributionCard(true);
            case "Consacrazione"            -> new ConsecrationCard(true);
            case "Voto di Ferro"            -> new IronVowCard(true);
            case "Castigo Sacro"            -> new SmiteCard(true);
            case "Benedizione"              -> new BlessingCard(true);
            case "Martello della Giustizia" -> new HammerOfJusticeCard(true);
            // Mago
            case "Nova di Fuoco"            -> new FireNovaCard(true);
            case "Tempesta Arcana"          -> new ArcaneStormCard(true);
            case "Dardo di Ghiaccio"        -> new FrostboltCard(true);
            case "Scudo di Mana"            -> new ManaShieldCard(true);
            // Dracomante
            case "Artiglio del Drago"       -> new DragonClawCard(true);
            case "Soffio del Drago"         -> new DragonBreathCard(true);
            case "Armatura di Scaglie"      -> new ScaleArmorCard(true);
            case "Zanna di Drago"           -> new DragonFangCard(true);
            // Assassino
            case "Lama Avvelenata"          -> new PoisonBladeCard(true);
            case "Passo nell'Ombra"         -> new ShadowStepCard(true);
            case "Colpo Letale"             -> new DeadlyStrikeCard(true);
            case "Veleno Acido"             -> new AcidPoisonCard(true);
            case "Doppia Lama"              -> new DoubleBladCard(true);
            default                         -> null;
        };
    }

    public static ICard getCardByName(String name) {
        if (name == null) return null;
        for (ICard card : getAllCards()) {
            if (name.equals(card.getName())) return card;
        }
        for (ICard card : getUpgradedPool()) {
            if (name.equals(card.getName())) return card;
        }
        return null;
    }
}
