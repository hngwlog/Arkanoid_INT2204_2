package com.raumania.gameplay.objects;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PaddleTest {

    @Test
    void testMoveDirections() {
        Paddle p = new Paddle(100, 100, 100, 10);
        p.moveLeft();
        assertEquals(-1, p.getDirection().x);
        p.moveRight();
        assertEquals(1, p.getDirection().x);
        p.stop();
        assertEquals(0, p.getDirection().x);
    }

    @Test
    void testBoundaryClamp() {
        Paddle p = new Paddle(-50, 100, 100, 10);
        p.checkCollisionWithBoundary();
        assertTrue(p.getX() >= 0);
    }
}
