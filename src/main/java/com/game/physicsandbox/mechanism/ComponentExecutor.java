package com.game.physicsandbox.mechanism;

import com.game.physicsandbox.object.Component;

import java.util.ArrayList;
import java.util.List;

public abstract class ComponentExecutor {

    protected final List<Component> components = new ArrayList<>();

    public void register(Component component) {
        synchronized (components) {
            if (!components.contains(component)) {
                components.add(component);
            }
        }
    }

    public void unregister(Component component) {
        synchronized (components) {
            components.remove(component);
        }
    }
}
