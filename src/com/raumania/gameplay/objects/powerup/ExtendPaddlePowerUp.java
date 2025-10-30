package com.raumania.gameplay.objects.powerup;

import com.raumania.core.SpriteSheet;
import com.raumania.gameplay.manager.GameManager;
import com.raumania.gameplay.objects.Paddle;
import com.raumania.utils.ResourcesLoader;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class ExtendPaddlePowerUp extends PowerUp {
    private static final double SCALE = 1.5;
    private PauseTransition timer;
    protected static int powerUpCounter = 0;

    public ExtendPaddlePowerUp(double x, double y, double width, double height) {
        super(x, y, width, height, PowerUpType.EXTEND_PADDLE);
        SpriteSheet texture = new SpriteSheet(
                ResourcesLoader.loadImage("extendpaddlepowerup.png"),
                16, 16, 6, 6);
        setPowerUpTexture(texture);
    }

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

    @Override
    public int getCounter() {
        return powerUpCounter;
    }

}
