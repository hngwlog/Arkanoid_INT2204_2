package com.raumania.gameplay.objects;

public class StrongBrick extends Brick{
   public StrongBrick(double x, double y, double width, double height) {
       super(x, y, width, height);
       setHitPoints(5);
   }
}
