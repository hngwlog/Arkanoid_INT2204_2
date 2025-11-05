package com.raumania.gameplay.objects.visioneffect;

import com.raumania.core.AudioManager;
import com.raumania.core.SpriteSheet;

import com.raumania.gameplay.objects.core.GameObject;
import com.raumania.gui.screen.GameScreen;
import com.raumania.math.Vec2f;
import javafx.scene.image.ImageView;

public abstract class VisionEffect extends GameObject {
    protected SpriteSheet visionEffectTexture;

    public VisionEffect(double x, double y, double width, double height) {
        super(x, y, width, height);
        AudioManager.getInstance().playSFX(AudioManager.BRICK_HIT);
    }

    public void setVisionEffectTexture(SpriteSheet visionEffectTexture) {
        this.visionEffectTexture = visionEffectTexture;
        this.visionEffectTexture.setFps(10.0);
        this.visionEffectTexture.getView().setX(getX());
        this.visionEffectTexture.getView().setY(getY());
        this.visionEffectTexture.getView().setFitWidth(getWidth());
        this.visionEffectTexture.getView().setFitHeight(getHeight());
        this.visionEffectTexture.play();
    }

    public ImageView getTexture() {
        return visionEffectTexture.getView();
    }

    public void updateView() {
        visionEffectTexture.getView().setX(getX());
        visionEffectTexture.getView().setY(getY());
    }

    public abstract boolean isDone();

}
