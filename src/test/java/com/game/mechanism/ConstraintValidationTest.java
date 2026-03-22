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

    private final double spf = 1.0 / 60;

    /**
     * 测试约束计算的原理可行性。
     * <p>
     * 场景设定：C是运动学的，与静止物体B用一个刚性定长连杆连接。B受到重力作用即将下坠。
     * <p>
     * 数据设定：C在(0, 0)处，运动学性质；
     * B在(-1, 0)处，质量为2且静止；
     * 帧长为2秒。
     * 用冲量修正实现上述约束。
     */
    @Test
    public void testConnectionConstraint() {
        //时间线
        for (double t = 0; t <= 2; t += spf) {
            //重力应用
            Vector2 gravity = new Vector2(0, 9.8);
            Vector2 deltaY = MathUtil.mul(gravity, spf);
            bSpeed = MathUtil.add(bSpeed, deltaY);
            //碰撞约束计算
            Vector2 bTarget = MathUtil.add(bPos, bSpeed);
            Vector2 b2c = MathUtil.add(cPos, MathUtil.negativeOf(bTarget));
        }
    }
}
