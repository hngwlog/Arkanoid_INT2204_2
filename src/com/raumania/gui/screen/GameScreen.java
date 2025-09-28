package com.raumania.gui.screen;

import com.raumania.gameplay.manager.GameManager;
import com.raumania.gui.manager.SceneManager;

public class GameScreen extends Screen {
    private GameManager gameManager;

    public GameScreen(SceneManager sceneManager) {
        super(sceneManager);
        this.gameManager = new GameManager(root);
    }

    @Override
    public void onStart() {
        // Initialize or reset game state if needed
        gameManager.initGame();
    }

    @Override
    public void onStop() {
        // Cleanup or pause game state if needed
    }
}
