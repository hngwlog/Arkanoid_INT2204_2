package com.raumania.gameplay.manager;

import com.raumania.core.AudioManager;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import java.util.List;

import com.raumania.gameplay.objects.*;
import static com.raumania.utils.Constants.*;
import com.raumania.math.Vec2f;

/**
 * Manages the overall game state, including all major game objects such as
 * the {@link Paddle}, {@link Ball}s, and {@link Brick}s.
 * <p>
 * This class is responsible for initializing objects, updating their logic
 * every frame, and adding their visual representations to the JavaFX scene graph.
 * </p>
 */
public class GameManager {
    public enum GameState { RUNNING, PAUSED, GAME_OVER }

    private Pane root;
    private Paddle paddle;
    private boolean leftHeld = false;
    private boolean rightHeld = false;
//    private List<Ball> balls;
    private Ball ball;
    private List<Brick> bricks;
    private ObjectProperty<GameState> gameState = new SimpleObjectProperty<>(GameState.RUNNING);

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
     * screen center, attaches its {@link javafx.scene.shape.Circle} view to
     * the scene graph, and setting the state to {@link GameState#RUNNING}.
     * </p>
     *
     * <p><b>Note:</b> Brick initialization will be implemented later.</p>
     */
    public void initGame() {
        root.getChildren().clear();
        gameState.set(GameState.RUNNING);
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
     * Detects and resolves collisions between game objects such as the ball and the paddle.
     * <p>
     * When the ball collides with the paddle from above (i.e., the ball is moving downward),
     * it is pushed back upward and its new reflection angle is computed based on the
     * horizontal contact point on the paddle:
     * </p>
     * <ul>
     *   <li>The ball’s position is corrected to rest just above the paddle to prevent overlap.</li>
     *   <li>The contact ratio {@code t} in range [-1, 1] is calculated, where:
     *     <ul>
     *       <li>{@code t = -1} → left edge of paddle</li>
     *       <li>{@code t = 0}  → center of paddle</li>
     *       <li>{@code t = +1} → right edge of paddle</li>
     *     </ul>
     *   </li>
     *   <li>The reflection angle is limited to a maximum of 60° from the vertical axis
     *       to prevent near-horizontal trajectories.</li>
     *   <li>A new normalized direction vector {@link Vec2f} is computed from that angle
     *       using sine and cosine, then applied to the ball.</li>
     * </ul>
     */
    public void checkCollisions() {
        if (ball.checkOverlap(paddle) && ball.getDirection().y > 0) {
            AudioManager.getInstance().playSFX(AudioManager.PADDLE_HIT);

            ball.setPosition(ball.getX(), paddle.getY() - ball.getHeight());
            double paddleCenter = paddle.getX() + paddle.getWidth() * 0.5;
            double ballCenter = ball.getX() + ball.getRadius();
            double t = (ballCenter - paddleCenter) / (paddle.getWidth() * 0.5);
            t = Math.max(-1, Math.min(1, t));
            double maxAngle = Math.toRadians(60);
            double angle = t * maxAngle;
            double dx = Math.sin(angle);
            double dy = - Math.cos(angle);
            ball.setDirection(new Vec2f(dx, dy));
        }
    }

    /**
     * Returns the current {@link GameState} of the game.
     * <p>
     * This value indicates whether the game is currently running,
     * paused, or has ended (game over).
     * </p>
     *
     * @return the current {@link GameState} of the game
     */
    public GameState getGameState() {
        return gameState.get();
    }

    /**
     * Returns the observable property representing the current {@link GameState}.
     * <p>
     * This property can be observed to react to changes in the game state,
     * such as transitioning to a game over screen when the state changes.
     * </p>
     *
     * @return the {@link ObjectProperty} representing the current {@link GameState}
     */
    public ObjectProperty<GameState> gameStateProperty() {
        return gameState;
    }

    /**
     * Sets the game state to {@link GameState#GAME_OVER}, typically called
     * when the ball becomes inactive (falls below the screen).
     */
    public void gameOver() {
        gameState.set(GameState.GAME_OVER);
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
        if (gameState.get() != GameState.RUNNING) {
            return;
        }
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
        checkCollisions();
        if (!ball.isActive()) {
            gameOver();
        }
    }
}
