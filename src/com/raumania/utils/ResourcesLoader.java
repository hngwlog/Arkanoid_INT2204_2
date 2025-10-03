package com.raumania.utils;

import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;

import java.net.URL;

public class ResourcesLoader {
    private static String load(String path) {
        URL url =  ResourcesLoader.class.getResource(path);
        if (url == null) {
            throw new RuntimeException("Resource not found: " + path);
        } else {
            return url.toExternalForm();
        }
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
    public static Media loadMusic(String filename) {
        return new Media(load("/resources/music/" + filename));
    }

    public static AudioClip loadSFX(String filename) {
        return new AudioClip(load("/resources/sfx/" + filename));
    }
}
