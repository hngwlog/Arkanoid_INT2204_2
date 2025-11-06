package com.raumania.gameplay.objects.boss;

import com.raumania.core.AStarInstructor;
import com.raumania.core.SpriteSheet;
import com.raumania.gameplay.objects.Paddle;
import com.raumania.gameplay.objects.brick.Brick;
import com.raumania.gameplay.objects.core.MovableObject;
import com.raumania.gui.screen.GameScreen;
import com.raumania.math.Vec2f;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
//import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Boss enemy in the game.
 * <p>
 * The Boss is a movable object that can follow a calculated path using A* pathfinding,
 * perform random movement when no path exists, and teleport if stuck for too long.
 * It interacts with {@link Paddle}, {@link Brick}, and can reduce the player's score
 * upon collision. The Boss can also "dash" toward the paddle when nearby.
 * </p>
 */
public class Boss extends MovableObject {
    /** The default size (width and height) of the boss. */
    public static final double BOSS_SIZE = 35.0;
    /** The distance threshold to consider the boss has arrived at a target point. */
    private static final double ARRIVAL_THRESHOLD = 8; // if the distance is less than arrival_threshold, consider that collision is happened
    /** Time threshold (in seconds) to determine if the boss is stuck. */
    private static final double STUCK_THRESHOLD = 2.5;
    /** The normal movement speed of the boss. */
    public static double BOSS_SPEED = 95.0;
    private SpriteSheet bossTexture;
    private boolean active = true;
    private double timeAccumulator = 0;
//    private Polyline bossPathLine;
    private List<Vec2f> pathPoints;
    private int currentTargetIndex;
    private boolean followingPath = true;
    private Vec2f lastPosition = new Vec2f(0, 0);
    private double stuckTimer = 0;
    private double randomDir = Math.random() > 0.5 ? 1 : -1;

    /**
     * Constructs a new Boss instance.
     *
     * @param x      initial X position
     * @param y      initial Y position
     * @param width  boss width
     * @param height boss height
     */
    public Boss(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.speed = BOSS_SPEED;
        this.setDirection(new Vec2f(0, 1));
//        this.bossPathLine = new Polyline();
//        this.bossPathLine.setStroke(Color.RED);
//        this.bossPathLine.setStrokeWidth(2.0);
//        this.bossPathLine.getStrokeDashArray().addAll(6.0, 6.0);
    }

    /**
     * Sets the sprite animation for the boss.
     *
     * @param bossTexture the {@link SpriteSheet} to display for the boss
     */
    public void setBossTexture(SpriteSheet bossTexture) {
        this.bossTexture = bossTexture;
        this.bossTexture.setFps(10.0);
        this.bossTexture.getView().setX(getX());
        this.bossTexture.getView().setY(getY());
        this.bossTexture.getView().setFitWidth(getWidth());
        this.bossTexture.getView().setFitHeight(getHeight());
        this.bossTexture.play();
    }

//    public Polyline getBossPathLine() {
//        return bossPathLine;
//    }

    /**
     * Returns the {@link ImageView} representing the boss texture.
     *
     * @return the {@link ImageView} for rendering
     */
    public ImageView getTexture() {
        return bossTexture.getView();
    }

    /**
     * Checks whether the boss is currently active in the game.
     *
     * @return {@code true} if the boss is active, {@code false} otherwise
     */
    public boolean isActive() {
        return active;
    }

    /** Updates the visual position of the boss's texture to match its logic position. */
    public void updateView() {
        bossTexture.getView().setX(getX());
        bossTexture.getView().setY(getY());
    }

    @Override
    public void update(double dt){}

    /**
     * Updates the boss logic and movement for each frame.
     *
     * @param dt      time delta in seconds
     * @param paddle  the player's paddle
     * @param layout  a grid representing passable and blocked cells
     * @param root    the game root pane
     * @param bricks  the list of bricks in the game
     * @param score   the current player score
     * @return updated score after boss interactions
     */
    public int bossUpdate(double dt, Paddle paddle, boolean[][] layout, Pane root,
                        List<Brick> bricks, int score) {
        drawBossPath(paddle, layout);
        if (pathPoints == null || pathPoints.isEmpty()) {
            randomMove(dt, bricks);
            return score;
        } else {
            return followPath(dt, paddle, root, bricks, score);
        }
    }

    /**
     * Deactivates the boss and removes it from the scene.
     *
     * @param root the root pane from which to remove the boss
     */
    public void deactivate(Pane root) {
        active = false;
        bossTexture.stop();
        root.getChildren().remove(bossTexture.getView());
        //root.getChildren().remove(this.bossPathLine);
    }

