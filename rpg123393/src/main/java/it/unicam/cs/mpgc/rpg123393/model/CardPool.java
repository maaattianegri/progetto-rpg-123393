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
        // Escludi le carte Plus dai reward (si ottengono solo tramite Fucina)
        pool.removeIf(c -> c.getName().endsWith("+"));
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
        // Aggiungi le Plus delle altre classi non ancora in getClassPool
        all.addAll(getUpgradedStarterAndOtherPool());
        return all;
    }

    /** Pool delle carte Plus NON-Paladino (Starter + altre classi). */
    public static List<ICard> getUpgradedStarterAndOtherPool() {
        return List.of(
                new StrikePlusCard(),
                new DefendPlusCard(),
                new FireballPlusCard(),
                new DevastatingStrikePlusCard(),
                new PoisonBladePlusCard(),
                new DragonFangPlusCard(),
                new DragonClawPlusCard(),
                new DragonBreathPlusCard(),
                new ScaleArmorPlusCard()
        );
    }

    /** Ritorna TUTTE le carte potenziabili (usato da getUpgradedCard e da Fucina). */
    public static List<ICard> getUpgradedPool() {
        List<ICard> all = new ArrayList<>(getUpgradedStarterAndOtherPool());
        // Le Plus Paladino sono già in getClassPool("Paladino")
        all.add(new HolyShieldPlusCard());
        all.add(new DivineLightPlusCard());
        all.add(new RetributionPlusCard());
        all.add(new ConsecrationPlusCard());
        all.add(new IronVowPlusCard());
        all.add(new SmitePlusCard());
        all.add(new BlessingPlusCard());
        all.add(new HammerOfJusticePlusCard());
        return all;
    }

    public static ICard getUpgradedCard(String name) {
        return switch (name) {
            case "Colpo di Spada"              -> new StrikePlusCard();
            case "Scudo di Legno"              -> new DefendPlusCard();
            case "Palla di Fuoco"              -> new FireballPlusCard();
            case "Colpo Devastante"            -> new DevastatingStrikePlusCard();
            case "Lama Avvelenata"             -> new PoisonBladePlusCard();
            case "Zanna di Drago"              -> new DragonFangPlusCard();
            case "Artiglio del Drago"          -> new DragonClawPlusCard();
            case "Soffio del Drago"            -> new DragonBreathPlusCard();
            case "Armatura di Scaglie"         -> new ScaleArmorPlusCard();
            // Paladino
            case "Scudo Sacro"                 -> new HolyShieldPlusCard();
            case "Luce Divina"                 -> new DivineLightPlusCard();
            case "Punizione Divina"            -> new RetributionPlusCard();
            case "Consacrazione"               -> new ConsecrationPlusCard();
            case "Voto di Ferro"               -> new IronVowPlusCard();
            case "Castigo Sacro"               -> new SmitePlusCard();
            case "Benedizione"                 -> new BlessingPlusCard();
            case "Martello della Giustizia"    -> new HammerOfJusticePlusCard();
            default                            -> null;
        };
    }

    /**
     * Restituisce una nuova istanza della carta con il nome specificato,
     * cercando in tutto il pool (starter + classi + neutral + upgraded).
     * Usato da GameService.restoreFromState() per ripristinare il deck da save.
     */
    public static ICard getCardByName(String name) {
        if (name == null) return null;
        for (ICard card : getAllCards()) {
            if (name.equals(card.getName())) return card;
        }
        // Cerca anche nelle Plus delle altre classi (non in getAllCards per evitare duplicati)
        for (ICard card : getUpgradedPool()) {
            if (name.equals(card.getName())) return card;
        }
        return null;
    }
}
