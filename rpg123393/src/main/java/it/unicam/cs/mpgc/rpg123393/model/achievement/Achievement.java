package it.unicam.cs.mpgc.rpg123393.model.achievement;

/**
 * Rappresenta un singolo achievement del gioco.
 *
 * Gli achievement segreti ({@code secret == true}) mostrano solo l'{@code secretHint}
 * finche' non vengono sbloccati; una volta sbloccati rivelano nome e descrizione completi.
 */
public class Achievement {

    private final String  id;
    private final String  name;
    private final String  description;
    private final boolean secret;
    /**
     * Testo mostrato al posto della descrizione per gli achievement segreti non ancora
     * sbloccati. Ignorato se {@code secret == false}.
     */
    private final String  secretHint;
    /** Emoji o percorso risorsa usato come icona nella schermata achievement. */
    private final String  icon;

    public Achievement(String id, String name, String description,
                       boolean secret, String secretHint, String icon) {
        this.id          = id;
        this.name        = name;
        this.description = description;
        this.secret      = secret;
        this.secretHint  = secretHint;
        this.icon        = icon;
    }

    /** Costruttore di comodo per achievement non segreti. */
    public Achievement(String id, String name, String description, String icon) {
        this(id, name, description, false, null, icon);
    }

    // -------------------------------------------------------
    // Getter
    // -------------------------------------------------------

    public String  getId()          { return id; }
    public String  getName()        { return name; }
    public String  getDescription() { return description; }
    public boolean isSecret()       { return secret; }
    public String  getSecretHint()  { return secretHint; }
    public String  getIcon()        { return icon; }

    /**
     * Restituisce il testo da mostrare in UI per questo achievement.
     * Se segreto e non ancora sbloccato, mostra l'hint; altrimenti la descrizione.
     *
     * @param unlocked true se il giocatore ha gia' sbloccato questo achievement
     */
    public String getDisplayDescription(boolean unlocked) {
        if (secret && !unlocked && secretHint != null) return secretHint;
        return description;
    }

    /**
     * Restituisce il nome da mostrare in UI.
     * Se segreto e non ancora sbloccato, restituisce "???".
     */
    public String getDisplayName(boolean unlocked) {
        if (secret && !unlocked) return "???";
        return name;
    }

    /**
     * Restituisce l'icona da mostrare in UI.
     * Se segreto e non ancora sbloccato, restituisce un'icona generica.
     */
    public String getDisplayIcon(boolean unlocked) {
        if (secret && !unlocked) return "\u2753"; // ❓
        return icon;
    }
}
