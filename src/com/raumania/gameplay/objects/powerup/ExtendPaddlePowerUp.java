package com.raumania.gameplay.objects.powerup;

import com.raumania.core.SpriteSheet;
import com.raumania.gameplay.manager.GameManager;
import com.raumania.gameplay.objects.Paddle;
import com.raumania.utils.ResourcesLoader;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class ExtendPaddlePowerUp extends PowerUp{
    private static final double SCALE = 1.2;
    private static final double DURATION = 5;

    public ExtendPaddlePowerUp(double x, double y, double width, double height) {
        super(x, y, width, height);
        SpriteSheet texture = new SpriteSheet(
                ResourcesLoader.loadImage("extendpaddlepowerup.png"),
                16, 16, 6, 6);
        setPowerUpTexture(texture);
    }

    @Override
    public void applyEffect(GameManager gameManager) {
        Paddle paddle = gameManager.getPaddle();
        double originalWidth = paddle.getWidth();
        double currentPaddleWidth = originalWidth * SCALE;

        paddle.setWidth(currentPaddleWidth);
        paddle.getTexture().setFitWidth(currentPaddleWidth);

        PauseTransition timer = new PauseTransition(Duration.seconds(DURATION));
        timer.setOnFinished(e -> {
            paddle.setWidth(originalWidth);
            paddle.getTexture().setFitWidth(originalWidth);
        });
        timer.play();
    }
}
