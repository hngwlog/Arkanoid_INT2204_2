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
        hs.addHighScore("A", 100);
        hs.addHighScore("B", 200);
        assertEquals("B", hs.getEntries().get(0).getName());
    }

    @Test
    void testSaveAndLoadHighScores() {
        HighScore hs = HighScore.getInstance();
        hs.addHighScore("Test", 300);
        hs.addHighScore("Test2", 250);
        File file = new File("./highscores.json");
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }

    @Test
    void testSetUnsavedScore() {
        HighScore hs = HighScore.getInstance();
        hs.setUnsavedScore(100);
        assertTrue(hs.hasUnsavedScore());
        hs.addHighScore("Player");
        assertFalse(hs.hasUnsavedScore());
    }
}
