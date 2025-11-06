package com.raumania.gameplay.objects.powerup;

import com.raumania.core.SpriteSheet;
import com.raumania.gameplay.manager.GameManager;
import com.raumania.gameplay.objects.Paddle;
import com.raumania.utils.ResourcesLoader;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

/**
 * Represents the "Extend Paddle" power-up in the game.
 * <p>
 * When collected by the player, this power-up temporarily increases the paddle’s
 * width by a fixed scale factor. If multiple "Extend Paddle" power-ups are active
 * at the same time, their effects stack in duration — the paddle will only return
 * to normal size when all active effects have expired.
 * </p>
 * <p>
 * The effect duration is handled via a {@link PauseTransition} timer, which automatically
 * resets the paddle size after the duration expires. The visual representation of this
 * power-up uses the {@code "extendpaddlepowerup.png"} sprite.
 * </p>
 *
 * @see PowerUp
 * @see com.raumania.gameplay.objects.Paddle
 * @see com.raumania.gameplay.manager.GameManager
 */
public class ExtendPaddlePowerUp extends PowerUp {
    private static final double SCALE = 1.5;

    /** Tracks the number of currently active "Extend Paddle" power-ups. */
    protected static int powerUpCounter = 0;

    /** Timer that controls how long the power-up effect lasts. */
    private PauseTransition timer;

    /**
     * Constructs a new ExtendPaddlePowerUp instance with the specified position and size.
     *
     * @param x       the X position of the power-up
     * @param y       the Y position of the power-up
     * @param width   the width of the power-up sprite
     * @param height  the height of the power-up sprite
     */
    public ExtendPaddlePowerUp(double x, double y, double width, double height) {
        super(x, y, width, height, PowerUpType.EXTEND_PADDLE);
        SpriteSheet texture = new SpriteSheet(
                ResourcesLoader.loadImage("extendpaddlepowerup.png"),
                16, 16, 6, 6);
        setPowerUpTexture(texture);
    }

    /**
     * Applies the "Extend Paddle" effect.
     * <p>
     * Increases the paddle width by {@link #SCALE}, updates its texture width,
     * and starts a timer that reverts the paddle to its original size after
     * the power-up duration expires. If multiple power-ups overlap, the paddle
     * size will only reset when the last one finishes.
     * </p>
     *
     * @param gameManager the {@link GameManager} managing the paddle and game objects
     */
    @Override
    public void applyEffect(GameManager gameManager) {
        powerUpCounter++;
        Paddle paddle = gameManager.getPaddle();
        paddle.setWidth(Paddle.PADDLE_WIDTH * SCALE);
        paddle.getTexture().setFitWidth(Paddle.PADDLE_WIDTH * SCALE);

        PauseTransition timer = new PauseTransition(Duration.seconds(getDuration()));
        timer.setOnFinished(e -> {
            powerUpCounter--;
            if (powerUpCounter == 0) {
                paddle.setWidth(Paddle.PADDLE_WIDTH);
                paddle.getTexture().setFitWidth(Paddle.PADDLE_WIDTH);
            }
        });
        timer.play();
    }

    /**
     * Returns the number of currently active "Extend Paddle" power-up effects.
     *
     * @return the current power-up counter value
     */
    @Override
    public int getCounter() {
        return powerUpCounter;
    }

}
