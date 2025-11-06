package com.raumania.gui.screen;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raumania.core.AudioManager;
import com.raumania.gui.manager.SceneManager;
import com.raumania.utils.ResourcesLoader;
import com.raumania.utils.UIUtils;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;

public class SettingScreen extends Screen {
    // file to save the volume
    private static final String CONFIG_FILE = "config.json";
    private static final Config DEFAULT_CONFIG = new Config(100, "A", "D", "LEFT", "RIGHT");
    public static Config sharedConfig = DEFAULT_CONFIG;
    private final Button firstLeftKeyButton;
    private final Button firstRightKeyButton;
    private final Button secondLeftKeyButton;
    private final Button secondRightKeyButton;
    private Button activeButton;

    public SettingScreen(SceneManager sceneManager) {
        super(sceneManager);

        this.loadConfig();
        AudioManager.getInstance().setVolume(sharedConfig.getVolume());

        // put components here
        // Volume Text
        Text volumeText =
                UIUtils.newText(
                        "Volume: " + AudioManager.getInstance().getVolume() + "%",
                        100,
                        115,
                        2.0,
                        2.0);
        volumeText.setFill(Color.WHITE);
        // Volume Slider
        Slider slider =
                UIUtils.newSlider(
                        0,
                        AudioManager.getInstance().getVolume() * 100,
                        100,
                        400,
                        100,
                        3.0,
                        3.0,
                        false,
                        false);
        slider.valueProperty().bindBidirectional(AudioManager.getInstance().getVolumeProperty());
        // Slider change value -> volume
        slider.valueProperty()
                .addListener(
                        (obs, oldVal, newVal) -> {
                            volumeText.setText("Volume: " + newVal.intValue() + "%");
                            Platform.runLater(root::requestFocus);
                            sharedConfig.volume = newVal.intValue();
                            saveConfig();
                        });
        //        HBox volumeBox = new HBox(200);
        //        volumeBox.setAlignment(Pos.CENTER_LEFT);
        //        volumeBox.getChildren().addAll(volumeText, slider);

        // Back to Home button
        Button back = UIUtils.centerButton("Back to Home", 500, 2.0, 2.0);
        back.setOnAction(
                e -> {
                    sceneManager.switchScreen(ScreenType.HOME);
                });

        Text moveTitle = UIUtils.newText("Movement Keys", 100, 200, 2.0, 2.0);
        moveTitle.setFill(Color.WHITE);
        Text firstLeftKeyText = UIUtils.newText("Player 1 Left Key", 115, 250, 2.0, 2.0);
        firstLeftKeyText.setFill(Color.WHITE);
        firstLeftKeyButton =
                UIUtils.newButton(sharedConfig.getFirstLeftKey().getName(), 450, 235, 2.0, 2.0);
        firstLeftKeyButton.setOnMouseClicked(this::changeKeyHandler);
        //        HBox firstLeft = new HBox(20);
        //        firstLeft.setAlignment(Pos.CENTER_LEFT);
        //        firstLeft.getChildren().addAll(firstLeftKeyText, firstLeftKeyButton);

        Text firstRightKeyText = UIUtils.newText("Player 1 Right Key", 117, 300, 2.0, 2.0);
        firstRightKeyText.setFill(Color.WHITE);
        firstRightKeyButton =
                UIUtils.newButton(sharedConfig.getFirstRightKey().getName(), 450, 285, 2.0, 2.0);
        firstRightKeyButton.setOnMouseClicked(this::changeKeyHandler);
        //        HBox firstRight = new HBox(20);
        //        firstRight.setAlignment(Pos.CENTER_LEFT);
        //        firstRight.getChildren().addAll(firstRightKeyText, firstRightKeyButton);

        Text secondLeftKeyText = UIUtils.newText("Player 2 Left Key", 115, 350, 2.0, 2.0);
        secondLeftKeyText.setFill(Color.WHITE);
        secondLeftKeyButton =
                UIUtils.newButton(sharedConfig.getSecondLeftKey().getName(), 450, 335, 2.0, 2.0);
        secondLeftKeyButton.setOnMouseClicked(this::changeKeyHandler);
        //        HBox secondLeft = new HBox(20);
        //        secondLeft.setAlignment(Pos.CENTER_LEFT);
        //        secondLeft.getChildren().addAll(secondLeftKeyText, secondLeftKeyButton);

        Text secondRightKeyText = UIUtils.newText("Player 2 Right Key", 117, 400, 2.0, 2.0);
        secondRightKeyText.setFill(Color.WHITE);
        secondRightKeyButton =
                UIUtils.newButton(sharedConfig.getSecondRightKey().getName(), 450, 385, 2.0, 2.0);
        secondRightKeyButton.setOnMouseClicked(this::changeKeyHandler);
        //        HBox secondRight = new HBox(20);
        //        secondRight.setAlignment(Pos.CENTER_LEFT);
        //        secondRight.getChildren().addAll(secondRightKeyText, secondRightKeyButton);

        //        VBox textBox = new VBox(50);
        //        textBox.setAlignment(Pos.TOP_LEFT);
        //        textBox.setLayoutX(100);
        //        textBox.setLayoutY(100);
        //        textBox.getChildren().addAll(volumeBox, moveTitle, firstLeft, firstRight,
        //                secondLeft, secondRight, back);
        //        root.getChildren().addAll(textBox);
        root.getChildren()
                .addAll(
                        volumeText,
                        slider,
                        moveTitle,
                        firstLeftKeyText,
                        firstLeftKeyButton,
                        firstRightKeyText,
                        firstRightKeyButton,
                        secondLeftKeyText,
                        secondRightKeyText,
                        secondLeftKeyButton,
                        secondRightKeyButton,
                        back);

        Background bg =
                new Background(
                        new BackgroundImage(
                                ResourcesLoader.loadImage("homescreen_bg.png"),
                                BackgroundRepeat.NO_REPEAT,
                                BackgroundRepeat.NO_REPEAT,
                                BackgroundPosition.CENTER,
                                new BackgroundSize(1.0, 1.0, true, true, false, true)));
        root.setBackground(bg);
    }