    /**
     * Calculates the path from the boss to the paddle using A* pathfinding.
     * The boss uses brick cells as a grid for navigation.
     *
     * @param paddle   the player's paddle
     * @param passable a boolean grid where {@code true} means the cell is passable
     */
    private void drawBossPath(Paddle paddle, boolean[][] passable) {
        int rows = 28;
        int cols = 13;

        int sr = (int) ((this.getY() + BOSS_SIZE/2)/ Brick.BRICK_HEIGHT);
        int sc = (int) ((this.getX() + BOSS_SIZE/2)/ Brick.BRICK_WIDTH);
        sr = Math.max(0, Math.min(sr, rows - 1));
        sc = Math.max(0, Math.min(sc, cols - 1));

        double paddleCenterX = paddle.getX() + paddle.getWidth() * 0.5;
        int gr = rows - 1;
        int gc = (int) (paddleCenterX / Brick.BRICK_WIDTH);
        gc = Math.max(0, Math.min(gc, cols - 1));

        boolean startWasBlocked = !passable[sr][sc];
        boolean goalWasBlocked = !passable[gr][gc];
        passable[sr][sc] = true;
        passable[gr][gc] = true;
        List<int[]> newPath = AStarInstructor.findPath(passable, sr, sc, gr, gc, true);
        passable[sr][sc] = !startWasBlocked;
        passable[gr][gc] = !goalWasBlocked;

        if (newPath == null || newPath.isEmpty()) return;

        // Nếu boss chưa đi qua ít nhất 1 điểm trong path cũ → KHÔNG thay path
        if (pathPoints != null && !pathPoints.isEmpty()) {
            // Nếu vẫn đang ở điểm đầu (chưa tiến thêm điểm nào)
            if (currentTargetIndex < 2) {
                return;
            }
        }

        // boss đã đi được ít nhất 1 điểm -> cập nhật path mới
        //bossPathLine.getPoints().clear();
        pathPoints = new ArrayList<>();

        for (int[] p : newPath) {
            int r = p[0];
            int c = p[1];
            double x = c * Brick.BRICK_WIDTH + Brick.BRICK_WIDTH * 0.5;
            double y = r * Brick.BRICK_HEIGHT + Brick.BRICK_HEIGHT * 0.5;
            //bossPathLine.getPoints().addAll(x, y);
            pathPoints.add(new Vec2f(x, y));
        }
        // reset về điểm đầu của path mới
        currentTargetIndex = 0;
    }

    /**
     * Moves the boss along the computed path toward the paddle, handling collisions
     * with bricks and the paddle.
     *
     * @param dt      delta time
     * @param paddle  the paddle instance
     * @param root    game scene root
     * @param bricks  list of bricks
     * @param score   current score
     * @return updated score
     */
    private int followPath(double dt, Paddle paddle, Pane root, List<Brick> bricks, int score) {
        boolean iscollidedWithPaddle = checkOverlap(paddle);
        if (getY() > GameScreen.GAME_HEIGHT || iscollidedWithPaddle) {
            deactivate(root);
            if (iscollidedWithPaddle) {
                return score - 1;
            }
            return score;
        }

        if (pathPoints == null || pathPoints.isEmpty()) {
            return score;
        }

        Vec2f target = pathPoints.get(0);
        Vec2f pos = new Vec2f(getX() + BOSS_SIZE / 2, getY() + BOSS_SIZE / 2);

        Vec2f toTarget = new Vec2f(target.x - pos.x, target.y - pos.y);
        double distance = toTarget.length();

        if (distance < ARRIVAL_THRESHOLD) {
            pathPoints.remove(0);
            currentTargetIndex++;

            if (pathPoints.isEmpty()) {
                followingPath = false;
                return score;
            }

            target = pathPoints.get(0);
            toTarget = new Vec2f(target.x - pos.x, target.y - pos.y);
        }

        boolean collidedX = false;
        boolean collidedY = false;

        for (Brick brick : bricks) {
            if (checkOverlap(brick)) {

                double bossCenterX = getX() + getWidth() / 2;
                double bossCenterY = getY() + getHeight() / 2;
                double brickCenterX = brick.getX() + brick.getWidth() / 2;
                double brickCenterY = brick.getY() + brick.getHeight() / 2;

                double dx = bossCenterX - brickCenterX;
                double dy = bossCenterY - brickCenterY;
                double overlapX = (brick.getWidth() / 2 + getWidth() / 2) - Math.abs(dx);
                double overlapY = (brick.getHeight() / 2 + getHeight() / 2) - Math.abs(dy);

                if (overlapX >= 0 && overlapY >= 0) {
                    double newX = getX();
                    double newY = getY();

                    if (overlapX < overlapY) {
                        newX += (dx > 0 ? overlapX : -overlapX);
                        collidedX = true;
                    } else {
                        newY += (dy > 0 ? overlapY : -overlapY);
                        collidedY = true;
                    }
                    setPosition(newX, newY);
                }

                if (collidedX || collidedY) {
                    if (collidedX) toTarget = new Vec2f(0, toTarget.y);
                    if (collidedY) toTarget = new Vec2f(toTarget.x, 0);
                }
            }
        }

        dash(paddle);

        toTarget.normalize();
        setDirection(toTarget);
        applyMovement(dt);
        updateView();

        teleport(dt);
        return score;
    }

