package com.raumania.main;

import com.raumania.gui.manager.SceneManager;
import com.raumania.gui.screen.ScreenType;
import javafx.stage.Stage;

/**
 * Main game class that initializes and starts the game.
 */
public class Game {
    private final SceneManager sceneManager;
    private final Stage primaryStage;

    public Game(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.sceneManager = new SceneManager(primaryStage);
    }

    /**
     * Start the home screen.
     */
    public void start() {
        sceneManager.switchScreen(ScreenType.HOME);
        primaryStage.show();
    }
}
