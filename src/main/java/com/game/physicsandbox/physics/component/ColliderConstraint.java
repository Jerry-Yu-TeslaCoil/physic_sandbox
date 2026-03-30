package com.game.physicsandbox.physics.component;

import com.game.physicsandbox.event.EventBus;
import com.game.physicsandbox.mechanism.UpdateLayer;
import com.game.physicsandbox.mechanism.UpdateStage;
import com.game.physicsandbox.object.component.Transform;
import com.game.physicsandbox.physics.Collider;
import com.game.physicsandbox.physics.Constraint;
import com.game.physicsandbox.render.Line;
import com.game.physicsandbox.util.Vector2;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.Objects;

@Slf4j
@UpdateLayer(UpdateStage.CONSTRAINT)
public class ColliderConstraint extends Constraint {

    @Getter
    private final Collider colliderA;
    @Getter
    private final Collider colliderB;

    @Getter
    private final ColliderConstraintIndex pair;

    private Vector2 accelerationA = Vector2.zero();
    private Vector2 accelerationB = Vector2.zero();

    RigidBody rigidBodyA;
    RigidBody rigidBodyB;

    private final EventBus eventBus;

    private boolean collided = false;

    public ColliderConstraint(Collider colliderA, Collider colliderB, ColliderConstraintIndex pair,
                              EventBus eventBus) {
        this.colliderA = colliderA;
        this.colliderB = colliderB;
        this.pair = pair;
        this.eventBus = eventBus;
        rigidBodyA = colliderA.getGameObject().getComponent(RigidBody.class);
        rigidBodyB = colliderB.getGameObject().getComponent(RigidBody.class);
    }

    @Override
    public void update(long currentTime, long delta) {
        if (rigidBodyA == null || rigidBodyB == null) {
            return;
        }

        Transform transformA = colliderA.getGameObject().getTransform();
        Transform transformB = colliderB.getGameObject().getTransform();
        Vector2 vectorA2B = colliderB.getWorldPosition().sub(colliderA.getWorldPosition());
        double constraintLength = colliderA.boundaryDistance(vectorA2B) + colliderB.boundaryDistance(vectorA2B.negate());
        double differ = constraintLength - vectorA2B.length();
        vectorA2B = vectorA2B.normalize();
        double massA = rigidBodyA.getMass();
        double massB = rigidBodyB.getMass();
        double sum = massA + massB;
        if (differ > 0) {
            collided = true;
            colliderA.setTriggered(true);
            colliderB.setTriggered(true);
            Vector2 velocityA = transformA.getVelocity();
            Vector2 velocityB = transformB.getVelocity();
            Vector2 newVelocityA = velocityA.mul((massA - massB) / sum).add(velocityB.mul(2 * massB / sum));
            Vector2 newVelocityB = velocityA.mul(2 * massA / sum).add(velocityB.mul((massB - massA) / sum));

            double differA = differ * (massB / (massA + massB));
            double differB = differ * (massA / (massA + massB));
            if (!rigidBodyA.isKinematic()) {
                accelerationA = newVelocityA.mul(1e9 / delta);
                transformA.setPosition(transformA.getPosition().add(vectorA2B.negate().mul(differA)));
            }
            if (!rigidBodyB.isKinematic()) {
                accelerationB = newVelocityB.mul(1e9 / delta);
                transformB.setPosition(transformB.getPosition().add(vectorA2B.mul(differB)));
            }
        }
    }

    @Override
    public void finalize(long currentTime, long deltaTime) {
        if (rigidBodyA == null || rigidBodyB == null) {
            return;
        }
        Transform transformA = colliderA.getGameObject().getTransform();
        Transform transformB = colliderB.getGameObject().getTransform();
        if (!rigidBodyA.isKinematic()) {
            transformA.addAcceleration(accelerationA);
        }
        if (!rigidBodyB.isKinematic()) {
            transformB.addAcceleration(accelerationB);
        }
        accelerationA = Vector2.zero();
        accelerationB = Vector2.zero();
        if (collided) {
            eventBus.publish(new CollideEvent(colliderA, colliderB));
        }
        collided = false;

    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ColliderConstraint that)) return false;
        return (Objects.equals(colliderA, that.colliderA) && Objects.equals(colliderB, that.colliderB)) ||
                (Objects.equals(colliderA, that.colliderB) && Objects.equals(colliderB, that.colliderA));
    }

    @Override
    public int hashCode() {
        return Objects.hash(colliderA, colliderB);
    }
}
