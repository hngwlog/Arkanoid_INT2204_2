package com.raumania.gameplay.objects;

import com.raumania.utils.InitializeJavaFx;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NormalBrickTest extends InitializeJavaFx {
    @Test
    void testTakeHit() {
        NormalBrick nb = new NormalBrick(0, 0, 1, 1);
        nb.takeHit();
        assertTrue(nb.isDestroyed());
    }
}
