package com.raumania.gui.screen;

import com.raumania.gui.manager.SceneManager;
import javafx.application.Platform;
import javafx.scene.text.Text;
import com.raumania.utils.UIUtils;

public class OptionScreen extends Screen {
    public OptionScreen(SceneManager sceneManager) {
        super(sceneManager);
        Platform.runLater(root::requestFocus);
    }
}
