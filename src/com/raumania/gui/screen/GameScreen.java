package com.raumania.gui.screen;

import com.raumania.core.AudioManager;
import com.raumania.core.HighScore;
import com.raumania.utils.ResourcesLoader;
import com.raumania.utils.UIUtils;
import com.raumania.utils.Constants;
import javafx.animation.AnimationTimer;

import com.raumania.gameplay.manager.GameManager;
import com.raumania.gui.manager.SceneManager;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Translate;
import javafx.util.Duration;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.awt.*;

/**
 * The game play screen that hosts the main game loop.
 * <p>
 * This screen owns a {@link GameManager} and a frame-based loop implemented via
 * {@link AnimationTimer}. When the screen starts, it initializes game objects and
 * begins ticking the game manager every frame with a computed delta time.
 * </p>
 */
public class GameScreen extends Screen {
    private GameManager manager;
    private AnimationTimer loop;
    private long past = - 1;
    Button pause;
    Pane gamePlayScreen;
    Pane mainPause;
    Pane backChoice;
    StackPane gamePane;
    Text score;
    Text fps;

    /**
     * Creates a new {@code GameScreen} and binds it to the given {@link SceneManager}.
     * <p>
     * The constructor also instantiates a {@link GameManager} using this screen's root pane for rendering.
     * </p>
     *
     * @param sceneManager controller responsible for switching between application screens
     */
    public GameScreen(SceneManager sceneManager) {
        super(sceneManager);
        this.manager = new GameManager();
        // handle game over state
        this.manager.gameStateProperty().addListener((obs, oldState, newState) -> {
            if (newState == GameManager.GameState.GAME_OVER) {
                loop.stop();
                HighScore.getInstance().setUnsavedScore(manager.getScore());
                // Pause for 2 seconds before switching screens
                PauseTransition pause = new PauseTransition();
                pause.setDuration(Duration.seconds(2));
                pause.setOnFinished(e -> sceneManager.switchScreen(ScreenType.GAME_OVER));
                AudioManager.getInstance().stop();
                AudioManager.getInstance().playSFX(AudioManager.GAME_OVER_SFX);
                pause.play();
            }
        });

        mainPause = new Pane();
        mainPause.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);"); // Add translucent black background
        mainPause.setPrefSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        backChoice = new Pane();
        backChoice.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);"); // Add translucent black background
        backChoice.setPrefSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        gamePlayScreen = new Pane();

        //Game play screen
        //Pause button
        pause = UIUtils.newButton("||", 940, 20, 2.0, 2.0);
        pause.setOnAction(e -> {
            this.pause();
        });
        //Game border
        Rectangle border = UIUtils.newRectangle(Constants.GAME_WIDTH, Constants.GAME_HEIGHT,
                Constants.GAME_START_X, Constants.GAME_START_Y);
        border.setFill(Color.TRANSPARENT);
        border.setStroke(Color.BLACK);
        border.setStrokeWidth(2);
        //Score
        score = UIUtils.newText("Score:", 600, 30, 2.0, 2.0);
        score.setFont(Font.font("System", FontWeight.BOLD, 14));
        score.setFill(Color.WHITE);
        //FPS
        fps = UIUtils.newText("FPS:", 350, 30, 2.0, 2.0);
        fps.setFont(Font.font("System", FontWeight.BOLD, 14));
        fps.setFill(Color.WHITE);
        gamePlayScreen.getChildren().addAll(pause, border, score, fps);
        gamePlayScreen.setVisible(true);

        //Pause screen
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
        // Title
        Text title1 = UIUtils.centerText("Are you sure", 100, 3.0, 3.0);
        title1.setFill(Color.GREEN);
        //Yes button
        //BUG : game over when play again (but still init)
        Button yes = UIUtils.centerButton("Yes", 200, 2.0, 2.0);
        yes.setOnAction(e -> {
            sceneManager.switchScreen(ScreenType.HOME);
            manager.initGame();
            manager.setGameState(GameManager.GameState.PAUSED);
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
                if (manager.getGameState() == GameManager.GameState.PAUSED) {
                    resume.fire();
                } else if (manager.getGameState() == GameManager.GameState.RUNNING) {
                    pause.fire();
                }
            } else if (e.getCode() == KeyCode.SPACE) {
                if (manager.getGameState() == GameManager.GameState.PAUSED) {
                    if (backChoice.isVisible()) {
                        // If in back choice screen, treat SPACE as "No" button
                        no.fire();
                    } else {
                        // If in main pause screen, resume the game
                        resume.fire();
                    }
                }
            } else {
                manager.handleInput(e.getCode(), true);
            }
        });

        scene.setOnKeyReleased(e -> manager.handleInput(e.getCode(), false));
        Pane game = manager.getRoot();
        game.setClip(new Rectangle(Constants.GAME_WIDTH, Constants.GAME_HEIGHT));
        game.getTransforms().add(new Translate(Constants.GAME_START_X, Constants.GAME_START_Y));

        gamePane = new StackPane();
        gamePane.getChildren().addAll(game, gamePlayScreen);
        root.getChildren().addAll(gamePane, mainPause,  backChoice);

        Background bg = new Background(new BackgroundImage(
            ResourcesLoader.loadImage("gamescreen_bg.png"),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(1.0, 1.0, true, true, false, true)
        ));
        root.setBackground(bg);
    }

    public GameManager getGameManager() {
        return manager;
    }

    /**
     * Starts the game screen lifecycle.
     * <p>
     * Re-initializes the game state via {@link GameManager#initGame()} and creates an
     * {@link AnimationTimer} that:
     * <ol>
     *   <li>Captures the current monotonic time in nanoseconds.</li>
     *   <li>Computes the frame delta time in seconds as
     *   {@code dt = (now - past) / 1_000_000_000.0}.</li>
     *   <li>Calls {@link GameManager#update(double)} with that {@code dt}.</li>
     * </ol>
     * On the very first tick, the loop only initializes {@link #past} and skips update
     * to avoid a large or undefined delta time.
     * </p>
     */
    @Override
    public void onStart() {
        // stop any playing music
        AudioManager.getInstance().stop();

        //Turn on game screen
        mainPause.setVisible(false);
        backChoice.setVisible(false);
        gamePane.setVisible(true);

        past = -1;
        loop = new AnimationTimer() {
            double lastFPSUpdate = 0;
            @Override
            public void handle(long now) {
                if (past < 0) {
                    past = now;
                    return;
                }
                double dt = (now - past) / 1_000_000_000.0;
                past = now;
                manager.update(dt);
                score.setText("Score: " + manager.getScore());
                if (now - lastFPSUpdate > 1_000_000_000.0) {
                    fps.setText("FPS: "+(int)(1.0/dt));
                    lastFPSUpdate = now;
                }
            }
        };
        switch (manager.getGameState()) {
            case GAME_OVER -> {
                manager.initGame();
            }
            case PAUSED -> manager.setGameState(GameManager.GameState.RUNNING);
        }
        Platform.runLater(root::requestFocus);
        loop.start();
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
    }

    public void resume() {
        past = -1;
        Platform.runLater(root::requestFocus);
        loop.start();
        manager.setGameState(GameManager.GameState.RUNNING);
        mainPause.setVisible(false);
        backChoice.setVisible(false);
        gamePane.setVisible(true);
    }

    public void pause() {
        loop.stop();
        manager.setGameState(GameManager.GameState.PAUSED);
        mainPause.setVisible(true);
        backChoice.setVisible(false);
        //gamePane.setVisible(false);
    }
}
