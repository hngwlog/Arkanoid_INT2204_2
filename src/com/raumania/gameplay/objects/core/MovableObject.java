package com.raumania.gameplay.objects.core;

import com.raumania.math.Vec2f;

/**
 * Abstract base class for movable objects (e.g., Ball, Paddle).
 *
 * <p>Movement is expressed by a normalized direction vector, a speed (units/s),
 * and an acceleration (units/s²). Call {@link #applyMovement(double)} inside
 * {@link #update(double)} to advance position frame-independently.</p>
 */
public abstract class MovableObject extends GameObject {
    protected Vec2f direction;
    protected double speed;
    protected double acceleration;

    /**
     * Creates a movable object with position and size.
     */
    public MovableObject(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.direction = new Vec2f(0f, 0f);
        this.speed = 0f;
        this.acceleration = 0f;
    }

    /**
     * Updates the object logic every frame.
     *
     * @param dt delta time in seconds
     */
    public abstract void update(double dt);

    /**
     * Applies kinematics: v = v + a*dt, then x = x + dir * v * dt.
     *
     * @param dt delta time in seconds
     */
    protected void applyMovement(double dt) {
        speed += acceleration * dt;
        if (speed < 0f) speed = 0f;
        double dx = direction.x * speed * dt;
        double dy = direction.y * speed * dt;
        translate(dx, dy);
    }

    /** Sets direction, will normalize if not zero. */
    public void setDirection(Vec2f dir) {
        if (dir == null) return;
        if (dir.x == 0f && dir.y == 0f) {
            this.direction.set(0f, 0f);
        } else {
            this.direction.set(dir.x, dir.y).normalize();
        }
    }

    public Vec2f getDirection() {
        return direction;
    }

    /** Sets speed (clamped to ≥ 0). */
    public void setSpeed(double speed) {
        this.speed = Math.max(0f, speed);
    }

    public double getSpeed() {
        return speed;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getWidth() {
        return this.width;
    }

    /** Sets acceleration (can be negative). */
    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

    public double getAcceleration() {
        return acceleration;
    }
}
