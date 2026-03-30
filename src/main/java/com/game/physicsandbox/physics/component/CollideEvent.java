package com.game.physicsandbox.physics.component;

import com.game.physicsandbox.event.Event;
import com.game.physicsandbox.physics.Collider;
import lombok.Getter;

@Getter
public class CollideEvent extends Event {
    private final Collider colliderA;
    private final Collider colliderB;

    public CollideEvent(Collider colliderA, Collider colliderB) {
        this.colliderA = colliderA;
        this.colliderB = colliderB;
    }

    @Override
    public String toString() {
        return "CollideEvent [colliderA=" + colliderA.getGameObject().getName() +
                ", colliderB=" + colliderB.getGameObject().getName() +
                " at timestamp=" + timestamp + "]";
    }
}
