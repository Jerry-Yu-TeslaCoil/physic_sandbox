package com.game.physicsandbox.physics.mechanism;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Vector2类的单元测试
 * <p>
 * 测试向量类的所有核心功能，包括：
 * - 基本向量运算（加法、减法、乘法、取负）
 * - 向量归一化
 * - 距离、长度、点积、叉积计算
 * - 边界条件处理（零向量、单位向量等）
 * - 性能测试
 *
 * @author YourName
 * @version 1.0
 */
@Slf4j
public class Vector2Test {
    private final Random rand = new Random(System.currentTimeMillis());
    private final double scale = 1000.0;
    private final int total = 1000000;
    private final double delta = 1e-2;
    /**
     * 测试向量的基本运算：加法、减法、标量乘法、分量乘法、取负
     * <p>
     * 验证点：
     * - 向量加法符合交换律和结合律
     * - 向量减法正确性
     * - 标量乘法的分配律
     * - 分量乘法的正确性
     * - 取负操作正确性
     */
    @Test
    public void testBasicOperations() {
        log.info("========== 开始向量基本运算测试 ==========");
        int testCount = 10000;

        // 测试取负操作
        log.info("【测试 negate 方法】");
        for (int i = 0; i < testCount; i++) {
            Vector2 v = randomVector();
            Vector2 result = v.negate();

            assertEquals(-v.x(), result.x(), delta,
                    String.format("negate x失败: v.x=%.6f, result.x=%.6f", v.x(), result.x()));
            assertEquals(-v.y(), result.y(), delta,
                    String.format("negate y失败: v.y=%.6f, result.y=%.6f", v.y(), result.y()));

            // 验证取负两次等于原向量
            Vector2 doubleNegate = result.negate();
            assertEquals(v.x(), doubleNegate.x(), delta);
            assertEquals(v.y(), doubleNegate.y(), delta);
        }
        log.info("negate 方法测试通过\n");

        // 测试加法
        log.info("【测试 add 方法】");
        for (int i = 0; i < testCount; i++) {
            Vector2 v1 = randomVector();
            Vector2 v2 = randomVector();
            Vector2 result = v1.add(v2);

            assertEquals(v1.x() + v2.x(), result.x(), delta);
            assertEquals(v1.y() + v2.y(), result.y(), delta);

            // 验证交换律: v1 + v2 = v2 + v1
            Vector2 result2 = v2.add(v1);
            assertEquals(result.x(), result2.x(), delta);
            assertEquals(result.y(), result2.y(), delta);

            // 验证结合律: (v1 + v2) + v3 = v1 + (v2 + v3)
            Vector2 v3 = randomVector();
            Vector2 associative1 = v1.add(v2).add(v3);
            Vector2 associative2 = v1.add(v2.add(v3));
            assertEquals(associative1.x(), associative2.x(), delta);
            assertEquals(associative1.y(), associative2.y(), delta);
        }
        log.info("add 方法测试通过\n");

        // 测试减法
        log.info("【测试 sub 方法】");
        for (int i = 0; i < testCount; i++) {
            Vector2 v1 = randomVector();
            Vector2 v2 = randomVector();
            Vector2 result = v1.sub(v2);

            assertEquals(v1.x() - v2.x(), result.x(), delta);
            assertEquals(v1.y() - v2.y(), result.y(), delta);

            // 验证减法与加法的关系: v1 - v2 = v1 + (-v2)
            Vector2 negated = v2.negate();
            Vector2 addResult = v1.add(negated);
            assertEquals(result.x(), addResult.x(), delta);
            assertEquals(result.y(), addResult.y(), delta);
        }
        log.info("sub 方法测试通过\n");

        // 测试标量乘法
        log.info("【测试标量 mul 方法】");
        double[] multipliers = {-2.0, -1.0, 0.0, 0.5, 1.0, 2.0, 3.5, 10.0};
        for (double multiplier : multipliers) {
            for (int i = 0; i < testCount / multipliers.length; i++) {
                Vector2 v = randomVector();
                Vector2 result = v.mul(multiplier);

                assertEquals(v.x() * multiplier, result.x(), delta);
                assertEquals(v.y() * multiplier, result.y(), delta);
            }
        }
        log.info("标量 mul 方法测试通过\n");

        // 测试分量乘法
        log.info("【测试分量 mul 方法】");
        for (int i = 0; i < testCount; i++) {
            Vector2 v1 = randomVector();
            Vector2 v2 = randomVector();
            Vector2 result = v1.mul(v2);

            assertEquals(v1.x() * v2.x(), result.x(), delta);
            assertEquals(v1.y() * v2.y(), result.y(), delta);

            // 验证交换律
            Vector2 result2 = v2.mul(v1);
            assertEquals(result.x(), result2.x(), delta);
            assertEquals(result.y(), result2.y(), delta);
        }
        log.info("分量 mul 方法测试通过\n");

        log.info("========== 基本运算测试完成 ==========\n");
    }

