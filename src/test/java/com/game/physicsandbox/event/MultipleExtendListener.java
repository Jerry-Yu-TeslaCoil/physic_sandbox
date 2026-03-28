package com.game.physicsandbox.event;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MultipleExtendListener extends TestListener {

    @Override
    public void onEvent(TestEvent event) {
        log.info("Multiple Listener Event: {}", event.toString());
    }

    @Override
    public void update(long currentTime, long deltaTime) {
        log.info("Multiple Listener Update: {} with gap time {}", currentTime, deltaTime);
    }
}
