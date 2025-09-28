package com.raumania.gui.screen;

import com.raumania.gui.manager.SceneManager;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class HomeScreen extends Screen {
    public HomeScreen(SceneManager sceneManager) {
        super(sceneManager);
        // put components here
        Text title = new Text("Welcome to Arkanoid!"); // create components
        // style components
        title.setX(300);
        title.setY(100);
        title.setScaleY(3.0);
        title.setScaleX(3.0);
        title.setFill(Color.GREEN);
        // add components to root
        root.getChildren().add(title);
    }
}
