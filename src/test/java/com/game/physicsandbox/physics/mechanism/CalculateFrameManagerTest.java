package com.game.physicsandbox.physics.mechanism;

import org.junit.jupiter.api.Test;

public class CalculateFrameManagerTest {
    @Test
    public void testFrameManagerFixedUpdate() {
        FrameManager frameManager = new FrameManager();
        frameManager.run();
    }
}
