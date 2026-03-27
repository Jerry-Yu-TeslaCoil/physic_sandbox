package com.game.physicsandbox.physics.frame;

import com.game.physicsandbox.physics.event.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CalculateFrame {

    private final EventBus eventBus;

    @Autowired
    public CalculateFrame(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void update(long nanoTime) {

    }
}
