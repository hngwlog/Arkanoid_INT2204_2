package com.raumania.gameplay.objects.boss;

import com.raumania.core.SpriteSheet;
import com.raumania.gameplay.objects.powerup.PowerUpType;
import com.raumania.utils.ResourcesLoader;

public class Pyramid extends Boss{
    public Pyramid(double x, double y, double width, double height) {
        super(x, y, width, height);
        SpriteSheet texture = new SpriteSheet(
                ResourcesLoader.loadImage("boss_pyramid.png"),
                16, 16, 8, 8);
        setBossTexture(texture);
    }

}
