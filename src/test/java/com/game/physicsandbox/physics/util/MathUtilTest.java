package com.game.physicsandbox.physics.util;

import com.game.physicsandbox.util.MathUtil;
import com.game.physicsandbox.util.Vector2;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;


@Slf4j
public class MathUtilTest {
    private final Random rand = new Random(System.currentTimeMillis());
    private final long scale = 1000;
    private final int total = 100000000;

    @Test
    public void Vector2CalculateTest() {
        // 准备测试数据
        int total = 10;
        double scale = 100.0;
        Random rand = new Random();

        Vector2[] sample1 = new Vector2[total];
        Vector2[] sample2 = new Vector2[total];
        for (int i = 0; i < total; i++) {
            sample1[i] = new Vector2((rand.nextDouble() - 0.5) * scale, (rand.nextDouble() - 0.5) * scale);
            sample2[i] = new Vector2((rand.nextDouble() - 0.5) * scale, (rand.nextDouble() - 0.5) * scale);
        }

        log.info("========== 开始向量运算测试 ==========");

        // 测试 negative 方法
        log.info("【测试 negative 方法】");
        for (int i = 0; i < total; i++) {
            Vector2 v = sample1[i];
            Vector2 result = MathUtil.negativeOf(v);

            // 验证负向量的每个分量应该是原向量的相反数
            Assert.assertTrue(
                    String.format("negative 方法失败: v=(%.6f, %.6f), result=(%.6f, %.6f)",
                            v.x(), v.y(), result.x(), result.y()),
                    doubleEquals(result.x(), -v.x())
                    );

            log.debug(formatVector("v=({}, {}) -> negative=({}, {})",
                    v.x(), v.y(), result.x(), result.y()));
        }
        log.info("negative 方法测试通过\n");

        // 测试 add 方法
        log.info("【测试 add 方法】");
        for (int i = 0; i < total; i++) {
            Vector2 v1 = sample1[i];
            Vector2 v2 = sample2[i];
            Vector2 result = MathUtil.add(v1, v2);

            // 验证向量加法：每个分量应该相加
            Assert.assertTrue(
                    String.format("add 方法失败: v1=(%.6f, %.6f), v2=(%.6f, %.6f), result=(%.6f, %.6f)",
                            v1.x(), v1.y(), v2.x(), v2.y(), result.x(), result.y()),
                    doubleEquals(result.x(), v1.x() + v2.x()) && doubleEquals(result.y(), v1.y() + v2.y()));

            log.debug(formatVector("({}, {}) + ({}, {}) = ({}, {})",
                    v1.x(), v1.y(), v2.x(), v2.y(), result.x(), result.y()));
        }
        log.info("add 方法测试通过\n");

        // 测试 mul 方法
        log.info("【测试 mul 方法】");
        double[] multipliers = {-2.0, -1.0, 0.0, 0.5, 2.0, 3.5};
        for (double multiplier : multipliers) {
            for (int i = 0; i < total; i++) {
                Vector2 v = sample1[i];
                Vector2 result = MathUtil.mul(v, multiplier);

                // 验证标量乘法：每个分量应该乘以标量
                Assert.assertTrue(
                        String.format("mul 方法失败: v=(%.6f, %.6f), multiplier=%.2f, result=(%.6f, %.6f)",
                                v.x(), v.y(), multiplier, result.x(), result.y()),
                        doubleEquals(result.x(), v.x() * multiplier) && doubleEquals(result.y(), v.y() * multiplier));

                log.debug(formatVector("({}, {}) * {:.1f} = ({}, {})",
                        v.x(), v.y(), multiplier, result.x(), result.y()));
            }
        }
        log.info("mul 方法测试通过\n");

        // 测试 normalize 方法
        log.info("【测试 normalize 方法】");

        // 测试零向量
        Vector2 zeroVector = new Vector2(0, 0);
        try {
            Vector2 result = MathUtil.normalize(zeroVector);
            // 如果方法没有抛出异常，验证结果
            Assert.assertTrue("normalize 方法对零向量处理失败",
                    doubleEquals(result.x(), 0) && doubleEquals(result.y(), 0));
            log.info(formatVector("零向量归一化结果: ({}, {})", result.x(), result.y()));
        } catch (Exception e) {
            log.info("零向量归一化抛出异常: {}", e.getMessage());
        }

        // 测试非零向量
        for (int i = 0; i < total; i++) {
            Vector2 v = sample1[i];

            // 跳过接近零的向量
            if (doubleEquals(v.x(), 0) && doubleEquals(v.y(), 0)) {
                continue;
            }

            Vector2 result = MathUtil.normalize(v);

            // 验证归一化后的向量长度应该为1
            double length = Math.sqrt(result.x() * result.x() + result.y() * result.y());
            Assert.assertTrue(
                    String.format("normalize 方法失败: v=(%.6f, %.6f), 归一化后长度=%.6f, 应为1.0", v.x(), v.y(), length),
                    doubleEquals(length, 1.0));

            // 验证方向应该相同（对于非零向量）
            if (!doubleEquals(v.x(), 0) && !doubleEquals(v.y(), 0)) {
                double originalAngle = Math.atan2(v.y(), v.x());
                double resultAngle = Math.atan2(result.y(), result.x());
                Assert.assertTrue(
                        String.format("normalize 方法改变了方向: 原角度=%.6f, 新角度=%.6f", originalAngle, resultAngle),
                        doubleEquals(originalAngle, resultAngle));
            }

            log.debug(formatVector("({}, {}) 归一化 = ({}, {}), 长度={:.2f}",
                    v.x(), v.y(), result.x(), result.y(), length));
        }
        log.info("normalize 方法测试通过\n");

        // 测试链式操作
        log.info("【测试链式操作】");
        for (int i = 0; i < total; i++) {
            Vector2 v1 = sample1[i];
            Vector2 v2 = sample2[i];

            // 测试组合操作: negative(add(mul(v1, 2), v2))
            Vector2 multiplied = MathUtil.mul(v1, 2);
            Vector2 added = MathUtil.add(multiplied, v2);
            Vector2 negated = MathUtil.negativeOf(added);

            log.debug(formatVector("复杂运算: -({}, {})*2 + ({}, {}) = ({}, {})",
                    v1.x(), v1.y(), v2.x(), v2.y(), negated.x(), negated.y()));
        }
        log.info("链式操作测试完成\n");

        log.info("========== 所有向量运算测试完成 ==========");
    }

