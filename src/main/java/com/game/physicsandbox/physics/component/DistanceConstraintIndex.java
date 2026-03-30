package com.game.physicsandbox.physics.component;

import com.game.physicsandbox.mechanism.UpdateLayer;
import com.game.physicsandbox.mechanism.UpdateStage;
import com.game.physicsandbox.physics.Constraint;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@UpdateLayer(UpdateStage.CONSTRAINT)
public class DistanceConstraintIndex extends Constraint {

    private DistanceConstraint pair;

    @Override
    public void update(long currentTime, long delta) {

    }

    @Override
    public void finalize(long currentTime, long deltaTime) {

    }
}
