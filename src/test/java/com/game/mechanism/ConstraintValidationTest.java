package com.game.mechanism;

import com.game.physicsandbox.physics.util.Vector2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class ConstraintValidationTest {
    private static final double LENGTH = 10;

    private Vector2 aPos = new Vector2(-2 * LENGTH, 0);
    private Vector2 bPos = new Vector2(-LENGTH, 0);
    private Vector2 cPos = new Vector2(0, 0);
    private Vector2 aSpeed = new Vector2(0, 0);
    private Vector2 bSpeed = new Vector2(0, 0);
    private Vector2 cSpeed = new Vector2(0, 0);
    private final double aMass = 1;
    private final double bMass = 2;
    private final double cMass = Double.POSITIVE_INFINITY;

    private final double spf = 1.0 / 480;
    private final double ZERO_DELTA = 1e-2;
    private final Vector2 gravity = new Vector2(0, -9.8);

    /**
     * 单重力测试
     */
    @Test
    public void testGravity() {
        boolean firstReached = false;
        double firstReachBottomTime = -1.0;
        log.info("帧长: {}", spf);
        //时间线
        for (double t = 0; t <= 2; t += spf) {
            //重力应用
            Vector2 gravityDiff = gravity.mul(spf);
            bSpeed = bSpeed.add(gravityDiff);
            //碰撞约束计算
            //无约束下落
            bPos = bPos.add(bSpeed.mul(spf));
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
     * 帧长为spf秒。
     * 用冲量修正实现上述约束。
     */
    @Test
    public void testConnectionConstraint() {
        boolean aReached = false;
        double aReachedTime = -1.0;
        boolean bReached = false;
        double bReachedTime = -1.0;
        log.info("帧长: {}", spf);
        long timeRec = System.nanoTime();
        //时间线
        for (double t = 0; t <= 5; t += spf) {
            Vector2 aRec = aPos;
            Vector2 bRec = bPos;
            //重力应用
            Vector2 gravityDiff = gravity.mul(spf);
            //log.info("当前帧重力影响: {}", gravityDiff);
            aSpeed = aSpeed.add(gravityDiff);
            bSpeed = bSpeed.add(gravityDiff);
            aPos = aPos.add(aSpeed.mul(spf));
            bPos = bPos.add(bSpeed.mul(spf));
            //碰撞约束计算
            Vector2 sumDiff = new Vector2(0, 0);
            for (int i = 0; i < 6; i++) {
                Vector2 b2c = cPos.sub(bPos);
                if (b2c.lengthSquared() < LENGTH - ZERO_DELTA || b2c.lengthSquared() > LENGTH + ZERO_DELTA) {
                    Vector2 posDiff = b2c.sub(b2c.normalize().mul(LENGTH));
                    Vector2 deltaDiff = bSpeed.projectionOnto(b2c).negate();
                    //log.info("位置补偿向量: {}", posDiff);
                    //log.info("约束补偿向量: {}", deltaDiff);
                    sumDiff = sumDiff.add(deltaDiff);
                    bSpeed = bSpeed.add(deltaDiff);
                    //log.info("约束后B速度在BC方向的投影: {}", bDelta.projectionOnto(b2c));
                    bPos = bPos.add(posDiff);
                }
            }
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
        log.info("计算帧帧率: {} fps", 1.0 / spf);
        log.info("{} 秒游戏时间计算全过程耗时: {} 毫秒", 5, (System.nanoTime() - timeRec) / 1000000);
        log.info("A第一次到达底端，时间为 {}", aReachedTime);
        log.info("B第一次到达底端，时间为 {}", bReachedTime);
    }

    /**
     * 测试约束计算的原理可行性。
     * <p>
     * 场景设定：C是运动学的，与静止物体B用一个刚性定长连杆连接。B受到重力作用即将下坠。
     * 同时A在B左侧，与B同样使用刚性连杆连接。
     * <p>
     * 数据设定：C在(0, 0)处，运动学性质；
     * B在(-10, 0)处，质量为2且静止；
     * A在(-20, 0)处，质量为1且静止；
     * 帧长为spf秒。
     * 用冲量修正实现上述约束。
     */
    @Test
    public void testConnectionMultipleConstraint() {
        boolean aReached = false;
        double aReachedTime = -1.0;
        boolean bReached = false;
        double bReachedTime = -1.0;
        log.info("帧长: {}", spf);
        long timeRec = System.nanoTime();
        Vector2 aRec = aPos;
        Vector2 bRec = bPos;
        //时间线
        for (double t = 0; t <= 5; t += spf) {
            //重力应用
            Vector2 gravityDiff = gravity.mul(spf);
            //log.info("当前帧重力影响: {}", gravityDiff);
            aSpeed = aSpeed.add(gravityDiff);
            bSpeed = bSpeed.add(gravityDiff);
            //碰撞约束计算
            Vector2 sumDiff = new Vector2(0, 0);
            for (int i = 0; i < 12; i++) {
                //BC连杆约束
                double b2cDeltaLength;
                Vector2 b2c = cPos.sub(bPos);
                double K = 100;
                if (b2c.lengthSquared() < LENGTH - ZERO_DELTA || b2c.lengthSquared() > LENGTH + ZERO_DELTA) {
                    b2cDeltaLength = b2c.length() - LENGTH;
                    //这是个符号量。b2cDeltaLength < 0时，被压缩，B和C都要被推开（但C是运动学的）
                    Vector2 bForce = b2c.normalize().mul(b2cDeltaLength * K);
                    Vector2 bElasticSpeed = bForce.mul(1 / bMass);
                    bSpeed = bSpeed.add(bElasticSpeed);
                    bPos = bPos.add(bElasticSpeed.mul(spf));
                }
                aPos = aPos.add(aSpeed.mul(spf));
                bPos = bPos.add(bSpeed.mul(spf));
                //AB连杆约束
                //AB位置满足((aPos + delta) - (bPos - delta)).length = 10
                //AB速度满足
                // (aSpeed.projection(AB) * aMass + bSpeed.projection(AB) * bMass) = abSpeed * (aMass + bMass)
                //用弹簧
                Vector2 a2b = bPos.sub(aPos);
                double a2bDeltaLength;
                if (a2b.lengthSquared() < LENGTH - ZERO_DELTA || a2b.lengthSquared() > LENGTH + ZERO_DELTA) {
                    a2bDeltaLength = a2b.length() - LENGTH;
                    //这是个符号量。a2bDeltaLength < 0时，被压缩，A和B都要被推开
                    Vector2 aForce = a2b.normalize().mul(a2bDeltaLength * K);
                    Vector2 bForce = a2b.normalize().negate().mul(a2bDeltaLength * K);
                    Vector2 aElasticSpeed = aForce.mul(1 / aMass);
                    Vector2 bElasticSpeed = bForce.mul(1 / aMass);
                    aSpeed = aSpeed.add(aElasticSpeed);
                    bSpeed = bSpeed.add(bElasticSpeed);
                    aPos = aPos.add(aElasticSpeed.mul(spf));
                    bPos = bPos.add(bElasticSpeed.mul(spf));
                }
            }
            //log.info("A的位置变化: {}", aPos.sub(aRec));
            //log.info("B的位置变化: {}", bPos.sub(bRec));
            //log.info("此帧总补偿向量: {}", sumDiff);
            //渲染
            log.info("A的位置: {}", aPos);
            log.info("B的位置: {}", bPos);
            //log.info("AB的Y方向差别: {}", aPos.y() - bPos.y());
            //log.info("B的速度: {}", bDelta);
            log.info("A与B的距离: {}", bPos.sub(aPos).length());
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
        log.info("计算帧帧率: {} fps", 1.0 / spf);
        log.info("{} 秒游戏时间计算全过程耗时: {} 毫秒", 5, (System.nanoTime() - timeRec) / 1000000);
        log.info("A第一次到达底端，时间为 {}", aReachedTime);
        log.info("B第一次到达底端，时间为 {}", bReachedTime);
    }

    /**
     * 单重力测试
     */
    @Test
    public void testGravityVerlet() {
        boolean firstReached = false;
        double firstReachBottomTime = -1.0;
        log.info("帧长: {}", spf);
        //时间线
        Vector2 bRec = bPos;
        for (double t = 0; t <= 2; t += spf) {
            //重力应用
            Vector2 gravityDiff = gravity.mul(spf * spf);
            Vector2 bPosNext = bPos.mul(2).sub(bRec).add(gravityDiff);
            //碰撞约束计算
            //无约束下落
            //更新
            bRec = bPos;
            bPos = bPosNext;
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
     * 帧长为spf秒。
     * 用冲量修正实现上述约束。
     */
    @Test
    public void testConnectionConstraintVerlet() {
        boolean aReached = false;
        double aReachedTime = -1.0;
        boolean bReached = false;
        double bReachedTime = -1.0;
        log.info("帧长: {}", spf);
        long timeRec = System.nanoTime();
        Vector2 aRec = aPos;
        Vector2 bRec = bPos;
        //时间线
        for (double t = 0; t <= 5; t += spf) {
            //重力应用
            Vector2 gravityDiff = gravity.mul(spf * spf);
            //log.info("当前帧重力影响: {}", gravityDiff);
            Vector2 aPosNext = aPos.mul(2).sub(aRec).add(gravityDiff);
            Vector2 bPosNext = bPos.mul(2).sub(bRec).add(gravityDiff);
            //碰撞约束计算
            for (int i = 0; i < 6; i++) {
                Vector2 b2c = cPos.sub(bPosNext);
                if (b2c.lengthSquared() < LENGTH - ZERO_DELTA || b2c.lengthSquared() > LENGTH + ZERO_DELTA) {
                    Vector2 posDiff = b2c.sub(b2c.normalize().mul(LENGTH));
                    bPosNext = bPosNext.add(posDiff);
                }
            }
            aRec = aPos;
            bRec = bPos;

            aPos = aPosNext;
            bPos = bPosNext;
            //log.info("A的位置变化: {}", aPosNext.sub(aRec));
            //log.info("B的位置变化: {}", bPosNext.sub(bRec));
            //log.info("此帧总补偿向量: {}", sumDiff);
            //渲染
            //log.info("A的位置: {}", aPosNext);
            log.info("B的位置: {}", bPosNext);
            //log.info("AB的Y方向差别: {}", aPosNext.y() - bPosNext.y());
            //log.info("B的速度: {}", bDelta);
            log.info("B与C的距离: {}", cPos.sub(bPosNext).length());
            if (aPosNext.y() < -LENGTH + ZERO_DELTA && !aReached) {
                aReached = true;
                aReachedTime = t;
            }
            if (bPosNext.y() < -LENGTH + ZERO_DELTA && !bReached) {
                bReached = true;
                bReachedTime = t;
            }
        }
        log.info("计算帧帧率: {} fps", 1.0 / spf);
        log.info("{} 秒游戏时间计算全过程耗时: {} 毫秒", 5, (System.nanoTime() - timeRec) / 1000000);
        log.info("A第一次到达底端，时间为 {}", aReachedTime);
        log.info("B第一次到达底端，时间为 {}", bReachedTime);
    }

    /**
     * 测试约束计算的原理可行性。
     * <p>
     * 场景设定：C是运动学的，与静止物体B用一个刚性定长连杆连接。B受到重力作用即将下坠。
     * 同时A在B左侧，与B同样使用刚性连杆连接。
     * <p>
     * 数据设定：C在(0, 0)处，运动学性质；
     * B在(-10, 0)处，质量为2且静止；
     * A在(-20, 0)处，质量为1且静止；
     * 帧长为spf秒。
     * 用冲量修正实现上述约束。
     * 使用Verlet积分
     * x2 = 2x1 - x0 + at^2
     */
    @Test
    public void testConnectionMultipleConstraintVerlet() {
        boolean aReached = false;
        double aReachedTime = -1.0;
        boolean bReached = false;
        double bReachedTime = -1.0;
        log.info("帧长: {}", spf);
        long timeRec = System.nanoTime();
        //上一帧记录
        Vector2 aRec = aPos;
        Vector2 bRec = bPos;
        //时间线
        for (double t = 0; t <= 5; t += spf) {
            //重力应用
            Vector2 gravityAcc = gravity.mul(spf * spf);
            //log.info("当前帧重力影响: {}", gravityAcc);
            Vector2 aPosNext = aPos.mul(2).sub(aRec).add(gravityAcc);
            Vector2 bPosNext = bPos.mul(2).sub(bRec).add(gravityAcc);
            //碰撞约束计算
            Vector2 sumDiff = new Vector2(0, 0);
            for (int i = 0; i < 12; i++) {
                //BC连杆约束
                double b2cDeltaLength;
                Vector2 b2c = cPos.sub(bPosNext);
                if (b2c.lengthSquared() < LENGTH - ZERO_DELTA || b2c.lengthSquared() > LENGTH + ZERO_DELTA) {
                    b2cDeltaLength = b2c.length() - LENGTH;
                    bPosNext = cPos.sub(b2c.normalize().mul(LENGTH));
                }
                //log.info("第 {} 次迭代，B与C的距离: {}", i, bPos.sub(cPos).length());
                //AB连杆约束
                //AB位置满足((aPos + delta) - (bPos - delta)).length = 10
                //AB速度满足
                // (aSpeed.projection(AB) * aMass + bSpeed.projection(AB) * bMass) = abSpeed * (aMass + bMass)
                //因此位置变化比为质量反比
                //直接更新位置
                Vector2 a2b = bPosNext.sub(aPosNext);
                double a2bDeltaLength;
                if (a2b.lengthSquared() < LENGTH - ZERO_DELTA || a2b.lengthSquared() > LENGTH + ZERO_DELTA) {
                    a2bDeltaLength = a2b.length() - LENGTH;
                    //这是个符号量。a2bDeltaLength < 0时，被压缩，A和B都要被推开
                    double massSum = aMass + bMass;
                    Vector2 a2bDelta = a2b.normalize().mul(a2bDeltaLength).mul((bMass / massSum));
                    Vector2 b2aDelta = a2b.negate().normalize().mul(a2bDeltaLength).mul((aMass / massSum));
                    aPosNext = aPosNext.add(a2bDelta);
                    bPosNext = bPosNext.add(b2aDelta);
                }
                //log.info("第 {} 次迭代，A与B的距离: {}", i, aPos.sub(bPos).length());
            }
            aRec = aPos;
            bRec = bPos;

            aPos = aPosNext;
            bPos = bPosNext;
            //log.info("A的位置变化: {}", aPos.sub(aRec));
            //log.info("B的位置变化: {}", bPos.sub(bRec));
            //log.info("此帧总补偿向量: {}", sumDiff);
            //渲染
            log.info("A的位置: {}", aPos);
            log.info("B的位置: {}", bPos);
            //log.info("AB的Y方向差别: {}", aPos.y() - bPos.y());
            //log.info("B的速度: {}", bDelta);
            log.info("A与B的距离: {}", bPos.sub(aPos).length());
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
        log.info("计算帧帧率: {} fps", 1.0 / spf);
        log.info("{} 秒游戏时间计算全过程耗时: {} 毫秒", 5, (System.nanoTime() - timeRec) / 1000000);
        log.info("A第一次到达底端，时间为 {}", aReachedTime);
        log.info("B第一次到达底端，时间为 {}", bReachedTime);
    }
}
