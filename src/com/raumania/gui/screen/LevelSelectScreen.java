package com.raumania.gui.screen;

import com.raumania.gui.manager.SceneManager;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;

import static com.raumania.utils.UIUtils.centerButton;
import static com.raumania.utils.UIUtils.newButton;


public class LevelSelectScreen extends Screen {

    public int currentLevel = 0;
    private final int maxLevels = 5;

    public LevelSelectScreen(SceneManager sceneManager) {
        super(sceneManager);
        // put components here
        //left button
        Button left = newButton("<" , 400, 550, 2.0, 2.0);
        root.getChildren().add(left);

        //right button
        Button right = newButton(">" , 575, 550, 2.0, 2.0);
        root.getChildren().add(right);

        //level button
        //press to select level & back to home
        Button level = centerButton("Level " + (currentLevel+1) , 550, 2.0, 2.0);
        level.setOnAction(e -> {sceneManager.switchScreen(ScreenType.HOME);});
        root.getChildren().add(level);

        //press left button -> level--
        left.setOnAction(e -> {
            if (currentLevel > 0) {
                currentLevel--;
            }
            else {
                currentLevel = maxLevels-1;
            }
            updateLevel(level);
        });

        //press right button -> level++
        right.setOnAction(e -> {
            if (currentLevel < maxLevels) {
                currentLevel++;
                currentLevel %= maxLevels;
                updateLevel(level);
            }
        });

        //arrow key -> left, right , enter -> chose level
        Platform.runLater(root::requestFocus);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case RIGHT:
                    right.fire();
                    break;
                case LEFT:
                    left.fire();
                    break;
                case ENTER:
                    level.fire();
                    break;
            }
        });

    }

    /**
     * Get selected level.
     * @return current level
     */
    public int getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Update screen when change level.
     * @param level button to show level
     */
    private void updateLevel(Button level) {
        level.setText("Level " + (currentLevel+1));
    }

}
