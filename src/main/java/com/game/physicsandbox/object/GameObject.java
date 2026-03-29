package com.game.physicsandbox.object;

import com.game.physicsandbox.lifecycle.LifeCycleManager;
import com.game.physicsandbox.object.impl.Transform;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 元素类。作为组件的容器。
 *
 * @author Jerry-Yu-TeslaCoil
 * @version 1.0
 */
public class GameObject {

    @Getter
    private final Transform transform = new Transform();
    private final List<Component> componentList = new ArrayList<>();
    @Getter
    private final boolean destroyed = false;

    private final LifeCycleManager lifeCycleManager;

    /**
     * 无参创建元素。
     */
    protected GameObject(LifeCycleManager lifeCycleManager) {
        componentList.add(transform);
        this.lifeCycleManager = lifeCycleManager;
    }

    public void addComponent(Component component) {
        if (!componentList.contains(component) && component.getGameObject() == null) {
            component.setGameObject(this);
            componentList.add(component);
            lifeCycleManager.slowRegisterToExecutor(component);
        }
    }

    public void removeComponent(Component component) {
        if (componentList.contains(component)) {
            component.setGameObject(null);
            componentList.remove(component);
            lifeCycleManager.slowUnregisterFromExecutor(component);
        }
    }

    public <T extends Component> T getComponent(Class<T> componentType) {
        for (Component component : componentList) {
            if (componentType.isInstance(component)) {
                return componentType.cast(component);
            }
        }
        return null;
    }

    public <T extends Component> List<T> getComponents(Class<T> componentType) {
        List<T> components = new ArrayList<>();
        for (Component component : componentList) {
            if (componentType.isInstance(component)) {
                components.add(componentType.cast(component));
            }
        }
        return components;
    }

    public Component[] getComponents() {
        return componentList.toArray(new Component[0]);
    }
}
