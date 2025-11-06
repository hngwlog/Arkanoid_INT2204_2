package com.raumania.gameplay.objects.brick;

import com.raumania.utils.ResourcesLoader;
import javafx.scene.image.ImageView;

public class StrongBrick extends Brick{

   public StrongBrick(double x, double y, double width, double height) {
       super(x, y, width, height);
       setHitPoints(1);
       this.setColorIndex(9);
       setBrickTexture(new ImageView(ResourcesLoader.loadImage("strongbrick.png")));
   }

    /**
     * Strong bricks are indestructible and do not take hits.
     */
    @Override
    public void takeHit() {
        return;
    }
}
