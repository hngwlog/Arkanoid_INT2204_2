package com.raumania.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raumania.gameplay.objects.powerup.PowerUp;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class MapLoader {
    private static final MapLoader instance = new MapLoader();
    private static final String LEVELS_PATH = "/resources/levels/";

    public static class LevelData {
        private final int levelNumber;
        private final String name;
        private final Map<String, String> legend;
        private final List<String> layout;
        @JsonCreator
        public LevelData(
                @JsonProperty("levelNumber") int levelNumber,
                @JsonProperty("name") String name,
                @JsonProperty("legend") Map<String, String> legend,
                @JsonProperty("layout") List<String> layout) {
            this.levelNumber = levelNumber;
            this.name = name;
            this.legend = legend;
            this.layout = layout;
        }

        @JsonGetter("levelNumber")
        public int getLevelNumber() { return levelNumber; }

        @JsonGetter("name")
        public String getName() { return name; }

        @JsonGetter("legend")
        public Map<String, String> getLegend() { return legend; }

        @JsonGetter("layout")
        public List<String> getLayout() { return layout; }
    }

    private MapLoader() {
    }

    public static MapLoader getInstance() {
        return instance;
    }

    public static LevelData loadLevel(String levelName) {
        try {
            String path = LEVELS_PATH + levelName + ".json";
            InputStream stream = MapLoader.class.getResourceAsStream(path);
            ObjectMapper mapper = new ObjectMapper();
            if (stream == null) {
                throw new RuntimeException("Level file not found: " + path);
            }
            try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                return mapper.readValue(reader, LevelData.class);
            }
        } catch (Exception e) {
            System.err.println("Error loading level: " + levelName + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
