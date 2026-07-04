package it.unicam.cs.mpgc.rpg123393.model;

import it.unicam.cs.mpgc.rpg123393.service.GameService;

import java.util.List;

/**
 * Rappresenta un evento narrativo della mappa.
 *
 * Un evento ha un testo descrittivo e una lista di scelte.
 * Ogni scelta ha un testo e un effetto (lambda) applicato a GameService,
 * così da poter modificare sia il player che l'oro, il mazzo, ecc.
 */
public class GameEvent {

    private final String             title;
    private final String             description;
    private final List<EventChoice>  choices;

    public GameEvent(String title, String description, List<EventChoice> choices) {
        this.title       = title;
        this.description = description;
        this.choices     = choices;
    }

    public String            getTitle()       { return title; }
    public String            getDescription() { return description; }
    public List<EventChoice> getChoices()     { return choices; }

    // -------------------------------------------------------
    // Inner class: singola scelta
    // -------------------------------------------------------

    public static class EventChoice {
        private final String                                    text;
        private final String                                    outcomeDescription;
        private final java.util.function.Consumer<GameService>  effect;

        public EventChoice(String text, String outcomeDescription,
                           java.util.function.Consumer<GameService> effect) {
            this.text               = text;
            this.outcomeDescription = outcomeDescription;
            this.effect             = effect;
        }

        public String getText()               { return text; }
        public String getOutcomeDescription() { return outcomeDescription; }

        /**
         * Applica l'effetto della scelta tramite GameService.
         * In questo modo l'effetto può toccare player, oro, mazzo, reliquie, ecc.
         */
        public void applyEffect(GameService gs) { effect.accept(gs); }
    }
}
