package com.game.physicsandbox.physics;

import com.game.physicsandbox.object.Component;

/**
 * 约束组件。需要在迭代求解完毕后对物体添加加速度以修正速度。
 * 成对出现，分别挂载到约束双方。双方均修正位置，并各自贡献一半的加速度。
 *
 * @author Jerry-Yu-TeslaCoil
 * @version 1.0
 */
public abstract class Constraint extends Component {
    /**
     * 对约束做整理。
     * 对物体添加加速度以更新速度，以及发布事件。
     * 由于约束成对，单个约束加速度将除以2。
     * @param currentTime 当前时间纳秒
     * @param deltaTime 时间增量纳秒
     */
    public abstract void finalize(long currentTime, long deltaTime);

    public abstract Constraint getPair();
}
