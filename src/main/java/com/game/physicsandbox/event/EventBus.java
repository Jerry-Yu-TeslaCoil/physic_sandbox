package com.game.physicsandbox.event;

import com.game.physicsandbox.exception.EventTypeException;
import com.game.physicsandbox.mechanism.ComponentExecutor;
import com.game.physicsandbox.object.Component;
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
@org.springframework.stereotype.Component
public class EventBus extends ComponentExecutor {

    // 事件类型到监听器列表的映射
    private final Map<Class<? extends Event>, List<EventListener<?>>> listenerMap = new ConcurrentHashMap<>();

    // 事件存储队列，按时间戳排序
    private final List<Event> eventQueue = new ArrayList<>();

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
     * @param currentTime 当前计算帧的时间纳秒
     */
    public void dispatchEvents(long currentTime, long deltaTime) {
        List<Event> eventsToDispatch = new ArrayList<>();

        synchronized (eventQueue) {
            Iterator<Event> iterator = eventQueue.iterator();
            while (iterator.hasNext()) {
                Event event = iterator.next();
                if (event.getTimestamp() <= currentTime) {
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

        components.forEach((component) -> {
            try {
               component.update(currentTime, deltaTime);
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

    @Override
    public void register(Component component) {
        synchronized (components) {
            if (!components.contains(component)) {
                components.add(component);
            }
        }
        if (component instanceof EventListener<? extends Event>) {
            registerListener((EventListener<? extends Event>) component);
        }
    }

    @Override
    public void unregister(Component component) {
        synchronized (components) {
            components.remove(component);
        }

        if (component instanceof EventListener<? extends Event>) {
            unregisterListener((EventListener<? extends Event>) component);
        }
    }

    private <T extends Event> void registerListener(EventListener<T> listener) {
        Class<T> eventType = cast(listener);
        listenerMap.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                .add(listener);
    }

    private <T extends Event> void unregisterListener(EventListener<T> listener) {
        Class<T> eventType = cast(listener);
        List<EventListener<?>> listeners = listenerMap.get(eventType);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Event> Class<T> cast(EventListener<T> listener) {
        if (listener == null) {
            throw new IllegalArgumentException("监听器不能为null");
        }
        ListensTo listensTo = listener.getClass().getAnnotation(ListensTo.class);
        if (listensTo == null) {
            throw new EventTypeException(listener.getClass() + "必须有注解@ListensTo指定监听器类型");
        }
        Class<? extends Event> value = listensTo.value();
        Class<T> eventType;
        try {
            eventType = (Class<T>) value;
            return eventType;
        } catch (Exception e) {
            throw new EventTypeException(
                    listener.getClass().getSimpleName() + "@ListensTo指定类型与监听器类型不一致");
        }
    }
}
