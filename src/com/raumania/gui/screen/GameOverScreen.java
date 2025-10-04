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
        currentTexts = new ArrayList<>();

        // put components here
        Button backToMenu = UIUtils.centerButton("Back to Home", 500, 2.0, 2.0);
        backToMenu.setOnAction(e -> {
            sceneManager.switchScreen(ScreenType.HOME);
        });
        root.getChildren().add(backToMenu);

        Text title = UIUtils.centerText("HighScore", 100, 3.0, 3.0);
        root.getChildren().add(title);
    }

    @Override
    public void onStart() {
        // this is dynamic so put it in Constructor wont update the list
        // clear previous texts
        for (Text text : currentTexts) {
            root.getChildren().remove(text);
        }
        currentTexts.clear();

        ArrayList<HighScoreEntry> entries = HighScore.getInstance().getEntries();
        for (HighScoreEntry entry : entries) {
            Text entryText = UIUtils.centerText((entries.indexOf(entry) + 1) + ". " + entry.getName() + ": " + entry.getScore(), 150 + entries.indexOf(entry) * 30, 2.0, 2.0);
            root.getChildren().add(entryText);
            currentTexts.add(entryText);
        }
    }
}
