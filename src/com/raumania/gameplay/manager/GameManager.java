package com.raumania.gameplay.manager;

import javafx.scene.layout.Pane;
import java.util.List;

import com.raumania.gameplay.objects.*;
import static com.raumania.utils.Constants.*;

/**
 * Manages the overall game state, including all major game objects such as
 * the {@link Paddle}, {@link Ball}s, and {@link Brick}s.
 * <p>
 * This class is responsible for initializing objects, updating their logic
 * every frame, and adding their visual representations to the JavaFX scene graph.
 * </p>
 */
public class GameManager {
    private Pane root;
    private Paddle paddle;
//    private List<Ball> balls;
    private Ball ball;
    private List<Brick> bricks;

    /**
     * Creates a new {@code GameManager} and attaches it to the given root pane.
     *
     * @param root the {@link Pane} where all game objects are rendered
     */
    public GameManager(Pane root) {
        this.root = root;
        initGame();
    }

    /**
     * Initializes all game objects and sets up the starting state of the game.
     * <p>
     * This method clears the rendering root, creates a new {@link Ball} at the
     * screen center, and attaches its {@link javafx.scene.shape.Circle} view to
     * the scene graph.
     * </p>
     *
     * <p><b>Note:</b> Paddle and brick initialization will be implemented later.</p>
     */
    public void initGame() {
        root.getChildren().clear();
        ball = new Ball((WINDOW_WIDTH - BALL_RADIUS * 2) / 2.0, (WINDOW_HEIGHT - BALL_RADIUS * 2) / 2.0);
        root.getChildren().setAll(ball.getView());
    }

    /**
     * Updates the logic of all active game objects.
     * <p>
     * Currently only updates the {@link Ball} each frame using its internal
     * physics and position logic.
     * </p>
     *
     * @param dt delta time in seconds since the last frame update
     */
    public void update(double dt) {
        ball.update(dt);
    }
}
