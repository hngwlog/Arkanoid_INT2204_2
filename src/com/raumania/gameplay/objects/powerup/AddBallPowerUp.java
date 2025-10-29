package com.raumania.gameplay.objects.powerup;

import com.raumania.gameplay.objects.Ball;
import com.raumania.core.SpriteSheet;
import com.raumania.gameplay.manager.GameManager;
import com.raumania.utils.ResourcesLoader;
import javafx.animation.PauseTransition;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import java.util.List;

import java.awt.*;

public class AddBallPowerUp extends PowerUp {
    private static final double DURATION = 5;
    public AddBallPowerUp(double x, double y, double width, double height) {
        super(x, y, width, height);
        SpriteSheet texture = new SpriteSheet(
                ResourcesLoader.loadImage("addballpowerup.png"),
                16, 16, 6, 6);
        setPowerUpTexture(texture);
    }

    @Override
    public void applyEffect(GameManager gameManager) {
        gameManager.spawnBall(Color.WHITESMOKE);

        PauseTransition timer = new PauseTransition(Duration.seconds(DURATION));
        timer.setOnFinished(e -> {
            List<Ball> balls
                    = gameManager.getBallsList();
            for (int i = 1; i < balls.size(); i++) {
                (balls.get(i)).deactivate();
            }
        });
        timer.play();
    }
}
