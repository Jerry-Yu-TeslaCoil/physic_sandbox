package com.game.physicsandbox.object.impl;

import com.game.physicsandbox.mechanism.UpdateLayer;
import com.game.physicsandbox.mechanism.UpdateStage;
import com.game.physicsandbox.object.Component;
import com.game.physicsandbox.util.Vector2;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@UpdateLayer(UpdateStage.PHYSICS)
public class Transform extends Component {

    private Vector2 position;
    private Vector2 velocity;

    @Override
    public void update(long time) {
        position.add(velocity.mul(time));
    }
}
