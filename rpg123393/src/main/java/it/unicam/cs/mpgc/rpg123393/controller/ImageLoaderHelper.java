package it.unicam.cs.mpgc.rpg123393.controller;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

import java.io.InputStream;

/**
 * Helper centralizzato per il caricamento di immagini da resources.
 *
 * Convenzione nomi file (png ha priorità su jpg):
 *   images/classes/      knight.png/.jpg | mage.png/.jpg | ...
 *   images/battle/       knight_battle.png/.jpg | ...
 *   images/enemies/      <nome_nemico_snake_case>.png
 *   images/backgrounds/  menu.jpg | battle.jpg | elite.jpg | boss.jpg | shop.jpg | rest.jpg
 */
public final class ImageLoaderHelper {

    private ImageLoaderHelper() {}

    /** Carica un'immagine su un ImageView; se il file manca non lancia eccezioni. */
    public static void load(ImageView view, String resourcePath) {
        if (view == null || resourcePath == null) return;
        InputStream stream = openStream(resourcePath);
        if (stream != null) {
            view.setImage(new Image(stream));
        }
    }

    /**
     * Applica un'immagine di sfondo (cover) a qualsiasi Region JavaFX.
     * Se il file manca non fa nulla.
     */
    public static void applyBackground(Region region, String resourcePath) {
        if (region == null || resourcePath == null) return;
        InputStream stream = openStream(resourcePath);
        if (stream != null) {
            region.setStyle(region.getStyle()
                    + "-fx-background-image: url('" + resourcePath + "');"
                    + "-fx-background-size: cover;"
                    + "-fx-background-position: center;"
            );
        }
    }

    /**
     * Apre uno stream per un resource path.
     * Prova prima il path esatto, poi come classpath senza slash iniziale.
     */
    private static InputStream openStream(String resourcePath) {
        InputStream stream = ImageLoaderHelper.class.getResourceAsStream(resourcePath);
        if (stream == null) {
            stream = ImageLoaderHelper.class.getClassLoader()
                    .getResourceAsStream(resourcePath.replaceFirst("^/", ""));
        }
        return stream;
    }

    /**
     * Risolve il path provando prima .png poi .jpg.
     * Restituisce il path della versione trovata, o il path .png di default.
     */
    private static String resolveWithFallback(String basePath) {
        String pngPath = basePath + ".png";
        String jpgPath = basePath + ".jpg";
        if (openStream(pngPath) != null) return pngPath;
        if (openStream(jpgPath) != null) return jpgPath;
        return pngPath; // default: png (mostrerd niente se assente, senza crash)
    }

    // -------------------------------------------------------
    // Path helpers
    // -------------------------------------------------------

    public static String classImagePath(String classKey) {
        return resolveWithFallback("/images/classes/" + classKey);
    }

    public static String battleImagePath(String classKey) {
        return resolveWithFallback("/images/battle/" + classKey + "_battle");
    }

    public static String enemyImagePath(String enemyKey) {
        return resolveWithFallback("/images/enemies/" + enemyKey);
    }

    public static String backgroundPath(String key) {
        return resolveWithFallback("/images/backgrounds/" + key);
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
