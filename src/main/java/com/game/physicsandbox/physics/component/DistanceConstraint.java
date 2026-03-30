package com.game.physicsandbox.physics.component;

import com.game.physicsandbox.mechanism.UpdateLayer;
import com.game.physicsandbox.mechanism.UpdateStage;
import com.game.physicsandbox.object.GameObject;
import com.game.physicsandbox.object.component.Transform;
import com.game.physicsandbox.physics.Constraint;
import com.game.physicsandbox.util.Vector2;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UpdateLayer(UpdateStage.CONSTRAINT)
public class DistanceConstraint extends Constraint {
    @Getter
    private final Transform transformA;
    @Getter
    private final Transform transformB;
    private final double distance;

    @Getter
    private final DistanceConstraintIndex pair;

    private RigidBody rigidBodyA;
    private RigidBody rigidBodyB;

    private DistanceConstraint(Transform transformA, Transform transformB, double distance, DistanceConstraintIndex pair) {
        this.transformA = transformA;
        this.transformB = transformB;
        this.distance = distance;
        this.pair = pair;
    }

    @Override
    public void updateGameObjectStatus(GameObject gameObject) {
        super.updateGameObjectStatus(gameObject);
        rigidBodyA = gameObject.getComponent(RigidBody.class);
        rigidBodyB = gameObject.getComponent(RigidBody.class);
    }

    @Override
    public void finalize(long currentTime, long deltaTime) {
    }

    @Override
    public void update(long currentTime, long deltaTime) {
        double delta = 1e-1;
        if (rigidBodyA == null || rigidBodyB == null) {
            return;
        }
        Vector2 differA2B = transformB.getPosition().sub(transformA.getPosition());

        if (differA2B.lengthSquared() < (distance * distance - delta) ||
                differA2B.lengthSquared() > (distance * distance + delta)) {
            double differ = differA2B.length() - distance;
            differA2B = differA2B.normalize();
            double massA = rigidBodyA.getMass();
            double massB = rigidBodyB.getMass();
            double sum = massA + massB;

            Vector2 differenceA = differA2B.mul(differ * (massB / sum));
            Vector2 differenceB = differA2B.negate().mul(differ * (massA / sum));
            transformA.setPosition(transformA.getPosition().add(differenceA), Transform.PositionSetStrategy.POSITION_ONLY);
            transformB.setPosition(transformB.getPosition().add(differenceB), Transform.PositionSetStrategy.POSITION_ONLY);
        }
    }

    public static void create(GameObject gameObjectA, GameObject gameObjectB, double distance) {
        DistanceConstraintIndex distanceConstraintIndex =
                new DistanceConstraintIndex();
        DistanceConstraint distanceConstraint = new DistanceConstraint(
                gameObjectA.getTransform(), gameObjectB.getTransform(), distance, distanceConstraintIndex);
        gameObjectA.addComponent(distanceConstraint);
        gameObjectB.addComponent(distanceConstraintIndex);
    }
}
