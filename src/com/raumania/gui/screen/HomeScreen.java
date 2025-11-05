package com.raumania.gui.screen;

import com.raumania.core.AudioManager;
import com.raumania.core.SpriteSheet;
import com.raumania.gui.manager.SceneManager;
import com.raumania.utils.UIUtils;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.input.KeyEvent;
import com.raumania.utils.ResourcesLoader;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.control.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeScreen extends Screen {
    int cnt = 0;
    Text chooseArrowLeft;
    Text chooseArrowRight;
    List<Button> buttons;
    List<Double> buttonY;
    public HomeScreen(SceneManager sceneManager) {
        super(sceneManager);

        //Game title
        Text title = UIUtils.centerText("Welcome to Arkanoid!", 100, 3.0, 3.0);
        // style components
        title.setFill(Color.GREEN);

        //Choosing arrow
        chooseArrowLeft = UIUtils.newText(">" , 423.0, 212.5, 2.0, 2.0);
        chooseArrowRight = UIUtils.newText("<" , 568.769, 212.5, 2.0, 2.0);

        //Play button
        Button play =  UIUtils.centerButton("Play Multiplayer", 200, 2.0, 2.0);
        play.setOnAction(e -> {
            sceneManager.switchScreen(ScreenType.MULTIPLAYER);
        });

        //Level select button
        Button levelSelect =  UIUtils.centerButton("Single player", 275, 2.0, 2.0);
        levelSelect.setOnAction(e -> {
            sceneManager.switchScreen(ScreenType.LEVEL_SELECT);
        });

        //Setting button
        Button setting = UIUtils.centerButton("Settings", 350, 2.0, 2.0);
        setting.setOnAction(e -> {
            sceneManager.switchScreen(ScreenType.SETTINGS);
        });

        //Option button
        Button option = UIUtils.centerButton("Skins", 425, 2.0, 2.0);
        option.setOnAction(e -> {
            sceneManager.switchScreen(ScreenType.OPTION);
        });

        //Quit button
        Button quit = UIUtils.centerButton("Quit", 500, 2.0, 2.0);
        quit.setOnAction(e -> {
            Platform.exit();
        });

        buttonY = new ArrayList<>();
        Collections.addAll(buttonY, 200.0, 275.0, 350.0, 425.0, 500.0);

        buttons = new ArrayList<>();
        Collections.addAll(buttons, play, levelSelect, setting, option, quit);
        for (Button button : buttons) {
            button.setOnMouseEntered(e -> {
                cnt = getIndex(button);
                updateCnt();
            });
        }

        int n = buttons.size();
        root.getChildren().addAll(title, chooseArrowLeft ,chooseArrowRight);
        root.getChildren().addAll(buttons);
        Platform.runLater(root::requestFocus);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case DOWN:
                    cnt = (cnt + 1) % n;
                    updateCnt();
                    break;
                case UP:
                    if (cnt == 0) cnt = n - 1;
                    else cnt--;
                    updateCnt();
                    break;
                case ENTER:
                    buttons.get(cnt).fire();
                    break;
            }
        });

        Background bg = new Background(new BackgroundImage(
            ResourcesLoader.loadImage("homescreen_bg.png"),
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(1.0, 1.0, true, true, false, true)
        ));
        root.setBackground(bg);
    }

    private int getIndex(Button button) {
        int i = 0;
        for (Button b : buttons) {
            if (b.equals(button)) return i;
            i++;
        }
        return -1;
    }

    private void updateCnt() {
        double gap = 60 + Math.max(buttons.get(cnt).getWidth()-60, 0) / 2;
        double arrowY = buttonY.get(cnt) + buttons.get(cnt).getHeight()/2;
        double arrowLeftX = buttons.get(cnt).getLayoutX() - gap
                - chooseArrowLeft.getLayoutBounds().getWidth();
        double arrowRightX = buttons.get(cnt).getLayoutX() + gap
                + buttons.get(cnt).getWidth();
        chooseArrowLeft.setY(arrowY);
        chooseArrowLeft.setX(arrowLeftX);
        chooseArrowRight.setY(arrowY);
        chooseArrowRight.setX(arrowRightX);
    }

    @Override
    public void onStart() {
        Platform.runLater(this::updateCnt);
        Platform.runLater(root::requestFocus);
        AudioManager.getInstance().playBGMusic(AudioManager.HOME_MUSIC);
    }
}
