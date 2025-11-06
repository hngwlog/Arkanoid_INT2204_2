package com.raumania.gui.screen;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raumania.gameplay.objects.Ball;
import com.raumania.gameplay.objects.Paddle;
import com.raumania.gui.manager.SceneManager;
import com.raumania.utils.ResourcesLoader;
import com.raumania.utils.UIUtils;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;

public class SkinSelectScreen extends Screen {

    private static final String CONFIG_FILE = "skins.json";
    private static final Config DEFAULT_CONFIG = new Config(0, 0);
    private static final int MAX_PADDLE_SKIN = 4;
    private static final int MAX_BALL_COLOR = 5;
    private static Config sharedConfig = DEFAULT_CONFIG;
    private final ImageView currentPaddle;
    private final Rectangle currentBall;

    public SkinSelectScreen(SceneManager sceneManager) {
        super(sceneManager);

        this.loadConfig();

        // paddle
        Text paddleText = UIUtils.newText("Paddle: ", 100, 200, 2.0, 2.0);
        paddleText.setFill(Color.WHITE);
        Button paddleLeft = UIUtils.newButton("<", 315, 185, 2.0, 2.0);
        Button paddleRight = UIUtils.newButton(">", 815, 185, 2.0, 2.0);
        currentPaddle =
                new ImageView(ResourcesLoader.loadImage("paddle" + sharedConfig.paddle + ".png"));
        currentPaddle.setLayoutX(520);
        currentPaddle.setLayoutY(190);
        currentPaddle.setFitWidth(100);
        currentPaddle.setPreserveRatio(true);

        paddleLeft.setOnAction(
                e -> {
                    changeCount("Paddle", -1);
                });
        paddleRight.setOnAction(
                e -> {
                    changeCount("Paddle", 1);
                });

        // ball
        Text ballText = UIUtils.newText("Ball Color: ", 100, 300, 2.0, 2.0);
        ballText.setFill(Color.WHITE);
        Button ballLeft = UIUtils.newButton("<", 315, 285, 2.0, 2.0);
        Button ballRight = UIUtils.newButton(">", 815, 285, 2.0, 2.0);
        currentBall = UIUtils.newRectangle(100, 50, 520, 275);
        currentBall.setFill(Ball.BALL_COLORS.get(sharedConfig.ball));
        ballLeft.setOnAction(
                e -> {
                    changeCount("Ball", -1);
                });
        ballRight.setOnAction(
                e -> {
                    changeCount("Ball", 1);
                });

        // Confirm changes button
        Button submit = UIUtils.centerButton("Confirm", 500, 2.0, 2.0);
        submit.setOnAction(
                e -> {
                    Paddle.setTextureIndex(sharedConfig.paddle);
                    Ball.setTextureIndex(sharedConfig.ball);

                    sceneManager.switchScreen(ScreenType.HOME);
                });

        root.getChildren()
                .addAll(
                        paddleText,
                        paddleLeft,
                        paddleRight,
                        currentPaddle,
                        ballText,
                        ballLeft,
                        ballRight,
                        currentBall,
                        submit);

        Background bg =
                new Background(
                        new BackgroundImage(
                                ResourcesLoader.loadImage("homescreen_bg.png"),
                                BackgroundRepeat.NO_REPEAT,
                                BackgroundRepeat.NO_REPEAT,
                                BackgroundPosition.CENTER,
                                new BackgroundSize(1.0, 1.0, true, true, false, true)));
        root.setBackground(bg);
    }

    @Override
    public void onStart() {
        Platform.runLater(root::requestFocus);
    }

    /**
     * get current skin of the current type.
     *
     * @param cntType - current skin type
     * @return curren skin
     */
    private Object cntCurrentText(String cntType) {
        if (cntType.equals("Paddle")) {
            return currentPaddle;
        } else if (cntType.equals("Ball")) {
            return currentBall;
        }
        return null;
    }

    /**
     * change curren config's skin.
     *
     * @param cntType - skin type
     * @param cnt - index of the skin
     */
    private void applyChange(String cntType, int cnt) {
        switch (cntType) {
            case "Paddle":
                sharedConfig.paddle = cnt;

                break;
            case "Ball":
                sharedConfig.ball = cnt;
                break;
        }
    }

    /**
     * get current skin's index of current type.
     *
     * @param cntType - curren type
     * @return current skin's index
     */
    private int getCount(String cntType) {
        return switch (cntType) {
            case "Paddle" -> sharedConfig.paddle;
            case "Ball" -> sharedConfig.ball;
            default -> 0;
        };
    }

    /**
     * get max skin number of current type.
     *
     * @param cntType - current type
     * @return max skin number
     */
    private int getMaxNumber(String cntType) {
        return switch (cntType) {
            case "Paddle" -> MAX_PADDLE_SKIN;
            case "Ball" -> MAX_BALL_COLOR;
            default -> 5;
        };
    }

    /**
     * Change the current skin of the current type.
     *
     * @param cntType current type
     * @param changeType -1 -> decrease, 1 -> increase
     */
    private void changeCount(String cntType, int changeType) {
        Platform.runLater(root::requestFocus);
        int cnt = getCount(cntType);
        int maxCnt = getMaxNumber(cntType);
        cnt += changeType;
        if (cnt < 0) cnt = maxCnt - 1;
        cnt %= maxCnt;
        applyChange(cntType, cnt);
        Object currentObject = cntCurrentText(cntType);
        if (currentObject instanceof ImageView) {
            ((ImageView) currentObject)
                    .setImage(ResourcesLoader.loadImage("paddle" + cnt + ".png"));
        } else if (currentObject instanceof Rectangle) {
            ((Rectangle) currentObject).setFill(Ball.BALL_COLORS.get(cnt));
        }
        saveConfig();
    }

    /** save current config from game to FILE. */
    private void saveConfig() {
        File file = new File(CONFIG_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating skins file!");
                return;
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(file, sharedConfig);
        } catch (IOException e) {
            System.err.println("Error saving skins file!" + e);
        }
    }

    /** load current config from FILE to the game. */
    private void loadConfig() {
        File file = new File(CONFIG_FILE);
        if (!file.exists() || file.length() == 0) {
            sharedConfig = DEFAULT_CONFIG;
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            sharedConfig = mapper.readValue(file, SkinSelectScreen.Config.class);
            Ball.setTextureIndex(sharedConfig.ball);
            Paddle.setTextureIndex(sharedConfig.paddle);
        } catch (DatabindException e) {
            System.err.println("Skins file has corrupted structure!");
        } catch (StreamReadException e) {
            System.err.println("Cant read skins file!");
        } catch (IOException e) {
            System.err.println("Error loading skins file!");
        }
    }

    /** Config. */
    public static class Config {
        private int paddle;
        private int ball;

        public Config(@JsonProperty("paddle") int paddle, @JsonProperty("ball") int ball) {
            this.paddle = paddle;
            this.ball = ball;
        }

        @JsonGetter
        public int getPaddle() {
            return paddle;
        }

        @JsonProperty("paddle")
        public void setPaddle(int paddle) {
            this.paddle = paddle;
        }

        @JsonGetter
        public int getBall() {
            return ball;
        }

        @JsonProperty("ball")
        public void setBall(int ball) {
            this.ball = ball;
        }
    }
}