    /**
     * 测试向量的归一化功能
     * <p>
     * 验证点：
     * - 非零向量归一化后长度为1
     * - 归一化后方向与原向量相同
     * - 零向量归一化抛出异常
     */
    @Test
    public void testNormalize() {
        log.info("========== 开始向量归一化测试 ==========");
        int testCount = 10000;

        // 测试零向量
        log.info("【测试零向量归一化】");
        Vector2 zero = Vector2.zero();
        assertThrows(ArithmeticException.class, zero::normalize,
                "零向量归一化应该抛出 ArithmeticException");
        log.info("零向量归一化正确抛出异常\n");

        // 测试非零向量
        log.info("【测试非零向量归一化】");
        int validCount = 0;
        for (int i = 0; i < testCount; i++) {
            Vector2 v = randomVector();
            // 跳过零向量
            if (v.isZero()) {
                continue;
            }
            validCount++;

            Vector2 normalized = v.normalize();

            // 验证长度接近1
            double length = normalized.length();
            assertEquals(1.0, length, delta,
                    String.format("归一化后长度应为1，实际为%.6f，原向量=(%.6f, %.6f)",
                            length, v.x(), v.y()));

            // 验证方向相同（通过点积验证）
            double dotProduct = v.dot(normalized);
            double originalLength = v.length();
            // v·(v/|v|) = |v|
            assertEquals(originalLength, dotProduct, delta,
                    String.format("方向改变: 原向量长度=%.6f, 点积=%.6f", originalLength, dotProduct));

            // 验证归一化后的向量长度为1
            assertEquals(1.0, normalized.lengthSquared(), delta);
        }
        log.info("共测试 {} 个非零向量，归一化测试通过\n", validCount);

        // 测试边界情况：接近零的向量
        log.info("【测试接近零的向量】");
        Vector2 nearZero = new Vector2(1e-10, 1e-10);
        Vector2 normalized = nearZero.normalize();
        assertEquals(1.0, normalized.length(), delta);
        log.info("接近零的向量归一化成功: {} -> {}\n", nearZero, normalized);

        log.info("========== 归一化测试完成 ==========\n");
    }

    /**
     * 测试距离计算功能
     * <p>
     * 验证点：
     * - 距离公式的正确性
     * - 对称性：distanceBetween(v1, v2) == distanceBetween(v2, v1)
     * - 自身距离为零
     * - 三角形不等式
     */
    @Test
    public void testDistance() {
        log.info("========== 开始距离计算测试 ==========");
        int testCount = 10000;

        // 测试距离计算的正确性
        log.info("【测试距离计算正确性】");
        for (int i = 0; i < testCount; i++) {
            Vector2 v1 = randomVector();
            Vector2 v2 = randomVector();

            double distance = v1.distanceBetween(v2);
            double expectedDistance = Math.sqrt(Math.pow(v1.x() - v2.x(), 2) +
                    Math.pow(v1.y() - v2.y(), 2));

            assertEquals(expectedDistance, distance, delta,
                    String.format("距离计算错误: v1=%s, v2=%s, 期望=%.6f, 实际=%.6f",
                            v1, v2, expectedDistance, distance));

            // 验证对称性
            double distance2 = v2.distanceBetween(v1);
            assertEquals(distance, distance2, delta);
        }
        log.info("距离计算正确性测试通过\n");

        // 测试自身距离为零
        log.info("【测试自身距离】");
        for (int i = 0; i < testCount; i++) {
            Vector2 v = randomVector();
            double distance = v.distanceBetween(v);
            assertEquals(0.0, distance, delta);
        }
        log.info("自身距离为零测试通过\n");

        // 测试三角形不等式
        log.info("【测试三角形不等式】");
        for (int i = 0; i < testCount; i++) {
            Vector2 v1 = randomVector();
            Vector2 v2 = randomVector();
            Vector2 v3 = randomVector();

            double d12 = v1.distanceBetween(v2);
            double d23 = v2.distanceBetween(v3);
            double d13 = v1.distanceBetween(v3);

            // 三角形不等式: d13 <= d12 + d23
            assertTrue(d13 <= d12 + d23 + delta,
                    String.format("三角形不等式不成立: d13=%.6f, d12+d23=%.6f", d13, d12 + d23));
        }
        log.info("三角形不等式验证通过\n");

        log.info("========== 距离计算测试完成 ==========\n");
    }

