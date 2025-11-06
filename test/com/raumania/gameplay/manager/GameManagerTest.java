package com.raumania.gameplay.manager;

import com.raumania.utils.InitializeJavaFx;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameManagerTest extends InitializeJavaFx {
    private GameManager manager;

    @BeforeEach
    void setUp() {
        manager = new GameManager();
        manager.initGame(); // Initialize game state
    }

    @Test
    void testInitialGameState() {
        assertEquals(GameManager.GameState.READY, manager.getGameState());
        assertEquals(0, manager.getScore());
    }

    @Test
    void testPauseGame() {
        manager.setGameState(GameManager.GameState.PAUSED);
        assertEquals(GameManager.GameState.PAUSED, manager.getGameState());
    }

    @Test
    void testGameOver() {
        manager.setGameState(GameManager.GameState.GAME_OVER);
        assertEquals(GameManager.GameState.GAME_OVER, manager.getGameState());
    }


}
