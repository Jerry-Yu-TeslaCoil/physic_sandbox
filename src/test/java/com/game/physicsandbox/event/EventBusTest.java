package com.game.physicsandbox.event;

import org.junit.Test;

public class EventBusTest {
    private final EventBus eventBus;

    public EventBusTest() {
        eventBus = new EventBus(null);
    }

    @Test
    public void test() {
        TestListener listener = new TestListener();
        MultipleExtendListener extendListener = new MultipleExtendListener();

        eventBus.register(listener);
        eventBus.register(extendListener);

        eventBus.publish(new TestEvent("Test Event Published"));

        eventBus.update(System.currentTimeMillis(), (long)(1e9 / 480));

        eventBus.unregister(listener);

        eventBus.update(System.currentTimeMillis(), (long)(1e9 / 480));
    }
}
