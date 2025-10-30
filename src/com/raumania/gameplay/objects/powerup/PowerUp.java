package com.raumania.gameplay.objects.powerup;

import com.raumania.core.SpriteSheet;
import com.raumania.gameplay.manager.GameManager;
import com.raumania.gameplay.objects.core.MovableObject;
import com.raumania.gui.screen.GameScreen;
import com.raumania.math.Vec2f;
import javafx.scene.image.ImageView;

public abstract class PowerUp extends MovableObject {
    private SpriteSheet powerUpTexture;
    private boolean active = true;
    public static final double POWERUP_SPEED = 100.0;
    protected final PowerUpType type;

    public PowerUp(double x, double y, double width, double height, PowerUpType type) {
        super(x, y, width, height);
        this.speed = POWERUP_SPEED;
        this.type = type;
        this.setDirection(new Vec2f(0, 1)); // falling downwards
    }

    public void setPowerUpTexture(SpriteSheet powerUpTexture) {
        this.powerUpTexture = powerUpTexture;
        this.powerUpTexture.setFps(10.0);
        this.powerUpTexture.getView().setX(getX());
        this.powerUpTexture.getView().setY(getY());
        this.powerUpTexture.getView().setFitWidth(getWidth());
        this.powerUpTexture.getView().setFitHeight(getHeight());
        this.powerUpTexture.play();
    }

    public ImageView getTexture() {
        return powerUpTexture.getView();
    }

    public boolean isActive() {
        return active;
    }

    public void updateView() {
        powerUpTexture.getView().setX(getX());
        powerUpTexture.getView().setY(getY());
    }

    @Override
    public void update(double dt) {
        applyMovement(dt);
        updateView();
        if (getY() > GameScreen.GAME_HEIGHT) {
            deactivate();
        }
    }

    public void deactivate() {
        active = false;
        powerUpTexture.stop();
    }

    public double getDuration() {
        return type.getDuration();
    }

    public PowerUpType getType() {
        return type;
    }

    public abstract void applyEffect(GameManager gameManager);
    public abstract int getCounter();
}
