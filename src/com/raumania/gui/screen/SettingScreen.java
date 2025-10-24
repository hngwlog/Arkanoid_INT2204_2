package com.raumania.gui.screen;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.raumania.core.AudioManager;
import com.raumania.gui.manager.SceneManager;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.text.Text;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

import static com.raumania.utils.UIUtils.centerButton;
import static com.raumania.utils.UIUtils.newText;
import static com.raumania.utils.UIUtils.newSlider;


public class SettingScreen extends Screen {
    private int volume = 50;
    private final String VOLUME_FILE = "volume.json";
    public SettingScreen(SceneManager sceneManager) {
        super(sceneManager);
        // put components here

        loadVolume();
        AudioManager.getInstance().setVolume(volume);

        //Volume Text
        Text volumeText = newText("Volume: " + AudioManager.getInstance().getVolume() + "%", 100, 100, 2.0, 2.0);
        //Volume Slider
        Slider slider = newSlider(0, AudioManager.getInstance().getVolume() * 100, 100, 400, 100,
                3.0, 3.0, false, false);
        slider.valueProperty().bindBidirectional(AudioManager.getInstance().getVolumeProperty());
        //Slider change value -> volume
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            volumeText.setText("Volume: " + newVal.intValue() + "%");
            volume = newVal.intValue();
            writeVolume();

        });

        //Back to Home button
        Button back = centerButton("Back to Home", 500, 2.0, 2.0);
        back.setOnAction(e -> {
            sceneManager.switchScreen(ScreenType.HOME);
        });

        root.getChildren().addAll(volumeText, slider, back);
    }

    private void writeVolume() {
        File file = new File(VOLUME_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating volume file!");
                return;
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(file, volume);
        } catch (IOException e) {
            System.err.println("Error saving volume file!" + e);
        }
    }

    private void loadVolume() {
        File file = new File(VOLUME_FILE);
        if (!file.exists() || file.length() == 0) {
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            volume = mapper.readValue(file, Integer.class);
        } catch (DatabindException e) {
            System.err.println("Volume file has corrupted structure!");
        } catch (StreamReadException e) {
            System.err.println("Cant read volume file!");
        } catch (IOException e) {
            System.err.println("Error loading high scores file!");
        }
    }

}
