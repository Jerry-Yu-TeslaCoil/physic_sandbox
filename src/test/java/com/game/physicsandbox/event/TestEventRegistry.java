package com.game.physicsandbox.event;

import com.game.physicsandbox.exception.EventNotRegisteredException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

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
        Assert.assertThrows(EventNotRegisteredException.class, () -> registry.get(Event.class));
        Assert.assertThrows(EventNotRegisteredException.class, () -> registry.recycle(new TestNotRegisteredEvent()));
    }
}
