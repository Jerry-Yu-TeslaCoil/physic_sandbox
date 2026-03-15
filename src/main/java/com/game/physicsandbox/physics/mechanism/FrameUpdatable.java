package com.game.physicsandbox.physics.mechanism;

import java.awt.*;

public interface FrameUpdatable {
    void addForceToFrame(Frame frame);
    void clearFrame();
    void onFrameUpdate(long time);
}
