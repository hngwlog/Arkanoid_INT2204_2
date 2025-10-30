package com.raumania.gui.screen;

import com.raumania.core.HighScore;
import com.raumania.core.HighScore.HighScoreEntry;
import com.raumania.gui.manager.SceneManager;
import com.raumania.utils.UIUtils;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class GameOverScreen extends Screen {
    private ArrayList<Text> currentTexts;
    private Pane highScoreInputPane;
    private Pane gameOverPane;

    public GameOverScreen(SceneManager sceneManager) {
        super(sceneManager);

        // put components here
        // initialize text placeholders
        gameOverPane = new Pane();
        currentTexts = new ArrayList<>(HighScore.MAX_ENTRIES);
        for (int i = 0; i < HighScore.MAX_ENTRIES; i++) {
            Text text = UIUtils.centerText("", 200 + i * 30, 2.0, 2.0);
            text.setVisible(false);
            currentTexts.add(text);
            gameOverPane.getChildren().add(text);
        }
        Text title = UIUtils.centerText("HighScore", 100, 3.0, 3.0);
        Button backToMenu = UIUtils.centerButton("Back to Home", 500, 2.0, 2.0);
        backToMenu.setOnAction(e -> {
            sceneManager.switchScreen(ScreenType.HOME);
        });
        gameOverPane.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case ENTER:
                    backToMenu.fire();
                    break;
            }
        });

        gameOverPane.getChildren().addAll(title, backToMenu);

        // if there is an unsaved score, show input pane
        highScoreInputPane = new Pane();
        Text congrats = UIUtils.centerText("New High Score!", 100, 3.0, 3.0);
        Text prompt = UIUtils.centerText("Enter your name:", 200, 2.0, 2.0);
        // center the input field
        TextField nameInput = new TextField();
        nameInput.setLayoutX(425);
        nameInput.setLayoutY(300);
        Button submit = UIUtils.centerButton("Submit", 500, 2.0, 2.0);
        submit.setOnAction(e -> {
            String name = nameInput.getText().trim();
            if (name.isEmpty()) {
                name = "Unknown"; // default name if none provided
            }
            HighScore.getInstance().addHighScore(name);
            updateHighScoreList();
            highScoreInputPane.setVisible(false);
            gameOverPane.setVisible(true);
        });
        highScoreInputPane.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case ENTER:
                    submit.fire();
                    break;
            }
        });

        highScoreInputPane.getChildren().addAll(congrats, prompt, nameInput, submit);

        root.getChildren().addAll(gameOverPane, highScoreInputPane);
    }

    public void updateHighScoreList() {
        ArrayList<HighScoreEntry> highScores = HighScore.getInstance().getEntries();
        for (int i = 0; i < HighScore.MAX_ENTRIES; i++) {
            if (i < highScores.size()) {
                HighScoreEntry e = highScores.get(i);
                currentTexts.get(i).setText((i + 1) + ". " + e.getName() + " - " + e.getScore());
                currentTexts.get(i).setVisible(true);
            } else {
                currentTexts.get(i).setVisible(false);
            }
        }
    }

    @Override
    public void onStart() {
        if (HighScore.getInstance().hasUnsavedScore()) {
            highScoreInputPane.setVisible(true);
            gameOverPane.setVisible(false);
        } else {
            updateHighScoreList();
            highScoreInputPane.setVisible(false);
            gameOverPane.setVisible(true);
        }
    }
}
