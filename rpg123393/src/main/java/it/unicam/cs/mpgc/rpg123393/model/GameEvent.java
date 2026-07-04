package it.unicam.cs.mpgc.rpg123393.model;

import java.util.List;

/**
 * Rappresenta un evento narrativo della mappa.
 *
 * Un evento ha un testo descrittivo e una lista di scelte.
 * Ogni scelta ha un testo e un effetto (lambda) applicato a GameCharacter del player.
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
        private final String                      text;
        private final String                      outcomeDescription;
        private final java.util.function.Consumer<GameCharacter> effect;

        public EventChoice(String text, String outcomeDescription,
                           java.util.function.Consumer<GameCharacter> effect) {
            this.text               = text;
            this.outcomeDescription = outcomeDescription;
            this.effect             = effect;
        }

        public String getText()               { return text; }
        public String getOutcomeDescription() { return outcomeDescription; }
        public void   applyEffect(GameCharacter player) { effect.accept(player); }
    }
}
