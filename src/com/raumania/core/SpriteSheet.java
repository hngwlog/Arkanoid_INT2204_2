package com.raumania.core;

import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SpriteSheet {
    private final Image img;
    private final ImageView view;
    private final AnimationTimer timer;
    private final int width;
    private final int height;
    private final int totalFrames;
    private final int columns;
    private int currentFrame = 0;
    private double fps = 1.0;

    /**
     * Constructs a new SpriteSheet with the given image and frame dimensions.
     *
     * @param image the sprite sheet image
     * @param width the width of each frame
     * @param height the height of each frame
     * @param totalFrames the total number of frames in the sprite sheet
     * @param columns the number of columns in the sprite sheet
     */
    public SpriteSheet(Image image, int width, int height, int totalFrames, int columns) {
        this.img = image;
        this.width = width;
        this.height = height;
        this.view = new ImageView(img);
        this.view.setViewport(new Rectangle2D(0, 0, width, height));
        this.columns = columns;
        this.totalFrames = totalFrames;
        this.timer =
                new AnimationTimer() {
                    private long lastUpdate = 0;

                    @Override
                    public void handle(long now) {
                        if (now - lastUpdate >= 1_000_000_000 / fps) {
                            nextFrame();
                            lastUpdate = now;
                        }
                    }
                };
    }

    /**
     * Constructs a new SpriteSheet with the given image and frame dimensions.
     *
     * <p>WARNING: This assumes the sprite sheet is fully filled with frames. DO NOT use this
     * constructor if the sprite sheet has empty space.
     *
     * @param image the sprite sheet image
     * @param width the width of each frame
     * @param height the height of each frame
     */
    public SpriteSheet(Image image, int width, int height) {
        this(
                image,
                width,
                height,
                (int) (image.getWidth() / width) * (int) (image.getHeight() / height),
                (int) (image.getWidth() / width));
    }

    /**
     * Returns the ImageView used to render this sprite sheet.
     *
     * @return the ImageView node
     */
    public ImageView getView() {
        return view;
    }

    /**
     * Returns the current frame index of the animation.
     *
     * @return the current frame index
     */
    public int getCurrentFrame() {
        return currentFrame;
    }

    /** Change the frames per second (FPS) of the animation. */
    public void setFps(double fps) {
        this.fps = fps;
    }

    /** Advances to the next frame in the sprite sheet animation. */
    public void nextFrame() {
        currentFrame = (currentFrame + 1) % totalFrames;
        int x = (currentFrame % columns) * height;
        int y = (currentFrame / columns) * width;
        view.setViewport(new Rectangle2D(x, y, width, height));
    }

    /** Plays the sprite sheet animation. */
    public void play() {
        timer.start();
    }

    /** Stops the sprite sheet animation. */
    public void stop() {
        timer.stop();
    }

    public boolean isFinalFrame() {
        return currentFrame == totalFrames - 1;
    }
}
