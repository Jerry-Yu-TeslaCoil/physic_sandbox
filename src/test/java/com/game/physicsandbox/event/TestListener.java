package com.game.physicsandbox.event;

import com.game.physicsandbox.physics.event.EventListener;
import com.game.physicsandbox.physics.event.ListensTo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ListensTo(TestEvent.class)
public class TestListener extends EventListener<TestEvent> {
    @Override
    public void onEvent(TestEvent event) {
        log.info("TestEvent received: {}", event.getEventString());
    }

    @Override
    public void update(long time) {
        log.info("Test listener updated: {}", time);
    }
}
