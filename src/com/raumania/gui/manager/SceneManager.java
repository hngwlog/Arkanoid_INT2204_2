package com.raumania.gui.manager;

import com.raumania.gui.screen.*;
import com.raumania.utils.ResourcesLoader;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Labeled;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.EnumMap;

/** Manages switching screens in the application. */
public class SceneManager {

    private final Stage primaryStage;
    // Constructor requires key type
    private final EnumMap<ScreenType, Screen> screens = new EnumMap<>(ScreenType.class);
    private Screen currentScreen;

    public SceneManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
        screens.put(ScreenType.HOME, new HomeScreen(this));
        screens.put(ScreenType.SETTINGS, new SettingScreen(this));
        screens.put(ScreenType.LEVEL_SELECT, new LevelSelectScreen(this));
        screens.put(ScreenType.GAME, new GameScreen(this));
        screens.put(ScreenType.MULTIPLAYER, new MultiplayerGameScreen(this));
        screens.put(ScreenType.GAME_OVER, new GameOverScreen(this));
        screens.put(ScreenType.SKIN_SELECT, new SkinSelectScreen(this));

        // apply font for all screens
        screens.forEach(
                (type, screen) -> {
                    Font font = ResourcesLoader.loadFont("CyberpunkCraftpixPixel.otf", 14);
                    applyFont(screen.getRoot(), font);
                });
    }

    public Screen getScreen(ScreenType screenType) {
        return screens.get(screenType);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Switch to the specified screen. Handle the screens by calling onStop and onStart methods (if
     * implemented).
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

    private void applyFont(Parent root, Font font) {
        for (Node node : root.getChildrenUnmodifiable()) {
            if (node instanceof Labeled labeled) {
                labeled.setFont(font);
            } else if (node instanceof Text text) {
                text.setFont(font);
            } else if (node instanceof Parent parent) {
                applyFont(parent, font);
            }
        }
    }
}
