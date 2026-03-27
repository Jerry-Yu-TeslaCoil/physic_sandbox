package com.game.physicsandbox.physics.event;

import com.game.physicsandbox.exception.EventTypeException;
import com.game.physicsandbox.physics.object.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 事件总线
 * 维护事件类型到监听器列表的映射，支持事件发布和分发
 * 每个监听器只监听一种事件类型
 */
@Slf4j
public class EventBus {

    // 事件类型到监听器列表的映射
    private final Map<Class<? extends Event>, List<EventListener<?>>> listenerMap = new ConcurrentHashMap<>();

    // 事件存储队列，按时间戳排序
    private final List<Event> eventQueue = new ArrayList<>();

    /**
     * 注册监听器
     * @param listener 事件监听器
     * @param <T> 事件类型
     */
    public <T extends Event> void registerListener(EventListener<T> listener) {
        Class<T> eventType = cast(listener);
        listenerMap.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                .add(listener);
    }

    /**
     * 注销监听器
     * @param listener 要注销的事件监听器
     * @param <T> 事件类型
     */
    public <T extends Event> void unregisterListener(EventListener<T> listener) {
        Class<T> eventType = cast(listener);
        List<EventListener<?>> listeners = listenerMap.get(eventType);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    /**
     * 发布事件（自动添加当前时间戳）
     * @param event 事件对象
     */
    public void publish(Event event) {
        publish(event, System.currentTimeMillis());
    }

    /**
     * 发布事件（指定时间戳）
     * @param event 事件对象
     * @param timestamp 事件发生的时间戳
     */
    public void publish(Event event, long timestamp) {
        event.setTimestamp(timestamp);
        synchronized (eventQueue) {
            eventQueue.add(event);
            eventQueue.sort(Comparator.comparingLong(Event::getTimestamp));
        }
    }

    /**
     * 分发指定时间戳之前的所有事件
     * 在计算帧前期调用，分发时间段内的所有事件
     * @param timestamp 当前计算帧的时间戳
     */
    public void dispatchEvents(long timestamp) {
        List<Event> eventsToDispatch = new ArrayList<>();

        synchronized (eventQueue) {
            Iterator<Event> iterator = eventQueue.iterator();
            while (iterator.hasNext()) {
                Event event = iterator.next();
                if (event.getTimestamp() <= timestamp) {
                    eventsToDispatch.add(event);
                    iterator.remove();
                } else {
                    break;
                }
            }
        }

        // 分发事件
        for (Event event : eventsToDispatch) {
            dispatchEvent(event);
        }

        listenerMap.values().forEach((eventListeners) -> {
            try {
               Component component = (Component) eventListeners;
               component.update(timestamp);
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        });
    }

    /**
     * 分发单个事件
     * @param event 事件对象
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void dispatchEvent(Event event) {
        List<EventListener<?>> listeners = listenerMap.get(event.getClass());
        if (listeners != null) {
            for (EventListener<?> listener : listeners) {
                try {
                    ((EventListener) listener).onEvent(event);
                } catch (Exception e) {
                    log.error("Error dispatching event {} to listener: {}",
                            event.getClass().getSimpleName(), e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 清除所有未处理的事件
     */
    public void clearAllEvents() {
        synchronized (eventQueue) {
            eventQueue.clear();
        }
    }

    /**
     * 获取待处理事件数量
     * @return 待处理事件数量
     */
    public int getPendingEventCount() {
        synchronized (eventQueue) {
            return eventQueue.size();
        }
    }

    /**
     * 获取指定事件类型的所有监听器
     * @param eventType 事件类型
     * @return 监听器列表
     */
    public List<EventListener<?>> getListeners(Class<? extends Event> eventType) {
        return listenerMap.getOrDefault(eventType, Collections.emptyList());
    }

    private <T extends Event> Class<T> cast(EventListener<T> listener) {
        ListensTo listensTo = listener.getClass().getAnnotation(ListensTo.class);
        if (listensTo == null) {
            throw new EventTypeException(listener.getClass() + " must have @ListensTo annotation");
        }
        Class<? extends Event> value = listensTo.value();
        Class<T> eventType;
        try {
            eventType = (Class<T>) value;
            return eventType;
        } catch (Exception e) {
            throw new EventTypeException(
                    listener.getClass().getSimpleName() + " uses @ListensTo with invalid event type");
        }
    }
}