    private void changeKeyHandler(MouseEvent event) {
        Platform.runLater(root::requestFocus);
        Button button = (Button) event.getSource();

        if (activeButton != null && activeButton != button) {
            restoreButton(activeButton);
        }
        activeButton = button;
        button.setText("Press any key...");

        scene.setOnKeyPressed(
                ev -> {
                    ev.consume(); // prevent default key actions
                    if (activeButton == firstLeftKeyButton) {
                        sharedConfig.firstLeftKey = ev.getCode();
                        activeButton.setText(sharedConfig.firstLeftKey.getName());
                    } else if (activeButton == firstRightKeyButton) {
                        sharedConfig.firstRightKey = ev.getCode();
                        activeButton.setText(sharedConfig.firstRightKey.getName());
                    } else if (activeButton == secondLeftKeyButton) {
                        sharedConfig.secondLeftKey = ev.getCode();
                        activeButton.setText(sharedConfig.secondLeftKey.getName());
                    } else if (activeButton == secondRightKeyButton) {
                        sharedConfig.secondRightKey = ev.getCode();
                        activeButton.setText(sharedConfig.secondRightKey.getName());
                    }

                    saveConfig();
                    scene.setOnKeyPressed(null);
                });
    }

    private void restoreButton(Button btn) {
        if (btn == null) {
            return;
        }

        if (btn == firstLeftKeyButton) {
            btn.setText(sharedConfig.firstLeftKey.getName());
        } else if (btn == firstRightKeyButton) {
            btn.setText(sharedConfig.firstRightKey.getName());
        } else if (btn == secondLeftKeyButton) {
            btn.setText(sharedConfig.getSecondLeftKey().getName());
        } else if (btn == secondRightKeyButton) {
            btn.setText(sharedConfig.getSecondRightKey().getName());
        }
    }

