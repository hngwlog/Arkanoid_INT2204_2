package com.raumania.gameplay.objects.visualeffect;

import com.raumania.core.SpriteSheet;
import com.raumania.utils.ResourcesLoader;
import com.raumania.core.AudioManager;

public class Explosion extends VisualEffect {
    private static final int totalFrames = 11;
    public Explosion(double x, double y, double width, double height) {
        super(x, y, width, height);
        SpriteSheet texture = new SpriteSheet(
                ResourcesLoader.loadImage("ve_explosion.png"),
                16, 16, totalFrames, totalFrames);
        setVisualEffectTexture(texture);
    }
}
