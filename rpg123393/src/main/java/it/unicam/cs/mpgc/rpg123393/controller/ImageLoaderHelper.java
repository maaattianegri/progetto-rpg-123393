package it.unicam.cs.mpgc.rpg123393.controller;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;

/**
 * Helper centralizzato per il caricamento di immagini da resources.
 * Gestisce i path assoluti (con '/' iniziale) e non lancia eccezioni
 * se il file non è presente: in tal caso l'ImageView viene lasciato vuoto.
 */
public final class ImageLoaderHelper {

    private ImageLoaderHelper() {}

    /**
     * Carica un'immagine da resources e la imposta sull'ImageView fornito.
     * Se il file non esiste, l'ImageView rimane invariato (nessun crash).
     *
     * @param view ImageView di destinazione
     * @param resourcePath path assoluto dalla root delle resources, es. "/images/classes/warrior.png"
     */
    public static void load(ImageView view, String resourcePath) {
        if (view == null || resourcePath == null) return;
        InputStream stream = ImageLoaderHelper.class.getResourceAsStream(resourcePath);
        if (stream == null) {
            stream = ImageLoaderHelper.class.getClassLoader()
                    .getResourceAsStream(resourcePath.replaceFirst("^/", ""));
        }
        if (stream != null) {
            view.setImage(new Image(stream));
        }
    }

    /**
     * Restituisce il path standard di un'immagine di classe.
     * Es. "warrior" → "/images/classes/warrior.png"
     */
    public static String classImagePath(String classKey) {
        return "/images/classes/" + classKey + ".png";
    }

    /**
     * Restituisce il path standard di un'immagine di nemico.
     * Es. "goblin" → "/images/enemies/goblin.png"
     */
    public static String enemyImagePath(String enemyKey) {
        return "/images/enemies/" + enemyKey + ".png";
    }
}
