package com.game.physicsandbox.mechanism;

import com.game.physicsandbox.object.Component;

public interface ComponentExecutor {
    void register(Component component);
    void unregister(Component component);
}
