package com.game.physicsandbox.event;

import com.game.physicsandbox.physics.event.EventBus;
import org.junit.jupiter.api.Test;

public class EventBusTest {
    private EventBus eventBus;

    public EventBusTest() {
        eventBus = new EventBus();
    }

    @Test
    public void test() {
        TestListener listener = new TestListener();
        eventBus.registerListener(listener);

        eventBus.publish(new TestEvent("Test Event Published"));

        eventBus.dispatchEvents(System.currentTimeMillis());

        eventBus.unregisterListener(listener);

        eventBus.dispatchEvents(System.currentTimeMillis());
    }
}
