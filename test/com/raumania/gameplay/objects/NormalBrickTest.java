package com.raumania.gameplay.objects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NormalBrickTest {
    @Test
    void testTakeHit() {
        NormalBrick nb = new NormalBrick(0, 0, 1, 1);
        nb.takeHit();
        assertTrue(nb.isDestroyed());
    }
}
