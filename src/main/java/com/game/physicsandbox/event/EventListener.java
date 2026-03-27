package com.game.physicsandbox.event;

import com.game.physicsandbox.object.Component;

/**
 * 事件监听器接口
 *
 * @param <T> 监听的事件类型
 */
public abstract class EventListener<T extends Event> extends Component {
    /**
     * 事件处理回调
     *
     * @param event 事件对象
     */
    public abstract void onEvent(T event);
}
