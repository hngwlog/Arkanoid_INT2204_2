package com.raumania.gameplay.objects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StrongBrickTest {
    @Test
    void testTakeHit() {
        StrongBrick sb = new StrongBrick(0, 0, 1, 1);
        sb.takeHit();
        assertFalse(sb.isDestroyed());
    }
}
