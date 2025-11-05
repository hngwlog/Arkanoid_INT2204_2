package com.raumania.gameplay.manager;

import com.raumania.core.AudioManager;
import com.raumania.gameplay.objects.boss.Boss;
import com.raumania.gameplay.objects.boss.Pyramid;
import com.raumania.gameplay.objects.brick.*;
import com.raumania.gameplay.objects.powerup.*;
import com.raumania.gameplay.objects.visioneffect.Explosion;
import com.raumania.gameplay.objects.visioneffect.VisionEffect;
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
import java.util.Random;

import com.raumania.gameplay.objects.*;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Line;

import com.raumania.math.Vec2f;
import com.raumania.core.MapLoader.*;
/**
 * Manages the overall game state, including all major game objects such as
 * the {@link Paddle}, {@link Ball}s, and {@link Brick}s.
 * <p>
 * This class is responsible for initializing objects, updating their logic
 * every frame, and adding their visual representations to the JavaFX scene graph.
 * </p>
 */
public class GameManager {
    public enum GameState { READY, RUNNING, PAUSED, GAME_OVER }

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
    private List<Boss> bosses = new ArrayList<>();
    private List<VisionEffect> visionEffects = new ArrayList<>();
    private boolean[][] layout;
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
            return;
        }

        score = 0;
        bricks.clear();
        balls.clear();
        powerUps.clear();
        effectCountDownList.clear();
        bosses.clear();
        visionEffects.clear();
        root.getChildren().clear();
        score = 0;
        layout = new boolean[28][13];
        gameState.set(GameState.READY);

        paddle = new Paddle((GameScreen.GAME_WIDTH - Paddle.PADDLE_WIDTH) * 0.5, GameScreen.GAME_HEIGHT - 80,
                Paddle.PADDLE_WIDTH, Paddle.PADDLE_HEIGHT);
        root.getChildren().add(paddle.getTexture());

        mainBall = spawnMainBall();
        mainBall.setSpeed(0);
        mainBall.setDirection(new Vec2f(0, 0));

        List<String> colorRows = currentLvl.getColors();
        boolean hasColors = (colorRows != null && colorRows.size() == currentLvl.getLayout().size());

        if (currentLvl.getBosses() != null) {
            for (BossData bossData : currentLvl.getBosses()) {
                Boss boss = null;
                switch (bossData.getType()) {
                    case "pyramid":
                        boss = new Pyramid(bossData.getX(), bossData.getY(), Boss.BOSS_SIZE, Boss.BOSS_SIZE);
                        break;
                }
                if (boss != null) {
                    bosses.add(boss);
                    root.getChildren().add(boss.getTexture());
                    //root.getChildren().add(boss.getBossPathLine());
                }
            }
        }

        for (int r = 0; r < currentLvl.getLayout().size(); r++) {
            String row = currentLvl.getLayout().get(r);
            String rowColor = hasColors ? colorRows.get(r) : null;
            for (int c = 0; c < row.length(); c++) {
                layout[r][c] = true;
                char type = row.charAt(c);
                int color = 0;
                if (rowColor != null) {
                    color = rowColor.charAt(c) - '0';
                } else {
                    color = new Random().nextInt(9);
                }
                double x = c * Brick.BRICK_WIDTH;
                double y = r * Brick.BRICK_HEIGHT;
                Brick brick = null;

                String brickType = currentLvl.getLegend().get(String.valueOf(type));
                if (brickType == null || brickType.equals("empty")) continue;

                switch (brickType) {
                    case "normal":
                        brick = new NormalBrick(x, y, Brick.BRICK_WIDTH, Brick.BRICK_HEIGHT, color);
                        break;
                    case "strong":
                        brick = new StrongBrick(x, y, Brick.BRICK_WIDTH, Brick.BRICK_HEIGHT);
                        break;
                    case "invisible":
                        brick = new InvisibleBrick(x, y, Brick.BRICK_WIDTH, Brick.BRICK_HEIGHT,
                                color);
                        break;
                    default:
                        continue;
                }

                if (brick != null) {
                    bricks.add(brick);
                    root.getChildren().add(brick.getTexture());
                    layout[r][c] = false;
                }
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
    public void checkCollisions(double dt) {
        List<Brick> allCollidedBricks = new ArrayList<>();
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
            if (ball.checkOverlap(paddle)) {
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
            List<Brick> collidedBricks = new ArrayList<>();
            for (Iterator<Brick> it = bricks.iterator(); it.hasNext();) {
                Brick brick = it.next();
                if (ball.checkOverlap(brick)) {
                    collidedBricks.add(brick);
                }
            }
            if (collidedBricks.size() == 3) {
                Brick chosenBrick = collidedBricks.get(0);
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (j == i) {
                            continue;
                        }
                        for (int k = 0; k < 3; k++) {
                            if (k == i || k == j) {
                                continue;
                            }
                            Brick brick1 = collidedBricks.get(i);
                            Brick brick2 = collidedBricks.get(j);
                            Brick brick3 = collidedBricks.get(k);
                            if (brick1.getX() == brick2.getX() && brick1.getY() == brick3.getY()) {
                                chosenBrick = brick1;
                            }
                        }
                    }
                }
                collidedBricks.remove(chosenBrick);
            }
            if (collidedBricks.size() == 1) {
                ball.update(- dt);
                Brick brick = collidedBricks.get(0);
                allCollidedBricks.add(brick);
                Vec2f direction = ball.getDirection();
                double velocityX = ball.getSpeed() * direction.x;
                double timeEntryX;
                if (velocityX > 0) {
                    timeEntryX = (brick.getX() - ball.getWidth() - ball.getX()) / velocityX;
                } else if (velocityX < 0) {
                    timeEntryX = (brick.getX() + brick.getWidth() - ball.getX()) / velocityX;
                } else {
                    timeEntryX = Double.NEGATIVE_INFINITY;
                }
                double velocityY = ball.getSpeed() * direction.y;
                double timeEntryY;
                if (velocityY > 0) {
                    timeEntryY = (brick.getY() - ball.getHeight() - ball.getY()) / velocityY;
                } else if (velocityY < 0) {
                    timeEntryY = (brick.getY() + brick.getHeight() - ball.getY()) / velocityY;
                } else {
                    timeEntryY = Double.NEGATIVE_INFINITY;
                }
                if (timeEntryX > timeEntryY) {
                    ball.bounceHorizontally();
                } else {
                    ball.bounceVertically();
                }
            } else if (collidedBricks.size() == 2) {
                ball.update(- dt);
                Brick brick1 = collidedBricks.get(0);
                Brick brick2 = collidedBricks.get(1);
                allCollidedBricks.add(brick1);
                allCollidedBricks.add(brick2);
                if (brick1.getX() == brick2.getX()) {
                    ball.bounceHorizontally();
                } else if (brick1.getY() == brick2.getY()) {
                    ball.bounceVertically();
                } else {
                    ball.bounceHorizontally();
                    ball.bounceVertically();
                }
            }

            for (Iterator<Boss> it = bosses.iterator(); it.hasNext();) {
                Boss boss = it.next();
                if (ball.checkOverlap(boss)) {
                    score += 1;
                    VisionEffect ve = new Explosion(boss.getX(), boss.getY(), Boss.BOSS_SIZE*1.2, Boss.BOSS_SIZE*1.2);
                    visionEffects.add(ve);
                    root.getChildren().add(ve.getTexture());
                    root.getChildren().remove(boss.getTexture());
                    it.remove();
                }
            }
        }
//        allCollidedBricks = allCollidedBricks.stream().distinct().toList();
        for (Iterator<Brick> it = allCollidedBricks.iterator(); it.hasNext();) {
            Brick brick = it.next();
            AudioManager.getInstance().playSFX(AudioManager.BRICK_HIT);
            brick.takeHit();
            if (brick.isDestroyed()) {
                score += 1;
                root.getChildren().remove(brick.getTexture());
                spawnRandomPowerUp(brick.getX() + ((double) Brick.BRICK_WIDTH - 16) / 2,
                        brick.getY() + (double) Brick.BRICK_HEIGHT / 2, 0.4);
                bricks.remove(brick);
                layout[(int)(brick.getY()/Brick.BRICK_HEIGHT)][(int)(brick.getX()/Brick.BRICK_WIDTH)] = true;
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

                if (type != PowerUpType.ADD_BALL) {
                    if (effectCountDownList.stream().anyMatch(x -> x.getEffectType() == type)) {
                        effectCountDownList.forEach(e -> {
                            if (e.getEffectType() == type) {
                                e.setStartTime(curTime);
                            }
                        });
                    } else {
                        effectCountDownList.add(new EffectCountDown(curTime, powerUp.getDuration(), type));
                    }
                }


                root.getChildren().remove(powerUp.getTexture());
                powerUp.deactivate();
                it.remove();
            }
        }
    }

    public LevelData getCurrentLvl() {
        return currentLvl;
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
     * Updates the logic of all active game objects according to the current {@link GameState}.
     * <p>
     * - In {@code READY}: the paddle can move and the main ball stays centered above it.<br>
     * - In {@code RUNNING}: updates balls, power-ups, and paddle, then checks collisions.<br>
     * - In other states: no update is performed.<br>
     * Ends the game if the main ball is lost or all destructible bricks are cleared.
     * </p>
     *
     * @param dt delta time in seconds since the last frame
     */
    public void update(double dt) {
        if (gameState.get() == GameState.READY) {
            paddle.update(dt);
            double ballX = paddle.getX() + (paddle.getWidth() - Ball.BALL_RADIUS * 2) / 2.0;
            double ballY = paddle.getY() - Ball.BALL_RADIUS * 2 - 1;
            mainBall.setPosition(ballX, ballY);
            mainBall.updateView();
            return;
        }
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
        checkCollisions(dt);

        for (Iterator<VisionEffect> it = visionEffects.iterator(); it.hasNext();) {
            VisionEffect ve = it.next();
            if (ve.isDone()) {
                root.getChildren().remove(ve.getTexture());
                it.remove();
            }
        }
        // Update boss path each frame to reflect paddle movement and boss position
        for (Iterator<Boss> iterator = bosses.iterator(); iterator.hasNext();) {
            Boss boss = iterator.next();
            int newScore = boss.bossUpdate(dt, paddle, layout, root, bricks, getScore());
            setScore(newScore);
            if (!boss.isActive()) {
                // Tạo hiệu ứng nổ tại vị trí boss
                 VisionEffect explosion = new Explosion(
                        boss.getX(),
                        boss.getY(),
                        Boss.BOSS_SIZE*1.2,
                        Boss.BOSS_SIZE*1.2
                );
                visionEffects.add(explosion);
                root.getChildren().add(explosion.getTexture());
                // Xóa boss khỏi danh sách
                iterator.remove();
                boss = null;
            }
        }
            if (mainBall == null
                || bricks.stream().allMatch((b) -> b instanceof StrongBrick)) { // all bricks destroyed
            gameOver();
        }
    }

    /**
     * Starts the main gameplay when the game is in {@link GameState#READY}.
     * <p>
     * This method is typically triggered when the player presses the <b>Space</b> key.
     * It initializes the motion of the main ball by setting its speed and upward
     * direction, and transitions the game state to {@link GameState#RUNNING}.
     * </p>
     */
    public void startGame() {
        if (gameState.get() == GameState.READY) {
            mainBall.setSpeed(Ball.BALL_SPEED);
            mainBall.setDirection(new Vec2f(0, -1));
            gameState.set(GameState.RUNNING);
        }
    }

    /**
     * Spawns a new ball at the center of the paddle.
     * <p>
     * The new ball is added to the list of active balls and its visual
     * representation is added to the scene graph.
     * </p>
     */
    public Ball spawnMainBall() {
        // Create ball at paddle center
        double ballX = paddle.getX() + (paddle.getWidth() - Ball.BALL_RADIUS * 2) / 2.0;
        double ballY = paddle.getY() - Ball.BALL_RADIUS * 2 - 1;
        Ball newBall = new Ball(ballX, ballY, Color.BLACK);
        balls.add(newBall);
        root.getChildren().add(newBall.getView());
        return newBall;
    }

    public void spawnAdditionalBall() {
        for (int i = 0; i < 2; i++) {
            Ball ball = new Ball(mainBall.getX(), mainBall.getY(), Color.WHITESMOKE);
            ball.setDirection(mainBall.getDirection().rotate(-30 + 60*i));
            balls.add(ball);
            root.getChildren().add(ball.getView());
        }
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
