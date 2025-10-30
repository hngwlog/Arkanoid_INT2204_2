package com.raumania.main;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    public static final int WINDOW_WIDTH = 1000;
    public static final int WINDOW_HEIGHT = 720;
    public static final String WINDOW_TITLE = "Arkanoid";

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(WINDOW_TITLE);
        primaryStage.setWidth(WINDOW_WIDTH);
        primaryStage.setHeight(WINDOW_HEIGHT);
        primaryStage.setResizable(false);

        Game game = new Game(primaryStage);
        game.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
