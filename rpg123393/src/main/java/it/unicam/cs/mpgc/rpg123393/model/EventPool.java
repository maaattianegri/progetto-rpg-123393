package it.unicam.cs.mpgc.rpg123393.model;

import it.unicam.cs.mpgc.rpg123393.service.GameService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Pool di eventi narrativi per i nodi EVENT della mappa.
 *
 * - Gli effetti agiscono su GameService (supporto a oro, carte, reliquie).
 * - Anti-ripetizione leggera per run: ogni evento viene estratto al massimo
 *   una volta per run; quando il pool è esaurito si resetta.
 */
public final class EventPool {

    private EventPool() {}

    private static final Random      RNG          = new Random();
    private static final Set<Integer> usedThisRun = new HashSet<>();

    /** Reimposta l'anti-ripetizione (chiamare a inizio nuova run). */
    public static void resetForNewRun() {
        usedThisRun.clear();
    }

    public static GameEvent random() {
        List<GameEvent> all = all();
        List<Integer> available = IntStream.range(0, all.size())
                .filter(i -> !usedThisRun.contains(i))
                .boxed()
                .collect(Collectors.toList());
        if (available.isEmpty()) {
            usedThisRun.clear();
            available = IntStream.range(0, all.size()).boxed().collect(Collectors.toList());
        }
        int idx = available.get(RNG.nextInt(available.size()));
        usedThisRun.add(idx);
        return all.get(idx);
    }

    public static List<GameEvent> all() {
        return List.of(

            // 1 — La Fonte Oscura
            new GameEvent(
                "La Fonte Oscura",
                "Trovi una fonte che emana luce viola. L'acqua sembra curare, ma qualcosa non torna.",
                List.of(
                    new GameEvent.EventChoice(
                        "Bevi l'acqua",
                        "Recuperi 20 HP, ma vieni avvelenato (veleno +3).",
                        gs -> { gs.getPlayer().heal(20); gs.getPlayer().addPoison(3); }
                    ),
                    new GameEvent.EventChoice(
                        "Ignora la fonte",
                        "Non succede nulla. Prosegui cauto.",
                        gs -> {}
                    )
                )
            ),

            // 2 — Il Mercante Misterioso
            new GameEvent(
                "Il Mercante Misterioso",
                "Un mercante senza volto ti offre qualcosa in cambio di sangue.",
                List.of(
                    new GameEvent.EventChoice(
                        "Accetta l'offerta",
                        "Perdi 15 HP ma ottieni 30 oro.",
                        gs -> { gs.getPlayer().takeDamage(15); gs.addGold(30); }
                    ),
                    new GameEvent.EventChoice(
                        "Rifiuta",
                        "Il mercante svanisce. Nessun effetto.",
                        gs -> {}
                    )
                )
            ),

            // 3 — L'Altare Dimenticato
            new GameEvent(
                "L'Altare Dimenticato",
                "Un altare coperto di rune. Senti che un'offerta potrebbe portare fortuna o sfortuna.",
                List.of(
                    new GameEvent.EventChoice(
                        "Offri 20 HP",
                        "Perdi 20 HP, ottieni 40 oro e uno scudo di 10.",
                        gs -> { gs.getPlayer().takeDamage(20); gs.addGold(40); gs.getPlayer().addBlock(10); }
                    ),
                    new GameEvent.EventChoice(
                        "Prega senza offrire",
                        "Recuperi 10 HP per la tua fede.",
                        gs -> gs.getPlayer().heal(10)
                    ),
                    new GameEvent.EventChoice(
                        "Distruggi l'altare",
                        "Vieni maledetto: veleno +2.",
                        gs -> gs.getPlayer().addPoison(2)
                    )
                )
            ),

            // 4 — Il Vecchio Cavaliere
            new GameEvent(
                "Il Vecchio Cavaliere",
                "Un cavaliere sconfitto giace a terra. Ti chiede un favore.",
                List.of(
                    new GameEvent.EventChoice(
                        "Aiutalo",
                        "Lo curi e in cambio ricevi 25 HP.",
                        gs -> gs.getPlayer().heal(25)
                    ),
                    new GameEvent.EventChoice(
                        "Derubalo",
                        "Ottieni 20 oro.",
                        gs -> gs.addGold(20)
                    )
                )
            ),

            // 5 — La Trappola
            new GameEvent(
                "La Trappola",
                "Il pavimento cede sotto i tuoi piedi. Riesci a reagire?",
                List.of(
                    new GameEvent.EventChoice(
                        "Schivi di scatto",
                        "Subisci solo 5 danni e guadagni uno scudo di 8.",
                        gs -> { gs.getPlayer().takeDamage(5); gs.getPlayer().addBlock(8); }
                    ),
                    new GameEvent.EventChoice(
                        "Cadi nella trappola",
                        "Subisci 20 danni.",
                        gs -> gs.getPlayer().takeDamage(20)
                    )
                )
            ),

            // 6 — Il Contrabbandiere
            new GameEvent(
                "Il Contrabbandiere",
                "Un uomo dall'aria losca ti avvicina nell'ombra. Ha qualcosa da vendere, a caro prezzo.",
                List.of(
                    new GameEvent.EventChoice(
                        "Compra la merce (costa 25 oro)",
                        "Spendi 25 oro e ottieni uno scudo di 15.",
                        gs -> {
                            if (gs.getGold() >= 25) {
                                gs.spendGold(25);
                                gs.getPlayer().addBlock(15);
                            } else {
                                gs.getPlayer().addBlock(5); // parziale se non hai abbastanza oro
                            }
                        }
                    ),
                    new GameEvent.EventChoice(
                        "Ignora e prosegui",
                        "Nessun effetto.",
                        gs -> {}
                    )
                )
            ),

            // 7 — Il Patto del Sangue
            new GameEvent(
                "Il Patto del Sangue",
                "Un'entità ti offre potere oscuro. Il prezzo è la tua vitalit\u00e0.",
                List.of(
                    new GameEvent.EventChoice(
                        "Accetta il patto",
                        "Perdi 30 HP ma ottieni 60 oro.",
                        gs -> { gs.getPlayer().takeDamage(30); gs.addGold(60); }
                    ),
                    new GameEvent.EventChoice(
                        "Rifiuta il patto",
                        "Ti ritiri. L'entit\u00e0 svanisce tra un sibilo.",
                        gs -> {}
                    )
                )
            ),

            // 8 — La Cassa Abbandonata
            new GameEvent(
                "La Cassa Abbandonata",
                "Trovi una cassa sigillata nel mezzo del corridoio. Potrebbe essere una trappola, o un tesoro.",
                List.of(
                    new GameEvent.EventChoice(
                        "Apri la cassa",
                        "Dentro trovi 35 oro!",
                        gs -> gs.addGold(35)
                    ),
                    new GameEvent.EventChoice(
                        "Scuoti la cassa prima",
                        "Senti qualcosa muoversi. Aprendola con cautela trovi 15 oro.",
                        gs -> gs.addGold(15)
                    ),
                    new GameEvent.EventChoice(
                        "Lascia perdere",
                        "Non vale il rischio. Prosegui.",
                        gs -> {}
                    )
                )
            )
        );
    }
}
