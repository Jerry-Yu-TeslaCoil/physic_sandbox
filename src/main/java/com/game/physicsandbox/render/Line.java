package com.game.physicsandbox.render;

import java.awt.*;

public abstract class Line extends Graphic {
    public abstract int getStartX(double scaleFactor, double xOffset);
    public abstract int getStartY(double scaleFactor, double yOffset);
    public abstract int getEndX(double scaleFactor, double xOffset);
    public abstract int getEndY(double scaleFactor, double yOffset);
    public abstract Color getColor();
}
