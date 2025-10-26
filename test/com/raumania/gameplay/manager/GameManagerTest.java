import com.raumania.gameplay.manager.GameManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameManagerTest {
    private GameManager manager;

    @BeforeEach
    void setUp() {
        manager = new GameManager();
        manager.initGame(); // Initialize game state
    }

    @Test
    void testInitialGameState() {
        assertEquals(GameManager.GameState.RUNNING, manager.getGameState());
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

    @Test
    void testUpdate() {
        double dt = 0.016; // Simulating ~60 FPS
        manager.update(dt);
        // Add assertions based on expected behavior after update
    }
}
