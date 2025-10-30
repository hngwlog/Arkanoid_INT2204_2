package com.raumania.gameplay.objects;

import com.raumania.utils.ResourcesLoader;
import javafx.scene.image.ImageView;

import java.util.Random;

public class InvisibleBrick extends Brick{
    public InvisibleBrick(double x, double y, double width, double height) {
        super(x, y, width, height);
        setHitPoints(2);
        int i = new Random().nextInt(9); // Random number between 0-8
        ImageView texture = new ImageView(ResourcesLoader.loadImage("brick" + i + ".png"));
        texture.setOpacity(0.0);
        setBrickTexture(texture);
    }

    @Override
    public void takeHit() {
        setHitPoints(getHitPoints() - 1);
        if (getHitPoints() == 1) {
            getBrickTexture().setOpacity(1.0);
        }
    }
}
