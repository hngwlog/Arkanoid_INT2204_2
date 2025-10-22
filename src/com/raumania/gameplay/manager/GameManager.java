package com.raumania.gameplay.manager;

import com.raumania.core.AudioManager;
import com.raumania.utils.ResourcesLoader;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundSize;

import java.util.ArrayList;
import java.util.Iterator;
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
    private List<Brick> bricks = new ArrayList<>();
    private ObjectProperty<GameState> gameState = new SimpleObjectProperty<>(GameState.RUNNING);
    private int score = 0;

    /**
     * Creates a new {@code GameManager} and attaches it to the given root pane.
     */
    public GameManager() {
        this.root = new Pane();
        initGame();

        Background bg = new Background(new BackgroundImage(
            ResourcesLoader.loadImage("gamepane_bg.png"),
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(1.0, 1.0, true, true, false, true)
        ));
        root.setBackground(bg);
    }

    /**
     * Return the render root that game is rendered on
     *
     * @return the root of the game
     */
    public Pane getRoot() {
        return root;
    }


    /**
     * Set the current {@link GameState} of the game.
     */
    public void setGameState(GameState gameState) {
        this.gameState.set(gameState);
    }


    /**
     * Initializes all game objects and sets up the starting state of the game.
     * <p>
     * This method creates a new {@link Ball} at the
     * screen center and a {@link Paddle} near the bottom, populates a grid of bricks, and
     * sets {@link #gameState} to {@link GameState#RUNNING}.
     * </p>
     */
    public void initGame() {
        bricks.clear();
        gameState.set(GameState.RUNNING);
        ball = new Ball((GAME_WIDTH - BALL_RADIUS * 2) / 2.0, (GAME_HEIGHT - BALL_RADIUS * 2) / 2.0);
        paddle = new Paddle((GAME_WIDTH - PADDLE_WIDTH) * 0.5, GAME_HEIGHT - 80, PADDLE_WIDTH
                , PADDLE_HEIGHT);
        root.getChildren().setAll(ball.getView(), paddle.getTexture());
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 4; c++) {
                double x = c * BRICK_WIDTH;
                double y = r * BRICK_HEIGHT;
                NormalBrick brick = new NormalBrick(x, y, BRICK_WIDTH, BRICK_HEIGHT);
                bricks.add(brick);
                root.getChildren().add(brick.getTexture());
            }
        }
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
            case LEFT, A -> leftHeld = pressed;
            case RIGHT, D -> rightHeld = pressed;
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
     * <p>
     * For ball–brick collisions, overlap depth on X/Y is compared across all
     * bricks touched in the frame. If any collision is more horizontal than
     * vertical, the ball reflects horizontally; otherwise it reflects vertically.
     * Destroyed bricks are removed from the scene.
     * </p>
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
        int cntHorizontally = 0;
        int cntVertically = 0;
        for (Iterator<Brick> it = bricks.iterator(); it.hasNext();) {
            Brick brick = it.next();
            if (ball.checkOverlap(brick)) {
                AudioManager.getInstance().playSFX(AudioManager.BRICK_HIT);

                brick.takeHit();
                double ballCenterX = ball.getX() + ball.getWidth() / 2;
                double ballCenterY = ball.getY() + ball.getHeight() / 2;
                double brickCenterX = brick.getX() + brick.getWidth() / 2;
                double brickCenterY = brick.getY() + brick.getHeight() / 2;
                double dx = ballCenterX - brickCenterX;
                double dy = ballCenterY - brickCenterY;
                double overlapX = (brick.getWidth() / 2 + ball.getWidth() / 2) - Math.abs(dx);
                double overlapY = (brick.getHeight() / 2 + ball.getHeight() / 2) - Math.abs(dy);
                if (overlapX < overlapY) {
                    cntHorizontally++;
                } else {
                    cntVertically++;
                }
                if (brick.isDestroyed()) {
                    score += 1;
                    root.getChildren().remove(brick.getTexture());
                    it.remove();
                }
            }
        }
        if (cntHorizontally > 0) {
            ball.bounceHorizontally();
        } else if (cntVertically > 0) {
            ball.bounceVertically();
        }
    }

    /**
     * Returns the current player score.
     * <p>
     * The score increases by 1 for each brick destroyed.
     * </p>
     *
     * @return the current player score
     */
    public int getScore() {
        return score;
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
     * If the game is not in {@link GameState#RUNNING}, this method returns immediately.
     * Otherwise, it updates the ball and paddle (including clamped movement based on
     * current input), performs collision detection, and transitions to
     * {@link #gameOver()} if the ball is inactive.
     * </p>
     *
     * @param dt delta time in seconds since the last frame
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
        if (!ball.isActive()
            || bricks.stream().noneMatch((b) -> !(b instanceof StrongBrick))) { // all bricks destroyed
            gameOver();
        }
    }
}
