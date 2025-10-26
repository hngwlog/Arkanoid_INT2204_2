package com.raumania.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MapLoaderTest {
    @Test
    void loadLevel() {
        MapLoader.LevelData lv = MapLoader.loadLevel("level_1");
        assertNotNull(lv);
    }
}