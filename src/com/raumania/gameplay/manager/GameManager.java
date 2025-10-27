package com.raumania.gameplay.manager;

//import com.raumania.gui.manager.SceneManager;
import com.raumania.core.MapLoader;
import com.raumania.core.AudioManager;
import com.raumania.gameplay.objects.powerup.AddBallPowerUp;
import com.raumania.gameplay.objects.powerup.ExtendPaddlePowerUp;
import com.raumania.gameplay.objects.powerup.ImortalPowerUp;
import com.raumania.gameplay.objects.powerup.PowerUp;
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
import com.raumania.core.MapLoader.LevelData;

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
    private List<Ball> balls = new ArrayList<>();
    private List<Brick> bricks = new ArrayList<>();
    private List<PowerUp> powerUps = new ArrayList<>();
    private ObjectProperty<GameState> gameState = new SimpleObjectProperty<>(GameState.RUNNING);
    private int score = 0;
    private LevelData currentLvl;

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
            new BackgroundSize(1.0, 1.0, true, true, false, false)
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
        if (currentLvl == null) {
            System.out.println("Level is null now.");
            return;
        }

        score = 0;
        bricks.clear();
        balls.clear();
        powerUps.clear();
        root.getChildren().clear();
        gameState.set(GameState.RUNNING);

        paddle = new Paddle((GAME_WIDTH - PADDLE_WIDTH) * 0.5, GAME_HEIGHT - 80,
                PADDLE_WIDTH, PADDLE_HEIGHT);
        spawnAdditionalBall();
        root.getChildren().add(paddle.getTexture());

        // Create bricks based on layout
        for (int r = 0; r < currentLvl.getLayout().size(); r++) {
            String row = currentLvl.getLayout().get(r);
            for (int c = 0; c < row.length(); c++) {
                char type = row.charAt(c);
                double x = c * BRICK_WIDTH;
                double y = r * BRICK_HEIGHT;

                Brick brick;
                switch (currentLvl.getLegend().get(String.valueOf(type))) {
                    case "normal":
                        brick = new NormalBrick(x, y, BRICK_WIDTH, BRICK_HEIGHT);
                        break;
                    case "strong":
                        brick = new StrongBrick(x, y, BRICK_WIDTH, BRICK_HEIGHT);
                        break;
                    case "empty":
                    default:
                        continue;
                }

                bricks.add(brick);
                root.getChildren().add(brick.getTexture());
            }
        }

        // Add power-ups

        if (currentLvl.getPowerups() != null) {
            for (MapLoader.PowerUpData powerup : currentLvl.getPowerups() ) {
                if (powerup.getType().equals("add_ball")) {
                    double x = powerup.getCol() * BRICK_WIDTH + BRICK_WIDTH/2;
                    double y = powerup.getRow() * BRICK_HEIGHT + BRICK_HEIGHT/2;
                    spawnRandomPowerUp(x, y);
                }
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
     * <p>
     * For paddle-power-up collisions, the power-up is activated and removed from the scene.
     * </p>
     */
    public void checkCollisions() {
        for (Iterator<Ball> ballIterator = balls.iterator(); ballIterator.hasNext();) {
            Ball ball = ballIterator.next();
            if (!ball.isActive()) {
                root.getChildren().remove(ball.getView());
                ballIterator.remove();
                continue;
            }
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
                        spawnRandomPowerUp(brick.getX() + (double) BRICK_WIDTH / 2,
                                brick.getY() + (double) BRICK_HEIGHT / 2);
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

        for (Iterator<PowerUp> it = powerUps.iterator(); it.hasNext();) {
            PowerUp powerUp = it.next();
            if (!powerUp.isActive()) {
                it.remove();
            }
            if (powerUp.checkOverlap(paddle)) {
                powerUp.applyEffect(this);
                root.getChildren().remove(powerUp.getTexture());
                powerUp.deactivate();
                it.remove();
            }
        }
    }

    public void setCurrentLvl(LevelData lvl) {
        currentLvl = lvl;
        initGame();
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

    public boolean isWinner() {
        return gameState.get() == GameState.GAME_OVER
                && bricks.stream().allMatch(brick -> brick instanceof StrongBrick)
                && !balls.isEmpty();
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
        for (Ball ball : balls) {
            ball.update(dt);
        }
        for (PowerUp powerUp : powerUps) {
            powerUp.update(dt);
        }
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
        if (balls.isEmpty()
            || bricks.stream().allMatch((b) -> b instanceof StrongBrick)) { // all bricks destroyed
            gameOver();
        }
    }

    /**
     * Spawns a new ball at the center of the paddle.
     * <p>
     * The new ball is added to the list of active balls and its visual
     * representation is added to the scene graph.
     * </p>
     */
    public void spawnAdditionalBall() {
        // Create ball at paddle center
        double ballX = paddle.getX() + (paddle.getWidth() - BALL_RADIUS * 2) / 2.0;
        double ballY = paddle.getY() - BALL_RADIUS * 2 - 1;
        Ball newBall = new Ball(ballX, ballY);
        balls.add(newBall);
        root.getChildren().add(newBall.getView());
    }


    public void applyImortalBalls(/*boolean state*/) {
        for (Iterator<Ball> ballIterator = balls.iterator(); ballIterator.hasNext();) {
            Ball ball = ballIterator.next();
            ball.setIsImortal(IMORTAL);
        }
    }

    public void extendPaddle() {
        double currentPaddleWidth = paddle.getWidth()*1.2;
        paddle.setWidth(currentPaddleWidth);
        paddle.getTexture().setFitWidth(currentPaddleWidth);
    }
    /**
     * Spawns a random power-up at the specified (x, y) position.
     * <p>
     * Currently, there is a 50% chance to spawn an {@link AddBallPowerUp}.
     * The spawned power-up is added to the list of active power-ups and its
     * visual representation is added to the scene graph.
     * </p>
     *
     * @param x the x-coordinate to spawn the power-up
     * @param y the y-coordinate to spawn the power-up
     */
    public void spawnRandomPowerUp(double x, double y) {
        double rand = Math.random();
        if (rand > 0.5) {
            //PowerUp powerUp = new AddBallPowerUp(x, y, 30, 30);
            //PowerUp powerUp = new ImortalPowerUp(x, y, 30, 30);
            //
            PowerUp powerUp = new ExtendPaddlePowerUp(x, y, 30, 30);
            powerUps.add(powerUp);
            root.getChildren().add(powerUp.getTexture());
        }
    }
}
