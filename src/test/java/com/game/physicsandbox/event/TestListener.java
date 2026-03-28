package com.game.physicsandbox.event;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ListensTo(TestEvent.class)
public class TestListener extends EventListener<TestEvent> {
    @Override
    public void onEvent(TestEvent event) {
        log.info("TestEvent received: {}", event.getEventString());
    }

    @Override
    public void update(long currentTime, long deltaTime) {
        log.info("Test listener updated: {}", currentTime);
        log.info("Update time gap: {}", deltaTime);
    }
}
