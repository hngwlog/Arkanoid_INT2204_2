package com.raumania.gameplay.objects.powerup;

import com.raumania.core.SpriteSheet;
import com.raumania.gameplay.manager.GameManager;
import com.raumania.gameplay.objects.Ball;
import com.raumania.utils.ResourcesLoader;

import java.awt.*;
import java.util.List;

public class AddBallPowerUp extends PowerUp {
    private static final int powerUpCounter = 0;

    public AddBallPowerUp(double x, double y, double width, double height) {
        super(x, y, width, height, PowerUpType.ADD_BALL);
        SpriteSheet texture = new SpriteSheet(
                ResourcesLoader.loadImage("addballpowerup.png"),
                16, 16, 6, 6);
        setPowerUpTexture(texture);
    }

    @Override
    public void applyEffect(GameManager gameManager) {
        List<Ball> balls = gameManager.getBallsList();
        int currentSize = balls.size();
        for (int i = 0; i < currentSize; i++ ) {
            if (currentSize + i >= 8) return;
            gameManager.spawnAdditionalBall(balls.get(i).getX(), balls.get(i).getY(), balls.get(i).getDirection());
        }
    }

    @Override
    public int getCounter() {
        return powerUpCounter;
    }

}
