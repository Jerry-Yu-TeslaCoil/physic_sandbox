package com.game.physicsandbox.mechanism;

import com.game.physicsandbox.object.Component;

import java.util.ArrayList;
import java.util.List;

public abstract class ComponentExecutor {

    protected final List<Component> components = new ArrayList<>();

    /**
     * 将组件挂载到组件执行器。
     * @param component 要挂载的组件
     */
    public void register(Component component) {
        synchronized (components) {
            if (!components.contains(component)) {
                components.add(component);
            }
        }
    }

    /**
     * 将组件从组件执行器注销
     * @param component 要注销的组件
     */
    public void unregister(Component component) {
        synchronized (components) {
            components.remove(component);
        }
    }

    /**
     * 更新组件。执行器父类提供的默认方法，将列表内的组件依次更新。
     * 事件总线等执行器可能需要重写此方法。
     * @param currentTime 当前时间纳秒
     * @param deltaTime 时间增量纳秒
     */
    public void update(long currentTime, long deltaTime) {
        synchronized (components) {
            for (Component component : components) {
                component.update(currentTime, deltaTime);
            }
        }
    }
}
