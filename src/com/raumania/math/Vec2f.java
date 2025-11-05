package com.raumania.math;

/**
 * Immutable-like 2D vector.
 *
 * <p>This utility class is mainly used for representing directions, positions, or
 * velocities in game logic. Although the fields {@link #x} and {@link #y} are public,
 * most operations return a new {@code Vec2f} for convenience.</p>
 */
public class Vec2f {
    public double x, y;

    /**
     * Creates a new vector with the given components.
     *
     * @param x the x component
     * @param y the y component
     */
    public Vec2f(double x, double y) {
        this.x = x;
        this.y = y;
    }


    /**
     * Sets this vector's components in place.
     *
     * @param x new x component
     * @param y new y component
     * @return this vector for chaining
     */
    public Vec2f set(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    /**
     * Computes the Euclidean length (magnitude) of this vector.
     *
     * @return vector length, always non-negative
     */
    public double length() {
        return (double) Math.sqrt(x * x + y * y);
    }

    /**
     * Normalizes this vector in place (scales it to length 1).
     *
     * <p>If the vector is zero-length, no change occurs.</p>
     *
     * @return this vector after normalization
     */
    public Vec2f normalize() {
        double len = (double) Math.sqrt(x * x + y * y);
        if (len != 0) {
            x /= len;
            y /= len;
        }
        return this;
    }

    /**
     * Creates a new vector that is this vector scaled by the given scalar.
     *
     * @param s scalar multiplier
     * @return new scaled vector
     */
    public Vec2f scale(double s) {
        return new Vec2f(x * s, y * s);
    }

    /**
     * Creates a new vector equal to the sum of this vector and another.
     *
     * @param other the vector to add
     * @return new vector representing (this + other)
     */
    public Vec2f add(Vec2f other) {
        return new Vec2f(x + other.x, y + other.y);
    }

    /**
     * Rotates this vector by the given angle (in degrees).
     * Positive angles rotate counter-clockwise, negative angles clockwise.
     *
     * @param degree the rotation angle in degrees
     * @return a new Vec2f representing the rotated vector
     */
    public Vec2f rotate(double degree) {
        double rad = Math.toRadians(degree); // đổi độ sang radian
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        double newX = x * cos - y * sin;
        double newY = x * sin + y * cos;

        return new Vec2f(newX, newY);
    }

}
