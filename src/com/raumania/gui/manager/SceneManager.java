package com.raumania.gui.manager;

import com.raumania.gui.screen.*;
import javafx.stage.Stage;

import java.util.EnumMap;

/**
 * Manages switching screens in the application.
 */
public class SceneManager {
    private final Stage primaryStage;
    // Constructor requires key type
    private EnumMap<ScreenType, Screen> screens = new EnumMap<>(ScreenType.class);
    private Screen currentScreen;

    public SceneManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
        screens.put(ScreenType.HOME, new HomeScreen(this));
        screens.put(ScreenType.SETTINGS, new SettingScreen(this));
        screens.put(ScreenType.LEVEL_SELECT, new LevelSelectScreen(this));
        screens.put(ScreenType.GAME, new GameScreen(this));
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Switch to the specified screen.
     * Handle the screens by calling onStop and onStart methods (if implemented).
     *
     * @param screenType The type of screen to switch to.
     */
    public void switchScreen(ScreenType screenType) {
        if (currentScreen != null) {
            currentScreen.onStop();
        }
        currentScreen = screens.get(screenType);
        currentScreen.onStart();
        primaryStage.setScene(currentScreen.getScene());
    }
}
