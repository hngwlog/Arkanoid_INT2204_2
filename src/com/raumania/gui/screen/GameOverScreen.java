package com.raumania.gui.screen;

import com.raumania.core.HighScore;
import com.raumania.core.HighScore.HighScoreEntry;
import com.raumania.gui.manager.SceneManager;
import com.raumania.utils.UIUtils;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class GameOverScreen extends Screen {
    private ArrayList<Text> currentTexts;

    public GameOverScreen(SceneManager sceneManager) {
        super(sceneManager);

        // initialize text placeholders
        currentTexts = new ArrayList<>(HighScore.MAX_ENTRIES);
        for (int i = 0; i < HighScore.MAX_ENTRIES; i++) {
            Text text = UIUtils.centerText("", 200 + i * 30, 2.0, 2.0);
            text.setVisible(false);
            currentTexts.add(text);
            root.getChildren().add(text);
        }

        // put components here
        Text title = UIUtils.centerText("HighScore", 100, 3.0, 3.0);
        root.getChildren().add(title);

        Button backToMenu = UIUtils.centerButton("Back to Home", 500, 2.0, 2.0);
        backToMenu.setOnAction(e -> {
            sceneManager.switchScreen(ScreenType.HOME);
        });
        root.getChildren().add(backToMenu);
    }

    @Override
    public void onStart() {
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
}
