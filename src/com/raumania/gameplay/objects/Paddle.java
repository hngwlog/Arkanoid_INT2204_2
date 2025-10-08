package com.raumania.gameplay.objects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.raumania.utils.Constants.PADDLE_SPEED;
import static com.raumania.utils.Constants.WINDOW_WIDTH;

import com.raumania.math.Vec2f;

/**
 * Represents the player's paddle in the game.
 * <p>
 * The paddle can move horizontally across the screen and is responsible for bouncing
 * the {@link Ball} back into play. Its movement speed and boundary clamping
 * are handled internally.
 * </p>
 */
public class Paddle extends MovableObject {
    private Rectangle view;

    /**
     * Constructs a new {@code Paddle} object with the specified position and size.
     *
     * @param x the x-coordinate of the paddle’s top-left corner
     * @param y the y-coordinate of the paddle’s top-left corner
     * @param width the paddle width
     * @param height the paddle height
     */
    public Paddle(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.speed = PADDLE_SPEED;
        this.view = new Rectangle(width, height);
        this.view.setFill(Color.RED);
        this.view.setArcWidth(8);
        this.view.setArcHeight(8);
    }

    /**
     * Returns the graphical representation of the paddle.
     *
     * @return the {@link Rectangle} node used to render this paddle
     */
    public Rectangle getView() {
        return view;
    }

    /**
     * Updates the position of the {@link Rectangle} view to match the paddle's current coordinates.
     * <p>
     * Should be called after each movement update.
     * </p>
     */
    public void updateView() {
        view.setTranslateX(x);
        view.setTranslateY(y);
    }

    /**
     * Sets the paddle’s movement direction to the left.
     */
    public void moveLeft() {
        setDirection(new Vec2f(- 1, 0));
    }

    /**
     * Sets the paddle’s movement direction to the right.
     */
    public void moveRight() {
        setDirection(new Vec2f(1, 0));
    }

    /**
     * Stops the paddle’s horizontal movement.
     */
    public void stop() {
        setDirection(new Vec2f(0, 0));
    }

    /**
     * Checks and handles collisions between the paddle and the game window boundaries.
     * <p>
     * Ensures that the paddle stays within the visible game area.
     * </p>
     */
    public void checkCollisionWithBoundary() {
        if (x < 0) {
            x = 0;
        }
        if (x + width >= WINDOW_WIDTH) {
            x = WINDOW_WIDTH - width;
        }
    }

    /**
     * Updates paddle movement per frame and keeps it within the window boundaries.
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
