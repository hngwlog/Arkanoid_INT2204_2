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
import java.util.List;

public class HighScore {
    public static final int MAX_ENTRIES = 10;
    private static final HighScore hiScore = new HighScore();
    private final String HIGHSCORE_FILE = "./highscores.json";
    private final boolean haveUnsavedScore = false;
    private final int unsavedScore = 0;
    private List<HighScoreEntry> entries;
    private HighScore() {
        loadHighScores();
    }

    public static HighScore getInstance() {
        return hiScore;
    }

    public List<HighScoreEntry> getEntries() {
        return entries;
    }

    /**
     * Adds a new high score entry and saves the updated list to file.
     * If the list exceeds {@value MAX_ENTRIES}, the lowest score is removed.
     * @param name The name of the player.
     * @param score The score of the player.
     */
    public void addHighScore(String level, String name, int score) {
        entries.add(new HighScoreEntry(level, name, score));
        entries.sort((a, b) -> b.score - a.score);

        List<HighScoreEntry> toRemove = entries.stream().filter(e -> e.level.equals(level)).toList();
        if (toRemove.size() > MAX_ENTRIES) {
            entries.removeAll(toRemove.subList(MAX_ENTRIES, toRemove.size()));
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

    /**
     * A single high score entry.
     */
    public static class HighScoreEntry {
        String level;
        String name;
        int score;

        @JsonCreator
        HighScoreEntry(@JsonProperty("level") String level, @JsonProperty("name") String name, @JsonProperty("score") int score) {
            this.level = level;
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

        @JsonGetter("level")
        public String getLevel() {
            return level;
        }
    }
}
