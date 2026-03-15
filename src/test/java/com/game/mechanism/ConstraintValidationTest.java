package com.game.mechanism;

import com.game.physicsandbox.physics.mechanism.Vector2;
import com.game.physicsandbox.physics.util.MathUtil;
import org.junit.jupiter.api.Test;

public class ConstraintValidationTest {
    private Vector2 aPos = new Vector2(-2, -1);
    private Vector2 bPos = new Vector2(-1, 0);
    private Vector2 cPos = new Vector2(0, 0);
    private Vector2 aSpeed = new Vector2(1, 1);
    private Vector2 bSpeed = new Vector2(0, 0);
    private Vector2 cSpeed = new Vector2(0, 0);
    private final double aMass = 1;
    private final double bMass = 2;
    private final double cMass = Double.POSITIVE_INFINITY;

    /**
     * 测试约束计算的原理可行性。
     * <p>
     * 场景设定：C是运动学的，与静止物体B用一个刚性定长连杆连接；同时运动中的A与B即将相撞。求解这一帧后A与B物体的状态。
     * <p>
     * 数据设定：C在(0, 0)处，运动学性质；
     * B在(-1, 0)处，质量为2且静止；
     * A在(-2, -1)处，质量为1且速度为(1, 1)。
     * 帧长为2秒。
     * A，B应满足的约束：
     * A与B的动量守恒，能量守恒。
     * 在1秒时A与B发生了碰撞，然后依次修改A，B位置并迭代多次。
     * 用弹簧来粗略实现上述约束。
     */
    @Test
    public void testConstraintValidation() {
        //时间线
        for (double t = 0; t <= 2; t += 1.0 / 60) {
            //碰撞约束计算
            Vector2 aTarget = MathUtil.add(aPos, aSpeed);
            Vector2 bTarget = MathUtil.add(bPos, bSpeed);
            if (MathUtil.distance(aTarget, bTarget) < 1) {
                //TODO: 完成机制验证
            }
        }
    }
}
