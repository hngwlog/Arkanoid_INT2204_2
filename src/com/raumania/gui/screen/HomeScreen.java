package com.raumania.gui.screen;

import com.raumania.gui.manager.SceneManager;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.control.Button;

import static com.raumania.utils.Constants.*;
import static com.raumania.utils.UIUtils.*;

public class HomeScreen extends Screen {
    public HomeScreen(SceneManager sceneManager) {
        super(sceneManager);

        //Game title
        root.getChildren().clear();
        // put components here
        Text title = centerText("Welcome to Arkanoid!", 100, 3.0, 3.0);
        // style components
        title.setFill(Color.GREEN);
        // add components to root
        root.getChildren().add(title);

        //Play button
        Button play =  centerButton("Play", 200, 2.0, 2.0);
        play.setOnAction(e -> {System.out.println("Play");});
        root.getChildren().add(play);

        //Level select button
        Button levelSelect =  centerButton("Level Select", 300, 2.0, 2.0);
        levelSelect.setOnAction(e -> {
            sceneManager.switchScreen(ScreenType.LEVEL_SELECT);
        });
        root.getChildren().add(levelSelect);

        //Setting button
        Button setting = centerButton("Setting", 400, 2.0, 2.0);
        setting.setOnAction(e -> {
            sceneManager.switchScreen(ScreenType.SETTINGS);
        });
        root.getChildren().add(setting);

        //Quit button
        Button quit = centerButton("Quit", 500, 2.0, 2.0);
        quit.setOnAction(e -> {
            Platform.exit();
        });
        root.getChildren().add(quit);

    }


}
