package com.game.physicsandbox.lifecycle;

import com.game.physicsandbox.object.Component;
import com.game.physicsandbox.object.GameObject;
import com.game.physicsandbox.object.component.Transform;
import lombok.Getter;
import java.util.List;

/**
 * 元素代理。内部引用由生命周期管理器维护，对元素被逻辑移除后的访问尝试报错。
 *
 * @author Jerry-Yu-TeslaCoil
 * @version 1.0
 */
public class GameObjectProxy extends GameObject {

    private GameObject gameObject;

    @Getter
    private boolean destroyed = false;

    /**
     * 无参创建元素代理。
     *
     * @param lifeCycleManager 生命周期管理器
     * @param gameObject 代理的元素
     */
    protected GameObjectProxy(LifeCycleManager lifeCycleManager, GameObject gameObject) {
        super(lifeCycleManager, gameObject.getName());
        this.gameObject = gameObject;
    }

    /**
     * 设置实际代理的元素。
     * @param gameObject 代理的元素对象
     */
    protected void setGameObject(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    /**
     * 销毁元素。
     */
    protected void destroy() {
        this.destroyed = true;
        this.gameObject = null;
    }

    @Override
    public void addComponent(Component component) {
        gameObject.addComponent(component);
        component.setGameObject(this);
    }

    @Override
    public void removeComponent(Component component) {
        gameObject.removeComponent(component);
    }

    @Override
    public <T extends Component> T getComponent(Class<T> componentType) {
        return gameObject.getComponent(componentType);
    }

    @Override
    public <T extends Component> List<T> getComponents(Class<T> componentType) {
        return gameObject.getComponents(componentType);
    }

    @Override
    public Component[] getComponents() {
        return gameObject.getComponents();
    }

    @Override
    public String getName() {
        return gameObject.getName();
    }

    @Override
    public void setName(String name) {
        this.gameObject.setName(name);
    }

    @Override
    public Transform getTransform() {
        return gameObject.getTransform();
    }
}
