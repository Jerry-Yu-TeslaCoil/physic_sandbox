package com.game.physicsandbox.render;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Setter
@Getter
public class Circle {
    private int centerX;
    private int centerY;
    private int radius;
    private boolean isFilled;
    private Color color = Color.BLACK;
}