    /** Save the current config to {@value CONFIG_FILE}. */
    private void saveConfig() {
        File file = new File(CONFIG_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating config file!");
                return;
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(file, sharedConfig);
        } catch (IOException e) {
            System.err.println("Error saving config file!" + e);
        }
    }

    /** Get the config from {@value CONFIG_FILE}. If not exists then use default. */
    private void loadConfig() {
        File file = new File(CONFIG_FILE);
        if (!file.exists() || file.length() == 0) {
            sharedConfig = DEFAULT_CONFIG;
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            sharedConfig = mapper.readValue(file, Config.class);
        } catch (DatabindException e) {
            System.err.println("Config file has corrupted structure!");
        } catch (StreamReadException e) {
            System.err.println("Cant read config file!");
        } catch (IOException e) {
            System.err.println("Error loading config file!");
        }
    }

    @Override
    public void onStart() {
        Platform.runLater(root::requestFocus);
    }

    public static class Config {
        private int volume;
        @JsonIgnore private KeyCode firstLeftKey;
        @JsonIgnore private KeyCode firstRightKey;
        @JsonIgnore private KeyCode secondLeftKey;
        @JsonIgnore private KeyCode secondRightKey;

        public Config(
                @JsonProperty("volume") int volume,
                @JsonProperty("firstLeftKey") String firstLeftKey,
                @JsonProperty("firstRightKey") String firstRightKey,
                @JsonProperty("secondLeftKey") String secondLeftKey,
                @JsonProperty("secondRightKey") String secondRightKey) {
            this.volume = volume;

            this.firstLeftKey = getKey(firstLeftKey, KeyCode.A);
            this.firstRightKey = getKey(firstRightKey, KeyCode.D);
            this.secondLeftKey = getKey(secondLeftKey, KeyCode.LEFT);
            this.secondRightKey = getKey(secondRightKey, KeyCode.RIGHT);
        }

        private KeyCode getKey(String key, KeyCode defaultKey) {
            if (key == null) {
                return defaultKey;
            }
            try {
                return KeyCode.valueOf(key.toUpperCase());
            } catch (IllegalArgumentException e) {
                return defaultKey;
            }
        }

        public KeyCode getFirstLeftKey() {
            return firstLeftKey;
        }

        @JsonProperty("firstLeftKey")
        public void setFirstLeftKey(String name) {
            this.firstLeftKey = getKey(name, KeyCode.A);
        }

        public KeyCode getFirstRightKey() {
            return firstRightKey;
        }

        @JsonProperty("firstRightKey")
        public void setFirstRightKey(String name) {
            this.firstRightKey = getKey(name, KeyCode.D);
        }

        public KeyCode getSecondLeftKey() {
            return secondLeftKey;
        }

        @JsonProperty("secondLeftKey")
        public void setSecondLeftKey(String name) {
            this.secondLeftKey = getKey(name, KeyCode.LEFT);
        }

        public KeyCode getSecondRightKey() {
            return secondRightKey;
        }

        @JsonProperty("secondRightKey")
        public void setSecondRightKey(String name) {
            this.secondRightKey = getKey(name, KeyCode.RIGHT);
        }

        @JsonGetter
        public int getVolume() {
            return volume;
        }

        @JsonGetter("firstLeftKey")
        public String getFirstLeftKeyName() {
            return firstLeftKey.name();
        }

        @JsonGetter("firstRightKey")
        public String getFirstRightKeyName() {
            return firstRightKey.name();
        }

        @JsonGetter("secondLeftKey")
        public String getSecondLeftKeyName() {
            return secondLeftKey.name();
        }

        @JsonGetter("secondRightKey")
        public String getSecondRightKeyName() {
            return secondRightKey.name();
        }
    }
}
