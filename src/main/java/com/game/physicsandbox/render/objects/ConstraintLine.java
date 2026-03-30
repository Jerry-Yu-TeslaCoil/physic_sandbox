package com.game.physicsandbox.render.objects;

import com.game.physicsandbox.object.component.Transform;
import com.game.physicsandbox.render.Line;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.Objects;

@Setter
@Getter
public class ConstraintLine extends Line {

    private Transform transformA;
    private Transform transformB;

    private Color color;

    /**
     * 根据端点两个Transform设置线条
     * @param transformA 端点Transform
     * @param transformB 端点Transform
     */
    public void setLine(Transform transformA, Transform transformB) {
        this.transformA = transformA;
        this.transformB = transformB;
    }

    public int getStartX(double scaleFactor, double xOffset) {
        return (int) (transformA.getPosition().x() * scaleFactor + xOffset);
    }

    public int getStartY(double scaleFactor, double yOffset) {
        return (int) (transformA.getPosition().y() * scaleFactor + yOffset);
    }

    public int getEndX(double scaleFactor, double xOffset) {
        return (int) (transformB.getPosition().x() * scaleFactor + xOffset);
    }

    public int getEndY(double scaleFactor, double yOffset) {
        return (int) (transformB.getPosition().y() * scaleFactor + yOffset);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ConstraintLine line)) return false;
        return Objects.equals(transformA, line.transformA) && Objects.equals(transformB, line.transformB);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transformA, transformB);
    }
}
