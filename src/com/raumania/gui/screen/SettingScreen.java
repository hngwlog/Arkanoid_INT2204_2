package com.raumania.gui.screen;

import com.raumania.gui.manager.SceneManager;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.text.Text;

import static com.raumania.utils.UIUtils.*;

public class SettingScreen extends Screen {
    public SettingScreen(SceneManager sceneManager) {
        super(sceneManager);
        // put components here

        //Volume Text
        Text volumeText = newText("Volume: 50%", 100, 100, 2.0, 2.0);
        root.getChildren().add(volumeText);

        //Volume Slider
        Slider slider = newSlider(0, 50, 100, 400, 100,
                3.0, 3.0, false, false);
        root.getChildren().add(slider);

        //Slider change value -> volume
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int volume = newVal.intValue();
            volumeText.setText("Volume: " + volume + "%");
            // Nếu dùng MediaPlayer: mediaPlayer.setVolume(volume / 100.0);

        });

        //Back to Home button
        Button back = centerButton("Back to Home", 500, 2.0, 2.0);
        back.setOnAction(e -> {
            sceneManager.switchScreen(ScreenType.HOME);
        });
        root.getChildren().add(back);

    }
}