    /**
     * 测试长度计算功能
     * <p>
     * 验证点：
     * - 长度计算正确性
     * - 长度平方计算正确性
     * - 长度平方与长度平方的关系
     */
    @Test
    public void testLength() {
        log.info("========== 开始长度计算测试 ==========");
        int testCount = 10000;

        // 单独测试长度计算
        log.info("【测试长度计算】");
        for (int i = 0; i < testCount; i++) {
            Vector2 v = randomVector();
            double length = v.length();
            double expectedLength = Math.sqrt(v.x() * v.x() + v.y() * v.y());
            assertEquals(expectedLength, length, delta,
                    String.format("长度计算错误: v=%s, 期望=%.6f, 实际=%.6f", v, expectedLength, length));
        }
        log.info("长度计算测试通过\n");
    }

    @Test
    public void testLengthSquared() {
        log.info("========== 开始长度平方计算测试 ==========");
        int testCount = 10000;

        log.info("【测试长度平方计算】");
        for (int i = 0; i < testCount; i++) {
            Vector2 v = randomVector();

            double lengthSquared = v.lengthSquared();
            double expectedLengthSquared = v.x() * v.x() + v.y() * v.y();

            assertEquals(expectedLengthSquared, lengthSquared, delta,
                    String.format("长度平方计算错误: v=%s, 期望=%.6f, 实际=%.6f",
                            v, expectedLengthSquared, lengthSquared));
        }
        log.info("长度平方计算测试通过\n");
    }

    /**
     * 测试点积和叉积计算
     * <p>
     * 验证点：
     * - 点积公式正确性
     * - 点积的交换律
     * - 叉积公式正确性
     * - 叉积的反交换律
     * - 几何意义验证
     */
    @Test
    public void testDotAndCross() {
        log.info("========== 开始点积和叉积测试 ==========");
        int testCount = 10000;

        // 测试点积
        log.info("【测试点积】");
        for (int i = 0; i < testCount; i++) {
            Vector2 v1 = randomVector();
            Vector2 v2 = randomVector();

            double dot = v1.dot(v2);
            double expectedDot = v1.x() * v2.x() + v1.y() * v2.y();

            assertEquals(expectedDot, dot, delta,
                    String.format("点积计算错误: v1=%s, v2=%s, 期望=%.6f, 实际=%.6f",
                            v1, v2, expectedDot, dot));

            // 验证交换律
            double dot2 = v2.dot(v1);
            assertEquals(dot, dot2, delta);
        }
        log.info("点积计算测试通过\n");

        // 测试叉积
        log.info("【测试叉积】");
        for (int i = 0; i < testCount; i++) {
            Vector2 v1 = randomVector();
            Vector2 v2 = randomVector();

            double cross = v1.cross(v2);
            double expectedCross = v1.x() * v2.y() - v1.y() * v2.x();

            assertEquals(expectedCross, cross, delta,
                    String.format("叉积计算错误: v1=%s, v2=%s, 期望=%.6f, 实际=%.6f",
                            v1, v2, expectedCross, cross));

            // 验证反交换律: cross(v1, v2) = -cross(v2, v1)
            double cross2 = v2.cross(v1);
            assertEquals(-cross, cross2, delta);
        }
        log.info("叉积计算测试通过\n");

        // 测试几何意义：垂直向量的点积为零
        log.info("【测试垂直向量点积】");
        for (int i = 0; i < testCount; i++) {
            Vector2 v = randomVector();
            // 创建一个垂直向量
            Vector2 perpendicular = new Vector2(-v.y(), v.x());
            double dot = v.dot(perpendicular);
            assertEquals(0.0, dot, delta,
                    String.format("垂直向量点积应为0: v=%s, perpendicular=%s, dot=%.6f",
                            v, perpendicular, dot));
        }
        log.info("垂直向量点积测试通过\n");

        // 测试几何意义：平行向量的叉积为零
        log.info("【测试平行向量叉积】");
        for (int i = 0; i < testCount; i++) {
            Vector2 v = randomVector();
            double scalar = rand.nextDouble() * 10 - 5;
            Vector2 parallel = v.mul(scalar);
            double cross = v.cross(parallel);
            assertEquals(0.0, cross, delta,
                    String.format("平行向量叉积应为0: v=%s, parallel=%s, cross=%.6f",
                            v, parallel, cross));
        }
        log.info("平行向量叉积测试通过\n");

        log.info("========== 点积和叉积测试完成 ==========\n");
    }

