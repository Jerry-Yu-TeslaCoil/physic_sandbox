package com.game.physicsandbox.physics.control;

import com.game.physicsandbox.exception.EventNotRegisteredException;
import com.game.physicsandbox.physics.event.EventBus;
import com.game.physicsandbox.physics.event.EventRegistry;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
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
public class Controller {

    private final EventRegistry registry;
    private final InputMapper inputMapper;
    private final EventBus eventBus;

    @Autowired
    public Controller(EventRegistry registry, InputMapper inputMapper, EventBus eventBus) {
        this.registry = registry;
        this.inputMapper = inputMapper;
        this.eventBus = eventBus;
    }

    /**
     * 窗口背景板被点击
     * @param event 点击的具体事件
     */
    public void padClicked(MouseEvent event) {
        Class<? extends ControlEvent> eventClass = inputMapper.getMappingEvent(event);
        ControlEvent controlEvent = getControlEvent(eventClass);
        controlEvent.set(event);
        eventBus.publish(controlEvent);
    }

    /**
     * 窗口键盘按下
     * @param event 键盘事件
     */
    public void keyPressed(KeyEvent event) {
        Class<? extends ControlEvent> eventClass = inputMapper.getMappingEvent(event);
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
