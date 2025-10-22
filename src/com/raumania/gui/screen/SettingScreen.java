package com.raumania.gui.screen;

import com.raumania.core.AudioManager;
import com.raumania.gui.manager.SceneManager;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.text.Text;

import static com.raumania.utils.UIUtils.centerButton;
import static com.raumania.utils.UIUtils.newText;
import static com.raumania.utils.UIUtils.newSlider;


public class SettingScreen extends Screen {
    public SettingScreen(SceneManager sceneManager) {
        super(sceneManager);
        // put components here

        //Volume Text
        Text volumeText = newText("Volume: " + AudioManager.getInstance().getVolume() + "%", 100, 100, 2.0, 2.0);

        //Volume Slider
        Slider slider = newSlider(0, AudioManager.getInstance().getVolume() * 100, 100, 400, 100,
                3.0, 3.0, false, false);
        slider.valueProperty().bindBidirectional(AudioManager.getInstance().getVolumeProperty());
        //Slider change value -> volume
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            volumeText.setText("Volume: " + newVal.intValue() + "%");
        });

        //Back to Home button
        Button back = centerButton("Back to Home", 500, 2.0, 2.0);
        back.setOnAction(e -> {
            sceneManager.switchScreen(ScreenType.HOME);
        });

        root.getChildren().addAll(volumeText, slider, back);
    }
}
