package com.raumania.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
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

        @JsonCreator
        HighScoreEntry(@JsonProperty("name") String name, @JsonProperty("score") int score) {
            this.name = name;
            this.score = score;
        }

        @JsonGetter("name")
        public String getName() {
            return name;
        }

        @JsonGetter("score")
        public int getScore() {
            return score;
        }
    }
    private final String HIGHSCORE_FILE = "./highscores.json";
    public static final int MAX_ENTRIES = 10;
    private static final HighScore hiScore = new HighScore();
    private boolean haveUnsavedScore = false;
    private int unsavedScore = 0;
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
     * Sets an unsaved score that can be added to the high score list later.
     * @param score The score to set.
     */
    public void setUnsavedScore(int score) {
        if (score < 0 || (entries.size() == MAX_ENTRIES && score <= entries.getLast().score)) {
            return;
        }
        haveUnsavedScore = true;
        unsavedScore = score;
    }

    /**
     * Check for an unsaved score.
     * @return true if there is an unsaved score, false otherwise.
     */
    public boolean hasUnsavedScore() {
        return haveUnsavedScore;
    }

    /**
     * Adds a new high score entry and saves the updated list to file.
     * If the list exceeds {@value MAX_ENTRIES}, the lowest score is removed.
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

    /**
     * Adds the unsaved score with the given name to the high score list.
     * If there is no unsaved score, this method does nothing.
     * After adding, the unsaved score is cleared.
     * @param name The name of the player.
     */
    public void addHighScore(String name) {
        if (!haveUnsavedScore) {
            return;
        }
        addHighScore(name, unsavedScore);
        haveUnsavedScore = false;
        unsavedScore = 0;
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
