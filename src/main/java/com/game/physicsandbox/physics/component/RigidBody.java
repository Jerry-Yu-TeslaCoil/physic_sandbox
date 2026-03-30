package com.game.physicsandbox.physics.component;

import com.game.physicsandbox.mechanism.UpdateLayer;
import com.game.physicsandbox.mechanism.UpdateStage;
import com.game.physicsandbox.object.Component;
import com.game.physicsandbox.util.Vector2;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Setter
@Getter
@UpdateLayer(UpdateStage.PHYSICS)
public class RigidBody extends Component {

    private double mass = 1.0;
    private final List<Vector2> forces = new ArrayList<>();

    private final double GRAVITY_COEFFICIENT = 9.8;

    private boolean gravity = false;
    private boolean isKinematic = false;

    public void addForce(Vector2 force) {
        this.forces.add(force);
    }

    @Override
    public void update(long currentTime, long delta) {
        Vector2 force = forces.stream().reduce(Vector2.zero(), Vector2::add);
        if (gravity) {
            force = force.add(new Vector2(0, GRAVITY_COEFFICIENT));
        }
        Vector2 acc = force.mul(1 / mass);
        if (!isKinematic) {
            this.gameObject.getTransform().addAcceleration(acc);
        }
        this.forces.clear();
    }
}
