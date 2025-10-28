package com.raumania.gameplay.objects.powerup;

import com.raumania.core.SpriteSheet;
import com.raumania.gameplay.manager.GameManager;
import com.raumania.gameplay.objects.Ball;
import com.raumania.utils.ResourcesLoader;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class ImmortalPowerUp extends PowerUp {
    private static final double DURATION = 10;

    public ImmortalPowerUp(double x, double y, double width, double height) {
        super(x, y, width, height);
        SpriteSheet texture = new SpriteSheet(
                ResourcesLoader.loadImage("immortalpowerup.png"),
                16, 16, 6, 6);
        setPowerUpTexture(texture);
    }

    @Override
    public void applyEffect(GameManager gameManager) {
        List<Ball> balls = gameManager.getBallsList();
        for (Iterator<Ball> ballIterator = balls.iterator(); ballIterator.hasNext();) {
            Ball ball = ballIterator.next();
            ball.setImmortal(true);
        }

        PauseTransition timer = new PauseTransition(Duration.seconds(DURATION));
        timer.setOnFinished(e -> {
            for (Iterator<Ball> ballIterator = balls.iterator(); ballIterator.hasNext();) {
                Ball ball = ballIterator.next();
                ball.setImmortal(false);
            }
        });
        timer.play();
    }
}
