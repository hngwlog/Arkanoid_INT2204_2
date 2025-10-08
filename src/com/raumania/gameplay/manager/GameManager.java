package com.raumania.gameplay.manager;

import javafx.scene.input.KeyCode;
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
    private boolean leftHeld = false;
    private boolean rightHeld = false;
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
        paddle = new Paddle((WINDOW_WIDTH - PADDLE_WIDTH) * 0.5, WINDOW_HEIGHT - 80, PADDLE_WIDTH
                , PADDLE_HEIGHT);
        root.getChildren().setAll(ball.getView(), paddle.getView());
    }

    /**
     * Handles player input to control paddle movement.
     *
     * @param key the pressed or released {@link KeyCode}
     * @param pressed {@code true} if the key was pressed, {@code false} if released
     */
    public void handleInput(KeyCode key, boolean pressed) {
        if (key == null) {
            return;
        }
        switch (key) {
            case LEFT -> leftHeld = pressed;
            case RIGHT -> rightHeld = pressed;
            default -> {}
        }
    }

    /**
     * Updates the logic of all active game objects.
     * <p>
     * This includes moving the ball, handling paddle input, and constraining
     * paddle movement within screen bounds.
     * </p>
     *
     * @param dt delta time in seconds since the last frame update
     */
    public void update(double dt) {
        ball.update(dt);
        if (leftHeld != rightHeld) {
            if (leftHeld) {
                paddle.moveLeft();
            } else {
                paddle.moveRight();
            }
        } else {
            paddle.stop();
        }
        paddle.update(dt);
    }
}
