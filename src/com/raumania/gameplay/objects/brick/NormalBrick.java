package com.raumania.gameplay.objects.brick;

import com.raumania.utils.ResourcesLoader;
import javafx.scene.image.ImageView;

import java.util.Random;

public class NormalBrick extends Brick{
    public NormalBrick(double x, double y, double width, double height) {
        super(x, y, width, height);
        setHitPoints(1);
        colorIndex = new Random().nextInt(9); // Random number between 0-8
        setBrickTexture(new ImageView(ResourcesLoader.loadImage("brick" + colorIndex + ".png")));
    }
}
