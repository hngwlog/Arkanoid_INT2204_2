package com.raumania.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Vec2fTest {
    @Test
    void testConstructor() {
        Vec2f vec = new Vec2f(1, 2);
        assertEquals(1.0, vec.x, 0);
        assertEquals(2.0, vec.y, 0);
    }

    @Test
    void testLength() {
        Vec2f vec = new Vec2f(2, 2);
        assertEquals(Math.sqrt(8), vec.length(), 0);
    }

    @Test
    void testNormalize() {
        Vec2f vec = new Vec2f(1, 2).normalize();
        assertEquals(0.44, vec.x, 0.01);
        assertEquals(0.89, vec.y, 0.01);
    }

    @Test
    void testScale() {
        Vec2f vec = new Vec2f(1, 2).scale(4);
        assertEquals(4.0, vec.x, 0);
        assertEquals(8.0, vec.y, 0);
    }

    @Test
    void testAdd() {
        Vec2f vec = new Vec2f(1, 2);
        Vec2f vec2 = new Vec2f(3, 4);
        Vec2f result = vec.add(vec2);
        assertEquals(vec.x + vec2.x, result.x, 0);
        assertEquals(vec.y + vec2.y, result.y, 0);
    }
}
