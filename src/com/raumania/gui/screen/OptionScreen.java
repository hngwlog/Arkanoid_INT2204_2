package com.raumania.gui.screen;

import com.raumania.gui.manager.SceneManager;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import com.raumania.utils.UIUtils;

public class OptionScreen extends Screen {

    int paddleCnt = 0;
    int ballCnt = 0;
    int backgroundCnt = 0;
    int maxPaddleNumber = 5;
    int maxBallNumber = 5;
    int maxBackgroundNumber = 5;
    Text currentPaddle;
    Text currentBall;
    Text currentBackground;

    public OptionScreen(SceneManager sceneManager) {
        super(sceneManager);
        //paddle
        Text paddleText = UIUtils.newText("Paddle: ", 100, 200, 2.0, 2.0);
        Button paddleLeft = UIUtils.newButton("<" , 315, 185, 2.0, 2.0);
        Button paddleRight = UIUtils.newButton(">" , 815, 185, 2.0, 2.0);
        currentPaddle =  UIUtils.newText("Paddle 1" , 550, 200, 2.0, 2.0);
        paddleLeft.setOnAction(e -> {
            changeCnt("Paddle", -1);
        });
        paddleRight.setOnAction(e -> {
            changeCnt("Paddle", 1);
        });
        //ball
        Text ballText =  UIUtils.newText("Ball: ", 100, 300, 2.0, 2.0);
        Button ballLeft = UIUtils.newButton("<" , 315, 285, 2.0, 2.0);
        Button ballRight =  UIUtils.newButton(">" , 815, 285, 2.0, 2.0);
        currentBall =  UIUtils.newText("Ball 1" , 550, 300, 2.0, 2.0);
        ballLeft.setOnAction(e -> {
            changeCnt("Ball", -1);
        });
        ballRight.setOnAction(e -> {
            changeCnt("Ball", 1);
        });
        //background
        Text backgroundText = UIUtils.newText("Background: ", 100, 400, 2.0, 2.0);
        Button backgroundLeft = UIUtils.newButton("<" , 315, 385, 2.0, 2.0);
        Button backgroundRight = UIUtils.newButton(">" , 815, 385, 2.0, 2.0);
        currentBackground =  UIUtils.newText("Background 1" , 550, 400, 2.0, 2.0);
        backgroundLeft.setOnAction(e -> {
            changeCnt("Background", -1);
        });
        backgroundRight.setOnAction(e -> {
            changeCnt("Background", 1);
        });

        root.getChildren().addAll(paddleText, paddleLeft, paddleRight, currentPaddle,
                ballText, ballLeft, ballRight, currentBall,
                backgroundText, backgroundLeft, backgroundRight, currentBackground);

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
                this.paddleCnt = cnt;
                break;
            case "Ball":
                this.ballCnt = cnt;
                break;
            case "Background":
                this.backgroundCnt = cnt;
                break;
        }
    }

    private int getCnt(String cntType) {
        return switch (cntType) {
            case "Paddle" -> this.paddleCnt;
            case "Ball" -> this.ballCnt;
            case "Background" -> this.backgroundCnt;
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
        if (cnt < 0) cnt = maxCnt-1;
        cnt %= maxCnt;
        applyChange(cntType, cnt);
        cntCurrentText(cntType).setText(cntType + " " + (cnt + 1));
    }

}
