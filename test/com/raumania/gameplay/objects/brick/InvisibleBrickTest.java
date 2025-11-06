package com.raumania.gameplay.objects.brick;

import com.raumania.utils.InitializeJavaFx;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InvisibleBrickTest extends InitializeJavaFx {
    @Test
    void testFirstCollision() {
        InvisibleBrick ib = new InvisibleBrick(0, 0, 1, 1, 1);
        ib.takeHit();
        assertEquals(ib.getTexture().getOpacity(), 1.0, 0.0001);
    }

    @Test
    void testInvisible() {
        InvisibleBrick ib = new InvisibleBrick(0, 0, 1, 1, 1);
        assertEquals(ib.getTexture().getOpacity(), 0.0, 0.0001);
    }
}
