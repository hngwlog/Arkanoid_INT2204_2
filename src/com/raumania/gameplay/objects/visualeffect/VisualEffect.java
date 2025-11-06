package com.raumania.gameplay.objects.visualeffect;

import com.raumania.core.SpriteSheet;
import com.raumania.gameplay.objects.core.GameObject;

import javafx.scene.image.ImageView;

public abstract class VisualEffect extends GameObject {
    protected SpriteSheet visualEffectTexture;

    public VisualEffect(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    public void setVisualEffectTexture(SpriteSheet visualEffectTexture) {
        this.visualEffectTexture = visualEffectTexture;
        this.visualEffectTexture.setFps(15.0);
        this.visualEffectTexture.getView().setX(getX());
        this.visualEffectTexture.getView().setY(getY());
        this.visualEffectTexture.getView().setFitWidth(getWidth());
        this.visualEffectTexture.getView().setFitHeight(getHeight());
        this.visualEffectTexture.play();
    }

    public ImageView getTexture() {
        return visualEffectTexture.getView();
    }

    public SpriteSheet getTextureSheet() {
        return visualEffectTexture;
    }

    public void play() {
        this.visualEffectTexture.play();
    }
}
