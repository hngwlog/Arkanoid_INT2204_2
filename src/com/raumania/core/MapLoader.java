package com.raumania.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class MapLoader {
    private static final MapLoader instance = new MapLoader();
    private static final String LEVELS_PATH = "/resources/levels/";

    public static class BossData {
        private final String type;
        private final int x;
        private final int y;

        @JsonCreator
        public BossData(
                @JsonProperty("type") String type,
                @JsonProperty("x") int x,
                @JsonProperty("y") int y
        ) {
            this.type = type;
            this.x = x;
            this.y = y;
        }

        @JsonGetter("type")
        public String getType() { return type; }

        @JsonGetter("x")
        public int getX() { return x; }

        @JsonGetter("y")
        public int getY() { return y; }
    }

    public static class LevelData {
        private final int levelNumber;
        private final String name;
        private final Map<String, String> legend;
        private final List<String> layout;
        private final List<BossData> bosses;

        @JsonCreator
        public LevelData(
                @JsonProperty("levelNumber") int levelNumber,
                @JsonProperty("name") String name,
                @JsonProperty("legend") Map<String, String> legend,
                @JsonProperty("layout") List<String> layout,
                @JsonProperty("boss") List<BossData> bosses
        ) {
            this.levelNumber = levelNumber;
            this.name = name;
            this.legend = legend;
            this.layout = layout;
            this.bosses = bosses;
        }

        @JsonGetter("levelNumber")
        public int getLevelNumber() { return levelNumber; }

        @JsonGetter("name")
        public String getName() { return name; }

        @JsonGetter("legend")
        public Map<String, String> getLegend() { return legend; }

        @JsonGetter("layout")
        public List<String> getLayout() { return layout; }

        @JsonGetter("boss")
        public List<BossData> getBosses() { return bosses; }
    }

    private MapLoader() { }

    public static MapLoader getInstance() {
        return instance;
    }

    public static LevelData loadLevel(String levelName) {
        String path = LEVELS_PATH + levelName + ".json";
        try (InputStream stream = MapLoader.class.getResourceAsStream(path)) {
            if (stream == null) {
                throw new RuntimeException("Level file not found: " + path);
            }

            ObjectMapper mapper = new ObjectMapper();
            try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                return mapper.readValue(reader, LevelData.class);
            }
        } catch (Exception e) {
            System.err.println("Error loading level: " + levelName);
            e.printStackTrace();
            return null;
        }
    }
}
