package com.game.physicsandbox.event;

import com.game.physicsandbox.physics.event.EventListener;
import com.game.physicsandbox.physics.event.ListensTo;
import com.game.physicsandbox.physics.object.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ListensTo(TestEvent.class)
public class TestListener extends Component implements EventListener<TestEvent> {
    @Override
    public void onEvent(TestEvent event) {
        log.info("TestEvent received: {}", event);
    }

    @Override
    public void update(long time) {

    }
}
