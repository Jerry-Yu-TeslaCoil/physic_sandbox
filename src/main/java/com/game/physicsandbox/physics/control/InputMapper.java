package com.game.physicsandbox.physics.control;

import com.game.physicsandbox.physics.event.Event;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * 按键映射器，用于将用户输入映射为事件
 * 映射器应根据给出的按键，返回映射到的事件类型，供Controller创建新事件并配置事件信息
 * 同时，映射器应管理相同类型按键互斥
 * 分为鼠标事件、键盘事件
 *
 * @author Jerry-Yu-TeslaCoil
 * @version 1.0
 */
public class InputMapper {

    Map<MouseButton, Class<? extends Event>> mouseMapping = new HashMap<>();
    Map<Class<? extends Event>, MouseButton> invertMouseMapping = new HashMap<>();

    Map<KeyCode, Class<? extends Event>> keyMapping = new HashMap<>();
    Map<Class<? extends Event>, KeyCode> invertKeyMapping = new HashMap<>();

    public void setMapping(MouseButton button, Class<? extends Event> event) {
        MouseButton conflictedButton;
        if ((conflictedButton = invertMouseMapping.getOrDefault(event, null)) != null) {
            //TODO: 处理键位冲突
        }
    }

    public Class<? extends Event> getMappingEvent(MouseEvent event) {
        return mouseMapping.getOrDefault(event.getButton(), Event.class);
    }

    public Class<? extends Event> getMappingEvent(KeyEvent event) {
        return keyMapping.getOrDefault(event.getCode(), Event.class);
    }
}
