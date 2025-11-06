package com.raumania.gameplay.objects;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BallTest {

    @Test
    void testBounceHorizontallyVertically() {
        Ball b = new Ball(100, 100);
        double dx = b.getDirection().x;
        double dy = b.getDirection().y;
        b.bounceHorizontally();
        b.bounceVertically();
        assertEquals(-dx, b.getDirection().x, 1e-9);
        assertEquals(-dy, b.getDirection().y, 1e-9);
    }

    @Test
    void testDeactivateAndIsActive() {
        Ball b = new Ball(100, 100);
        assertTrue(b.isActive());
        b.deactivate();
        assertFalse(b.isActive());
    }
}