    /**
     * Causes the boss to dash (increase speed) when close to the paddle.
     *
     * @param paddle the paddle instance
     */
    private void dash(Paddle paddle) {
        double bossCenterX = getX() + BOSS_SIZE/2;
        double bossCenterY = getY() + BOSS_SIZE/2;
        double paddleCenterX = paddle.getX() + Paddle.PADDLE_WIDTH/2;
        double paddleCenterY = paddle.getY();

        double distance = Math.sqrt(
               Math.pow(bossCenterX - paddleCenterX, 2)
               + Math.pow(bossCenterY - paddleCenterY, 2)
        );
        if (distance <= 100) this.speed = BOSS_SPEED*2.5;
    }

    /**
     * Handles random horizontal movement when no valid path exists.
     *
     * @param dt     delta time
     * @param bricks list of bricks for collision detection
     */
    private void randomMove(double dt, List<Brick> bricks) {
        timeAccumulator += dt;

        Vec2f dir = new Vec2f(randomDir, 0);
        dir.normalize();
        setDirection(dir);

        applyMovement(dt);

        boolean collidedX = false;

        double minX = 0;
        double maxX = GameScreen.GAME_WIDTH - getWidth();

        if (getX() <= minX) {
            setPosition(minX, getY());
            collidedX = true;
        } else if (getX() >= maxX) {
            setPosition(maxX, getY());
            collidedX = true;
        }

        for (Brick brick : bricks) {
            if (checkOverlap(brick)) {
                double bossCenterX = getX() + getWidth() / 2;
                double brickCenterX = brick.getX() + brick.getWidth() / 2;
                double dx = bossCenterX - brickCenterX;
                double overlapX = (brick.getWidth() / 2 + getWidth() / 2) - Math.abs(dx);
                if (overlapX > 0) {
                    setPosition(getX() + (dx > 0 ? overlapX  : -overlapX ), getY());
                    collidedX = true;
                }
            }
        }
        if (collidedX) {
            randomDir *= -1;
        }
        updateView();
    }

    /**
     * Returns the center position of the boss.
     *
     * @return a {@link Vec2f} representing the boss's center
     */
    public Vec2f getCenterBoss() {
        double centerX = getX() + getWidth() * 0.5;
        double centerY = getY() + getHeight() * 0.5;
        return new Vec2f(centerX, centerY);
    }

    /**
     * Teleports the boss ahead along its path if it remains stuck for too long.
     * Also adds a blinking red visual effect before teleportation.
     *
     * @param dt delta time
     */
    private void teleport(double dt) {
        double dx = getX() - lastPosition.x;
        double dy = getY() - lastPosition.y;
        double distMoved = Math.sqrt(dx * dx + dy * dy);

        if (pathPoints != null && !pathPoints.isEmpty()) {
            if (distMoved < ARRIVAL_THRESHOLD) {
                stuckTimer += dt;
            } else {
                stuckTimer = 0;
                bossTexture.getView().setEffect(null); // remove tint if moving again
            }

            //  Flash red when nearly stuck
            if (stuckTimer >= STUCK_THRESHOLD * 0.6 && stuckTimer < STUCK_THRESHOLD) {
                double blink = Math.sin(stuckTimer * 20); // fast blink
                if (blink > 0) {
                    bossTexture.getView().setStyle("-fx-effect: innershadow(gaussian, red, 25, 0.5, 0, 0);");
                } else {
                    bossTexture.getView().setStyle(null);
                }
            }

            //  Teleport when stuck for too long
            if (stuckTimer >= STUCK_THRESHOLD && pathPoints.size() > 2) {
                bossTexture.getView().setStyle(null); // clear effect before teleport

                // Skip two points
                pathPoints.remove(0);
                pathPoints.remove(0);

                Vec2f jumpTarget = pathPoints.get(0);
                setPosition(jumpTarget.x - getWidth() / 2, jumpTarget.y - getHeight() / 2);
                updateView();

                stuckTimer = 0;
            }
        }
        lastPosition = new Vec2f(getX(), getY());
    }
}
