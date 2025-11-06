package com.raumania.gameplay.objects.powerup;

import com.raumania.core.SpriteSheet;
import com.raumania.gameplay.manager.GameManager;
import com.raumania.gameplay.objects.Ball;
import com.raumania.utils.ResourcesLoader;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.util.List;

/**
 * Represents the "Immortal" power-up in the game.
 * <p>
 * When activated, this power-up makes all active balls in the game immune to death
 * (for example, they will not be lost when hitting the bottom of the screen).
 * The effect lasts for a limited time, after which the balls return to their
 * normal, mortal state.
 * </p>
 * <p>
 * If multiple "Immortal" power-ups are collected consecutively, their durations stack.
 * The immortality effect will only end once all active timers have expired.
 * </p>
 * <p>
 * The visual representation of this power-up is defined by the sprite image
 * {@code "immortalpowerup.png"}.
 * </p>
 *
 * @see PowerUp
 * @see com.raumania.gameplay.objects.Ball
 * @see com.raumania.gameplay.manager.GameManager
 */
public class ImmortalPowerUp extends PowerUp {

    /** Tracks the number of currently active "Immortal" power-ups. */
    protected static int powerUpCounter = 0;

    /**
     * Constructs a new ImmortalPowerUp instance with specified position and size.
     *
     * @param x       the X position of the power-up
     * @param y       the Y position of the power-up
     * @param width   the width of the power-up sprite
     * @param height  the height of the power-up sprite
     */
    public ImmortalPowerUp(double x, double y, double width, double height) {
        super(x, y, width, height, PowerUpType.IMMORTAL);
        SpriteSheet texture =
                new SpriteSheet(ResourcesLoader.loadImage("immortalpowerup.png"), 16, 16, 6, 6);
        setPowerUpTexture(texture);
    }

    /**
     * Applies the "Immortal" effect to all active balls in the game.
     * <p>
     * Sets all balls to immortal mode and starts a timer to revert them
     * to their normal state after the duration expires. If multiple instances
     * of this power-up are active, immortality persists until the last timer
     * finishes.
     * </p>
     *
     * @param gameManager the {@link GameManager} managing game objects and state
     */
    @Override
    public void applyEffect(GameManager gameManager) {
        powerUpCounter++;
        List<Ball> balls = gameManager.getBallsList();
        // Enable immortality for all active balls
        for (Ball ball : balls) {
            ball.setImmortal(true);
        }

        // Schedule deactivation after the effect duration expires
        PauseTransition timer = new PauseTransition(Duration.seconds(getDuration()));
        timer.setOnFinished(
                e -> {
                    powerUpCounter--;
                    if (powerUpCounter == 0) {
                        for (Ball ball : balls) {
                            ball.setImmortal(false);
                        }
                    }
                });
        timer.play();
    }

    /**
     * Returns the number of currently active "Immortal" power-up effects.
     *
     * @return the number of active immortality effects
     */
    @Override
    public int getCounter() {
        return powerUpCounter;
    }
}
