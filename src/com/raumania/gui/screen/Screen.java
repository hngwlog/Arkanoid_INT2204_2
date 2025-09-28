package com.raumania.gui.screen;

import static com.raumania.utils.Constants.*;

import com.raumania.gui.manager.SceneManager;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

/**
 * Abstract base class for all screens in the application.
 */
public abstract class Screen {
    protected final Scene scene;
    protected final Pane root;

    public Screen(SceneManager sceneManager) {
        this.root = new Pane();
        this.scene = new Scene(this.root, WINDOW_WIDTH, WINDOW_HEIGHT); // Default size, can be adjusted
    }

    public Scene getScene() {
        return this.scene;
    }

    public Pane getRoot() {
        return this.root;
    }

    /**
     * Called when the screen is displayed.
     */
    public void onStart() {}
    /**
     * Called when the screen is closed.
     */
    public void onStop() {}
}
