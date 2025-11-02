package com.raumania.core;

import org.junit.jupiter.api.*;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

public class HighScoreTest {

    @BeforeEach
    void setup() {
        HighScore.getInstance().getEntries().clear();
    }

    @Test
    void testAddHighScoreOrdersDescending() {
        HighScore hs = HighScore.getInstance();
        hs.addHighScore("1", "A", 100);
        hs.addHighScore("1", "B", 200);
        assertEquals("B", hs.getEntries().getFirst().getName());
    }

    @Test
    void testSaveAndLoadHighScores() {
        HighScore hs = HighScore.getInstance();
        hs.addHighScore("1", "Test", 300);
        hs.addHighScore("1", "Test2", 250);
        File file = new File("./highscores.json");
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }
}
