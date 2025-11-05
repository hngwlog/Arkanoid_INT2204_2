package com.raumania.gui.screen;

import com.raumania.core.MapLoader;
import com.raumania.gui.manager.SceneManager;
import com.raumania.utils.UIUtils;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;

import static com.raumania.utils.UIUtils.centerButton;
import static com.raumania.utils.UIUtils.newButton;


public class LevelSelectScreen extends Screen {

    public int currentLevel = 0;
    private final int maxLevels = 15;

    public LevelSelectScreen(SceneManager sceneManager) {
        super(sceneManager);
        // put components here
        //level button
        //press to select level & back to home
        Button level = centerButton("Level " + (currentLevel+1) , 550, 2.0, 2.0);
        System.out.println(level.getLayoutX());
        level.setOnAction(e -> {sceneManager.switchScreen(ScreenType.HOME);});

        //left button
        Button left = newButton("<" , 400, 550, 2.0, 2.0);

        //right button
        Button right = newButton(">" , 575, 550, 2.0, 2.0);

        int gap = 70;

        level.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            double Y = level.getLayoutY() + newVal.getHeight() / 2 - left.getHeight() / 2;

            double leftX = level.getLayoutX() - gap
                    - left.getLayoutBounds().getWidth();
            double rightX = level.getLayoutX() + gap
                    + newVal.getWidth();

            left.setLayoutY(Y);
            left.setLayoutX(leftX);
            right.setLayoutY(Y);
            right.setLayoutX(rightX);
        });
        //level button
        //press to select level & back to home
        level.setOnAction(e -> {
            MapLoader.LevelData levelData = MapLoader.loadLevel("level_" + (currentLevel + 1));
            ((GameScreen) sceneManager.getScreen(ScreenType.GAME))
                    .getGameManager()
                    .setCurrentLvl(levelData);
            sceneManager.switchScreen(ScreenType.GAME);
        });

        //press left button -> level--
        left.setOnAction(e -> {
            Platform.runLater(root::requestFocus);
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
            Platform.runLater(root::requestFocus);
            if (currentLevel < maxLevels) {
                currentLevel++;
                currentLevel %= maxLevels;
                updateLevel(level);
            }
        });

        //exit button
        Button exit = UIUtils.newButton("Back", 60, 40, 2.0, 2.0);
        exit.setOnAction(e -> {
            sceneManager.switchScreen(ScreenType.HOME);
        });

        //arrow key -> left, right , enter -> chose level
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
                case ESCAPE:
                    exit.fire();
                    break;
            }
        });

        root.getChildren().addAll(left, right, level, exit);
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

    @Override
    public void onStart() {
        Platform.runLater(root::requestFocus);
    }

}
