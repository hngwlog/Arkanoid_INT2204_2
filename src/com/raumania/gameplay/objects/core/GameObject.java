package com.raumania.gameplay.objects.core;

/** Base class for all in-game objects (e.g., bricks, ball, paddle). */
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

    /**
     * Checks whether this game object overlaps (intersects) with another one.
     *
     * <p>The check is based on the intersection of two axis-aligned bounding boxes (AABB), using
     * their top-left coordinates ({@code x}, {@code y}) and dimensions ({@code width}, {@code
     * height}). Overlap is detected if the projections of the two rectangles intersect on both the
     * X and Y axes.
     *
     * @param other another {@link GameObject} to check for overlap with
     * @return {@code true} if the two objects overlap (collide), {@code false} otherwise
     */
    public boolean checkOverlap(GameObject other) {
        return (Math.max(this.x, other.getX())
                        <= Math.min(this.x + this.width, other.getX() + other.getWidth())
                && Math.max(this.y, other.getY())
                        <= Math.min(this.y + this.height, other.getY() + other.getHeight()));
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
