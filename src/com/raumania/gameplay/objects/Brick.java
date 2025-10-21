package com.raumania.gameplay.objects;

import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

/**
 * Represents a single brick in the game.
 * <p>
 * A brick has a finite number of hit points and becomes destroyed after
 * sufficient hits. Each brick also owns a JavaFX {@link Rectangle} view
 * used for rendering.
 * </p>
 */
public abstract class Brick extends GameObject {
    private ImageView brickTexture;
    private int hitPoints;

    /**
     * Constructs a new {@code Brick} with the given position and size.
     *
     * @param x the x-coordinate of the brick's top-left corner
     * @param y the y-coordinate of the brick's top-left corner
     * @param width the width of the brick
     * @param height the height of the brick
     */
    public Brick(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    /**
     * Sets the graphical representation of the brick.
     *
     * @param brick the {@link ImageView} node used to render this brick
     */
    public void setBrickTexture(ImageView brick) {
        this.brickTexture = brick;
        this.brickTexture.setX(getX());
        this.brickTexture.setY(getY());
        this.brickTexture.setFitWidth(getWidth());
        this.brickTexture.setFitHeight(getHeight());
    }

    /**
     * Returns the graphical representation of the brick.
     *
     * @return the {@link ImageView} node used to render this brick
     */
    public ImageView getTexture() {
        return brickTexture;
    }

    /**
     * Sets the current hit points of this brick.
     *
     * @param hitPoints the new hit points value
     */
    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
    }

    /**
     * Reduces the brick's hit points by one to reflect a successful hit.
     */
    public void takeHit() {
        hitPoints--;
    }

    /**
     * Checks whether the brick is destroyed.
     *
     * @return {@code true} if hit points are zero or less; {@code false} otherwise
     */
    public boolean isDestroyed() {
        return hitPoints <= 0;
    }
}
