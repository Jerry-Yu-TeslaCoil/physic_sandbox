package com.game.physicsandbox.physics.util;

/**
 * 二维向量记录类，提供基本的向量运算功能。
 * <p>
 * 该类是不可变的，所有运算方法都会返回新的Vector2实例，不会修改原对象。
 * 适用于物理模拟、游戏开发等需要向量计算的场景。
 *
 * @author Jerry-Yu-TeslaCoil
 * @version 1.0
 */
public record Vector2(double x, double y) {

    private static final double ZERO_DELTA = 1e-12;

    /**
     * 获得当前向量的负向量（反向向量）。
     * <p>
     * 负向量与原向量大小相等，方向相反。
     *
     * @return 当前向量的负向量 {@code new Vector2(-x, -y)}
     */
    public Vector2 negate() {
        return new Vector2(-this.x(), -this.y());
    }

    /**
     * 向量加法：将当前向量与另一个向量相加。
     * <p>
     * 计算公式：{@code (x + v.x, y + v.y)}
     *
     * @param v 要相加的向量，不能为null
     * @return 相加后的新向量
     * @throws NullPointerException 如果参数v为null
     */
    public Vector2 add(Vector2 v) {
        return new Vector2(this.x() + v.x(), this.y() + v.y());
    }

    /**
     * 向量减法：当前向量减去另一个向量。
     * <p>
     * 计算公式：{@code (x - v.x, y - v.y)}
     *
     * @param v 要减去的向量，不能为null
     * @return 相减后的新向量
     * @throws NullPointerException 如果参数v为null
     */
    public Vector2 sub(Vector2 v) {
        return new Vector2(this.x() - v.x(), this.y() - v.y());
    }

    /**
     * 向量标量乘法：当前向量乘以一个标量因子。
     * <p>
     * 计算公式：{@code (x * factor, y * factor)}
     *
     * @param factor 缩放因子
     * @return 缩放后的新向量
     */
    public Vector2 mul(double factor) {
        return new Vector2(this.x() * factor, this.y() * factor);
    }

    /**
     * 向量分量乘法（Hadamard积）：将当前向量与另一个向量的对应分量相乘。
     * <p>
     * 计算公式：{@code (x * v.x, y * v.y)}
     *
     * @param v 另一个向量，不能为null
     * @return 分量相乘后的新向量
     * @throws NullPointerException 如果参数v为null
     */
    public Vector2 mul(Vector2 v) {
        return new Vector2(this.x() * v.x(), this.y() * v.y());
    }

    /**
     * 向量归一化：将当前向量转换为单位向量（长度为1）。
     * <p>
     * 归一化公式：{@code v / |v|}，其中|v|为向量长度。
     *
     * @return 归一化后的单位向量
     * @throws ArithmeticException 如果当前向量为零向量（长度趋近于0）
     */
    public Vector2 normalize() {
        double x = this.x();
        double y = this.y();

        double maxAbs = Math.max(Math.abs(x), Math.abs(y));
        if (maxAbs < ZERO_DELTA) {
            throw new ArithmeticException("Cannot normalize " + this);
        }

        double scale_factor = 1.0 / maxAbs;
        x *= scale_factor;
        y *= scale_factor;

        double magSq = x * x + y * y;
        double invMag = MathUtil.invSqrt(magSq);
        x *= invMag;
        y *= invMag;

        return new Vector2(x, y);
    }

    /**
     * 计算当前向量与另一个向量之间的欧几里得距离。
     * <p>
     * 距离计算公式：{@code sqrt((x1-x2)² + (y1-y2)²)}
     *
     * @param v2 另一个向量，不能为null
     * @return 两点之间的欧几里得距离
     * @throws NullPointerException 如果参数v2为null
     */
    public double distanceBetween(Vector2 v2) {
        double dx = this.x() - v2.x();
        double dy = this.y() - v2.y();
        double distanceSquared = dx * dx + dy * dy;

        return distanceSquared * MathUtil.invSqrt(distanceSquared);
    }

    /**
     * 计算向量的长度（模）。
     * <p>
     * 长度计算公式：{@code sqrt(x² + y²)}
     *
     * @return 向量的长度
     */
    public double length() {
        double x = this.x();
        double y = this.y();
        double magSq = x * x + y * y;
        return magSq * MathUtil.invSqrt(magSq);
    }

    /**
     * 计算向量的长度平方（模的平方）。
     * <p>
     * 长度平方计算公式：{@code x² + y²}
     * 此方法避免了开平方运算，性能更好，适用于只需要比较长度的场景。
     *
     * @return 向量的长度平方
     */
    public double lengthSquared() {
        return this.x() * this.x() + this.y() * this.y();
    }

    /**
     * 计算当前向量与另一个向量的点积。
     * <p>
     * 点积计算公式：{@code x1*x2 + y1*y2}
     *
     * @param v 另一个向量，不能为null
     * @return 两个向量的点积
     * @throws NullPointerException 如果参数v为null
     */
    public double dot(Vector2 v) {
        return this.x() * v.x() + this.y() * v.y();
    }

    /**
     * 计算二维向量的叉积（标量形式）。
     * <p>
     * 叉积计算公式：{@code x1*y2 - y1*x2}
     * 返回值表示两个向量构成的平行四边形的有向面积。
     *
     * @param v 另一个向量，不能为null
     * @return 两个向量的叉积（标量）
     * @throws NullPointerException 如果参数v为null
     */
    public double cross(Vector2 v) {
        return this.x() * v.y() - this.y() * v.x();
    }

    /**
     * 判断当前向量是否为零向量。
     *
     * @return 如果向量长度平方小于1e-12返回true，否则返回false
     */
    public boolean isZero() {
        return lengthSquared() < ZERO_DELTA;
    }

    /**
     * 获取零向量常量。
     *
     * @return 零向量 {@code new Vector2(0, 0)}
     */
    public static Vector2 zero() {
        return new Vector2(0, 0);
    }

    /**
     * 获取单位X轴向量。
     *
     * @return X轴单位向量 {@code new Vector2(1, 0)}
     */
    public static Vector2 unitX() {
        return new Vector2(1, 0);
    }

    /**
     * 获取单位Y轴向量。
     *
     * @return Y轴单位向量 {@code new Vector2(0, 1)}
     */
    public static Vector2 unitY() {
        return new Vector2(0, 1);
    }

    /**
     * 将向量转换为字符串表示。
     *
     * @return 向量的字符串表示，格式为 "(x, y)"
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    /**
     * 计算当前向量在另一个向量方向上的投影向量
     * @param v 目标方向向量
     * @return 投影向量
     */
    public Vector2 projectionOnto(Vector2 v) {
        double dotProduct = this.x * v.x + this.y * v.y;
        double otherLengthSquared = v.x * v.x + v.y * v.y;
        if (v.isZero()) {
            return new Vector2(0, 0);
        }
        double scalar = dotProduct / otherLengthSquared;
        return new Vector2(scalar * v.x, scalar * v.y);
    }
}