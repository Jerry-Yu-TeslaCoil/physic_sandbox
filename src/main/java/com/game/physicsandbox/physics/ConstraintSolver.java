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
}
