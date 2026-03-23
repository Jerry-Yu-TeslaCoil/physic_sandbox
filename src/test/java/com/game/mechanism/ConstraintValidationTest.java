package com.game.mechanism;

import com.game.physicsandbox.physics.mechanism.Vector2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class ConstraintValidationTest {
    private static final double LENGTH = 10;

    private Vector2 aPos = new Vector2(-LENGTH, 0);
    private Vector2 bPos = new Vector2(-LENGTH, 0);
    private Vector2 cPos = new Vector2(0, 0);
    private Vector2 aDelta = new Vector2(0, 0);
    private Vector2 bDelta = new Vector2(0, 0);
    private Vector2 cDelta = new Vector2(0, 0);
    private final double aMass = 1;
    private final double bMass = 2;
    private final double cMass = Double.POSITIVE_INFINITY;

    private final double spf = 1.0 / 240;
    private final double ZERO_DELTA = 1e-2;
    private final Vector2 gravity = new Vector2(0, -9.8);

    @Test
    public void testGravity() {
        boolean firstReached = false;
        double firstReachBottomTime = -1.0;
        log.info("帧长: {}", spf);
        //时间线
        for (double t = 0; t <= 1; t += spf) {
            //重力应用
            Vector2 gravityDiff = gravity.mul(spf).mul(spf);
            bDelta = bDelta.add(gravityDiff);
            //碰撞约束计算
            //无约束下落
            bPos = bPos.add(bDelta);
            //渲染
            log.info("B的位置: {}", bPos);
            if (bPos.y() < -LENGTH + ZERO_DELTA && !firstReached) {
                firstReached = true;
                firstReachBottomTime = t;
            }
        }
        log.info("第一次到达-10，时间为 {}", firstReachBottomTime);
    }

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
        boolean aReached = false;
        double aReachedTime = -1.0;
        boolean bReached = false;
        double bReachedTime = -1.0;
        log.info("帧长: {}", spf);
        //时间线
        for (double t = 0; t <= 5; t += spf) {
            Vector2 aRec = aPos;
            Vector2 bRec = bPos;
            //重力应用
            Vector2 gravityDiff = gravity.mul(spf).mul(spf);
            //log.info("当前帧重力影响: {}", gravityDiff);
            aDelta = aDelta.add(gravityDiff);
            bDelta = bDelta.add(gravityDiff);
            //碰撞约束计算
            Vector2 sumDiff = new Vector2(0, 0);
            for (int i = 0; i < 6; i++) {
                Vector2 b2c = cPos.sub(bPos);
                if (b2c.lengthSquared() < LENGTH - ZERO_DELTA || b2c.lengthSquared() > LENGTH + ZERO_DELTA) {
                    Vector2 posDiff = b2c.sub(b2c.normalize().mul(LENGTH));
                    Vector2 deltaDiff = bDelta.projectionOnto(b2c).negate();
                    //log.info("位置补偿向量: {}", posDiff);
                    //log.info("约束补偿向量: {}", deltaDiff);
                    sumDiff = sumDiff.add(deltaDiff);
                    bDelta = bDelta.add(deltaDiff);
                    //log.info("约束后B速度在BC方向的投影: {}", bDelta.projectionOnto(b2c));
                    bPos = bPos.add(posDiff);
                }
            }
            aPos = aPos.add(aDelta);
            bPos = bPos.add(bDelta);
            //log.info("A的位置变化: {}", aPos.sub(aRec));
            //log.info("B的位置变化: {}", bPos.sub(bRec));
            //log.info("此帧总补偿向量: {}", sumDiff);
            //渲染
            //log.info("A的位置: {}", aPos);
            log.info("B的位置: {}", bPos);
            //log.info("AB的Y方向差别: {}", aPos.y() - bPos.y());
            //log.info("B的速度: {}", bDelta);
            log.info("B与C的距离: {}", cPos.sub(bPos).length());
            if (aPos.y() < -LENGTH + ZERO_DELTA && !aReached) {
                aReached = true;
                aReachedTime = t;
            }
            if (bPos.y() < -LENGTH + ZERO_DELTA && !bReached) {
                bReached = true;
                bReachedTime = t;
            }
        }
        log.info("A第一次到达底端，时间为 {}", aReachedTime);
        log.info("B第一次到达底端，时间为 {}", bReachedTime);
    }
}
