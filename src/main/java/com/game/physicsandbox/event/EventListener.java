package com.game.physicsandbox.event;

import com.game.physicsandbox.object.Component;
import lombok.Getter;
import org.springframework.core.GenericTypeResolver;

/**
 * 事件监听器接口
 *
 * @param <T> 监听的事件类型
 */
@Getter
public abstract class EventListener<T extends Event> extends Component {

    private final Class<T> eventType;

    @SuppressWarnings("unchecked")
    public EventListener() {
        // 使用Spring的GenericTypeResolver解析泛型
        Class<?> clazz = GenericTypeResolver.resolveTypeArgument(getClass(), EventListener.class);
        this.eventType = (Class<T>) clazz;
    }

    /**
     * 事件处理回调
     *
     * @param event 事件对象
     */
    public abstract void onEvent(T event);
}
