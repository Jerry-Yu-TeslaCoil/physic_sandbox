package com.game.physicsandbox.physics.component;

import com.game.physicsandbox.physics.Collider;
import com.game.physicsandbox.util.Vector2;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RoundCollider extends Collider {

    private double radius;

    public RoundCollider(double radius) {
        this.radius = radius;
    }

    @Override
    public double boundaryDistance(Vector2 vector) {
        return radius;
    }
}
