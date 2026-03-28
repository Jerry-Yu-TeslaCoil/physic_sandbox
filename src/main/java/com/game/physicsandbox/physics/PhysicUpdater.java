package com.game.physicsandbox.physics;

import com.game.physicsandbox.mechanism.ComponentExecutor;
import com.game.physicsandbox.object.Component;

/**
 *
 */
public class PhysicUpdater extends ComponentExecutor {

    public void update(long currentTime, long deltaTime) {
        for (Component component : components) {
            component.update(currentTime, deltaTime);
        }
    }
}
