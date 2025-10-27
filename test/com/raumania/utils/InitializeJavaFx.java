package com.raumania.utils;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;

/**
 * This class will be inherited by any test that need JavaFX to be initialized
 * to run properly.
 */
public class InitializeJavaFx {
    private static boolean initialized = false;

    @BeforeAll
    static void init() {
        if (!initialized) {
            Platform.startup(() -> {});
            initialized = true;
        }
    }
}
