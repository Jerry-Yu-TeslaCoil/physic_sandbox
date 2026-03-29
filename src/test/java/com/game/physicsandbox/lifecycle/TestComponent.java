package com.game.physicsandbox.lifecycle;

import com.game.physicsandbox.object.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestComponent extends Component {
    @Override
    public void update(long currentTime, long delta) {
        log.info("Test component update called at {} gap time {}", currentTime, delta);
    }
}
