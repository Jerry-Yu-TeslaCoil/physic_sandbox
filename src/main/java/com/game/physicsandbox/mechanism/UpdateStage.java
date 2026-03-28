package com.game.physicsandbox.mechanism;

public enum UpdateStage {
    EVENT,
    Components,
    PHYSICS,
    UPDATE,
    COLLISION,
    CONSTRAINT,
    CLEAN,
    RENDER
}
