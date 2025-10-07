package com.raumania.gameplay.objects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BrickTest {
    @Test
    void testTakeHit(){
        Brick b = new Brick(0, 0, 1, 1);
        assertFalse(b.isDestroyed());
        for (int i = 0; i < 5; i ++) {
            b.takeHit();
            assertTrue(b.isDestroyed());
        }
    }
}
