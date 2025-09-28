package com.raumania.main;

import static com.raumania.utils.Constants.*;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
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
