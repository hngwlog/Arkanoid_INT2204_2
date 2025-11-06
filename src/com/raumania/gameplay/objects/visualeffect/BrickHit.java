package com.raumania.gameplay.objects.visualeffect;

import com.raumania.core.SpriteSheet;
import com.raumania.utils.ResourcesLoader;

public class BrickHit extends VisualEffect {
    private static final int totalFrames = 6;

    public BrickHit(double x, double y, double width, double height, int colorIndex) {
        super(x, y, width, height);
        SpriteSheet texture =
                new SpriteSheet(
                        ResourcesLoader.loadImage("brick_hit" + colorIndex + ".png"),
                        16,
                        16,
                        totalFrames,
                        totalFrames);
        setVisualEffectTexture(texture);
    }
}
