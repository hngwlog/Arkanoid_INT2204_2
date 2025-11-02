package com.raumania.gameplay.manager;

//import com.raumania.gui.manager.SceneManager;
import com.raumania.core.AudioManager;
import com.raumania.gameplay.objects.brick.Brick;
import com.raumania.gameplay.objects.brick.InvisibleBrick;
import com.raumania.gameplay.objects.brick.NormalBrick;
import com.raumania.gameplay.objects.brick.StrongBrick;
import com.raumania.gameplay.objects.powerup.*;
import com.raumania.gui.screen.GameScreen;
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
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.raumania.gameplay.objects.*;
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
    private Ball mainBall = null;
    private List<Ball> balls = new ArrayList<>();
    private List<Brick> bricks = new ArrayList<>();
    private List<PowerUp> powerUps = new ArrayList<>();
    private ObjectProperty<GameState> gameState = new SimpleObjectProperty<>(GameState.RUNNING);
    private int score = 0;
    private LevelData currentLvl;
    private List<EffectCountDown> effectCountDownList = new ArrayList<>();
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
        effectCountDownList.clear();
        root.getChildren().clear();
        score = 0;
        gameState.set(GameState.RUNNING);

        paddle = new Paddle((GameScreen.GAME_WIDTH - Paddle.PADDLE_WIDTH) * 0.5, GameScreen.GAME_HEIGHT - 80,
                Paddle.PADDLE_WIDTH, Paddle.PADDLE_HEIGHT);
        spawnBall(Color.BLACK);
        mainBall = balls.get(0);
        root.getChildren().add(paddle.getTexture());

        // Create bricks based on layout
        for (int r = 0; r < currentLvl.getLayout().size(); r++) {
            String row = currentLvl.getLayout().get(r);
            for (int c = 0; c < row.length(); c++) {
                char type = row.charAt(c);
                double x = c * Brick.BRICK_WIDTH;
                double y = r * Brick.BRICK_HEIGHT;

                Brick brick;
                switch (currentLvl.getLegend().get(String.valueOf(type))) {
                    case "normal":
                        brick = new NormalBrick(x, y, Brick.BRICK_WIDTH, Brick.BRICK_HEIGHT);
                        break;
                    case "strong":
                        brick = new StrongBrick(x, y, Brick.BRICK_WIDTH, Brick.BRICK_HEIGHT);
                        break;
                    case "invisible":
                        brick = new InvisibleBrick(x, y, Brick.BRICK_WIDTH, Brick.BRICK_HEIGHT);
                        break;
                    case "empty":
                    default:
                        continue;
                }

                bricks.add(brick);
                root.getChildren().add(brick.getTexture());
            }
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
                if (ball == mainBall) {
                    mainBall = null;
                }
                ballIterator.remove();
                continue;
            }
            if (ball.checkOverlap(paddle) /*&& ball.getDirection().y > 0*/) {
                AudioManager.getInstance().playSFX(AudioManager.PADDLE_HIT);
                double sign = (ball.getDirection().y > 0) ? 1 : -1;

                ball.setPosition(ball.getX(), paddle.getY() - sign*ball.getHeight());
                double paddleCenter = paddle.getX() + paddle.getWidth() * 0.5;
                double ballCenter = ball.getX() + ball.getRadius();
                double t = (ballCenter - paddleCenter) / (paddle.getWidth() * 0.5);
                t = Math.max(-1, Math.min(1, t));
                double maxAngle = Math.toRadians(60);
                double angle = t * maxAngle;
                double dx = Math.sin(angle);
                double dy = - sign*Math.cos(angle);
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
                        spawnRandomPowerUp(brick.getX() + (double) Brick.BRICK_WIDTH / 2,
                                brick.getY() + (double) Brick.BRICK_HEIGHT / 2, 0.4);
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
                continue;
            }

            if (powerUp.checkOverlap(paddle)) {
                powerUp.applyEffect(this);

                double curTime = System.currentTimeMillis() / 1000.0;
                PowerUpType type = powerUp.getType();

                if (type != PowerUpType.ADD_BALL
                        && effectCountDownList.stream().anyMatch(x -> x.getEffectType() == type)) {
                    effectCountDownList.forEach(e -> {
                        if (e.getEffectType() == type) {
                            e.setStartTime(curTime);
                        }
                    });
                } else {
                    effectCountDownList.add(new EffectCountDown(curTime, powerUp.getDuration(), type));
                }

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

    public void setScore(int score) {
        this.score = score;
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
     * Returns current alive balls on the GameScreen.
     */
    public List<Ball> getBallsList() {
        return balls;
    }

    /**
     * Returns the player paddle.
     */
    public Paddle getPaddle() {
        return this.paddle;
    }

    public List<EffectCountDown> getEffectCountDownList() {
        return effectCountDownList;
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
        paddle.update(dt);
        checkCollisions();

        if (mainBall == null
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
    public Ball spawnBall(Color color) {
        // Create ball at paddle center
        double ballX = paddle.getX() + (paddle.getWidth() - Ball.BALL_RADIUS * 2) / 2.0;
        double ballY = paddle.getY() - Ball.BALL_RADIUS * 2 - 1;
        Ball newBall = new Ball(ballX, ballY, color);
        balls.add(newBall);
        root.getChildren().add(newBall.getView());
        return newBall;
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
    public void spawnRandomPowerUp(double x, double y, double randomThreshold) {
        double rand = Math.random();
        if (rand < randomThreshold) {
            PowerUp powerUp;

            if (rand < randomThreshold/3) powerUp = new AddBallPowerUp(x, y, 30, 30);
            else if (rand < randomThreshold*2/3) powerUp = new ExtendPaddlePowerUp(x, y, 30, 30);
            else powerUp = new ImmortalPowerUp(x, y, 30, 30);

            powerUps.add(powerUp);
            root.getChildren().add(powerUp.getTexture());
        }
    }
}
