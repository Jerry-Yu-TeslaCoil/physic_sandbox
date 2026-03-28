package com.game.physicsandbox.physics;

import com.game.physicsandbox.mechanism.ComponentExecutor;
import com.game.physicsandbox.object.Component;

/**
 * 组件更新器。在此为物体的Transform添加加速度或设置位移
 * 也可以为力学组件添加力
 * 本质上是个一般组件执行器
 *
 * @author Jerry-Yu-TeslaCoil
 * @version 1.0
 */
@org.springframework.stereotype.Component
public class ComponentUpdater extends ComponentExecutor {

    /**
     * 更新组件。
     * @param currentTime 当前时间纳秒
     * @param deltaTime 时间变化量纳秒
     */
    public void update(long currentTime, long deltaTime) {
        for (Component component : components) {
            component.update(currentTime, deltaTime);
        }
    }
}
