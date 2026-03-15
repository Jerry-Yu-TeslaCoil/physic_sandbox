package com.game.physicsandbox.physics.mechanism;

import java.awt.*;

public interface FrameUpdatable {
    void addVector(Vector2 vector);
    void clearVector();
    void onFrameUpdate(long time);
}
