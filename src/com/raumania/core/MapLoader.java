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

    private MapLoader() {}

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

    public record BossData(@JsonGetter("type") String type,@JsonGetter("x") int x,@JsonGetter("y") int y) {
            @JsonCreator
            public BossData(
                    @JsonProperty("type") String type,
                    @JsonProperty("x") int x,
                    @JsonProperty("y") int y) {
                this.type = type;
                this.x = x;
                this.y = y;
            }
        }

    public record LevelData(@JsonGetter("levelNumber") int levelNumber, @JsonGetter("name") String name, @JsonGetter("legend") Map<String, String> legend, @JsonGetter("layout") List<String> layout, @JsonGetter("boss") List<BossData> bosses, @JsonGetter("colors") List<String> colors) {
            @JsonCreator
            public LevelData(
                    @JsonProperty("levelNumber") int levelNumber,
                    @JsonProperty("name") String name,
                    @JsonProperty("legend") Map<String, String> legend,
                    @JsonProperty("layout") List<String> layout,
                    @JsonProperty("boss") List<BossData> bosses,
                    @JsonProperty("colors") List<String> colors) {
                this.levelNumber = levelNumber;
                this.name = name;
                this.legend = legend;
                this.layout = layout;
                this.bosses = bosses;
                this.colors = colors;
            }
        }
}
