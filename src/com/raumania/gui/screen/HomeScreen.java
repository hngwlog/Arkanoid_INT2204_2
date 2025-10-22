package com.raumania.gui.screen;

import com.raumania.core.AudioManager;
import com.raumania.gui.manager.SceneManager;
import com.raumania.utils.UIUtils;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
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
        Button play =  UIUtils.centerButton("Play", 200, 2.0, 2.0);
        //Button play = new Button("Play");
        play.setOnAction(e -> {
            sceneManager.switchScreen(ScreenType.GAME);
        });

        //Level select button
        Button levelSelect =  UIUtils.centerButton("Level Select", 300, 2.0, 2.0);
        //Button levelSelect = new Button("Select Level");
        levelSelect.setOnAction(e -> {
            sceneManager.switchScreen(ScreenType.LEVEL_SELECT);
        });

        //Setting button
        Button setting = UIUtils.centerButton("Setting", 400, 2.0, 2.0);
        //Button setting = new Button("Setting");
        setting.setOnAction(e -> {
            sceneManager.switchScreen(ScreenType.SETTINGS);
        });

        //Quit button
        Button quit = UIUtils.centerButton("Quit", 500, 2.0, 2.0);
        //Button quit = new Button("Quit");
        quit.setOnAction(e -> {
            Platform.exit();
        });

        buttonY = new ArrayList<>();
        Collections.addAll(buttonY, 200.0, 300.0, 400.0, 500.0);

        buttons = new ArrayList<>();
        Collections.addAll(buttons, play, levelSelect, setting, quit);
        for (Button button : buttons) {
            button.setOnMouseEntered(e -> {
                cnt = getIndex(button);
                updateCnt();
            });
        }

        int n = buttons.size();
        root.getChildren().addAll(title, chooseArrowLeft ,chooseArrowRight);
        //VBox button = new VBox(100);
        //button.setAlignment(Pos.CENTER);
//        button.getChildren().addAll(title
//                //, chooseArrowLeft
//                //, chooseArrowRight
//                );
        //button.getChildren().addAll(buttons);
        root.getChildren().addAll(buttons);
        //button.layoutXProperty().bind(root.widthProperty().subtract(button.widthProperty()).divide(2));
        //button.layoutYProperty().bind(root.heightProperty().subtract(button.heightProperty()).divide(2));
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
        double gap = 50;
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
        AudioManager.getInstance().playBGMusic(AudioManager.HOME_MUSIC);
    }
}
