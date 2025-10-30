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
    private static int powerUpCounter = 0;

    public AddBallPowerUp(double x, double y, double width, double height) {
        super(x, y, width, height, PowerUpType.ADD_BALL);
        SpriteSheet texture = new SpriteSheet(
                ResourcesLoader.loadImage("addballpowerup.png"),
                16, 16, 6, 6);
        setPowerUpTexture(texture);
    }

    @Override
    public void applyEffect(GameManager gameManager) {
        Ball spawned = gameManager.spawnBall(Color.WHITESMOKE);
        PauseTransition timer = new PauseTransition(Duration.seconds(getDuration()));
        timer.setOnFinished(e -> {
            if (spawned != null && spawned.isActive()) {
                spawned.deactivate();
            }
        });
        timer.play();
    }

    @Override
    public int getCounter() {
        return powerUpCounter;
    }

}
