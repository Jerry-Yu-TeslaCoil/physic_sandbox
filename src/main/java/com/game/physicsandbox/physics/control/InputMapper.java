package com.game.physicsandbox.physics.control;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * 按键映射器，用于将用户输入映射为事件
 * 映射器应根据给出的按键，返回映射到的事件类型工厂，供Controller创建新事件并配置事件信息
 * 同时，映射器应管理相同类型按键互斥
 * 分为鼠标事件、键盘事件
 *
 * @author Jerry-Yu-TeslaCoil
 * @version 1.0
 */
public class InputMapper {

    Map<MouseButton, ControlEventFactory> mouseMapping = new HashMap<>();
    Map<ControlEventFactory, MouseButton> invertMouseMapping = new HashMap<>();

    Map<KeyCode, ControlEventFactory> keyMapping = new HashMap<>();
    Map<ControlEventFactory, KeyCode> invertKeyMapping = new HashMap<>();

    /**
     * 设置鼠标按钮映射关系。新的关系会置空原先冲突的映射
     * @param button 鼠标按钮
     * @param factory 对应事件工厂
     */
    public void setMapping(MouseButton button, ControlEventFactory factory) {
        MouseButton conflictedButton;
        if ((conflictedButton = invertMouseMapping.getOrDefault(factory, null)) != null) {
            mouseMapping.put(conflictedButton, null);
        }
        mouseMapping.put(button, factory);
        invertMouseMapping.put(factory, button);
    }

    /**
     * 设置映射关系。新的关系会置空原先冲突的映射
     * @param keyCode 键盘按钮码
     * @param factory 对应事件工厂
     */
    public void setMapping(KeyCode keyCode, ControlEventFactory factory) {
        KeyCode conflictedKeyCode;
        if ((conflictedKeyCode = invertKeyMapping.getOrDefault(factory, null)) != null) {
            keyMapping.put(conflictedKeyCode, null);
        }
        keyMapping.put(keyCode, factory);
        invertKeyMapping.put(factory, keyCode);
    }

    /**
     * 将鼠标按钮事件映射到事件工厂
     * @param event 鼠标事件
     * @return 其对应的事件工厂
     */
    public ControlEventFactory getMappingEvent(MouseEvent event) {
        return mouseMapping.getOrDefault(event.getButton(), null);
    }

    /**
     * 将键盘按钮事件映射到事件工厂
     * @param event 键盘事件
     * @return 其对应的事件工厂
     */
    public ControlEventFactory getMappingEvent(KeyEvent event) {
        return keyMapping.getOrDefault(event.getCode(), null);
    }
}
