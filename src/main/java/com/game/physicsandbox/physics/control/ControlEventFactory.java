package com.game.physicsandbox.physics.control;


import com.game.physicsandbox.physics.event.Event;
import com.game.physicsandbox.physics.input.KeyEvent;
import com.game.physicsandbox.physics.input.MouseEvent;

/**
 * 控制事件工厂。用于控制器根据用户输入生成事件。
 * 分为键盘事件和鼠标事件。
 *
 * @author Jerry-Yu-TeslaCoil
 * @version 1.0
 */
public abstract class ControlEventFactory {

    /**
     * 根据键盘事件创建控制事件
     * @param event 输入的键盘事件
     * @return 创建的控制事件
     */
    public abstract Event create(KeyEvent event);

    /**
     * 根据鼠标事件创建控制事件
     * @param event 输入的鼠标事件
     * @return 创建的控制事件
     */
    public abstract Event create(MouseEvent event);
}
