package com.raumania.gameplay.manager;

import com.raumania.gameplay.objects.*;
import com.raumania.gui.Renderer;

import java.util.List;

/**
 * Central class responsible for managing the game state.
 * Handles initialization, updates, and rendering of all game objects.
 */
public class GameManager {
    private Paddle paddle;
    private List<Ball> balls;
    private List<Brick> bricks;
    private Renderer renderer;

    /**
     * Creates a new GameManager with a given renderer.
     *
     * @param renderer the renderer responsible for drawing
     */
    public GameManager(Renderer renderer) {
        this.renderer = renderer;
        initGame();
    }

    /**
     * Initializes the game objects and sets up the initial state. (Not yet implemented)
     */
    public void initGame() {
        // TODO: @hngwlog
    }

    /**
     * Updates the state of all game objects. (Not yet implemented)
     *
     * @param dt delta time in seconds
     */
    public void update(double dt) {
        // TODO: @hngwlog
    }

    /**
     * Renders all game objects on the screen. (Not yet implemented)
     */
    public void render() {
        // TODO: @cieldontcry
    }
}
