package com.game.physicsandbox.event;

import org.junit.Test;

public class EventBusTest {
    private final EventBus eventBus;

    public EventBusTest() {
        eventBus = new EventBus();
    }

    @Test
    public void test() {
        TestListener listener = new TestListener();
        eventBus.register(listener);

        eventBus.publish(new TestEvent("Test Event Published"));

        eventBus.dispatchEvents(System.currentTimeMillis());

        eventBus.unregister(listener);

        eventBus.dispatchEvents(System.currentTimeMillis());
    }
}
