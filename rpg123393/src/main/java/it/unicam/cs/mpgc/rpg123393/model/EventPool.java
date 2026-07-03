package it.unicam.cs.mpgc.rpg123393.model;

import java.util.List;
import java.util.Random;

/**
 * Pool di eventi narrativi per i nodi EVENT della mappa.
 *
 * Ogni evento ha due o tre scelte con effetti meccanici sul player.
 * Gli effetti sono semplici: heal, danno, veleno, blocco.
 * La complessità narrativa aumenta nella Fase 3 (ispirazione Hollow Knight).
 */
public final class EventPool {

    private EventPool() {}

    private static final Random RNG = new Random();

    public static GameEvent random() {
        List<GameEvent> all = all();
        return all.get(RNG.nextInt(all.size()));
    }

    public static List<GameEvent> all() {
        return List.of(

            new GameEvent(
                "La Fonte Oscura",
                "Trovi una fonte che emana luce viola. L'acqua sembra curare, ma qualcosa non torna.",
                List.of(
                    new GameEvent.EventChoice(
                        "Bevi l'acqua",
                        "Recuperi 20 HP, ma vieni avvelenato (veleno +3).",
                        p -> { p.heal(20); p.addPoison(3); }
                    ),
                    new GameEvent.EventChoice(
                        "Ignora la fonte",
                        "Non succede nulla. Prosegui cauto.",
                        p -> {}
                    )
                )
            ),

            new GameEvent(
                "Il Mercante Misterioso",
                "Un mercante senza volto ti offre qualcosa in cambio di sangue.",
                List.of(
                    new GameEvent.EventChoice(
                        "Accetta l'offerta",
                        "Perdi 15 HP ma ottieni 30 oro.",
                        p -> p.takeDamage(15)
                    ),
                    new GameEvent.EventChoice(
                        "Rifiuta",
                        "Il mercante svanisce. Nessun effetto.",
                        p -> {}
                    )
                )
            ),

            new GameEvent(
                "L'Altare Dimenticato",
                "Un altare coperto di rune. Senti che un'offerta potrebbe portare fortuna o sfortuna.",
                List.of(
                    new GameEvent.EventChoice(
                        "Offri 20 HP",
                        "Perdi 20 HP e ottieni 40 oro e uno scudo di 10.",
                        p -> { p.takeDamage(20); p.addBlock(10); }
                    ),
                    new GameEvent.EventChoice(
                        "Prega senza offrire",
                        "Recuperi 10 HP per la tua fede.",
                        p -> p.heal(10)
                    ),
                    new GameEvent.EventChoice(
                        "Distruggi l'altare",
                        "Vieni maledetto: veleno +2, ma ti senti più forte.",
                        p -> p.addPoison(2)
                    )
                )
            ),

            new GameEvent(
                "Il Vecchio Cavaliere",
                "Un cavaliere sconfitto giace a terra. Ti chiede un favore.",
                List.of(
                    new GameEvent.EventChoice(
                        "Aiutalo",
                        "Lo curi e in cambio ricevi 25 HP.",
                        p -> p.heal(25)
                    ),
                    new GameEvent.EventChoice(
                        "Derubalo",
                        "Ottieni 20 oro, ma ti senti in colpa. Nessun bonus.",
                        p -> {}
                    )
                )
            ),

            new GameEvent(
                "La Trappola",
                "Il pavimento cede sotto i tuoi piedi. Riesci a reagire?",
                List.of(
                    new GameEvent.EventChoice(
                        "Schivi di scatto",
                        "Subisci solo 5 danni e guadagni uno scudo di 8.",
                        p -> { p.takeDamage(5); p.addBlock(8); }
                    ),
                    new GameEvent.EventChoice(
                        "Cadi nella trappola",
                        "Subisci 20 danni.",
                        p -> p.takeDamage(20)
                    )
                )
            )
        );
    }
}
