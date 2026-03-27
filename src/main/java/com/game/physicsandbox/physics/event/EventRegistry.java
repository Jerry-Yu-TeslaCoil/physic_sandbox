package com.game.physicsandbox.physics.event;

import com.game.physicsandbox.exception.EventNotRegisteredException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;

/**
 * 事件对象池。避免反复创建销毁导致性能问题。
 * <p>
 * 注册器维护一个ConcurrentHashMap来管理不同类型的对象池。注册时提供一个对象
 * </p>
 *
 * @author Jerry-Yu-TeslaCoil
 * @version 1.0
 */
@Component
public class EventRegistry {
    private final Map<Class<? extends Event>, EventPool<? extends Event>> eventPool = new ConcurrentHashMap<>();

    /**
     * 注册一个事件类型并提供创建方法，供对象池管理。
     * 同一种事件只能注册一次。再次注册会直接返回。
     * @param eventClass 事件类型
     * @param supplier 事件创建方法
     * @param capacity 对象池容量，超过容量时对象回收会被直接销毁
     * @param <T> 具体事件类型
     */
    public <T extends Event> void  register(Class<T> eventClass, Supplier<T> supplier, int capacity) {
        if (eventPool.containsKey(eventClass)) {
            return;
        }
        eventPool.put(eventClass, new EventPool<>(supplier, capacity));
    }

    /**
     * 取消注册事件类型。取消注册会直接取消对象池的引用。
     * 取消注册后即可再次注册。
     * @param eventClass 要取消注册的事件类型
     * @param <T> 具体的事件类型
     */
    public <T extends Event> void  unregister(Class<T> eventClass) {
        eventPool.remove(eventClass);
    }

    /**
     * 获得一个指定Event子类型的事件对象。
     * @param eventClass 指定的Event子类类型
     * @return 对应的事件对象
     * @param <T> Event子类泛型
     */
    @SuppressWarnings("unchecked")
    public <T extends Event> T get(Class<T> eventClass) throws EventNotRegisteredException {
        if (!eventPool.containsKey(eventClass)) {
            throw new EventNotRegisteredException("尝试获得未注册的事件类型: " + eventClass);
        }
        return (T)eventPool.get(eventClass).get();
    }

    /**
     * 回收Event对象到对象池。
     * @param event 要回收的事件对象
     */
    public void recycle(Event event) throws EventNotRegisteredException {
        if (!eventPool.containsKey(event.getClass())) {
            throw new EventNotRegisteredException("尝试回收未注册的事件类型: " + event.getClass());
        }
        eventPool.get(event.getClass()).recycle(event);
    }
}

/**
 * 事件对象池。使用BlockingQueue以支持多线程操作。
 * 当回收对象超过容量时会直接丢弃。
 */
class EventPool <T extends Event> {
    private final BlockingQueue<T> events;
    private final Supplier<T> supplier;

    /**
     * 创建时指定生产方法和队列最大容量。
     * @param supplier 创建事件对象的函数式接口
     * @param capacity 最大容量
     */
    EventPool(Supplier<T> supplier, int capacity) {
        this.supplier = supplier;
        this.events = new LinkedBlockingQueue<>(capacity);
    }

    /**
     * 获取一个T事件类型的对象。
     * 若队列为空，直接创建一个对象；否则返回队列内对象。
     * @return 事件对象
     */
    T get() {
        T event = events.poll();  // 非阻塞获取
        if (event != null) {
            return event;
        }
        return this.supplier.get();  // 队列为空时才创建
    }

    /**
     * 回收事件对象。超过容量时直接丢弃。
     * @param event 要回收的对象
     */
    @SuppressWarnings("unchecked")
    void recycle(Event event) {
        boolean ignored = events.offer((T)event);
    }
}