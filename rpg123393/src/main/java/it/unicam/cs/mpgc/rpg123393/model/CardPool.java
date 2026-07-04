package it.unicam.cs.mpgc.rpg123393.model;

import it.unicam.cs.mpgc.rpg123393.model.player.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardPool {

    public static List<ICard> getClassPool(String className) {
        if (className == null) return new ArrayList<>();
        return switch (className) {
            case "Guerriero" -> List.of(
                    new DevastatingStrikeCard(),
                    new DevastatingStrikePlusCard(),
                    new BattleCryCard(),
                    new BattleCryPlusCard(),
                    new WhirlwindCard(),
                    new WhirlwindPlusCard(),
                    new BerserkerRageCard(),
                    new BerserkerRagePlusCard(),
                    new TauntCard(),
                    new TauntPlusCard()
            );
            case "Paladino" -> List.of(
                    new HolyShieldCard(),
                    new HolyShieldPlusCard(),
                    new DivineLightCard(),
                    new DivineLightPlusCard(),
                    new RetributionCard(),
                    new RetributionPlusCard(),
                    new ConsecrationCard(),
                    new ConsecrationPlusCard(),
                    new IronVowCard(),
                    new IronVowPlusCard(),
                    new SmiteCard(),
                    new SmitePlusCard(),
                    new BlessingCard(),
                    new BlessingPlusCard(),
                    new HammerOfJusticeCard(),
                    new HammerOfJusticePlusCard()
            );
            case "Mago" -> List.of(
                    new FireNovaCard(),
                    new FireNovaPlusCard(),
                    new ArcaneStormCard(),
                    new ArcaneStormPlusCard(),
                    new FrostboltCard(),
                    new FrostboltPlusCard(),
                    new ManaShieldCard(),
                    new ManaShieldPlusCard()
            );
            case "Dracomante" -> List.of(
                    new DragonClawCard(),
                    new DragonClawPlusCard(),
                    new DragonBreathCard2(),
                    new DragonBreathPlusCard(),
                    new ScaleArmorCard(),
                    new ScaleArmorPlusCard(),
                    new DragonFangCard(),
                    new DragonFangPlusCard()
            );
            case "Assassino" -> List.of(
                    new PoisonBladeCard(),
                    new PoisonBladePlusCard(),
                    new ShadowStepCard(),
                    new ShadowStepPlusCard(),
                    new DeadlyStrikeCard(),
                    new DeadlyStrikePlusCard(),
                    new AcidPoisonCard(),
                    new AcidPoisonPlusCard(),
                    new DoubleBladCard(),
                    new DoubleBladPlusCard()
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
        all.add(new StrikeCard());
        all.add(new StrikePlusCard());
        all.add(new DefendCard());
        all.add(new DefendPlusCard());
        all.add(new FireballCard());
        all.add(new FireballPlusCard());
        for (String cls : List.of("Guerriero", "Paladino", "Mago", "Dracomante", "Assassino"))
            all.addAll(getClassPool(cls));
        all.addAll(getNeutralPool());
        return all;
    }

    public static List<ICard> getUpgradedPool() {
        List<ICard> all = new ArrayList<>();
        all.add(new StrikePlusCard());
        all.add(new DefendPlusCard());
        all.add(new FireballPlusCard());
        // Guerriero
        all.add(new DevastatingStrikePlusCard());
        all.add(new BattleCryPlusCard());
        all.add(new WhirlwindPlusCard());
        all.add(new BerserkerRagePlusCard());
        all.add(new TauntPlusCard());
        // Paladino
        all.add(new HolyShieldPlusCard());
        all.add(new DivineLightPlusCard());
        all.add(new RetributionPlusCard());
        all.add(new ConsecrationPlusCard());
        all.add(new IronVowPlusCard());
        all.add(new SmitePlusCard());
        all.add(new BlessingPlusCard());
        all.add(new HammerOfJusticePlusCard());
        // Mago
        all.add(new FireNovaPlusCard());
        all.add(new ArcaneStormPlusCard());
        all.add(new FrostboltPlusCard());
        all.add(new ManaShieldPlusCard());
        // Dracomante
        all.add(new DragonClawPlusCard());
        all.add(new DragonBreathPlusCard());
        all.add(new ScaleArmorPlusCard());
        all.add(new DragonFangPlusCard());
        // Assassino
        all.add(new PoisonBladePlusCard());
        all.add(new ShadowStepPlusCard());
        all.add(new DeadlyStrikePlusCard());
        all.add(new AcidPoisonPlusCard());
        all.add(new DoubleBladPlusCard());
        return all;
    }

    public static ICard getUpgradedCard(String name) {
        return switch (name) {
            case "Colpo di Spada"           -> new StrikePlusCard();
            case "Scudo di Legno"           -> new DefendPlusCard();
            case "Palla di Fuoco"           -> new FireballPlusCard();
            // Guerriero
            case "Colpo Devastante"         -> new DevastatingStrikePlusCard();
            case "Grida di Battaglia"       -> new BattleCryPlusCard();
            case "Mulinello"                -> new WhirlwindPlusCard();
            case "Furia Berserker"          -> new BerserkerRagePlusCard();
            case "Sfida"                    -> new TauntPlusCard();
            // Paladino
            case "Scudo Sacro"              -> new HolyShieldPlusCard();
            case "Luce Divina"              -> new DivineLightPlusCard();
            case "Punizione Divina"         -> new RetributionPlusCard();
            case "Consacrazione"            -> new ConsecrationPlusCard();
            case "Voto di Ferro"            -> new IronVowPlusCard();
            case "Castigo Sacro"            -> new SmitePlusCard();
            case "Benedizione"              -> new BlessingPlusCard();
            case "Martello della Giustizia" -> new HammerOfJusticePlusCard();
            // Mago
            case "Nova di Fuoco"            -> new FireNovaPlusCard();
            case "Tempesta Arcana"          -> new ArcaneStormPlusCard();
            case "Dardo di Ghiaccio"        -> new FrostboltPlusCard();
            case "Scudo di Mana"            -> new ManaShieldPlusCard();
            // Dracomante
            case "Artiglio del Drago"       -> new DragonClawPlusCard();
            case "Soffio del Drago"         -> new DragonBreathPlusCard();
            case "Armatura di Scaglie"      -> new ScaleArmorPlusCard();
            case "Zanna di Drago"           -> new DragonFangPlusCard();
            // Assassino
            case "Lama Avvelenata"          -> new PoisonBladePlusCard();
            case "Passo nell'Ombra"         -> new ShadowStepPlusCard();
            case "Colpo Letale"             -> new DeadlyStrikePlusCard();
            case "Veleno Acido"             -> new AcidPoisonPlusCard();
            case "Doppia Lama"              -> new DoubleBladPlusCard();
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
