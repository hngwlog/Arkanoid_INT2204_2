package com.raumania.gui.screen;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raumania.gui.manager.SceneManager;
import com.raumania.utils.ResourcesLoader;
import com.raumania.utils.UIUtils;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;

public class OptionScreen extends Screen {

    private static final String CONFIG_FILE = "skins.json";
    private static final Config DEFAULT_CONFIG = new Config(0, 0, 0);
    public static Config sharedConfig;
    int maxPaddleNumber = 5;
    int maxBallNumber = 5;
    int maxBackgroundNumber = 5;
    Text currentPaddle;
    Text currentBall;
    Text currentBackground;
    private Config config;

    public OptionScreen(SceneManager sceneManager) {
        super(sceneManager);

        this.loadConfig();

        // paddle
        Text paddleText = UIUtils.newText("Paddle: ", 100, 200, 2.0, 2.0);
        paddleText.setFill(Color.WHITE);
        Button paddleLeft = UIUtils.newButton("<", 315, 185, 2.0, 2.0);
        Button paddleRight = UIUtils.newButton(">", 815, 185, 2.0, 2.0);
        currentPaddle = UIUtils.newText("Paddle " + (config.paddle + 1), 550, 200, 2.0, 2.0);
        currentPaddle.setFill(Color.WHITE);
        paddleLeft.setOnAction(
                e -> {
                    changeCnt("Paddle", -1);
                });
        paddleRight.setOnAction(
                e -> {
                    changeCnt("Paddle", 1);
                });
        // ball
        Text ballText = UIUtils.newText("Ball: ", 100, 300, 2.0, 2.0);
        ballText.setFill(Color.WHITE);
        Button ballLeft = UIUtils.newButton("<", 315, 285, 2.0, 2.0);
        Button ballRight = UIUtils.newButton(">", 815, 285, 2.0, 2.0);
        currentBall = UIUtils.newText("Ball " + (config.ball + 1), 550, 300, 2.0, 2.0);
        currentBall.setFill(Color.WHITE);
        ballLeft.setOnAction(
                e -> {
                    changeCnt("Ball", -1);
                });
        ballRight.setOnAction(
                e -> {
                    changeCnt("Ball", 1);
                });
        // background
        Text backgroundText = UIUtils.newText("Background: ", 100, 400, 2.0, 2.0);
        backgroundText.setFill(Color.WHITE);
        Button backgroundLeft = UIUtils.newButton("<", 315, 385, 2.0, 2.0);
        Button backgroundRight = UIUtils.newButton(">", 815, 385, 2.0, 2.0);
        currentBackground =
                UIUtils.newText("Background " + (config.background + 1), 550, 400, 2.0, 2.0);
        currentBackground.setFill(Color.WHITE);
        backgroundLeft.setOnAction(
                e -> {
                    changeCnt("Background", -1);
                });
        backgroundRight.setOnAction(
                e -> {
                    changeCnt("Background", 1);
                });

        // Back to Home button
        Button back = UIUtils.centerButton("Back to Home", 500, 2.0, 2.0);
        back.setOnAction(
                e -> {
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
                        backgroundText,
                        backgroundLeft,
                        backgroundRight,
                        currentBackground,
                        back);

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

    private Text cntCurrentText(String cntType) {
        return switch (cntType) {
            case "Ball" -> this.currentBall;
            case "Background" -> this.currentBackground;
            default -> this.currentPaddle;
        };
    }

    private void applyChange(String cntType, int cnt) {
        switch (cntType) {
            case "Paddle":
                config.paddle = cnt;
                break;
            case "Ball":
                config.ball = cnt;
                break;
            case "Background":
                config.background = cnt;
                break;
        }
    }

    private int getCnt(String cntType) {
        return switch (cntType) {
            case "Paddle" -> config.paddle;
            case "Ball" -> config.ball;
            case "Background" -> config.background;
            default -> 0;
        };
    }

    private int getMaxNumber(String cntType) {
        return switch (cntType) {
            case "Paddle" -> this.maxPaddleNumber;
            case "Ball" -> this.maxBallNumber;
            case "Background" -> this.maxBackgroundNumber;
            default -> 5;
        };
    }

    private void changeCnt(String cntType, int changeType) {
        Platform.runLater(root::requestFocus);
        int cnt = getCnt(cntType);
        int maxCnt = getMaxNumber(cntType);
        cnt += changeType;
        if (cnt < 0) cnt = maxCnt - 1;
        cnt %= maxCnt;
        applyChange(cntType, cnt);
        cntCurrentText(cntType).setText(cntType + " " + (cnt + 1));
        saveConfig();
    }

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
            mapper.writeValue(file, config);
        } catch (IOException e) {
            System.err.println("Error saving skins file!" + e);
        }
        sharedConfig = config;
    }

    private void loadConfig() {
        File file = new File(CONFIG_FILE);
        if (!file.exists() || file.length() == 0) {
            config = DEFAULT_CONFIG;
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            config = mapper.readValue(file, OptionScreen.Config.class);
        } catch (DatabindException e) {
            System.err.println("Skins file has corrupted structure!");
        } catch (StreamReadException e) {
            System.err.println("Cant read skins file!");
        } catch (IOException e) {
            System.err.println("Error loading skins file!");
        }
        sharedConfig = config;
    }

    public static class Config {
        @JsonIgnore private int paddle;
        @JsonIgnore private int ball;
        @JsonIgnore private int background;

        public Config(
                @JsonProperty("paddle") int paddle,
                @JsonProperty("ball") int ball,
                @JsonProperty("background") int background) {
            this.paddle = paddle;
            this.ball = ball;
            this.background = background;
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

        @JsonGetter
        public int getBackground() {
            return background;
        }

        @JsonProperty("background")
        public void setBackground(int background) {
            this.background = background;
        }
    }
}
