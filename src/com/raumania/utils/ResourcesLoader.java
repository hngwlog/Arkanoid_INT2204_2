package com.raumania.utils;

import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class ResourcesLoader {
    private static String load(String path) {
        return ResourcesLoader.class.getResource(path).toExternalForm();
    }

    /**
     * Load image from resources/images folder
     * @param filename Only image file name (e.g. "paddle.png")
     * @return Image object
     */
    public static Image loadImage(String filename) {
        return new Image(load("/resources/images/" + filename));
    }

    /**
     * Load music from resources/music folder
     * @param filename Only music file name (e.g. "background.mp3")
     * @return MediaPlayer object
     */
    public static MediaPlayer loadMusic(String filename) {
        Media media = new Media(load("/resources/music/" + filename));
        return new MediaPlayer(media);
    }
}
