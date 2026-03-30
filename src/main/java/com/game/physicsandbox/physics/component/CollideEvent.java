package com.game.physicsandbox.physics.component;

import com.game.physicsandbox.event.Event;
import com.game.physicsandbox.physics.Collider;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CollideEvent extends Event {
    private Collider colliderA;
    private Collider colliderB;

    @Override
    public String toString() {
        return "CollideEvent [colliderA=" + colliderA.getGameObject().getName() +
                ", colliderB=" + colliderB.getGameObject().getName() +
                " at timestamp=" + timestamp + "]";
    }
}
