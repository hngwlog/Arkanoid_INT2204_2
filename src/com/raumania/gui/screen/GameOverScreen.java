package com.raumania.gui.screen;

import com.raumania.core.HighScore;
import com.raumania.core.HighScore.HighScoreEntry;
import com.raumania.gameplay.manager.GameManager;
import com.raumania.gui.manager.SceneManager;
import com.raumania.utils.UIUtils;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class GameOverScreen extends Screen {
    private final GameManager singlePlayerGameManager;
    private final ArrayList<Text> currentTexts;
    private final Pane highScoreInputPane;
    private final Pane gameOverPane;
    private final Text level;

    public GameOverScreen(SceneManager sceneManager) {
        super(sceneManager);
        this.singlePlayerGameManager = ((GameScreen) sceneManager.getScreen(ScreenType.GAME)).getGameManager();

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
        level = UIUtils.centerText("Level 0", 150, 1.5, 1.5);
        Button backToMenu = UIUtils.centerButton("Back to Home", 500, 2.0, 2.0);
        backToMenu.setOnAction(e -> {
            sceneManager.switchScreen(ScreenType.HOME);
        });
        gameOverPane.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                backToMenu.fire();
            }
        });

        gameOverPane.getChildren().addAll(title, level, backToMenu);

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
            HighScore.getInstance().addHighScore(singlePlayerGameManager.getCurrentLvl().name(),
                    name,
                    singlePlayerGameManager.getScore());
            updateHighScoreList(singlePlayerGameManager.getCurrentLvl().name());
            highScoreInputPane.setVisible(false);
            gameOverPane.setVisible(true);
        });
        highScoreInputPane.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                submit.fire();
            }
        });

        highScoreInputPane.getChildren().addAll(congrats, prompt, nameInput, submit);

        root.getChildren().addAll(gameOverPane, highScoreInputPane);
    }

    public void updateHighScoreList(String levelName) {
        List<HighScoreEntry> highScores = HighScore.getInstance().getEntries().stream()
            .filter(e -> e.getLevel().equals(levelName))
            .toList();
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
        Platform.runLater(root::requestFocus);
        List<HighScoreEntry> highScoreOfLevel = HighScore.getInstance().getEntries().stream()
                .filter(e -> e.getLevel().equals(singlePlayerGameManager.getCurrentLvl().name()))
                .toList();
        int minScoreOfLevel = highScoreOfLevel.stream().mapToInt(HighScoreEntry::getScore).min().orElse(0);

        if (singlePlayerGameManager.getScore() > minScoreOfLevel || highScoreOfLevel.size() < HighScore.MAX_ENTRIES) {
            highScoreInputPane.setVisible(true);
            gameOverPane.setVisible(false);
        } else {
            updateHighScoreList(singlePlayerGameManager.getCurrentLvl().name());
            highScoreInputPane.setVisible(false);
            gameOverPane.setVisible(true);
        }

        level.setText(singlePlayerGameManager.getCurrentLvl().name());
    }
}
