package com.game.physicsandbox.object;

import com.game.physicsandbox.lifecycle.LifeCycleManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 元素创建工厂。会对创建的元素自动初始化。
 *
 * @author Jerry-Yu-TeslaCoil
 * @version 1.0
 */
@Component
public class GameObjectFactory {

    private final LifeCycleManager lifeCycleManager;

    @Autowired
    public GameObjectFactory(LifeCycleManager lifeCycleManager) {
        this.lifeCycleManager = lifeCycleManager;
    }

    /**
     * 创建并初始化一个GameObject。
     * @return 创建的GameObject(代理对象)
     */
    public GameObject create(String name) {
        GameObject gameObject = new GameObject(lifeCycleManager, name);
        gameObject = lifeCycleManager.initializeGameObject(gameObject);
        return gameObject;
    }
}
