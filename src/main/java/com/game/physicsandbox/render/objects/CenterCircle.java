package com.game.physicsandbox.render.objects;

import com.game.physicsandbox.object.component.Transform;
import com.game.physicsandbox.render.Circle;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Setter
@Getter
public class CenterCircle extends Circle {
    private Transform transform = new Transform();
    private double radius = 2;

    private Color color = Color.BLACK;

    @Override
    public int getCenterX(double scaleFactor, double xOffset) {
        return (int) (transform.getPosition().x() * scaleFactor + xOffset);
    }

    @Override
    public int getCenterY(double scaleFactor, double yOffset) {
        return (int) (transform.getPosition().y() * scaleFactor + yOffset);
    }

    @Override
    public boolean isFilled() {
        return true;
    }

    @Override
    public int getRadius(double scaledFactor) {
        return (int) (radius);
    }
}
