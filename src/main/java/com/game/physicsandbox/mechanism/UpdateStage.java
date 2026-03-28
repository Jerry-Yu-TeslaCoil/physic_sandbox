package com.game.physicsandbox.mechanism;

public enum UpdateStage {
    CLEAN,
    EVENT,
    Components,
    PHYSICS,
    UPDATE,
    COLLISION,
    CONSTRAINT,
    RENDER
}