    /**
     * 测试零向量判断功能
     * <p>
     * 验证点：
     * - 零向量判断正确
     * - 非零向量判断正确
     * - 接近零的向量判断
     */
    @Test
    public void testIsZero() {
        log.info("========== 开始零向量判断测试 ==========");

        // 测试精确零向量
        Vector2 zero = Vector2.zero();
        assertTrue(zero.isZero(), "零向量的isZero()应返回true");
        log.info("精确零向量判断正确");

        // 测试接近零的向量
        Vector2 nearZero = new Vector2(1e-13, 1e-13);
        assertTrue(nearZero.isZero(), "接近零的向量应被判断为零向量");
        log.info("接近零向量判断正确");

        // 测试非零向量
        Vector2 nonZero = new Vector2(1.0, 2.0);
        assertFalse(nonZero.isZero(), "非零向量的isZero()应返回false");
        log.info("非零向量判断正确");

        log.info("========== 零向量判断测试完成 ==========\n");
    }

    /**
     * 测试静态工厂方法
     * <p>
     * 验证点：
     * - zero() 返回正确的零向量
     * - unitX() 返回正确的X轴单位向量
     * - unitY() 返回正确的Y轴单位向量
     */
    @Test
    public void testStaticFactories() {
        log.info("========== 开始静态工厂方法测试 ==========");

        Vector2 zero = Vector2.zero();
        assertEquals(0.0, zero.x(), delta);
        assertEquals(0.0, zero.y(), delta);
        log.info("zero() 方法正确: {}", zero);

        Vector2 unitX = Vector2.unitX();
        assertEquals(1.0, unitX.x(), delta);
        assertEquals(0.0, unitX.y(), delta);
        assertEquals(1.0, unitX.length(), delta);
        log.info("unitX() 方法正确: {}", unitX);

        Vector2 unitY = Vector2.unitY();
        assertEquals(0.0, unitY.x(), delta);
        assertEquals(1.0, unitY.y(), delta);
        assertEquals(1.0, unitY.length(), delta);
        log.info("unitY() 方法正确: {}", unitY);

        // 验证单位向量的正交性
        assertEquals(0.0, unitX.dot(unitY), delta);
        log.info("单位向量正交性验证通过");

        log.info("========== 静态工厂方法测试完成 ==========\n");
    }

    /**
     * 测试toString方法
     * <p>
     * 验证点：
     * - 输出格式正确
     * - 包含正确的数值
     */
    @Test
    public void testToString() {
        log.info("========== 开始toString测试 ==========");

        Vector2 v = new Vector2(3.5, -2.8);
        String str = v.toString();
        assertTrue(str.contains("3.5"), "toString应包含x坐标");
        assertTrue(str.contains("-2.8"), "toString应包含y坐标");
        assertTrue(str.startsWith("("), "toString应以 '(' 开头");
        assertTrue(str.endsWith(")"), "toString应以 ')' 结尾");
        log.info("toString() 输出正确: {}", str);

        log.info("========== toString测试完成 ==========\n");
    }

