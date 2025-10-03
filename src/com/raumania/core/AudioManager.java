package com.raumania.core;

import com.raumania.utils.ResourcesLoader;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class AudioManager {
    private static final AudioManager instance = new AudioManager();
    // use DoubleProperty for easy binding with UI components
    private final DoubleProperty volume;
    private MediaPlayer currentMusic = null;

    // All audio assets get preloaded here
//    public static final Media HOME_MUSIC = ResourcesLoader.loadMusic("home_bgm.mp3");
//    public static final Media GAME_MUSIC = ResourcesLoader.loadMusic("game_bgm.mp3");
//    public static final Media GAME_OVER_MUSIC = ResourcesLoader.loadMusic("game_over.mp3");
    // SFX preload
//    public static final AudioClip BUTTON_CLICK = ResourcesLoader.loadSFX("button_click.wav");

    private AudioManager() {
        this.volume = new SimpleDoubleProperty(100); // Default volume level
    }

    /**
     * Get the instance of AudioManager.
     *
     * @return the singleton instance
     */
    public static AudioManager getInstance() {
        return instance;
    }

    public void setVolume(int volume) {
        this.volume.set(volume);
    }

    public int getVolume() {
        return this.volume.intValue();
    }

    public DoubleProperty getVolumeProperty() {
        return volume;
    }

    /**
     * Check if the specified music is currently playing.
     * @param music the Media object to check
     * @return true if the specified music is playing, false otherwise
     */
    public boolean isPlaying(Media music) {
        return isPlaying() && currentMusic.getMedia().equals(music);
    }

    /**
     * Check if music is currently playing.
     * @return true if music is playing, false otherwise
     */
    public boolean isPlaying() {
        return currentMusic != null && currentMusic.getStatus() == MediaPlayer.Status.PLAYING;
    }

    /**
     * Play the specified music in a loop.
     * If music is already playing, it will be stopped first.
     *
     * @param music the Media object to play
     */
    public void playBGMusic(Media music) {
        if (isPlaying()) {
            this.stop();
        }

        currentMusic = new MediaPlayer(music);
        currentMusic.setCycleCount(MediaPlayer.INDEFINITE); // Loop indefinitely
        // Bind volume when media is ready so it doesnt throw NullPointerException
        currentMusic.setOnReady(() -> {
            currentMusic.volumeProperty().bind(this.volume.multiply(0.01)); // bind volume to volume property, map 0.0-1.0
        });
        currentMusic.play();
    }

    /**
     * Play the SFX once.
     * @param sfx the Media object to play
     */
    public void playSFX(AudioClip sfx) {
        sfx.play();
    }

    /**
     * Stop the currently playing music, if any.
     */
    public void stop() {
        if (isPlaying()) {
            currentMusic.stop();
            currentMusic.dispose();
            currentMusic = null;
        }
    }
}
