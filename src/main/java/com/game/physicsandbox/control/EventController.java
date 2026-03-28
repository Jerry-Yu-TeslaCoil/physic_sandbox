package com.game.physicsandbox.control;

import com.game.physicsandbox.event.EventBus;
import com.game.physicsandbox.event.EventRegistry;
import com.game.physicsandbox.exception.EventNotRegisteredException;
import com.game.physicsandbox.input.KeyEvent;
import com.game.physicsandbox.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 从用户获得输入事件，并翻译成自定义事件发布到事件总线
 *
 * @author Jerry-Yu-TeslaCoil
 * @version 1.0
 */
@Slf4j
@Component
public class EventController {

    private final EventRegistry registry;
    private final EventMapper eventMapper;
    private final EventBus eventBus;

    @Autowired
    public EventController(EventRegistry registry, EventMapper eventMapper, EventBus eventBus) {
        this.registry = registry;
        this.eventMapper = eventMapper;
        this.eventBus = eventBus;
    }

    /**
     * 窗口背景板被点击
     * @param event 点击的具体事件
     */
    public void padClicked(MouseEvent event) {
        Class<? extends ControlEvent> eventClass = eventMapper.getMappingEvent(event);
        ControlEvent controlEvent = getControlEvent(eventClass);
        controlEvent.set(event);
        eventBus.publish(controlEvent);
    }

    /**
     * 窗口键盘按下
     * @param event 键盘事件
     */
    public void keyPressed(KeyEvent event) {
        Class<? extends ControlEvent> eventClass = eventMapper.getMappingEvent(event);
        ControlEvent controlEvent = getControlEvent(eventClass);
        controlEvent.set(event);
        eventBus.publish(controlEvent);
    }

    private ControlEvent getControlEvent(Class<? extends ControlEvent> eventClass) {
        ControlEvent controlEvent;
        try {
            controlEvent = registry.get(eventClass);
        } catch (EventNotRegisteredException e) {
            log.info(e.getMessage());
            throw new EventNotRegisteredException(e.getMessage());
        }
        return controlEvent;
    }
}
