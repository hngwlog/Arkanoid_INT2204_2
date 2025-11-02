package com.raumania.gui.screen;

import com.raumania.core.AudioManager;
import com.raumania.core.HighScore;
import com.raumania.main.Main;
import com.raumania.utils.ResourcesLoader;
import com.raumania.utils.UIUtils;
import javafx.animation.AnimationTimer;

import com.raumania.gameplay.manager.*;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.geometry.Insets;
import java.util.Iterator;
/**
 * The game play screen that hosts the main game loop.
 * <p>
 * This screen owns a {@link GameManager} and a frame-based loop implemented via
 * {@link AnimationTimer}. When the screen starts, it initializes game objects and
 * begins ticking the game manager every frame with a computed delta time.
 * </p>
 */
public class GameScreen extends Screen {
    public static final int GAME_WIDTH = 400;
    public static final int GAME_START_X = (Main.WINDOW_WIDTH - GAME_WIDTH) / 2;
    public static final int GAME_HEIGHT = 620;
    public static final int GAME_START_Y = (Main.WINDOW_HEIGHT - GAME_HEIGHT) / 2 + 10;

    private final GameManager manager;
    private InputHandler inputHandler;
    private AnimationTimer loop;
    private long past = - 1;
    private final Button pause;
    private Pane mainPause;
    private Pane backChoice;
    private Pane timeRemainings;
    private StackPane gamePane;
    private final Text score;
    private final Text fps;
    private int pauseState = 0;
    private int pauseCnt = 0;
    private int homeCnt = 0;
    private final List<Button> pauseButtons;
    private final List<Button> homeButtons;
    private final List<Double> pauseButtonYs;
    private final List<Double> homeButtonYs;
    private final Text pauseChooseArrowLeft;
    private final Text pauseChooseArrowRight;
    private final Text homeChooseArrowLeft;
    private final Text homeChooseArrowRight;
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
        this.inputHandler = new InputHandler(manager, KeyCode.A, KeyCode.D);
        // handle game over state
        this.manager.gameStateProperty().addListener((obs, oldState, newState) -> {
            if (newState == GameManager.GameState.GAME_OVER) {
                loop.stop();
                inputHandler.stop();
                // Pause for 2 seconds before switching screens
                PauseTransition pause = new PauseTransition();
                pause.setDuration(Duration.seconds(2));
                pause.setOnFinished(e -> sceneManager.switchScreen(ScreenType.GAME_OVER));
                AudioManager.getInstance().stop();
                AudioManager.getInstance().playSFX(AudioManager.GAME_OVER_SFX);
                pause.play();
            }
        });

        //Game play screen
        Pane gamePlayScreen = new Pane();
        //Pause button
        pause = UIUtils.newButton("||", 940, 20, 2.0, 2.0);
        pause.setOnAction(e -> {
            this.pause();
        });
        //Game border
        Rectangle border = UIUtils.newRectangle(GAME_WIDTH, GAME_HEIGHT,
                GAME_START_X, GAME_START_Y);
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

        timeRemainings = new Pane();
        timeRemainings.setLayoutX(80);
        timeRemainings.setLayoutY(80);
        gamePlayScreen.getChildren().add(timeRemainings);

        gamePlayScreen.getChildren().addAll(pause, border, score, fps);
        gamePlayScreen.setVisible(true);

        //Pause screen
        mainPause = new Pane();
        //mainPause.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);"); // Add translucent black background
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
            pauseState = 2;
            pauseCnt = 0;
            updateCnt();
            Platform.runLater(root::requestFocus);
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
            manager.initGame();
            manager.setGameState(GameManager.GameState.PAUSED);
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
        homeChooseArrowRight = UIUtils.newText("<" , 610.76, 212.5, 2.0, 2.0);
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
            if (e.getCode() == KeyCode.ESCAPE) {
                if (manager.getGameState() == GameManager.GameState.PAUSED) {
                    resume.fire();
                } else if (manager.getGameState() == GameManager.GameState.RUNNING) {
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
            }
            // game manager handle input
            else {
                inputHandler.onKeyPressed(e.getCode());
            }
        });

        scene.setOnKeyReleased(e -> inputHandler.onKeyReleased(e.getCode()));
        Pane game = manager.getRoot();
        game.setClip(new Rectangle(GAME_WIDTH, GAME_HEIGHT));
        game.getTransforms().add(new Translate(GAME_START_X, GAME_START_Y));

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

        //change player's key
        KeyCode leftKey = SettingScreen.sharedConfig.getFirstLeftKey();
        KeyCode rightKey = SettingScreen.sharedConfig.getFirstRightKey();
        inputHandler = new InputHandler(manager, leftKey, rightKey);

        //Turn on game screen
        mainPause.setVisible(false);
        backChoice.setVisible(false);
        gamePane.setVisible(true);
        pauseState = 0;

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
                showTime();
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
        inputHandler.start();
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
        if (inputHandler != null) {
            inputHandler.stop();
        }
    }

    /**
     * pause -> resume.
     * set gameState, unfocus, start loop,set visible
     */
    public void resume() {
        past = -1;
        Platform.runLater(root::requestFocus);
        loop.start();
        manager.setGameState(GameManager.GameState.RUNNING);
        mainPause.setVisible(false);
        backChoice.setVisible(false);
        gamePane.setVisible(true);
        pauseState = 0;
    }

    /**
     * pause.
     * set gameState, unfocus, stop loop, set visible
     */
    public void pause() {
        loop.stop();
        manager.setGameState(GameManager.GameState.PAUSED);
        mainPause.setVisible(true);
        backChoice.setVisible(false);
        gamePane.setVisible(false);
        pauseState = 1;
        Platform.runLater(root::requestFocus);
    }

    /**
     * update arrow buttons when change cnt.
     */
    private void updateCnt() {
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

    private void showTime() {
        timeRemainings.getChildren().clear();
        double y = 0;
        double currentTime = System.currentTimeMillis() / 1000.0;

        for (Iterator<EffectCountDown> iterator = manager.getEffectCountDownList().iterator(); iterator.hasNext();) {
            EffectCountDown effectCountDown = iterator.next();
            double timeRemaining = effectCountDown.getTimeRemaining(currentTime);

            // Nếu hết thời gian thì xóa
            if (timeRemaining <= 0) {
                iterator.remove();
                continue;
            }

            Text text = new Text();
            text.setFont(Font.font("System", FontWeight.BOLD, 14));
            // if time remaining is less than 1.5s, apply blink effect
            if (timeRemaining <= 1.5) {
                long millis = System.currentTimeMillis();

                // frequency = 4Hz
                boolean blink = (millis / 250) % 2 == 0;

                if (blink)
                    text.setFill(Color.RED);
                else
                    text.setFill(Color.WHITE);
            } else {
                text.setFill(Color.WHITE);
            }

            text.setText(effectCountDown.getEffectType() + ": " + String.format("%.1f", timeRemaining) + "s");
            text.setLayoutX(0);
            text.setLayoutY(y + 20);
            y += 20;

            timeRemainings.getChildren().add(text);
        }
    }
}
