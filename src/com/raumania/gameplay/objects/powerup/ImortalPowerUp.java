package com.raumania.gameplay.objects.powerup;

import com.raumania.core.SpriteSheet;
import com.raumania.gameplay.manager.GameManager;
import com.raumania.utils.ResourcesLoader;

public class ImortalPowerUp extends PowerUp {
    public ImortalPowerUp(double x, double y, double width, double height) {
        super(x, y, width, height);
        SpriteSheet texture = new SpriteSheet(
                ResourcesLoader.loadImage("imortalpowerup.png"),
                16, 16, 6, 6);
        setPowerUpTexture(texture);
    }

    @Override
    public void applyEffect(GameManager gameManager) {
        //gameManager.applyImortalBalls(boolean currentState);
        gameManager.applyImortalBalls();
    }
}
