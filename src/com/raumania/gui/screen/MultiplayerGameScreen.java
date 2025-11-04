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
    private GameManager leftManager;
    private InputHandler leftInputHandler;
    private GameManager rightManager;
    private InputHandler rightInputHandler;
    private AnimationTimer loop;
    private long past = - 1;
    Button pause;
    Pane gamePlayScreen;
    Pane mainPause;
    Pane backChoice;
    StackPane gamePane;
    Pane winPane;
    Text rightScore;
    Text leftScore;

    public MultiplayerGameScreen(SceneManager sceneManager) {
        super(sceneManager);

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
        gamePlayScreen = new Pane();
        //Pause button
        pause = UIUtils.newButton("||", 940, 20, 2.0, 2.0);
        pause.setOnAction(e -> {
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
        mainPause = new Pane();
        // mainPause.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);"); // Add translucent black background
        mainPause.setBackground(
                new Background(new BackgroundFill(new Color(0.0, 0.0, 0.0, 0.7), CornerRadii.EMPTY, Insets.EMPTY))
        );
        mainPause.setPrefSize(Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
        //Pause Title
        Text title = UIUtils.centerText("Pause", 100, 3.0, 3.0);
        title.setFill(Color.GREEN);
        //Resume button
        Button resume = UIUtils.centerButton("Resume", 200, 2.0, 2.0);
        resume.setOnAction(e -> {
            this.resume();
        });
        //Home button
        Button home = UIUtils.centerButton("Back to Home", 300, 2.0, 2.0);
        home.setOnAction(e -> {
            mainPause.setVisible(false);
            backChoice.setVisible(true);
        });
        mainPause.getChildren().addAll(title, resume, home);
        mainPause.setVisible(false);

        // Confirmation Screen
        backChoice = new Pane();
        // backChoice.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);"); // Add translucent black background
        backChoice.setBackground(
                new Background(new BackgroundFill(new Color(0.0, 0.0, 0.0, 0.7), CornerRadii.EMPTY, Insets.EMPTY))
        );
        backChoice.setPrefSize(Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
        // Title
        Text title1 = UIUtils.centerText("Are you sure", 100, 3.0, 3.0);
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
            mainPause.setVisible(true);
            backChoice.setVisible(false);
        });
        backChoice.getChildren().addAll(title1, yes, no);
        backChoice.setVisible(false);

        // key event
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                if (leftManager.getGameState() == GameManager.GameState.PAUSED) {
                    resume.fire();
                } else if (leftManager.getGameState() == GameManager.GameState.RUNNING) {
                    pause.fire();
                }
            } else if (e.getCode() == KeyCode.SPACE) {
                if (leftManager.getGameState() == GameManager.GameState.PAUSED) {
                    if (backChoice.isVisible()) {
                        // If in back choice screen, treat SPACE as "No" button
                        no.fire();
                    } else {
                        // If in main pause screen, resume the game
                        resume.fire();
                    }
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

        gamePane = new StackPane();
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
        for (int i = 0; i < new Random().nextInt(3) + 4; i++) {
            StringBuilder layout = new StringBuilder();
            for (int j = 0; j < 4; j++) {
                layout.append(new Random().nextInt(2));
            }
            bricks.add(layout.toString());
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
                bosses
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
    }

    public void pause() {
        loop.stop();
        leftManager.setGameState(GameManager.GameState.PAUSED);
        rightManager.setGameState(GameManager.GameState.PAUSED);
        mainPause.setVisible(true);
        backChoice.setVisible(false);
    }
}
