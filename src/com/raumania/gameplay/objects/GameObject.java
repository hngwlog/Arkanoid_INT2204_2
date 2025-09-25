package com.raumania.gameplay.objects;

import com.raumania.gui.Renderer;

/**
 * Base class for all in-game objects (e.g., bricks, ball, paddle).
 */
public abstract class GameObject {
    protected double x, y, width, height;

    /**
     * Creates a new game object.
     *
     * @param x initial x-coordinate (top-left)
     * @param y initial y-coordinate (top-left)
     * @param width object width (> 0)
     * @param height object height (> 0)
     * @throws IllegalArgumentException if width or height is non-positive
     */
    public GameObject(double x, double y, double width, double height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("width and height must be positive");
        }
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Updates the object logic every frame.
     *
     * @param dt delta time in seconds
     */
    public abstract void update(double dt);

    /** Renders the object using the provided renderer abstraction. */
    public abstract void render(Renderer renderer);

    /** Moves the object to an absolute position (top-left). */
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /** Translates the object by the given delta. */
    public void translate(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}
