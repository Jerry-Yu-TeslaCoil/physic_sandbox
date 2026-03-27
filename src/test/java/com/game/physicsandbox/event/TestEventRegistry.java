package com.game.physicsandbox.event;

import com.game.physicsandbox.exception.EventNotRegisteredException;
import com.game.physicsandbox.physics.event.Event;
import com.game.physicsandbox.physics.event.EventListener;
import com.game.physicsandbox.physics.event.EventRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class TestEventRegistry {

    private final EventRegistry registry = new EventRegistry();

    @Test
    public void test() {
        registry.register(TestEvent.class, () -> new TestEvent("Test Event"), 32);
        TestEvent event = registry.get(TestEvent.class);
        log.info(event.toString());
        registry.recycle(event);
        registry.get(TestEvent.class);
        log.info(event.toString());
        Assertions.assertThrows(EventNotRegisteredException.class, () -> registry.get(Event.class));
        Assertions.assertThrows(EventNotRegisteredException.class, () -> registry.recycle(new TestNotRegisteredEvent()));
    }
}
