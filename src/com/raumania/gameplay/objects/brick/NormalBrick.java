package com.raumania.gameplay.objects.brick;

import com.raumania.utils.ResourcesLoader;
import javafx.scene.image.ImageView;

public class NormalBrick extends Brick {
    public NormalBrick(double x, double y, double width, double height, int color) {
        super(x, y, width, height);
        setHitPoints(1);
        setBrickTexture(new ImageView(ResourcesLoader.loadImage("brick" + color + ".png")));
        this.setColorIndex(color);
    }
}
