package it.unicam.cs.mpgc.rpg123393.controller;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

import java.io.InputStream;

/**
 * Helper centralizzato per il caricamento di immagini da resources.
 *
 * Convenzione nomi file:
 *   images/classes/      knight.jpg | mage.jpg | draco.jpg | paladin.jpg | assassin.jpg
 *   images/battle/       knight_battle.jpg | ...
 *   images/enemies/      <nome_nemico_snake_case>.png
 *   images/backgrounds/  menu.jpg | battle.jpg | elite.jpg | boss.jpg | shop.jpg | rest.jpg
 */
public final class ImageLoaderHelper {

    private ImageLoaderHelper() {}

    /** Carica un'immagine su un ImageView; se il file manca non lancia eccezioni. */
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
     * Applica un'immagine di sfondo (cover) a qualsiasi Region JavaFX.
     * Se il file manca non fa nulla.
     *
     * @param region       nodo radice o contenitore a cui applicare lo sfondo
     * @param resourcePath path assoluto dalla root delle resources, es. "/images/backgrounds/menu.jpg"
     */
    public static void applyBackground(Region region, String resourcePath) {
        if (region == null || resourcePath == null) return;
        InputStream stream = ImageLoaderHelper.class.getResourceAsStream(resourcePath);
        if (stream == null) {
            stream = ImageLoaderHelper.class.getClassLoader()
                    .getResourceAsStream(resourcePath.replaceFirst("^/", ""));
        }
        if (stream != null) {
            Image img = new Image(stream);
            String url = img.getUrl();
            // fallback: usiamo -fx-background-image via setStyle
            region.setStyle(region.getStyle()
                    + "-fx-background-image: url('" + resourcePath + "');"
                    + "-fx-background-size: cover;"
                    + "-fx-background-position: center;"
            );
        }
    }

    // -------------------------------------------------------
    // Path helpers
    // -------------------------------------------------------

    public static String classImagePath(String classKey) {
        return "/images/classes/" + classKey + ".jpg";
    }

    public static String battleImagePath(String classKey) {
        return "/images/battle/" + classKey + "_battle.jpg";
    }

    public static String enemyImagePath(String enemyKey) {
        return "/images/enemies/" + enemyKey + ".png";
    }

    public static String backgroundPath(String key) {
        return "/images/backgrounds/" + key + ".jpg";
    }

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
