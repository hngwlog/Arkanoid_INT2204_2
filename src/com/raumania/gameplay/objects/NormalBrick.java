package com.raumania.gameplay.objects;

public class NormalBrick extends Brick{
    public NormalBrick(double x, double y, double width, double height) {
        super(x, y, width, height);
        setHitPoints(1);
    }
}
