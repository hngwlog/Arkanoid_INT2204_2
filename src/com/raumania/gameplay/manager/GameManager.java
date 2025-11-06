package com.raumania.gameplay.manager;

import com.raumania.core.AudioManager;
import com.raumania.core.MapLoader.*;
import com.raumania.gameplay.objects.*;
import com.raumania.gameplay.objects.boss.Boss;
import com.raumania.gameplay.objects.boss.Pyramid;
import com.raumania.gameplay.objects.brick.*;
import com.raumania.gameplay.objects.powerup.*;
import com.raumania.gameplay.objects.visualeffect.BrickHit;
import com.raumania.gameplay.objects.visualeffect.Explosion;
import com.raumania.gameplay.objects.visualeffect.VisualEffect;
import com.raumania.gui.screen.GameScreen;
import com.raumania.math.Vec2f;
import com.raumania.utils.ResourcesLoader;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.*;

/**
 * Manages the overall game state, including all major game objects such as
 * the {@link Paddle}, {@link Ball}s, and {@link Brick}s.
 * <p>
 * This class is responsible for initializing objects, updating their logic
 * every frame, and adding their visual representations to the JavaFX scene graph.
 * </p>
 */
public class GameManager {
    private final Pane root;
    private Paddle paddle;
    private Ball mainBall = null;
    private final List<Ball> balls = new ArrayList<>();
    private final List<Brick> bricks = new ArrayList<>();
    private final List<PowerUp> powerUps = new ArrayList<>();
    private final ObjectProperty<GameState> gameState = new SimpleObjectProperty<>(GameState.RUNNING);
    private int score = 0;
    private LevelData currentLvl;
    private final List<EffectCountDown> effectCountDownList = new ArrayList<>();
    private final List<Boss> bosses = new ArrayList<>();
    private final List<VisualEffect> visualEffects = new ArrayList<>();
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
     * Returns the root {@link Pane} where the game objects are rendered.
     *
     * @return the root pane of the game
     */
    public Pane getRoot() {
        return root;
    }

