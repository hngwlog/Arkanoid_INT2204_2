package com.raumania.gameplay.objects;

/**
 * Represents the ball in the game.
 * The ball moves freely and interacts with paddle and bricks.
 */
public class Ball extends MovableObject {
    private double radius;

    /**
     * Constructs a ball with the specified position and radius.
     *
     * @param x the x-coordinate of the ball's center
     * @param y the y-coordinate of the ball's center
     * @param radius the radius of the ball (must be positive)
     * @throws IllegalArgumentException if radius is non-positive
     */
    public Ball(double x, double y, double radius) {
        super(x, y, radius * 2.0, radius * 2.0);
        if (radius <= 0) {
            throw new IllegalArgumentException("radius must be positive");
        }
        this.radius = radius;
    }

    /**
     * Returns the radius of the ball.
     *
     * @return ball radius
     */
    public double getRadius() {
        return radius;
    }


    /**
     * Updates ball movement. (Not yet implemented)
     *
     * @param dt delta time in seconds
     */
    @Override
    public void update(double dt) {
        // TODO: @gawgua
        applyMovement(dt);
    }
}
