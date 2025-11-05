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
import com.raumania.gameplay.objects.visioneffect.VisionEffect;
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
    public static double BOSS_SPEED = 95.0;
    public static final double BOSS_SIZE = 32.0;
    private double timeAccumulator = 0;

//    private Polyline bossPathLine;
    private List<Vec2f> pathPoints;
    private int currentTargetIndex;
    private boolean followingPath = true;
    private static final double ARRIVAL_THRESHOLD = 10; // if the distance is less than arrival_threshold, consider that collision is happened

    public Boss(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.speed = BOSS_SPEED;
        this.setDirection(new Vec2f(0, 1));
//        this.bossPathLine = new Polyline();
//        this.bossPathLine.setStroke(Color.RED);
//        this.bossPathLine.setStrokeWidth(2.0);
//        this.bossPathLine.getStrokeDashArray().addAll(6.0, 6.0);
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

//    public Polyline getBossPathLine() {
//        return bossPathLine;
//    }

    public boolean isActive() {
        return active;
    }

    public void updateView() {
        bossTexture.getView().setX(getX());
        bossTexture.getView().setY(getY());
    }

    @Override
    public void update(double dt){}
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

    public void deactivate(Pane root) {
        active = false;
        bossTexture.stop();
        root.getChildren().remove(bossTexture.getView());
        //root.getChildren().remove(this.bossPathLine);
    }

    /**
     * Compute boss path on the brick grid (cells = bricks) and draw it as a polyline.
     * Uses the AStar implementation on a grid where cells containing a brick are blocked.
     * The path goes from the boss current cell to the bottom row near the paddle.
     */
    private void drawBossPath(Paddle paddle, boolean[][] passable) {
        int rows = 27;
        int cols = 10;

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

    private int followPath(double dt, Paddle paddle, Pane root, List<Brick> bricks, int score) {
        // Boss rơi ra khỏi màn hình hoặc chạm paddle → hủy
        boolean iscollidedWithPaddle = checkOverlap(paddle);
        if (getY() > GameScreen.GAME_HEIGHT || iscollidedWithPaddle) {
            deactivate(root);
            if (iscollidedWithPaddle) {
                return score - 1;
            }
            return score;
        }

        // Nếu chưa có đường đi hoặc không đang theo path thì thôi
        if (pathPoints == null || pathPoints.isEmpty()) {
            return score;
        }

        // Lấy điểm mục tiêu hiện tại (điểm đầu của path)
        Vec2f target = pathPoints.get(0);
        Vec2f pos = new Vec2f(getX() + BOSS_SIZE / 2, getY() + BOSS_SIZE / 2);

        Vec2f toTarget = new Vec2f(target.x - pos.x, target.y - pos.y);
        double distance = toTarget.length();

        // Khi boss đến gần điểm này → remove khỏi path
        if (distance < ARRIVAL_THRESHOLD) {
            pathPoints.remove(0);
            currentTargetIndex++;

            if (pathPoints.isEmpty()) {
                followingPath = false;
                return score;
            }

            // Cập nhật target mới
            target = pathPoints.get(0);
            toTarget = new Vec2f(target.x - pos.x, target.y - pos.y);
        }

        boolean collidedX = false;
        boolean collidedY = false;

        // Kiểm tra va chạm với bricks
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

                    // Đẩy boss ra khỏi brick theo hướng va chạm
                    if (overlapX < overlapY) {
                        newX += (dx > 0 ? overlapX : -overlapX);
                        collidedX = true; // va chạm theo chiều X
                    } else {
                        newY += (dy > 0 ? overlapY : -overlapY);
                        collidedY = true; // va chạm theo chiều Y
                    }
                    setPosition(newX, newY);
                }

                // Nếu boss va chạm tường → khóa hướng theo chiều đó
                if (collidedX || collidedY) {
                    if (collidedX) toTarget = new Vec2f(0, toTarget.y);
                    if (collidedY) toTarget = new Vec2f(toTarget.x, 0);
                }
            }
        }
        // neu gan paddle, dash
        dash(paddle);

        toTarget.normalize();
        setDirection(toTarget);
        applyMovement(dt);
        updateView();
        return score;
    }

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

    private double randomDir = 1; // 1 = sang phải, -1 = sang trái

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

}
