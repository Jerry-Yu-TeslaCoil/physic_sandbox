package com.game.physicsandbox.physics;

import com.game.physicsandbox.mechanism.ComponentExecutor;
import com.game.physicsandbox.object.Component;

/**
 * 物理分析器。物体力学组件在此更新。
 * 本质上是一个力学组件执行器。
 *
 * @author Jerry-Yu-TeslaCoil
 * @version 1.0
 */
public class PhysicAnalyzer extends ComponentExecutor {

    /**
     * 更新力学组件。
     * @param currentTime 当前时间纳秒
     * @param deltaTime 时间增量纳秒
     */
    public void update(long currentTime, long deltaTime) {
        for (Component component : components) {
            component.update(currentTime, deltaTime);
        }
    }

}
