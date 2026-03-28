package com.game.physicsandbox.object.impl;

import com.game.physicsandbox.mechanism.UpdateLayer;
import com.game.physicsandbox.mechanism.UpdateStage;
import com.game.physicsandbox.object.Component;
import com.game.physicsandbox.util.Vector2;
import lombok.Getter;
import lombok.Setter;


@UpdateLayer(UpdateStage.UPDATE)
public class Transform extends Component {
    @Setter
    @Getter
    private Vector2 position;

    private Vector2 positionRecord;

    @Override
    public void update(long currentTime, long deltaTime) {
    }
}
