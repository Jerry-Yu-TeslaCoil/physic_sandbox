package com.game.physicsandbox.render;

import java.awt.*;

public abstract class Circle extends Graphic {
    public abstract int getRadius(double scaledFactor);
    public abstract int getCenterX(double scaleFactor, double xOffset);
    public abstract int getCenterY(double scaleFactor, double yOffset);
    public abstract Color getColor();
    public abstract boolean isFilled();
}
