package com.raumania.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AudioManagerTest {

    @Test
    void testSingletonInstance() {
        AudioManager a1 = AudioManager.getInstance();
        AudioManager a2 = AudioManager.getInstance();
        assertSame(a1, a2);
    }

    @Test
    void testVolumeProperty() {
        AudioManager manager = AudioManager.getInstance();
        manager.setVolume(50);
        assertEquals(50, manager.getVolume());
    }

    @Test
    void testMaxVolumeProperty() {
        AudioManager manager = AudioManager.getInstance();
        manager.setVolume(101);
        assertEquals(100, manager.getVolume());
    }
}
