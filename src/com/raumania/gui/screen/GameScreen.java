package com.raumania.gui.screen;

import javafx.animation.AnimationTimer;

import com.raumania.gameplay.manager.GameManager;
import com.raumania.gui.manager.SceneManager;

/**
 * The game play screen that hosts the main game loop.
 * <p>
 * This screen owns a {@link GameManager} and a frame-based loop implemented via
 * {@link AnimationTimer}. When the screen starts, it initializes game objects and
 * begins ticking the game manager every frame with a computed delta time.
 * </p>
 */
public class GameScreen extends Screen {
    private GameManager manager;
    private AnimationTimer loop;
    private long past = - 1;

    /**
     * Creates a new {@code GameScreen} and binds it to the given {@link SceneManager}.
     * <p>
     * The constructor also instantiates a {@link GameManager} using this screen's root pane for rendering.
     * </p>
     *
     * @param sceneManager controller responsible for switching between application screens
     */
    public GameScreen(SceneManager sceneManager) {
        super(sceneManager);
        this.manager = new GameManager(root);
    }

    /**
     * Starts the game screen lifecycle.
     * <p>
     * Re-initializes the game state via {@link GameManager#initGame()} and creates an
     * {@link AnimationTimer} that:
     * <ol>
     *   <li>Captures the current monotonic time in nanoseconds.</li>
     *   <li>Computes the frame delta time in seconds as
     *   {@code dt = (now - past) / 1_000_000_000.0}.</li>
     *   <li>Calls {@link GameManager#update(double)} with that {@code dt}.</li>
     * </ol>
     * On the very first tick, the loop only initializes {@link #past} and skips update
     * to avoid a large or undefined delta time.
     * </p>
     */
    @Override
    public void onStart() {
        manager.initGame();
        scene.setOnKeyPressed(e -> manager.handleInput(e.getCode(), true));
        scene.setOnKeyReleased(e -> manager.handleInput(e.getCode(), false));
        loop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (past < 0) {
                    past = now;
                    return;
                }
                double dt = (now - past) / 1_000_000_000.0;
                past = now;
                manager.update(dt);
            }
        };
        loop.start();
    }

    /**
     * Stops the game loop when this screen is no longer active.
     * <p>
     * This halts per-frame updates by calling {@link AnimationTimer#stop()} on the loop.
     * </p>
     */
    @Override
    public void onStop() {
        if (loop != null) {
            loop.stop();
        }
    }
}
