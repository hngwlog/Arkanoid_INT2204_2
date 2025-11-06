package com.raumania.gameplay.objects.powerup;

import com.raumania.core.SpriteSheet;
import com.raumania.gameplay.manager.GameManager;
import com.raumania.gameplay.objects.Ball;
import com.raumania.utils.ResourcesLoader;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.util.List;

public class SlowPowerUp extends PowerUp{
    /** Tracks the number of currently active "Slow ball" power-ups. */
    protected static int powerUpCounter = 0;

    /**
     * Constructs a new ImmortalPowerUp instance with specified position and size.
     *
     * @param x       the X position of the power-up
     * @param y       the Y position of the power-up
     * @param width   the width of the power-up sprite
     * @param height  the height of the power-up sprite
     */
    public SlowPowerUp(double x, double y, double width, double height) {
        super(x, y, width, height, PowerUpType.SLOW);
        SpriteSheet texture =
                new SpriteSheet(ResourcesLoader.loadImage("slowpowerup.png"), 16, 16, 6, 6);
        setPowerUpTexture(texture);
    }

    /**
     * Applies the "slow" effect to all active balls in the game.
     * <p>
     * Sets all balls to slow mode and starts a timer to revert them
     * to their normal state after the duration expires. If multiple instances
     * of this power-up are active, slow persists until the last timer
     * finishes.
     * </p>
     *
     * @param gameManager the {@link GameManager} managing game objects and state
     */
    @Override
    public void applyEffect(GameManager gameManager) {
        powerUpCounter++;
        List<Ball> balls = gameManager.getBallsList();
        // Enable slow for all active balls
        for (Ball ball : balls) {
            ball.setSpeed(Ball.BALL_SPEED*0.8);
        }

        // Schedule deactivation after the effect duration expires
        PauseTransition timer = new PauseTransition(Duration.seconds(getDuration()));
        timer.setOnFinished(
                e -> {
                    powerUpCounter--;
                    if (powerUpCounter == 0) {
                        for (Ball ball : balls) {
                            ball.setSpeed(Ball.BALL_SPEED);
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
