package com.game.physicsandbox.object;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class Component {

    protected boolean activate = true;
    protected boolean autoDispose = false;

    protected GameObject gameObject;

    public abstract void update(long currentTime, long delta);
}
