package com.game.physicsandbox.frame;

import com.game.physicsandbox.event.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CalculateFrame {

    private final EventBus eventBus;

    @Autowired
    public CalculateFrame(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    /**
     * 计算帧更新。根据提供的当前时间和时间增量更新各执行器
     * @param currentTime 当前时间纳秒
     * @param deltaTime 时间增量纳秒
     */
    public void update(long currentTime, long deltaTime) {

        log.trace("Calculate frame time: from {} to {}", currentTime - deltaTime, currentTime);
        log.trace("Fixed time gap: {}ns", currentTime - deltaTime);
        log.trace("Set fixed time gap: {}ns", deltaTime);

        //更新事件总线
        eventBus.dispatchEvents(currentTime);
        log.trace("Update EventBus: {}ns", currentTime);


    }
}
