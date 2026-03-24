package com.game.physicsandbox.physics.object;

import com.game.physicsandbox.physics.object.impl.Transform;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class GameObject {

    @Getter
    private final Transform transform = new Transform();
    private final List<Component> componentList = new ArrayList<>();

    public GameObject() {
        componentList.add(transform);
    }

    public void addComponent(Component component) {
        if (!componentList.contains(component) && component.getGameObject() == null) {
            component.setGameObject(this);
            componentList.add(component);
        }
    }

    public void removeComponent(Component component) {
        if (componentList.contains(component)) {
            component.setGameObject(null);
            componentList.remove(component);
        }
    }

    public <T extends Component> T getComponent(Class<T> componentType) {
        for (Component component : componentList) {
            if (component.getClass() == componentType) {
                return componentType.cast(component);
            }
        }
        return null;
    }

    public Component[] getComponents() {
        return componentList.toArray(new Component[0]);
    }
}
