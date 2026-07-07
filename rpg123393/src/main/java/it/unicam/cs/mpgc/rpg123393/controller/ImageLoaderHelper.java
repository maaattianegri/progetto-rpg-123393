package it.unicam.cs.mpgc.rpg123393.controller;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;

/**
 * Helper centralizzato per il caricamento di immagini da resources.
 * Gestisce i path assoluti (con '/' iniziale) e non lancia eccezioni
 * se il file non è presente: in tal caso l'ImageView viene lasciato vuoto.
 *
 * Convenzione nomi file:
 *   images/classes/   knight.jpg | mage.jpg | draco.jpg | paladin.jpg | assassin.jpg
 *   images/battle/    knight_battle.jpg | mage_battle.jpg | draco_battle.jpg | paladin_battle.jpg | assassin_battle.jpg
 *   images/enemies/   <nome_nemico_snake_case>.png
 */
public final class ImageLoaderHelper {

    private ImageLoaderHelper() {}

    /**
     * Carica un'immagine da resources e la imposta sull'ImageView fornito.
     * Se il file non esiste, l'ImageView rimane invariato (nessun crash).
     *
     * @param view         ImageView di destinazione
     * @param resourcePath path assoluto dalla root delle resources, es. "/images/classes/knight.jpg"
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
     * Path immagine card selezione classe.
     * classKey -> nome file senza estensione (es. "knight" -> "/images/classes/knight.jpg")
     */
    public static String classImagePath(String classKey) {
        return "/images/classes/" + classKey + ".jpg";
    }

    /**
     * Path immagine modello in battaglia.
     * classKey -> nome file senza estensione (es. "knight" -> "/images/battle/knight_battle.jpg")
     */
    public static String battleImagePath(String classKey) {
        return "/images/battle/" + classKey + "_battle.jpg";
    }

    /**
     * Path immagine nemico.
     * enemyKey -> nome snake_case (es. "goblin" -> "/images/enemies/goblin.png")
     */
    public static String enemyImagePath(String enemyKey) {
        return "/images/enemies/" + enemyKey + ".png";
    }

    /**
     * Mappa il nome display della classe nella classKey usata per i file.
     * Es. "Cavaliere" -> "knight"
     */
    public static String classKey(String className) {
        return switch (className) {
            case "Cavaliere" -> "knight";
            case "Mago"      -> "mage";
            case "Dracomante"-> "draco";
            case "Paladino"  -> "paladin";
            case "Assassino" -> "assassin";
            default          -> className.toLowerCase();
        };
    }
}
