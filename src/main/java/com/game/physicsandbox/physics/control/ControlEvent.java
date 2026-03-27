package com.game.physicsandbox.physics.control;

import com.game.physicsandbox.physics.event.Event;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * 控制事件类。由用户输入转换而成的事件均继承此类。
 * 提供事件注入方法，可以通过接收的用户输入事件中的信息设置事件参数
 */
public abstract class ControlEvent extends Event {

    /**
     * 根据鼠标事件设置参数信息
     * @param event 要设置的鼠标事件
     */
    public abstract void set(MouseEvent event);

    /**
     * 根据键盘事件设置参数信息
     * @param event 要设置的键盘事件
     */
    public abstract void set(KeyEvent event);
}