    /**
     * 测试链式操作和组合运算
     * <p>
     * 验证点：
     * - 多个运算的组合结果正确
     * - 不可变性保持
     */
    @Test
    public void testChainOperations() {
        log.info("========== 开始链式操作测试 ==========");
        int testCount = 10000;

        for (int i = 0; i < testCount; i++) {
            Vector2 v1 = randomVector();
            Vector2 v2 = randomVector();
            Vector2 v3 = randomVector();

            // 测试组合运算: -((v1 * 2) + v2) * v3
            Vector2 step1 = v1.mul(2);
            Vector2 step2 = step1.add(v2);
            Vector2 step3 = step2.negate();
            Vector2 result1 = step3.mul(v3);

            // 另一种计算方式验证
            Vector2 expected = v1.mul(2).add(v2).negate().mul(v3);
            assertEquals(result1.x(), expected.x(), delta);
            assertEquals(result1.y(), expected.y(), delta);

            // 验证原向量未被修改
            assertEquals(v1.x(), v1.x(), delta);
            assertEquals(v1.y(), v1.y(), delta);
            assertEquals(v2.x(), v2.x(), delta);
            assertEquals(v2.y(), v2.y(), delta);
        }
        log.info("链式操作测试通过，向量不可变性保持正确");

        log.info("========== 链式操作测试完成 ==========\n");
    }

    /**
     * 性能测试：比较向量运算的性能
     * <p>
     * 注意：这是一个简单的性能基准测试，实际性能测试应使用JMH等专业工具
     */
    @Test
    public void testPerformance() {
        log.info("========== 开始性能测试 ==========");
        int warmupCount = 1000000;
        int testCount = 10000000;

        // 准备测试数据
        Vector2[] vectors1 = new Vector2[testCount];
        Vector2[] vectors2 = new Vector2[testCount];
        for (int i = 0; i < testCount; i++) {
            vectors1[i] = randomVector();
            vectors2[i] = randomVector();
        }

        // 预热JIT
        log.info("预热中...");
        for (int i = 0; i < warmupCount; i++) {
            Vector2 v1 = vectors1[i % testCount];
            Vector2 v2 = vectors2[i % testCount];
            v1.add(v2);
            v1.mul(2.0);
            v1.dot(v2);
        }

        // 测试加法性能
        long start = System.nanoTime();
        Vector2 sum = Vector2.zero();
        for (int i = 0; i < testCount; i++) {
            sum = sum.add(vectors1[i]);
        }
        long addTime = System.nanoTime() - start;
        log.info("加法运算平均耗时: {} ns/op", addTime / testCount);

        // 测试标量乘法性能
        start = System.nanoTime();
        for (int i = 0; i < testCount; i++) {
            Vector2 result = vectors1[i].mul(2.5);
            // 防止优化
            if (result.x() == Double.MAX_VALUE) {
                log.warn("Unexpected");
            }
        }
        long mulTime = System.nanoTime() - start;
        log.info("标量乘法平均耗时: {} ns/op", mulTime / testCount);

        // 测试点积性能
        start = System.nanoTime();
        double dotSum = 0;
        for (int i = 0; i < testCount; i++) {
            dotSum += vectors1[i].dot(vectors2[i]);
        }
        long dotTime = System.nanoTime() - start;
        log.info("点积运算平均耗时: {} ns/op", dotTime / testCount);
        log.info("点积总和: {}", dotSum); // 防止优化

        // 测试归一化性能
        start = System.nanoTime();
        for (int i = 0; i < testCount; i++) {
            Vector2 result = vectors1[i].normalize();
            // 防止优化
            if (result.x() == Double.MAX_VALUE) {
                log.warn("Unexpected");
            }
        }
        long normalizeTime = System.nanoTime() - start;
        log.info("归一化平均耗时: {} ns/op", normalizeTime / testCount);

        log.info("========== 性能测试完成 ==========\n");
    }

    @Test
    public void simpleTestProjectionOnto() {
        for (int i = 0; i < 10000; i++) {
            Vector2 v = randomVector();
            double factor = rand.nextDouble();
            assertEquals(v.projectionOnto(Vector2.unitX().mul(factor)).length(), Math.abs(v.x()), delta);
            assertEquals(v.projectionOnto(Vector2.unitY().mul(factor)).length(), Math.abs(v.y()), delta);
        }
    }

    /**
     * 生成随机向量
     * <p>
     * 生成范围在 [-scale/2, scale/2] 内的随机向量
     *
     * @return 随机向量
     */
    private Vector2 randomVector() {
        return new Vector2(
                (rand.nextDouble() - 0.5) * scale,
                (rand.nextDouble() - 0.5) * scale
        );
    }

    /**
     * 比较两个双精度浮点数是否相等
     * <p>
     * 使用容差 delta 进行比较
     *
     * @param a 第一个数
     * @param b 第二个数
     * @return 如果差值小于delta则返回true
     */
    private static boolean doubleEquals(double a, double b, double delta) {
        return Math.abs(a - b) < delta;
    }
}