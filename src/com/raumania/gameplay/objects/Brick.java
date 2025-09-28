package com.raumania.gameplay.objects;

/**
 * Represents a single brick in the game.
 * A brick has hit points and can be destroyed after repeated hits.
 */
public class Brick extends GameObject {
    private int hitPoints;

    /**
     * Constructs a new Brick object with given position and size.
     *
     * @param x the x-coordinate of the brick's top-left corner
     * @param y the y-coordinate of the brick's top-left corner
     * @param width the width of the brick
     * @param height the height of the brick
     */
    public Brick(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.hitPoints = 1;
    }

    /**
     * Reduces the brick's hit points by one.
     */
    public void takeHit() {
        hitPoints--;
    }

    /**
     * Checks whether the brick is destroyed.
     *
     * @return true if the brick has zero or fewer hit points, false otherwise
     */
    public boolean isDestroyed() {
        return hitPoints <= 0;
    }

    /**
     * Updates the brick state. (Not yet implemented)
     *
     * @param dt delta time in seconds
     */
    @Override
    public void update(double dt) {
        // TODO: @tuld1806
    }
}
