package com.raumania.gameplay.objects.boss;

import com.raumania.core.AStarInstructor;
import com.raumania.core.SpriteSheet;
import com.raumania.gameplay.manager.GameManager;
import com.raumania.gameplay.objects.brick.Brick;
import com.raumania.gameplay.objects.core.MovableObject;
import com.raumania.gameplay.objects.core.GameObject;
import com.raumania.gameplay.objects.powerup.PowerUpType;
import com.raumania.gameplay.objects.Paddle;
import com.raumania.gameplay.objects.Ball;
import com.raumania.gui.screen.GameScreen;
import com.raumania.math.Vec2f;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;

import java.util.Iterator;
import java.util.List;

import java.util.ArrayList;
public class Boss extends MovableObject {
    private SpriteSheet bossTexture;
    private boolean active = true;
    public static final double BOSS_SPEED = 95.0;
    public static final double BOSS_SIZE = 30.0;
    private Polyline bossPathLine;

    private List<Vec2f> pathPoints;
    private int currentTargetIndex;
    private boolean followingPath = true;
    private static final double ARRIVAL_THRESHOLD = 2.0; // if the distance is less than arrival_threshold, consider that collision is happened

    public Boss(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.speed = BOSS_SPEED;
        this.setDirection(new Vec2f(0, 1));
        this.bossPathLine = new Polyline();
        this.bossPathLine.setStroke(Color.RED);
        this.bossPathLine.setStrokeWidth(2.0);
        this.bossPathLine.getStrokeDashArray().addAll(6.0, 6.0);
    }

    public void setBossTexture(SpriteSheet bossTexture) {
        this.bossTexture = bossTexture;
        this.bossTexture.setFps(10.0);
        this.bossTexture.getView().setX(getX());
        this.bossTexture.getView().setY(getY());
        this.bossTexture.getView().setFitWidth(getWidth());
        this.bossTexture.getView().setFitHeight(getHeight());
        this.bossTexture.play();
    }

    public ImageView getTexture() {
        return bossTexture.getView();
    }

    public Polyline getBossPathLine() {
        return bossPathLine;
    }

    public boolean isActive() {
        return active;
    }

    public void updateView() {
        bossTexture.getView().setX(getX());
        bossTexture.getView().setY(getY());
    }

    @Override
    public void update(double dt){}
    public void bossUpdate(double dt, Paddle paddle, boolean[][] layout, Pane root, List<Brick> bricks) {
        drawBossPath(paddle, layout);
        followPath(dt, paddle, root, bricks);
    }

    public void deactivate(Pane root) {
        active = false;
        bossTexture.stop();
        root.getChildren().remove(bossTexture.getView());
        root.getChildren().remove(this.bossPathLine);
    }

    /**
     * Compute boss path on the brick grid (cells = bricks) and draw it as a polyline.
     * Uses the AStar implementation on a grid where cells containing a brick are blocked.
     * The path goes from the boss current cell to the bottom row near the paddle.
     */
    private void drawBossPath(Paddle paddle, boolean[][] passable) {
        int rows = 27;
        int cols = 10;

        int sr = (int) (this.getY() / Brick.BRICK_HEIGHT);
        int sc = (int) (this.getX() / Brick.BRICK_WIDTH);
        sr = Math.max(0, Math.min(sr, rows - 1));
        sc = Math.max(0, Math.min(sc, cols - 1));
        // choose goal column near paddle center, goal row = bottom row of brick grid
        double paddleCenterX = paddle.getX() + paddle.getWidth() * 0.5;
        int gr = rows - 1;
        int gc = (int) (paddleCenterX / Brick.BRICK_WIDTH);
        gc = Math.max(0, Math.min(gc, cols - 1));

        // ensure start and goal are temporarily passable for path search
        boolean startWasBlocked = !passable[sr][sc];
        boolean goalWasBlocked = !passable[gr][gc];
        passable[sr][sc] = true;
        passable[gr][gc] = true;
        List<int[]> path = AStarInstructor.findPath(passable, sr, sc, gr, gc, true);
        // restore (not strictly necessary for this ephemeral array but keep semantics)
        passable[sr][sc] = !startWasBlocked;
        passable[gr][gc] = !goalWasBlocked;
        bossPathLine.getPoints().clear();
        pathPoints = new ArrayList<>();

        for (int[] p : path) {
            int r = p[0];
            int c = p[1];
            double x = c * Brick.BRICK_WIDTH + Brick.BRICK_WIDTH * 0.5;
            double y = r * Brick.BRICK_HEIGHT + Brick.BRICK_HEIGHT * 0.5;
            bossPathLine.getPoints().addAll(x, y);
            pathPoints.add(new Vec2f(x, y));
        }
    }

    private void followPath(double dt, Paddle paddle, Pane root, List<Brick> bricks) {
        if (getY() > GameScreen.GAME_HEIGHT || checkCollisionWith(paddle)) {
            deactivate(root);
            return;
        }
        if (!followingPath || pathPoints == null || pathPoints.isEmpty()) return;

        Vec2f target = pathPoints.get(currentTargetIndex);
        Vec2f pos = new Vec2f(getX(), getY());

        Vec2f toTarget = new Vec2f(target.x - pos.x, target.y - pos.y);
        double distance = toTarget.length();

        boolean collidedWithBrick = false;

        if (distance < ARRIVAL_THRESHOLD) {
            currentTargetIndex++;
            if (currentTargetIndex >= pathPoints.size()) {
                followingPath = false;
                return;
            }
            target = pathPoints.get(currentTargetIndex);
            toTarget = new Vec2f(target.x - pos.x, target.y - pos.y);
            distance = toTarget.length();

            for (Brick brick : bricks) {
                if (checkCollisionWith(brick)) {
                    collidedWithBrick = true;

                    double bossCenterX = getX() + getWidth() / 2;
                    double bossCenterY = getY() + getHeight() / 2;
                    double brickCenterX = brick.getX() + brick.getWidth() / 2;
                    double brickCenterY = brick.getY() + brick.getHeight() / 2;

                    double dx = bossCenterX - brickCenterX;
                    double dy = bossCenterY - brickCenterY;
                    double overlapX = (brick.getWidth() / 2 + getWidth() / 2) - Math.abs(dx);
                    double overlapY = (brick.getHeight() / 2 + getHeight() / 2) - Math.abs(dy);

                    if (overlapX > 0 && overlapY > 0) {
                        double newX = getX();
                        double newY = getY();

                        if (overlapX < overlapY) {
                            newX += (dx > 0 ? overlapX : -overlapX);
                        } else {
                            newY += (dy > 0 ? overlapY : -overlapY);
                        }

                        setPosition(newX, newY);
                        updateView();
                    }
                }
            }
        }

        if (!collidedWithBrick) {
            toTarget.normalize();
            setDirection(toTarget);
            applyMovement(dt);
            updateView();
        }
    }


    private boolean checkCollisionWith(GameObject obj) {
        double bossLeft = getX();
        double bossRight = getX() + getWidth();
        double bossTop = getY();
        double bossBottom = getY() + getHeight();

        double objLeft = obj.getX();
        double objRight = obj.getX() + obj.getWidth();
        double objTop = obj.getY();
        double objBottom = obj.getY() + obj.getHeight();

        return bossRight >= objLeft &&
                bossLeft <= objRight &&
                bossBottom >= objTop &&
                bossTop <= objBottom;
    }
}
