package com.raumania.gameplay.objects.brick;

import com.raumania.utils.InitializeJavaFx;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StrongBrickTest extends InitializeJavaFx {
    @Test
    void testTakeHit() {
        StrongBrick sb = new StrongBrick(0, 0, 1, 1);
        sb.takeHit();
        assertFalse(sb.isDestroyed());
    }
}
