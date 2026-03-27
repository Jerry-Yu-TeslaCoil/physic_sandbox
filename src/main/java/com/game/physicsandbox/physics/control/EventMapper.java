package com.game.physicsandbox.physics.control;

import com.game.physicsandbox.physics.input.KeyCode;
import com.game.physicsandbox.physics.input.KeyEvent;
import com.game.physicsandbox.physics.input.MouseButton;
import com.game.physicsandbox.physics.input.MouseEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 按键映射器，用于将用户输入映射为事件
 * 映射器应根据给出的按键，返回映射到的事件类型，供Controller访问事件注册器，创建新事件并配置事件信息
 * 同时，映射器应管理相同类型按键互斥
 * 分为鼠标事件、键盘事件
 *
 * @author Jerry-Yu-TeslaCoil
 * @version 1.0
 */
@Component
public class EventMapper {

    private final Map<MouseButton, Class<? extends ControlEvent>> mouseMapping = new HashMap<>();
    private final Map<Class<? extends ControlEvent>, MouseButton> invertMouseMapping = new HashMap<>();
    private final Map<KeyCode, Class<? extends ControlEvent>> keyMapping = new HashMap<>();
    private final Map<Class<? extends ControlEvent>, KeyCode> invertKeyMapping = new HashMap<>();

    /**
     * 设置鼠标按钮映射关系。新的关系会置空原先冲突的映射
     * @param button 鼠标按钮
     * @param eventClass 对应事件类型
     */
    public void setMapping(MouseButton button, Class<? extends ControlEvent> eventClass) {
        MouseButton conflictedButton;
        if ((conflictedButton = invertMouseMapping.getOrDefault(eventClass, null)) != null) {
            mouseMapping.put(conflictedButton, null);
        }
        mouseMapping.put(button, eventClass);
        invertMouseMapping.put(eventClass, button);
    }

    /**
     * 设置映射关系。新的关系会置空原先冲突的映射
     * @param keyCode 键盘按钮码
     * @param eventClass 对应事件类型
     */
    public void setMapping(KeyCode keyCode, Class<? extends ControlEvent> eventClass) {
        KeyCode conflictedKeyCode;
        if ((conflictedKeyCode = invertKeyMapping.getOrDefault(eventClass, null)) != null) {
            keyMapping.put(conflictedKeyCode, null);
        }
        keyMapping.put(keyCode, eventClass);
        invertKeyMapping.put(eventClass, keyCode);
    }

    /**
     * 将鼠标按钮事件映射到事件类型
     * @param event 鼠标事件
     * @return 其对应的事件类型
     */
    public Class<? extends ControlEvent> getMappingEvent(MouseEvent event) {
        return mouseMapping.getOrDefault(event.button(), null);
    }

    /**
     * 将键盘按钮事件映射到事件类型
     * @param event 键盘事件
     * @return 其对应的事件类型
     */
    public Class<? extends ControlEvent> getMappingEvent(KeyEvent event) {
        return keyMapping.getOrDefault(event.code(), null);
    }
}
