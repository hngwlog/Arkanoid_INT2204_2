package com.raumania.gameplay.manager;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import java.util.concurrent.atomic.AtomicBoolean;

public class InputHandler {
    // thread-safe gameState
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread thread;
    private GameManager gameManager;
    private KeyCode leftKey;
    private KeyCode rightKey;
    // keep track of key up or down
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    public InputHandler(GameManager gameManager, KeyCode leftKey, KeyCode rightKey) {
        this.gameManager = gameManager;
        this.leftKey = leftKey;
        this.rightKey = rightKey;
    }

    /**
     * Spawn a new thread to handle input
     */
    public void start() {
        // avoid spam thread
        if (running.get()) {
            return;
        }

        running.set(true);
        thread = new Thread(() -> {
            while (running.get()) {
                // if not pressed, send release event will not hurt
                // use Platform.runLater because movement affects UI
                if (leftPressed != rightPressed) {
                    Platform.runLater(() -> {
                        if (leftPressed) {
                            gameManager.getPaddle().moveLeft();
                        } else if (rightPressed) {
                            gameManager.getPaddle().moveRight();
                        }
                    });
                } else {
                    Platform.runLater(gameManager.getPaddle()::stop);
                }

                try {
                    // avoid full CPU
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    stop();
                }
            }
        });
        // close thread when application exits
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Stop the input handler thread
     */
    public void stop() {
        running.set(false);
        if (thread != null) {
            thread.interrupt();
        }
    }

    /**
     * Flag the key as pressed
     * @param keyCode the inp key
     */
    public void onKeyPressed(KeyCode keyCode) {
        if (keyCode == leftKey) {
            leftPressed = true;
        } else if (keyCode == rightKey) {
            rightPressed = true;
        }
    }

    /**
     * Flag the key as released
     * @param keyCode the inp key
     */
    public void onKeyReleased(KeyCode keyCode) {
        if (keyCode == leftKey) {
            leftPressed = false;
        } else if (keyCode == rightKey) {
            rightPressed = false;
        }
    }
}
