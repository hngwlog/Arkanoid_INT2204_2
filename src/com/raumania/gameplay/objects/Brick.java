package com.raumania.gameplay.objects;

import javafx.scene.paint.Color;
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
    private Rectangle view;
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
        this.view = new Rectangle(width, height);
        this.view.setX(x);
        this.view.setY(y);
        this.view.setFill(Color.RED);
        this.view.setStroke(Color.BLACK);
        this.view.setStrokeWidth(3);
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

    /**
     * Returns the graphical representation of the brick.
     *
     * @return the {@link Rectangle} node used to render this brick
     */
    public Rectangle getView() {
        return view;
    }
}
