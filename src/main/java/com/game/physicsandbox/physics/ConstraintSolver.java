package com.game.physicsandbox.physics;

import com.game.physicsandbox.mechanism.ComponentExecutor;
import org.springframework.stereotype.Component;

/**
 * 约束求解器。对组件的约束进行求解，并修正物体位置。
 * 将约束求解下放到组件。本质上是个约束组件执行器。
 *
 * @author Jerry-Yu-TeslaCoil
 */
@Component
public class ConstraintSolver extends ComponentExecutor {

    /**
     * 在约束位置迭代求解完毕后调用。为物体更新速度。
     * @param currentTime 当前时间纳秒
     * @param deltaTime 时间增量纳秒
     */
    public void updateAcceleration(long currentTime, long deltaTime) {
        components.stream().filter(component -> component instanceof Constraint)
                .forEach(component -> ((Constraint) component).finalize(currentTime, deltaTime));
    }
}
