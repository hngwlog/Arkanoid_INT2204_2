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

    public static class LevelData {
        private final int levelNumber;
        private final String name;
        private final BrickConfig brick;
        private final Map<String, String> legend;
        private final List<String> layout;
        private final List<PowerUpData> powerups;

        @JsonCreator
        public LevelData(
                @JsonProperty("levelNumber") int levelNumber,
                @JsonProperty("name") String name,
                @JsonProperty("brick") BrickConfig brick,
                @JsonProperty("legend") Map<String, String> legend,
                @JsonProperty("layout") List<String> layout,
                @JsonProperty("powerups") List<PowerUpData> powerups) {
            this.levelNumber = levelNumber;
            this.name = name;
            this.brick = brick;
            this.legend = legend;
            this.layout = layout;
            this.powerups = powerups;
        }

        @JsonGetter("levelNumber")
        public int getLevelNumber() { return levelNumber; }

        @JsonGetter("name")
        public String getName() { return name; }

        @JsonGetter("brick")
        public BrickConfig getBrick() { return brick; }

        @JsonGetter("legend")
        public Map<String, String> getLegend() { return legend; }

        @JsonGetter("layout")
        public List<String> getLayout() { return layout; }

        @JsonGetter("powerups")
        public List<PowerUpData> getPowerups() { return powerups; }
    }

    public static class BrickConfig {
        private final int width;
        private final int height;
        private final int paddingX;
        private final int paddingY;
        private final int offsetX;
        private final int offsetY;

        @JsonCreator
        public BrickConfig(
                @JsonProperty("width") int width,
                @JsonProperty("height") int height,
                @JsonProperty("paddingX") int paddingX,
                @JsonProperty("paddingY") int paddingY,
                @JsonProperty("offsetX") int offsetX,
                @JsonProperty("offsetY") int offsetY) {
            this.width = width;
            this.height = height;
            this.paddingX = paddingX;
            this.paddingY = paddingY;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }

        @JsonGetter("width")
        public int getWidth() { return width; }

        @JsonGetter("height")
        public int getHeight() { return height; }

        @JsonGetter("paddingX")
        public int getPaddingX() { return paddingX; }

        @JsonGetter("paddingY")
        public int getPaddingY() { return paddingY; }

        @JsonGetter("offsetX")
        public int getOffsetX() { return offsetX; }

        @JsonGetter("offsetY")
        public int getOffsetY() { return offsetY; }
    }

    public static class PowerUpData {
        private final String type;
        private final int row;
        private final int col;

        @JsonCreator
        public PowerUpData(
                @JsonProperty("type") String type,
                @JsonProperty("row") int row,
                @JsonProperty("col") int col) {
            this.type = type;
            this.row = row;
            this.col = col;
        }

        @JsonGetter("type")
        public String getType() { return type; }

        @JsonGetter("row")
        public int getRow() { return row; }

        @JsonGetter("col")
        public int getCol() { return col; }
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
