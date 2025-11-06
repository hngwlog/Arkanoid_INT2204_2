package com.raumania.gui.screen;

import com.raumania.core.AudioManager;
import com.raumania.core.MapLoader;
import com.raumania.gameplay.manager.GameManager;
import com.raumania.gameplay.manager.InputHandler;
import com.raumania.gui.manager.SceneManager;
import com.raumania.main.Main;
import com.raumania.utils.ResourcesLoader;
import com.raumania.utils.UIUtils;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import java.util.*;

public class MultiplayerGameScreen extends Screen {
    private final Button pause;
    private final Pane mainPause;
    private final Pane backChoice;
    private final Pane winPane;
    private final Text rightScore;
    private final Text leftScore;
    private final List<Button> pauseButtons;
    private final List<Button> homeButtons;
    private final List<Double> pauseButtonYs;
    private final List<Double> homeButtonYs;
    private final Text pauseChooseArrowLeft;
    private final Text pauseChooseArrowRight;
    private final Text homeChooseArrowLeft;
    private final Text homeChooseArrowRight;
    private final GameManager leftManager;
    private final GameManager rightManager;
    private final Text title;
    private final Text title1;
    private InputHandler leftInputHandler;
    private InputHandler rightInputHandler;
    private AnimationTimer loop;
    private long past = - 1;
    private int pauseState = 0;
    private int pauseCnt = 0;
    private int homeCnt = 0;

