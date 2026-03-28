package com.game.physicsandbox.frame;

import com.game.physicsandbox.event.EventBus;
import com.game.physicsandbox.physics.ComponentUpdater;
import com.game.physicsandbox.physics.PhysicAnalyzer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CalculateFrame {

    private final EventBus eventBus;
    private final ComponentUpdater componentUpdater;
    private final PhysicAnalyzer physicAnalyzer;

    @Autowired
    public CalculateFrame(EventBus eventBus,
                          ComponentUpdater componentUpdater,
                          PhysicAnalyzer physicAnalyzer) {
        this.eventBus = eventBus;
        this.componentUpdater = componentUpdater;
        this.physicAnalyzer = physicAnalyzer;
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
        eventBus.dispatchEvents(currentTime, deltaTime);
        log.trace("Update EventBus: {}ns", currentTime);

        //更新组件更新器
        componentUpdater.update(currentTime, deltaTime);
        log.trace("Update ComponentUpdater: {}ns", currentTime);

        //更新物体分析器
        this.physicAnalyzer.update(currentTime, deltaTime);
        log.trace("Update PhysicAnalyzer: {}ns", currentTime);
    }
}
