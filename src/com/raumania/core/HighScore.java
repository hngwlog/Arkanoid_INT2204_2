package com.raumania.core;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class HighScore {
    /**
     * A single high score entry.
     */
    public static class HighScoreEntry {
        String name;
        int score;

        HighScoreEntry(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }
    }
    private final String HIGHSCORE_FILE = "./highscores.json";
    private final int MAX_ENTRIES = 10;
    private static final HighScore hiScore = new HighScore();
    private ArrayList<HighScoreEntry> entries;

    private HighScore() {
        loadHighScores();
    }

    public static HighScore getInstance() {
        return hiScore;
    }

    public ArrayList<HighScoreEntry> getEntries() {
        return entries;
    }

    /**
     * Adds a new high score entry and saves the updated list to file.
     * If the list exceeds MAX_ENTRIES, the lowest score is removed.
     * @param name The name of the player.
     * @param score The score of the player.
     */
    public void addHighScore(String name, int score) {
        entries.add(new HighScoreEntry(name, score));
        entries.sort((a, b) -> b.score - a.score);
        if (entries.size() > MAX_ENTRIES) {
            entries.removeLast();
        }
        saveHighScores();
    }

    private void loadHighScores() {
        entries = new ArrayList<>();
        File file = new File(HIGHSCORE_FILE);
        if (!file.exists() || file.length() == 0) {
            System.out.println("No highScores yet!");
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            entries = mapper.readValue(file, new TypeReference<ArrayList<HighScoreEntry>>(){});
        } catch (DatabindException e) {
            System.err.println("High scores file has corrupted structure!");
        } catch (StreamReadException e) {
            System.err.println("Cant read high scores file!");
        } catch (IOException e) {
            System.err.println("Error loading high scores file!");
        }
    }

    private void saveHighScores() {
        File file = new File(HIGHSCORE_FILE);
        if (!file.exists()) {
            try {
               file.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating high scores file!");
                return;
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(file, entries);
        } catch (IOException e) {
            System.err.println("Error saving high scores file!" + e);
        }
    }
}
