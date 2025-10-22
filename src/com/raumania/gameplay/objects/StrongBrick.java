package com.raumania.gameplay.objects;

import com.raumania.utils.ResourcesLoader;
import javafx.scene.image.ImageView;

public class StrongBrick extends Brick{
   public StrongBrick(double x, double y, double width, double height) {
       super(x, y, width, height);
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
