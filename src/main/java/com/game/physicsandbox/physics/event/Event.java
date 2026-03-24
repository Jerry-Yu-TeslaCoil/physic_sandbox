package com.game.physicsandbox.physics.event;

import lombok.Getter;
import lombok.Setter;

/**
 * 事件抽象父类
 */
@Setter
@Getter
public abstract class Event {
    private long timestamp;
}
