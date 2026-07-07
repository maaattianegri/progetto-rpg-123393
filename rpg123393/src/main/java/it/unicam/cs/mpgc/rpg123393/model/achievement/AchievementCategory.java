package it.unicam.cs.mpgc.rpg123393.model.achievement;

/**
 * Categorie degli achievement, usate per raggruppare le card nella schermata UI.
 */
public enum AchievementCategory {

    COMBATTIMENTO("\u2694\uFE0F Combattimento"),
    RUN          ("\uD83C\uDFC3 Run"),
    CLASSI       ("\uD83D\uDDE1\uFE0F Classi"),
    PROGRESSIONE ("\uD83D\uDCB0 Progressione"),
    SEGRETI      ("\uD83C\uDF11 Segreti");

    private final String displayName;

    AchievementCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { return displayName; }
}
