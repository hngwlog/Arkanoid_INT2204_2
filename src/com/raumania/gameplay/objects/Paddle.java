package com.raumania.gameplay.objects;

import com.raumania.renderer.Renderer;

/**
 * Represents the paddle controlled by the player.
 * The paddle can move horizontally and interacts with the ball.
 */
public class Paddle extends MovableObject {
    /**
     * Constructs a paddle with the specified position and size.
     *
     * @param x the x-coordinate of the paddle's top-left corner
     * @param y the y-coordinate of the paddle's top-left corner
     * @param width the width of the paddle
     * @param height the height of the paddle
     */
    public Paddle(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    /**
     * Updates paddle movement and applies clamping to keep it inside the game area.
     *
     * @param dt delta time in seconds
     */
    @Override
    public void update(double dt) {
        // TODO: gawgua
        applyMovement(dt);
    }

    /**
     * Renders the paddle on the screen. (Not yet implemented)
     *
     * @param renderer the renderer responsible for drawing
     */
    @Override
    public void render(Renderer renderer) {
        // TODO: cieldontcry
    }
}
