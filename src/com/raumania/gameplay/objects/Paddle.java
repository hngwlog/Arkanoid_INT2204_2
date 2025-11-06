package com.raumania.gameplay.objects;

import static com.raumania.gui.screen.GameScreen.GAME_WIDTH;

import com.raumania.gameplay.objects.core.MovableObject;
import com.raumania.math.Vec2f;
import com.raumania.utils.ResourcesLoader;

import javafx.scene.image.ImageView;

/**
 * Represents the player's paddle in the game.
 * <p>
 * The paddle can move horizontally across the screen and is responsible for bouncing
 * the {@link Ball} back into play. Its movement speed and boundary clamping
 * are handled internally.
 * </p>
 */
public class Paddle extends MovableObject {
    public static final int PADDLE_WIDTH = 100;
    public static final int PADDLE_HEIGHT = 15;
    public static final int PADDLE_SPEED = 350;
    private static int textureIndex = 0;
    private final ImageView paddleTexture;

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
        this.paddleTexture = new ImageView(ResourcesLoader.loadImage("paddle" + textureIndex + ".png"));
        this.paddleTexture.setFitWidth(width);
        this.paddleTexture.setFitHeight(height);
    }

    /**
     * Set the graphical representation of the paddle.
     *
     * @param paddleTextureIndex index of img used to render this paddle
     */
    public static void setTextureIndex(int paddleTextureIndex) {
        textureIndex = paddleTextureIndex;
    }

    /**
     * Returns the graphical representation of the paddle.
     *
     * @return the {@link ImageView} node used to render this paddle
     */
    public ImageView getTexture() {
        return paddleTexture;
    }

    /**
     * Updates the position of the {@link ImageView} view to match the paddle's current coordinates.
     * <p>
     * Should be called after each movement update.
     * </p>
     */
    public void updateView() {
        paddleTexture.setTranslateX(x);
        paddleTexture.setTranslateY(y);
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
        if (x + width >= GAME_WIDTH) {
            x = GAME_WIDTH - width;
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