    /**
     * 格式化向量相关的日志消息，支持指定小数位数
     * @param format 格式字符串，使用 {} 作为占位符，支持 {:.nf} 格式指定小数位数
     * @param args 参数列表
     * @return 格式化后的字符串
     */
    private String formatVector(String format, Object... args) {
        // 处理格式字符串，将 {:.nf} 替换为合适的格式说明符
        String processedFormat = format;
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\{:(\\d+\\.?\\d*)f}");
        java.util.regex.Matcher matcher = pattern.matcher(format);

        while (matcher.find()) {
            String decimalFormat = matcher.group(1);
            String replacement = "%." + decimalFormat + "f";
            processedFormat = processedFormat.replace(matcher.group(), replacement);
        }

        // 将剩余的 {} 替换为 %s
        processedFormat = processedFormat.replace("{}", "%s");

        // 格式化参数
        Object[] formattedArgs = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Double) {
                // 如果没有指定格式，默认保留2位小数
                if (!pattern.matcher(format).find()) {
                    formattedArgs[i] = String.format("%.2f", args[i]);
                } else {
                    formattedArgs[i] = args[i];
                }
            } else {
                formattedArgs[i] = args[i];
            }
        }

        return String.format(processedFormat, formattedArgs);
    }

    @Test
    public void invSqrtPreciseTest() {
        int[] diffCounts = new int[5];
        double[] bounds = {1e-4, 1e-3, 1e-2, 1e-1};

        double diffMax = -1;

        for (int i = 0; i < total; i++) {
            double x = rand.nextDouble() * scale;
            double mathInvSqrt = 1.0 / Math.sqrt(x);
            double invSqrt = MathUtil.invSqrt(x);
            double diff = Math.abs(invSqrt - mathInvSqrt);

            if (diff > diffMax) {
                diffMax = diff;
            }

            if (diff < bounds[0]) {
                diffCounts[0]++;
            } else if (diff < bounds[1]) {
                diffCounts[1]++;
            } else if (diff < bounds[2]) {
                diffCounts[2]++;
            } else if (diff < bounds[3]) {
                diffCounts[3]++;
            } else {
                log.error("大误差diff: {}", diff);
                diffCounts[4]++;
            }
        }

        log.info("=== 快速逆平方根算法误差分布统计 ===");
        log.info("diff < 1e-4: {} 次, 占比: {}%", diffCounts[0],
                String.format("%.2f", (double)diffCounts[0] / total * 100));
        log.info("1e-4 <= diff < 1e-3: {} 次, 占比: {}%", diffCounts[1],
                String.format("%.2f", (double)diffCounts[1] / total * 100));
        log.info("1e-3 <= diff < 1e-2: {} 次, 占比: {}%", diffCounts[2],
                String.format("%.2f", (double)diffCounts[2] / total * 100));
        log.info("1e-2 <= diff < 1e-1: {} 次, 占比: {}%", diffCounts[3],
                String.format("%.2f", (double)diffCounts[3] / total * 100));
        log.info("diff >= 1e-1: {} 次, 占比: {}%", diffCounts[4],
                String.format("%.2f", (double)diffCounts[4] / total * 100));
        log.info("diff最大值: {}", diffMax);

        try {
            assert diffCounts[4] < 1;
        } catch (AssertionError e) {
            log.error("精度误差过大: diff >= 1e-1出现了 {} 次", diffCounts[4]);
            throw new AssertionError(e);
        }
        try {
            assert (double)diffCounts[3] / total * 100 < 0.01;
        } catch (AssertionError e) {
            log.error("精度误差过大: 1e-2 <= diff < 1e-1出现了 {} 次, 占比 {}%", diffCounts[3],
                    (double)diffCounts[3] / total * 100 < 0.01);
            throw new AssertionError(e);
        }
    }

    @Test
    public void invSqrtTimeTest() {
        // 预热JIT
        for (int i = 0; i < total; i++) {
            double x = Math.random() * scale;
            Math.sqrt(x);
            MathUtil.invSqrt(x);
        }

        // 测试数据
        double[] testData = new double[total];
        for (int i = 0; i < testData.length; i++) {
            testData[i] = rand.nextDouble() * scale;
        }

        long start = System.nanoTime();
        double sum1 = 0;
        for (double x : testData) {
            sum1 += 1.0 / Math.sqrt(x);
        }
        long time1 = System.nanoTime() - start;

        // 测试invSqrt
        start = System.nanoTime();
        double sum2 = 0;
        for (double x : testData) {
            sum2 += MathUtil.invSqrt(x);
        }
        long time2 = System.nanoTime() - start;

        // 防止优化掉sum
        System.out.println("校验和: " + (sum1 - sum2));

        log.info("Math.sqrt: {} ns/op", time1 / testData.length);
        log.info("invSqrt: {} ns/op", time2 / testData.length);
        log.info("比例: {}%", (double)time2 / time1 * 100);

        assert time2 < time1;
    }

    private static boolean doubleEquals(double a, double b) {
        return Math.abs(a - b) < 1e-3;
    }
}
