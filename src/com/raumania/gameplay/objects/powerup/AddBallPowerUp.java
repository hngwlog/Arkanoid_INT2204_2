package com.raumania.gameplay.objects.powerup;

import com.raumania.core.SpriteSheet;
import com.raumania.gameplay.manager.GameManager;
import com.raumania.gameplay.objects.Ball;
import com.raumania.utils.ResourcesLoader;

import java.awt.*;
import java.util.List;

/**
 * Represents the "Add Ball" power-up in the game.
 * <p>
 * When collected by the player, this power-up duplicates all existing balls
 * up to a maximum of 8 total balls. Each new ball inherits the position
 * and direction of its source ball at the moment of duplication.
 * </p>
 * <p>
 * The visual appearance of this power-up is defined by a sprite sheet named
 * {@code "addballpowerup.png"}.
 * </p>
 *
 * @see PowerUp
 * @see com.raumania.gameplay.objects.Ball
 * @see com.raumania.gameplay.manager.GameManager
 */
public class AddBallPowerUp extends PowerUp {
    private static final int powerUpCounter = 0;

    /**
     * Constructs a new AddBallPowerUp instance with specified position and size.
     *
     * @param x       the X position of the power-up
     * @param y       the Y position of the power-up
     * @param width   the width of the power-up sprite
     * @param height  the height of the power-up sprite
     */
    public AddBallPowerUp(double x, double y, double width, double height) {
        super(x, y, width, height, PowerUpType.ADD_BALL);
        SpriteSheet texture = new SpriteSheet(
                ResourcesLoader.loadImage("addballpowerup.png"),
                16, 16, 6, 6);
        setPowerUpTexture(texture);
    }

    /**
     * Applies the "Add Ball" effect to the game.
     * <p>
     * For each currently active ball, this method spawns a new ball at the same position
     * and with the same direction, up to a maximum total of 8 balls.
     * </p>
     *
     * @param gameManager the {@link GameManager} controlling all active game objects
     */
    @Override
    public void applyEffect(GameManager gameManager) {
        List<Ball> balls = gameManager.getBallsList();
        int currentSize = balls.size();
        for (int i = 0; i < currentSize; i++ ) {
            if (currentSize + i >= 8) return;
            gameManager.spawnAdditionalBall(balls.get(i).getX(), balls.get(i).getY(), balls.get(i).getDirection());
        }
    }

    /**
     * Returns the internal counter value of this power-up type.
     * <p>
     * Currently, this always returns {@code 0} since no active tracking is implemented.
     * </p>
     *
     * @return the counter value (always {@code 0})
     */
    @Override
    public int getCounter() {
        return powerUpCounter;
    }

}
