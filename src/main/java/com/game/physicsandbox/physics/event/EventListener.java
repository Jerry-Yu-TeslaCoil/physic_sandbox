package com.game.physicsandbox.physics.event;

/**
 * 事件监听器接口
 *
 * @param <T> 监听的事件类型
 */
@FunctionalInterface
public interface EventListener<T extends Event> {
    /**
     * 事件处理回调
     *
     * @param event 事件对象
     */
    void onEvent(T event);
}
