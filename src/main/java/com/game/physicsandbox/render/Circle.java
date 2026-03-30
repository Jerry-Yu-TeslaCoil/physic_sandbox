package com.game.physicsandbox.render;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Circle {
    private int centerX;
    private int centerY;
    private int radius;
    private boolean isFilled;
}
