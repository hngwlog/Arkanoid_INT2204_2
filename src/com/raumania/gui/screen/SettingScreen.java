package com.raumania.gui.screen;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
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
import com.raumania.utils.UIUtils;


public class SettingScreen extends Screen {
    private static class Config {
        private int volume;

        public Config(@JsonProperty("volume") int volume) {
            this.volume = volume;
        }

        @JsonGetter
        public int getVolume() {
            return volume;
        }
    }
    // file to save the volume
    private static final String CONFIG_FILE = "config.json";
    private static final Config DEFAULT_CONFIG = new Config(100);
    private Config config;

    public SettingScreen(SceneManager sceneManager) {
        super(sceneManager);

        this.loadConfig();
        AudioManager.getInstance().setVolume(config.getVolume());

        // put components here
        //Volume Text
        Text volumeText = UIUtils.newText("Volume: " + AudioManager.getInstance().getVolume()
                + "%", 100, 100, 2.0, 2.0);
        //Volume Slider
        Slider slider = UIUtils.newSlider(0,
                AudioManager.getInstance().getVolume() * 100, 100, 400, 100,
                3.0, 3.0, false, false);
        slider.valueProperty().bindBidirectional(AudioManager.getInstance().getVolumeProperty());
        //Slider change value -> volume
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            volumeText.setText("Volume: " + newVal.intValue() + "%");
            config.volume = newVal.intValue();
            saveConfig();
        });

        //Back to Home button
        Button back = UIUtils.centerButton("Back to Home", 500, 2.0, 2.0);
        back.setOnAction(e -> {
            sceneManager.switchScreen(ScreenType.HOME);
        });

        root.getChildren().addAll(volumeText, slider, back);
    }

    /**
     * Save the current config to {@value CONFIG_FILE}.
     */
    private void saveConfig() {
        File file = new File(CONFIG_FILE);
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
            mapper.writeValue(file, config);
        } catch (IOException e) {
            System.err.println("Error saving volume file!" + e);
        }
    }

    /**
     * Get the config from {@value CONFIG_FILE}.
     * If not exists then use default.
     */
    private void loadConfig() {
        File file = new File(CONFIG_FILE);
        if (!file.exists() || file.length() == 0) {
            config = DEFAULT_CONFIG;
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            config = mapper.readValue(file, Config.class);
        } catch (DatabindException e) {
            System.err.println("Config file has corrupted structure!");
        } catch (StreamReadException e) {
            System.err.println("Cant read config file!");
        } catch (IOException e) {
            System.err.println("Error loading config file!");
        }
    }

}
