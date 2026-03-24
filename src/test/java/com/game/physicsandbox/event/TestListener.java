package com.game.physicsandbox.event;

import com.game.physicsandbox.physics.event.EventBus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestListener implements EventBus.EventListener<TestEvent> {
    @Override
    public void onEvent(TestEvent event) {
        log.info("TestEvent received: {}", event);
    }
}
