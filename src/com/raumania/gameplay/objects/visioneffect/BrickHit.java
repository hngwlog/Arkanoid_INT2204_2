package com.raumania.gameplay.objects.visioneffect;

import com.raumania.core.SpriteSheet;
import com.raumania.utils.ResourcesLoader;

public class BrickHit extends VisionEffect{
    private static final int totalFrames = 6;
    public BrickHit(double x, double y, double width, double height, int color) {
        super(x, y, width, height);
        SpriteSheet texture = new SpriteSheet(
                ResourcesLoader.loadImage("brick_hit" + color + ".png"),
                16, 16, totalFrames, totalFrames);
        setVisionEffectTexture(texture);
    }

    @Override
    public boolean isDone() {
        return this.visionEffectTexture.getCurrentFrame() + 1 >= totalFrames;
    }
}
