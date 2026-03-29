package com.game.physicsandbox.frame;

import com.game.physicsandbox.event.EventBus;
import com.game.physicsandbox.lifecycle.LifeCycleManager;
import com.game.physicsandbox.mechanism.UpdateStage;
import com.game.physicsandbox.physics.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CalculateFrame {

    private final int CONSTRAINT_ITERATE_TIME;

    private final LifeCycleManager lifeCycleManager;
    private final EventBus eventBus;
    private final ComponentUpdater componentUpdater;
    private final PhysicAnalyzer physicAnalyzer;
    private final PhysicUpdater physicUpdater;
    private final ColliderAnalyzer colliderAnalyzer;
    private final ConstraintSolver constraintSolver;

    @Autowired
    public CalculateFrame(@Value("${settings.physics.constraint_iterate_time}") int constraintIterateTime,
                          LifeCycleManager lifeCycleManager,
                          EventBus eventBus,
                          ComponentUpdater componentUpdater,
                          PhysicAnalyzer physicAnalyzer,
                          PhysicUpdater physicUpdater, ColliderAnalyzer colliderAnalyzer,
                          ConstraintSolver constraintSolver) {
        CONSTRAINT_ITERATE_TIME = constraintIterateTime;
        this.lifeCycleManager = lifeCycleManager;
        this.eventBus = eventBus;
        this.componentUpdater = componentUpdater;
        this.physicAnalyzer = physicAnalyzer;
        this.physicUpdater = physicUpdater;
        this.colliderAnalyzer = colliderAnalyzer;
        this.constraintSolver = constraintSolver;
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

        lifeCycleManager.stageSwift(UpdateStage.CLEAN);

        //更新生命周期管理器
        lifeCycleManager.update(currentTime, deltaTime);
        log.trace("Update LifeCycleManager: {}ns", deltaTime);

        lifeCycleManager.stageSwift(UpdateStage.EVENT);

        //更新事件总线
        eventBus.update(currentTime, deltaTime);
        log.trace("Update EventBus: {}ns", currentTime);

        lifeCycleManager.stageSwift(UpdateStage.COMPONENTS);

        //更新组件更新器
        componentUpdater.update(currentTime, deltaTime);
        log.trace("Update ComponentUpdater: {}ns", currentTime);

        lifeCycleManager.stageSwift(UpdateStage.PHYSICS);

        //更新物理分析器
        this.physicAnalyzer.update(currentTime, deltaTime);
        log.trace("Update PhysicAnalyzer: {}ns", currentTime);

        lifeCycleManager.stageSwift(UpdateStage.UPDATE);

        //更新物理更新器
        this.physicUpdater.update(currentTime, deltaTime);
        log.trace("Update PhysicUpdater: {}ns", currentTime);

        for (int iterateTime = 0; iterateTime < CONSTRAINT_ITERATE_TIME; iterateTime++) {

            lifeCycleManager.stageSwift(UpdateStage.COLLISION);

            //更新碰撞分析器
            colliderAnalyzer.update(currentTime, deltaTime);
            log.trace("Update ColliderAnalyzer: {}ns", currentTime);

            lifeCycleManager.stageSwift(UpdateStage.CONSTRAINT);

            //更新约束求解器
            constraintSolver.update(currentTime, deltaTime);
            log.trace("Update ConstraintSolver: {}ns", currentTime);
        }
    }
}
