package com.game.physicsandbox.physics.util;

import com.game.physicsandbox.physics.mechanism.Vector2;

public class MathUtil {

    public static Vector2 negativeOf(Vector2 v) {
        return new Vector2(-v.x(), -v.y());
    }

    public static Vector2 add(Vector2 v1, Vector2 v2) {
        return new Vector2(v1.x() + v2.x(), v1.y() + v2.y());
    }

    public static Vector2 mul(Vector2 v, double factor) {
        return new Vector2(v.x() * factor, v.y() * factor);
    }

    public static Vector2 normalize(Vector2 v) {
        double x = v.x();
        double y = v.y();
        double magSq = x * x + y * y;
        if (magSq > 1e-12) {
            double invMag = invSqrt(magSq);
            x *= invMag;
            y *= invMag;
        } else {
            throw new ArithmeticException("Cannot normalize " + v);
        }
        return new Vector2(x, y);
    }

    public static double invSqrt(double x) {
        double xHalf = 0.5 * x;
        long i = Double.doubleToLongBits(x);
        i = 0x5fe6ec85e7de30daL - (i >> 1);
        double y = Double.longBitsToDouble(i);

        y = y * (1.5 - xHalf * y * y);
        y = y * (1.5 - xHalf * y * y);

        return y;
    }
}