    public MultiplayerGameScreen(SceneManager sceneManager) {
        super(sceneManager);
        backChoice = new Pane();
        mainPause = new Pane();

        this.leftManager = new GameManager();
        // handle game over state
        this.leftManager.gameStateProperty().addListener((obs, oldState, newState) -> {
            if (newState == GameManager.GameState.GAME_OVER) {
                if (leftManager.isWinner()) {
                    onPlayerWin(1);
                } else {
                    onPlayerWin(2);
                }
                PauseTransition pause = new PauseTransition();
                pause.setDuration(Duration.seconds(2));
                pause.setOnFinished(e -> sceneManager.switchScreen(ScreenType.HOME));
                pause.play();
            }
        });

        this.rightManager = new GameManager();
        this.rightManager.gameStateProperty().addListener((obs, oldState, newState) -> {
            if (newState == GameManager.GameState.GAME_OVER) {
                if (rightManager.isWinner()) {
                    onPlayerWin(2);
                } else {
                    onPlayerWin(1);
                }
                PauseTransition pause = new PauseTransition();
                pause.setDuration(Duration.seconds(2));
                pause.setOnFinished(e -> sceneManager.switchScreen(ScreenType.HOME));
                pause.play();
            }
        });

        winPane = new Pane();
        winPane.setBackground(
                new Background(new BackgroundFill(new Color(0.0, 0.0, 0.0, 0.7), CornerRadii.EMPTY, Insets.EMPTY))
        );
        winPane.setPrefSize(Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
        Text win = new Text();
        win.setFill(Color.YELLOW);
        win.setScaleX(3.0);
        win.setScaleY(3.0);
        winPane.getChildren().add(win);
        winPane.setVisible(false);

        //Game play screen
        Pane gamePlayScreen = new Pane();
        //Pause button
        pause = UIUtils.newButton("||", 1060, 20, 2.0, 2.0);
        pause.setOnAction(e -> {
            Platform.runLater(root::requestFocus);
            this.pause();
        });

        // Border
        Rectangle leftBorder = UIUtils.newRectangle(GameScreen.GAME_WIDTH, GameScreen.GAME_HEIGHT,
                30, GameScreen.GAME_START_Y);
        leftBorder.setFill(Color.TRANSPARENT);
        leftBorder.setStroke(Color.BLACK);
        leftBorder.setStrokeWidth(2);
        Rectangle rightBorder = UIUtils.newRectangle(GameScreen.GAME_WIDTH, GameScreen.GAME_HEIGHT,
                Main.WINDOW_WIDTH - GameScreen.GAME_WIDTH - 30, GameScreen.GAME_START_Y);
        rightBorder.setFill(Color.TRANSPARENT);
        rightBorder.setStroke(Color.BLACK);
        rightBorder.setStrokeWidth(2);
        //Score
        rightScore = UIUtils.newText("Score:", 600, 30, 2.0, 2.0);
        rightScore.setFont(Font.font("System", FontWeight.BOLD, 14));
        rightScore.setFill(Color.WHITE);
        leftScore = UIUtils.newText("Score:", 320, 30, 2.0, 2.0);
        leftScore.setFont(Font.font("System", FontWeight.BOLD, 14));
        leftScore.setFill(Color.WHITE);
        gamePlayScreen.getChildren().addAll(pause, rightScore, leftScore, leftBorder, rightBorder);
        gamePlayScreen.setVisible(true);

        //Pause screen
        // mainPause.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);"); // Add translucent black background
        mainPause.setBackground(
                new Background(new BackgroundFill(new Color(0.0, 0.0, 0.0, 0.7), CornerRadii.EMPTY, Insets.EMPTY))
        );
        mainPause.setPrefSize(Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
        //Pause Title
        title = UIUtils.centerText("Pause", 100, 3.0, 3.0);
        title.setFill(Color.GREEN);
        //Resume button
        Button resume = UIUtils.centerButton("Resume", 200, 2.0, 2.0);
        resume.setOnAction(e -> {
            this.resume();
        });
        //Home button
        Button home = UIUtils.centerButton("Back to Home", 300, 2.0, 2.0);
        home.setOnAction(e -> {
            Platform.runLater(root::requestFocus);
            mainPause.setVisible(false);
            backChoice.setVisible(true);
            pauseState = 2;
            pauseCnt = 0;
            updateCnt();
        });
        //choose arrows
        pauseChooseArrowLeft = UIUtils.newText(">" , 383.75, 212.5, 2.0, 2.0);
        pauseChooseArrowLeft.setFill(Color.GREEN);
        pauseChooseArrowRight = UIUtils.newText("<" , 610.76, 212.5, 2.0, 2.0);
        pauseChooseArrowRight.setFill(Color.GREEN);
        //buttons list
        pauseButtons = new ArrayList<>();
        Collections.addAll(pauseButtons, resume, home);
        for (Button button : pauseButtons) {
            button.setOnMouseEntered(e -> {
                pauseCnt = getIndex(button);
                updateCnt();
            });
        }
        //button's Y list
        pauseButtonYs = new ArrayList<>();
        Collections.addAll(pauseButtonYs, 200.0, 300.0);
        //add to mainPause pane
        mainPause.getChildren().addAll(pauseButtons);
        mainPause.getChildren().addAll(title, pauseChooseArrowLeft, pauseChooseArrowRight);
        mainPause.setVisible(false);

        // Confirmation Screen
        // backChoice.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);"); // Add translucent black background
        backChoice.setBackground(
                new Background(new BackgroundFill(new Color(0.0, 0.0, 0.0, 0.7), CornerRadii.EMPTY, Insets.EMPTY))
        );
        backChoice.setPrefSize(Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
        // Title
        title1 = UIUtils.centerText("Are you sure", 100, 3.0, 3.0);
        title1.setFill(Color.GREEN);
        //Yes button
        //BUG : game over when play again (but still init)
        Button yes = UIUtils.centerButton("Yes", 200, 2.0, 2.0);
        yes.setOnAction(e -> {
            sceneManager.switchScreen(ScreenType.HOME);
            leftManager.initGame();
            leftManager.setGameState(GameManager.GameState.PAUSED);
        });
        //No button
        Button no = UIUtils.centerButton("No", 300, 2.0, 2.0);
        no.setOnAction(e -> {
            Platform.runLater(root::requestFocus);
            mainPause.setVisible(true);
            backChoice.setVisible(false);
            pauseState = 1;
            homeCnt = 0;
            updateCnt();
        });
        //choose arrow
        homeChooseArrowLeft = UIUtils.newText(">" , 383.75, 212.5, 2.0, 2.0);
        homeChooseArrowLeft.setFill(Color.GREEN);
        homeChooseArrowRight = UIUtils.newText("<" , 610.76, 212.5, 2.0, 2.0);
        homeChooseArrowRight.setFill(Color.GREEN);
        //buttons list
        homeButtons = new ArrayList<>();
        Collections.addAll(homeButtons, yes, no);
        for (Button button : homeButtons) {
            button.setOnMouseEntered(e -> {
                homeCnt = getIndex(button);
                updateCnt();
            });
        }
        //button's Y list
        homeButtonYs = new ArrayList<>();
        Collections.addAll(homeButtonYs, 200.0, 300.0);
        //add to backChoice pane
        backChoice.getChildren().addAll(homeButtons);
        backChoice.getChildren().addAll(title1, homeChooseArrowLeft, homeChooseArrowRight);
        backChoice.setVisible(false);

        // key event
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                if (rightManager.getGameState() == GameManager.GameState.READY) {
                    rightManager.startGame();
                    leftManager.startGame();
                }
            } else if (e.getCode() == KeyCode.ESCAPE) {
                if (rightManager.getGameState() == GameManager.GameState.PAUSED) {
                    resume.fire();
                } else if (rightManager.getGameState() == GameManager.GameState.RUNNING) {
                    pause.fire();
                }
            } else if (e.getCode() == KeyCode.UP) {
                // if on mainPause pane
                if (pauseState == 1) {
                    pauseCnt = 1 - pauseCnt;
                    updateCnt();
                }
                // if on backChoice pane
                else if (pauseState == 2) {
                    homeCnt = 1 - homeCnt;
                    updateCnt();
                }
            } else if (e.getCode() == KeyCode.DOWN) {
                // if on mainPause pane
                if (pauseState == 1) {
                    pauseCnt = 1 - pauseCnt;
                    updateCnt();
                }
                // if on backChoice pane
                else if (pauseState == 2) {
                    homeCnt = 1 - homeCnt;
                    updateCnt();
                }
            } else if (e.getCode() == KeyCode.ENTER) {
                // if on mainPause pane
                if (pauseState == 1) {
                    pauseButtons.get(pauseCnt).fire();
                }
                // if on backChoice pane
                else if (pauseState == 2) {
                    homeButtons.get(homeCnt).fire();
                }
            } else {
                leftInputHandler.onKeyPressed(e.getCode());
                rightInputHandler.onKeyPressed(e.getCode());
            }
        });

        scene.setOnKeyReleased(e -> {
            leftInputHandler.onKeyReleased(e.getCode());
            rightInputHandler.onKeyReleased(e.getCode());
        });

        Pane leftGame = leftManager.getRoot();
        leftGame.setClip(new Rectangle(GameScreen.GAME_WIDTH, GameScreen.GAME_HEIGHT));
        leftGame.getTransforms().add(new Translate(30, GameScreen.GAME_START_Y));

        Pane rightGame = rightManager.getRoot();
        rightGame.setClip(new Rectangle(GameScreen.GAME_WIDTH, GameScreen.GAME_HEIGHT));
        rightGame.getTransforms().add(new Translate(Main.WINDOW_WIDTH - GameScreen.GAME_WIDTH - 30, GameScreen.GAME_START_Y));

        StackPane gamePane = new StackPane();
        gamePane.getChildren().addAll(leftGame, rightGame, gamePlayScreen);
        root.getChildren().addAll(gamePane, mainPause, backChoice, winPane);

        Background bg = new Background(new BackgroundImage(
                ResourcesLoader.loadImage("gamescreen_bg.png"),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(1.0, 1.0, true, true, false, true)
        ));
        root.setBackground(bg);
    }

