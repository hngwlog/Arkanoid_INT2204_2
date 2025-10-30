package com.raumania.gameplay.objects.powerup;

import com.raumania.core.SpriteSheet;
import com.raumania.gameplay.manager.GameManager;
import com.raumania.gameplay.objects.Ball;
import com.raumania.utils.ResourcesLoader;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.util.List;

public class ImmortalPowerUp extends PowerUp {
    protected static int powerUpCounter = 0;

    public ImmortalPowerUp(double x, double y, double width, double height) {
        super(x, y, width, height, PowerUpType.IMMORTAL);
        SpriteSheet texture = new SpriteSheet(
                ResourcesLoader.loadImage("immortalpowerup.png"),
                16, 16, 6, 6);
        setPowerUpTexture(texture);
    }

    @Override
    public void applyEffect(GameManager gameManager) {
        powerUpCounter++;
        List<Ball> balls = gameManager.getBallsList();
        for (Ball ball : balls) {
            ball.setImmortal(true);
        }

        PauseTransition timer = new PauseTransition(Duration.seconds(getDuration()));
        timer.setOnFinished(e -> {
            powerUpCounter--;
            if (powerUpCounter == 0) {
                for (Ball ball : balls) {
                    ball.setImmortal(false);
                }
            }
        });
        timer.play();
    }

    @Override
    public int getCounter() {
        return powerUpCounter;
    }
}
