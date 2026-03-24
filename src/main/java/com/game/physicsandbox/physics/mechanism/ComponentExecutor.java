package com.game.physicsandbox.physics.mechanism;

import com.game.physicsandbox.physics.object.Component;

public interface ComponentExecutor {
    void register(Component component);
    void unregister(Component component);
}