    @Override
    public void onStart() {
        // stop any playing music
        AudioManager.getInstance().stop();

        Platform.runLater(this::updateCnt);
        UIUtils.setCenterText(title);
        UIUtils.setCenterText(title1);

        MapLoader.LevelData lv = generateRandomLevel();
        leftManager.setCurrentLvl(lv);
        rightManager.setCurrentLvl(lv);

        //Turn on game screen
        mainPause.setVisible(false);
        backChoice.setVisible(false);
        winPane.setVisible(false);

        past = -1;
        loop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (past < 0) {
                    past = now;
                    return;
                }
                double dt = (now - past) / 1_000_000_000.0;
                past = now;
                leftManager.update(dt);
                rightManager.update(dt);
                leftScore.setText("Score: " + leftManager.getScore());
                rightScore.setText("Score: " + rightManager.getScore());
            }
        };

        switch (leftManager.getGameState()) {
            case GAME_OVER -> leftManager.initGame();
            case PAUSED -> leftManager.setGameState(GameManager.GameState.RUNNING);
        }

        switch (rightManager.getGameState()) {
            case GAME_OVER -> rightManager.initGame();
            case PAUSED -> rightManager.setGameState(GameManager.GameState.RUNNING);
        }

        Platform.runLater(root::requestFocus);
        loop.start();

        this.leftInputHandler = new InputHandler(this.leftManager,
                SettingScreen.sharedConfig.getFirstLeftKey(),
                SettingScreen.sharedConfig.getFirstRightKey());
        this.rightInputHandler = new InputHandler(this.rightManager,
                SettingScreen.sharedConfig.getSecondLeftKey(),
                SettingScreen.sharedConfig.getSecondRightKey());
        leftInputHandler.start();
        rightInputHandler.start();
    }

    /**
     * Stops the game loop when this screen is no longer active.
     * <p>
     * This halts per-frame updates by calling {@link AnimationTimer#stop()} on the loop.
     * </p>
     */
    @Override
    public void onStop() {
        if (loop != null) {
            loop.stop();
        }
        if (leftInputHandler != null) {
            leftInputHandler.stop();
        }
        if (rightInputHandler != null) {
            rightInputHandler.stop();
        }
    }

    private void onPlayerWin(int winPlayer) {
        loop.stop();

        winPane.setVisible(true);
        Text win = (Text) winPane.getChildren().getFirst();
        win.setText("Player " + winPlayer + " wins!");
        win.setX((double) Main.WINDOW_WIDTH / 2 - win.getBoundsInLocal().getWidth() / 2);
        win.setY((double) Main.WINDOW_HEIGHT / 2 - win.getBoundsInLocal().getHeight() / 2);

        AudioManager.getInstance().stop();
        AudioManager.getInstance().playSFX(AudioManager.GAME_OVER_SFX);
    }

    private MapLoader.LevelData generateRandomLevel() {
        Map<String, String> legends = new HashMap<>();
        legends.put("0", "empty");
        legends.put("1", "normal");

        List<String> bricks = new ArrayList<>();
        List<String> colors = new ArrayList<>();
        for (int i = 0; i < new Random().nextInt(7) + 4; i++) {
            StringBuilder layout = new StringBuilder();
            StringBuilder color = new StringBuilder();
            for (int j = 0; j < 4; j++) {
                double rand = Math.random();
                layout.append( rand > 0.25 ? 1 : 0);
                color.append(1);
            }
            bricks.add(layout.toString());
            colors.add(color.toString());
        }

        List<MapLoader.BossData> bosses = new ArrayList<>();
        bosses.add(
                new MapLoader.BossData(
                        "pyramid",
                        200,
                        0
                )
        );
        return new MapLoader.LevelData(
                0,
                "random_map",
                legends,
                bricks,
                bosses,
                colors
        );
    }

    public void resume() {
        past = -1;
        Platform.runLater(root::requestFocus);
        loop.start();
        leftManager.setGameState(GameManager.GameState.RUNNING);
        rightManager.setGameState(GameManager.GameState.RUNNING);
        mainPause.setVisible(false);
        backChoice.setVisible(false);
        pauseState = 0;
    }

    public void pause() {
        loop.stop();
        leftManager.setGameState(GameManager.GameState.PAUSED);
        rightManager.setGameState(GameManager.GameState.PAUSED);
        mainPause.setVisible(true);
        backChoice.setVisible(false);
        pauseState = 1;
    }

    /**
     * update arrow buttons when change cnt.
     */
    private void updateCnt() {
        System.out.println(pauseCnt + " " + homeCnt);
        double gap = 60 + Math.max(pauseButtons.get(pauseCnt).getWidth() - 60, 0) / 2;
        double arrowY = pauseButtonYs.get(pauseCnt) + pauseButtons.get(pauseCnt).getHeight() / 2;
        double arrowLeftX = pauseButtons.get(pauseCnt).getLayoutX() - gap
                - pauseChooseArrowLeft.getLayoutBounds().getWidth();
        double arrowRightX = pauseButtons.get(pauseCnt).getLayoutX() + gap
                + pauseButtons.get(pauseCnt).getWidth();
        pauseChooseArrowLeft.setY(arrowY);
        pauseChooseArrowLeft.setX(arrowLeftX);
        pauseChooseArrowRight.setY(arrowY);
        pauseChooseArrowRight.setX(arrowRightX);
        gap = 60 + Math.max(homeButtons.get(homeCnt).getWidth() - 60, 0) / 2;
        arrowY = homeButtonYs.get(homeCnt) + homeButtons.get(homeCnt).getHeight() / 2;
        arrowLeftX = homeButtons.get(homeCnt).getLayoutX() - gap
                - homeChooseArrowLeft.getLayoutBounds().getWidth();
        arrowRightX = homeButtons.get(homeCnt).getLayoutX() + gap
                + homeButtons.get(homeCnt).getWidth();
        homeChooseArrowLeft.setY(arrowY);
        homeChooseArrowLeft.setX(arrowLeftX);
        homeChooseArrowRight.setY(arrowY);
        homeChooseArrowRight.setX(arrowRightX);
    }

    /**
     * get button index of buttonList.
     * @param button Button
     * @return button's index
     */
    private int getIndex(Button button) {
        int i = 0;
        for (Button b : pauseButtons) {
            if (b.equals(button)) return i;
            i++;
        }
        i = 0;
        for (Button b : homeButtons) {
            if (b.equals(button)) return i;
            i++;
        }
        return -1;
    }

}
