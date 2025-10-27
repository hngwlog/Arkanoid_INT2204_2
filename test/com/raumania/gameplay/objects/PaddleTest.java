package com.raumania.gameplay.objects;

import com.raumania.utils.InitializeJavaFx;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PaddleTest extends InitializeJavaFx {

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
