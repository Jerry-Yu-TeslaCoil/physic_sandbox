package com.game.physicsandbox.object;

import lombok.Getter;
import lombok.Setter;

/**
 * 组件类。
 *
 * @author Jerry-Yu-TeslaCoil
 * @version 1.0
 */
@Setter
@Getter
public abstract class Component {

    protected boolean activate = true;
    protected boolean autoDispose = false;

    protected GameObject gameObject;

    /**
     * 更新组件使组件功能生效。
     * @param currentTime 当前时间纳秒
     * @param delta 时间增量纳秒
     */
    public abstract void update(long currentTime, long delta);

    /**
     * 元素或组件发生改变时调用，用于查找元素和组件。
     * @param gameObject 挂载的元素
     */
    public void updateGameObjectStatus(GameObject gameObject) {
        this.gameObject = gameObject;
    }
}
