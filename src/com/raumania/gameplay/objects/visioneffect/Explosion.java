package com.raumania.gameplay.objects.visioneffect;

import com.raumania.core.SpriteSheet;
import com.raumania.gui.screen.GameScreen;
import com.raumania.utils.ResourcesLoader;
import com.raumania.core.AudioManager;

public class Explosion extends VisionEffect{
    private static final int totalFrames = 11;
    public Explosion(double x, double y, double width, double height) {
        super(x, y, width, height);
        AudioManager.getInstance().playSFX(AudioManager.EXPLOSION);
        SpriteSheet texture = new SpriteSheet(
                ResourcesLoader.loadImage("ve_explosion.png"),
                16, 16, totalFrames, totalFrames);
        setVisionEffectTexture(texture);
    }

    @Override
    public boolean isDone() {
        return this.visionEffectTexture.getCurrentFrame() + 1 >= totalFrames;
    }

}