    /**
     * Initializes all game objects and sets up the initial state of the level.
     * <p>
     * This method creates a new {@link Paddle}, spawns an initial {@link Ball},
     * and loads {@link Brick}s from the current level data.
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
        visualEffects.clear();
        root.getChildren().clear();
        score = 0;
        layout = new boolean[28][13];
        gameState.set(GameState.READY);

        paddle = new Paddle((GameScreen.GAME_WIDTH - Paddle.PADDLE_WIDTH) * 0.5, GameScreen.GAME_HEIGHT - 80,
                Paddle.PADDLE_WIDTH, Paddle.PADDLE_HEIGHT);
        root.getChildren().add(paddle.getTexture());

        spawnAdditionalBall(paddle.getX() + Paddle.PADDLE_WIDTH*0.5, paddle.getY(), new Vec2f(0,0));
        Ball firstBall = balls.get(0);
        firstBall.setSpeed(0);
        firstBall.setDirection(new Vec2f(0, 0));

        List<String> colorRows = currentLvl.colors();
        boolean hasColors = (colorRows != null && colorRows.size() == currentLvl.layout().size());

        if (currentLvl.bosses() != null) {
            for (BossData bossData : currentLvl.bosses()) {
                Boss boss = null;
                switch (bossData.type()) {
                    case "pyramid":
                        boss = new Pyramid(bossData.x(), bossData.y(), Boss.BOSS_SIZE, Boss.BOSS_SIZE);
                        break;
                }
                if (boss != null) {
                    bosses.add(boss);
                    root.getChildren().add(boss.getTexture());
                    //root.getChildren().add(boss.getBossPathLine());
                }
            }
        }

        for (int r = 0; r < currentLvl.layout().size(); r++) {
            String row = currentLvl.layout().get(r);
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

                String brickType = currentLvl.legend().get(String.valueOf(type));
                Brick brick = BrickFactory.createBrick(brickType, x, y, color);

                if (brick != null) {
                    bricks.add(brick);
                    root.getChildren().add(brick.getTexture());
                    layout[r][c] = false;
                }
            }
        }
    }

    /**
     * Triggers a chain explosion effect that destroys all adjacent bricks
     * (up, down, left, right) around the specified {@code start} brick,
     * including subsequent {@link ExplosiveBrick}s connected in these directions.
     * <p>
     * This method is invoked when an {@link ExplosiveBrick} is destroyed.
     * It performs a breadth-first search (BFS) starting from {@code start},
     * propagating explosions through all neighboring explosive bricks.
     * <br>
     * During propagation, each affected non-strong brick in the four cardinal
     * directions is destroyed, the {@code layout} occupancy grid and game
     * {@code score} are updated, and an {@link Explosion} visual effect and
     * explosion sound are played.
     * </p>
     * @param start the {@link Brick} whose destruction triggers the detonation;
     *              must already exist in the current {@code bricks} list.
     */
    private void detonateNeighbors(Brick start) {
        ArrayDeque<Brick> q = new ArrayDeque<>();
        q.add(start);
        boolean[][] visited = new boolean[28][13];
        int startR = (int)(start.getY() / Brick.BRICK_HEIGHT);
        int startC = (int)(start.getX() / Brick.BRICK_WIDTH);
        visited[startR][startC] = true;
        ArrayList<Brick> removeBricks = new ArrayList<>();
        while (!q.isEmpty()) {
            Brick brick = q.poll();
            int r = (int)(brick.getY() / Brick.BRICK_HEIGHT);
            int c = (int)(brick.getX() / Brick.BRICK_WIDTH);
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    if (dr * dc != 0) {
                        continue;
                    }
                    int newR = r + dr;
                    int newC = c + dc;
                    if (newR < 0 || newR >= 28 || newC < 0 || newC >= 13) {
                        continue;
                    }
                    if (visited[newR][newC]) {
                        continue;
                    }
                    Brick victim = null;
                    for (Brick otherBrick: bricks) {
                        if ((int)(otherBrick.getY() / Brick.BRICK_HEIGHT) != newR) {
                            continue;
                        }
                        if ((int)(otherBrick.getX() / Brick.BRICK_WIDTH) != newC) {
                            continue;
                        }
                        victim = otherBrick;
                        break;
                    }
                    visited[newR][newC] = true;
                    if (victim == null) {
                        continue;
                    }
                    if (victim instanceof StrongBrick) {
                        continue;
                    }
                    VisualEffect ve = new Explosion(victim.getX(), victim.getY(), Brick.BRICK_WIDTH,
                            Brick.BRICK_HEIGHT);
                    visualEffects.add(ve);
                    ve.play();
                    AudioManager.getInstance().playSFX(AudioManager.EXPLOSION);
                    root.getChildren().add(ve.getTexture());
                    if (!layout[newR][newC]) {
                        removeBricks.add(victim);
                    }
                    if (victim instanceof ExplosiveBrick) {
                        q.add(victim);
                    }
                }
            }
        }
        for (Brick brick: removeBricks) {
            int r = (int)(brick.getY() / Brick.BRICK_HEIGHT);
            int c = (int)(brick.getX() / Brick.BRICK_WIDTH);
            root.getChildren().remove(brick.getTexture());
            bricks.remove(brick);
            layout[r][c] = true;
            score += 1;
        }
    }

    /**
     * Detects and resolves collisions between all active game objects.
     * <p>
     * Handles collisions between:
     * <ul>
     *   <li>{@link Ball} and {@link Paddle}</li>
     *   <li>{@link Ball} and {@link Brick}</li>
     *   <li>{@link Ball} and {@link Boss}</li>
     *   <li>{@link Paddle} and {@link PowerUp}</li>
     * </ul>
     * Updates score, spawns power-ups, triggers explosions, and removes destroyed entities.
     * </p>
     *
     * @param dt delta time in seconds since the last frame
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
                    VisualEffect hit = new BrickHit(ball.getView().getCenterX(), ball.getView().getCenterY(),
                            35, 35, brick.getColorIndex());
                    visualEffects.add(hit);
                    root.getChildren().add(hit.getTexture());
                    AudioManager.getInstance().playSFX(AudioManager.BRICK_HIT);
                    hit.play();
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
                                break;
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
                ball.update(-dt);
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
                    VisualEffect ve = new Explosion(boss.getX(), boss.getY(), Boss.BOSS_SIZE*1.2, Boss.BOSS_SIZE*1.2);
                    visualEffects.add(ve);
                    ve.play();
                    AudioManager.getInstance().playSFX(AudioManager.EXPLOSION);
                    root.getChildren().add(ve.getTexture());
                    root.getChildren().remove(boss.getTexture());
                    it.remove();
                }
            }
        }
//        allCollidedBricks = allCollidedBricks.stream().distinct().toList();
        for (Brick brick: allCollidedBricks) {
            brick.takeHit();
            if (brick.isDestroyed()) {
                if (brick instanceof ExplosiveBrick) {
                    VisualEffect ve = new Explosion(brick.getX(), brick.getY(), Brick.BRICK_WIDTH,
                            Brick.BRICK_HEIGHT);
                    visualEffects.add(ve);
                    ve.play();
                    AudioManager.getInstance().playSFX(AudioManager.EXPLOSION);
                    root.getChildren().add(ve.getTexture());
                    detonateNeighbors(brick);
                }
            }
        }
        for (Iterator<Brick> it = allCollidedBricks.iterator(); it.hasNext();) {
            Brick brick = it.next();
            if (brick.isDestroyed()) {
                if (!layout[(int)(brick.getY()/Brick.BRICK_HEIGHT)][(int)(brick.getX()/Brick.BRICK_WIDTH)]) {
                    root.getChildren().remove(brick.getTexture());
                    bricks.remove(brick);
                    layout[(int)(brick.getY()/Brick.BRICK_HEIGHT)][(int)(brick.getX()/Brick.BRICK_WIDTH)] = true;
                    score += 1;
                    // 40% to spawn powerup
                    PowerUp powerUp = PowerUpFactory.createRandomPowerUp(brick.getX(), brick.getY(), 30, 30, 0.4);
                    if (powerUp != null) {
                        root.getChildren().add(powerUp.getTexture());
                        powerUps.add(powerUp);
                    }
                };
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

    /**
     * Returns the current level data.
     *
     * @return the currently loaded {@link LevelData}
     */
    public LevelData getCurrentLvl() {
        return currentLvl;
    }

    /**
     * Sets the current level and reinitializes the game state.
     *
     * @param lvl the level data to load
     */
    public void setCurrentLvl(LevelData lvl) {
        currentLvl = lvl;
        initGame();
    }

    /**
     * Returns the current player score.
     *
     * @return the score value
     */
    public int getScore() {
        return score;
    }

    /** Sets the current player score. */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Returns the current {@link GameState} of the game.
     *
     * @return the current game state
     */
    public GameState getGameState() {
        return gameState.get();
    }

    /** Sets the current {@link GameState}. */
    public void setGameState(GameState gameState) {
        this.gameState.set(gameState);
    }

    /**
     * Returns all currently active {@link Ball}s.
     *
     * @return list of active balls
     */
    public List<Ball> getBallsList() {
        return balls;
    }

    /**
     * Returns the player’s {@link Paddle}.
     *
     * @return the paddle object
     */
    public Paddle getPaddle() {
        return this.paddle;
    }

    /**
     * Returns the list of currently active effect countdowns.
     *
     * @return the list of {@link EffectCountDown} objects
     */
    public List<EffectCountDown> getEffectCountDownList() {
        return effectCountDownList;
    }

    /**
     * Returns the observable property representing the current {@link GameState}.
     * <p>
     * This property can be observed by the UI to react to state changes,
     * such as transitioning to a game-over screen.
     * </p>
     *
     * @return the {@link ObjectProperty} for the game state
     */
    public ObjectProperty<GameState> gameStateProperty() {
        return gameState;
    }

    /**
     * Sets the game state to {@link GameState#GAME_OVER},
     * typically called when all balls are lost or all bricks are cleared.
     */
    public void gameOver() {
        gameState.set(GameState.GAME_OVER);
    }

    /**
     * Checks if the player has won the game.
     *
     * @return {@code true} if all destructible bricks are cleared
     */
    public boolean isWinner() {
        return gameState.get() == GameState.GAME_OVER
                && bricks.stream().allMatch(brick -> brick instanceof StrongBrick)
                && !balls.isEmpty();
    }

    /**
     * Updates the logic of all active game objects based on the current {@link GameState}.
     * <p>
     * - In {@code READY}: aligns the ball above the paddle.<br>
     * - In {@code RUNNING}: updates all entities and checks collisions.<br>
     * - Ends the game if all balls are lost or all destructible bricks are gone.
     * </p>
     *
     * @param dt delta time in seconds since the last frame
     */
    public void update(double dt) {
        if (gameState.get() == GameState.READY) {
            paddle.update(dt);
            double ballX = paddle.getX() + (paddle.getWidth() - Ball.BALL_RADIUS * 2) / 2.0;
            double ballY = paddle.getY() - Ball.BALL_RADIUS * 2 - 1;
            balls.get(0).setPosition(ballX, ballY);
            balls.get(0).updateView();
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

        for (Iterator<VisualEffect> it = visualEffects.iterator(); it.hasNext();) {
            VisualEffect ve = it.next();
            if (ve.getTextureSheet().isFinalFrame()) {
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
                 VisualEffect explosion = new Explosion(
                        boss.getX(),
                        boss.getY(),
                        Boss.BOSS_SIZE*1.2,
                        Boss.BOSS_SIZE*1.2
                );
                visualEffects.add(explosion);
                root.getChildren().add(explosion.getTexture());
                // Xóa boss khỏi danh sách
                iterator.remove();
                boss = null;
            }
        }
            if (balls.size() == 0
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
            balls.get(0).setSpeed(Ball.BALL_SPEED);
            balls.get(0).setDirection(new Vec2f(0, -1));
            gameState.set(GameState.RUNNING);
        }
    }

    /**
     * Spawns an additional {@link Ball} at the given position and direction.
     *
     * @param x   x-coordinate of spawn position
     * @param y   y-coordinate of spawn position
     * @param dir initial direction of the ball
     */
    public void spawnAdditionalBall(double x, double y, Vec2f dir) {
        Ball ball = new Ball(x, y, Color.BLACK);
        Vec2f newDir;
        newDir = dir.rotate( (Math.random() > 0.5 ? 1 : -1) * (Math.random()*55 + 45) );
        if (newDir.y == 0) newDir.y = newDir.x;
        ball.setDirection(newDir);
        balls.add(ball);
        root.getChildren().add(ball.getView());
    }

    /**
     * Enumeration of all possible game states.
     */
    public enum GameState { READY, RUNNING, PAUSED, GAME_OVER }
}
