package com.game.physicsandbox.physics;

import com.game.physicsandbox.mechanism.ComponentExecutor;
import com.game.physicsandbox.object.Component;

/**
 * 物理分析器。在此为物体的Transform添加加速度或设置位移
 * 也可以为力学组件添加力
 * 本质上是个组件执行器
 *
 * @author Jerry-Yu-TeslaCoil
 * @version 1.0
 */
public class PhysicsAnalyzer implements ComponentExecutor {

    @Override
    public void register(Component component) {

    }

    @Override
    public void unregister(Component component) {

    }
}
