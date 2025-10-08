package com.raumania.gameplay.objects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import com.raumania.math.Vec2f;

import static com.raumania.utils.Constants.*;

/**
 * Represents the ball in the game.
 * <p>
 * The ball moves freely under its velocity and can collide with the paddle,
 * bricks, or boundaries. It visually corresponds to a {@link Circle} node.
 * </p>
 */
public class Ball extends MovableObject {
    private double radius;
    private Circle view;

    /**
     * Constructs a ball at the specified position with the default radius and speed.
     *
     * @param x the x-coordinate of the ball's center
     * @param y the y-coordinate of the ball's center
     */
    public Ball(double x, double y) {
        super(x, y, BALL_RADIUS * 2.0, BALL_RADIUS * 2.0);
        this.radius = BALL_RADIUS;
        this.setDirection(new Vec2f(1, -1));
        this.setSpeed(BALL_SPEED);
        this.view = new Circle(radius, Color.BLACK);
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
     * Updates the ball's radius and synchronizes its visual representation.
     *
     * @param radius the new radius (must be positive)
     * @throws IllegalArgumentException if {@code radius <= 0}
     */
    public void setRadius(double radius) {
        if (radius <= 0) {
            throw new IllegalArgumentException("radius must be positive");
        }
        this.radius = radius;
        this.getView().setRadius(radius);
        this.updateView();
    }

    /**
     * Returns the graphical representation of the ball.
     *
     * @return the {@link Circle} node used to render this ball
     */
    public Circle getView() {
        return view;
    }

    /**
     * Updates the position of the {@link Circle} view to match the ball's current coordinates.
     * <p>
     * Should be called after updating the ball's position or radius.
     * </p>
     */
    public void updateView() {
        view.setCenterX(x + radius);
        view.setCenterY(y + radius);
    }

    /**
     * Checks and handles collisions between the ball and the game window boundaries.
     * <p>
     * When the ball hits the left, right, top, or bottom edges of the visible game
     * area, its corresponding velocity component is inverted to simulate a bounce.
     * The ball’s position is also clamped so that it never goes outside the screen.
     * </p>
     *
     * <p>
     * A small offset (40 pixels) is subtracted from {@code WINDOW_HEIGHT} to account
     * for the window title bar and decoration height, ensuring that the ball bounces
     * correctly at the bottom edge of the visible play area.
     * </p>
     */
    public void checkCollisionWithBoundary() {
        if (x <= 0) {
            x = 0;
            direction.x *= - 1;
        } else if (x + width >= WINDOW_WIDTH) {
            x = WINDOW_WIDTH - width;
            direction.x *= -1;
        }
        if (y <= 0) {
            y = 0;
            direction.y *= - 1;
        }
        else if (y + height >= WINDOW_HEIGHT - 40) {
            y = WINDOW_HEIGHT - 40 - height;
            direction.y *= - 1;
        }
    }

    /**
     * Updates the ball’s position according to its velocity and acceleration.
     * <p>
     * Also synchronizes the visual representation by calling {@link #updateView()}.
     * </p>
     *
     * @param dt delta time in seconds since the last frame
     */
    @Override
    public void update(double dt) {
        applyMovement(dt);
        checkCollisionWithBoundary();
        updateView();
    }
}
