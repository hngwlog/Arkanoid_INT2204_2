package com.raumania.gameplay.objects.brick;

import com.raumania.utils.InitializeJavaFx;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class NormalBrickTest extends InitializeJavaFx {
    @Test
    void testTakeHit() {
        NormalBrick nb = new NormalBrick(0, 0, 1, 1, new Random().nextInt(9));
        nb.takeHit();
        assertTrue(nb.isDestroyed());
    }
}
