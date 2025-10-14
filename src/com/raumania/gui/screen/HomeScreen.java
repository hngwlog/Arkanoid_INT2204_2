package com.raumania.gui.screen;

import com.raumania.core.AudioManager;
import com.raumania.core.SpriteSheet;
import com.raumania.gui.manager.SceneManager;
import com.raumania.utils.ResourcesLoader;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.control.Button;

import static com.raumania.utils.UIUtils.centerButton;
import static com.raumania.utils.UIUtils.centerText;

public class HomeScreen extends Screen {
    public HomeScreen(SceneManager sceneManager) {
        super(sceneManager);

        //Game title
        // put components here
        Text title = centerText("Welcome to Arkanoid!", 100, 3.0, 3.0);
        // style components
        title.setFill(Color.GREEN);

        //Play button
        Button play =  centerButton("Play", 200, 2.0, 2.0);
        play.setOnAction(e -> {
            sceneManager.switchScreen(ScreenType.GAME);
        });

        //Level select button
        Button levelSelect =  centerButton("Level Select", 300, 2.0, 2.0);
        levelSelect.setOnAction(e -> {
            sceneManager.switchScreen(ScreenType.LEVEL_SELECT);
        });

        //Setting button
        Button setting = centerButton("Setting", 400, 2.0, 2.0);
        setting.setOnAction(e -> {
            sceneManager.switchScreen(ScreenType.SETTINGS);
        });

        //Quit button
        Button quit = centerButton("Quit", 500, 2.0, 2.0);
        quit.setOnAction(e -> {
            Platform.exit();
        });

        root.getChildren().addAll(title, play, levelSelect, setting, quit);
    }

    @Override
    public void onStart() {
        AudioManager.getInstance().playBGMusic(AudioManager.HOME_MUSIC);
    }
}
