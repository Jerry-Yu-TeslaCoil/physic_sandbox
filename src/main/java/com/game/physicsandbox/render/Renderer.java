package com.game.physicsandbox.render;

import com.game.physicsandbox.mechanism.ComponentExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 画面渲染器。执行渲染组件的功能，将物体渲染到UI上。
 * 本质上是一个渲染组件执行器。
 */
@Component
public class Renderer extends ComponentExecutor {

    private final Refreshable frame;

    @Autowired
    public Renderer(Refreshable frame) {
        this.frame = frame;
    }

    @Override
    public void update(long currentTime, long deltaTime) {
        super.update(currentTime, deltaTime);
        frame.refreshFrame();
    }
}
