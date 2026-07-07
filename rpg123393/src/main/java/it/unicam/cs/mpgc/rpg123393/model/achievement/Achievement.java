package it.unicam.cs.mpgc.rpg123393.model.achievement;

/**
 * Rappresenta un singolo achievement del gioco.
 *
 * Gli achievement segreti ({@code secret == true}) mostrano solo l'{@code secretHint}
 * finche' non vengono sbloccati; una volta sbloccati rivelano nome e descrizione completi.
 */
public class Achievement {

    private final String              id;
    private final String              name;
    private final String              description;
    private final boolean             secret;
    private final String              secretHint;
    private final String              icon;
    private final AchievementCategory category;

    public Achievement(String id, String name, String description,
                       boolean secret, String secretHint, String icon,
                       AchievementCategory category) {
        this.id          = id;
        this.name        = name;
        this.description = description;
        this.secret      = secret;
        this.secretHint  = secretHint;
        this.icon        = icon;
        this.category    = category;
    }

    /** Costruttore di comodo per achievement non segreti. */
    public Achievement(String id, String name, String description,
                       String icon, AchievementCategory category) {
        this(id, name, description, false, null, icon, category);
    }

    // -------------------------------------------------------
    // Getter
    // -------------------------------------------------------

    public String              getId()          { return id; }
    public String              getName()        { return name; }
    public String              getDescription() { return description; }
    public boolean             isSecret()       { return secret; }
    public String              getSecretHint()  { return secretHint; }
    public String              getIcon()        { return icon; }
    public AchievementCategory getCategory()    { return category; }

    public String getDisplayDescription(boolean unlocked) {
        if (secret && !unlocked && secretHint != null) return secretHint;
        return description;
    }

    public String getDisplayName(boolean unlocked) {
        if (secret && !unlocked) return "???";
        return name;
    }

    public String getDisplayIcon(boolean unlocked) {
        if (secret && !unlocked) return "\u2753";
        return icon;
    }
}
