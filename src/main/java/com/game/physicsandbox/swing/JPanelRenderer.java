package com.game.physicsandbox.swing;

import com.game.physicsandbox.mechanism.UpdateLayer;
import com.game.physicsandbox.mechanism.UpdateStage;
import com.game.physicsandbox.object.GameObject;
import com.game.physicsandbox.physics.component.DistanceConstraint;
import com.game.physicsandbox.physics.component.RoundCollider;
import com.game.physicsandbox.render.Line;
import com.game.physicsandbox.render.objects.ConstraintLine;
import com.game.physicsandbox.render.objects.CenterCircle;
import com.game.physicsandbox.render.objects.ColliderCircle;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 渲染器组件类
 */
@UpdateLayer(UpdateStage.RENDER)
public class JPanelRenderer extends com.game.physicsandbox.object.Component {
    private final CenterCircle centerCircle;
    private final ColliderCircle colliderCircle;
    private final GraphicFrame frame;

    private final List<Line> lines = new ArrayList<>();

    // 定义不同状态的颜色
    private static final Color TRIGGERED_COLOR = Color.RED;      // trigger为true时的颜色
    private static final Color NON_TRIGGERED_COLOR = Color.BLACK;  // trigger为false时的颜色
    private static final Color CENTER_COLOR = Color.BLUE;        // 中心圆颜色

    protected JPanelRenderer(GraphicFrame frame) {
        this.frame = frame;

        centerCircle = new CenterCircle();
        centerCircle.setCreator(this);
        centerCircle.setColor(CENTER_COLOR);

        colliderCircle = new ColliderCircle();
        colliderCircle.setCreator(this);
        colliderCircle.setColor(NON_TRIGGERED_COLOR); // 初始颜色为黑色
        colliderCircle.setTriggeredColor(TRIGGERED_COLOR);
        colliderCircle.setUntriggeredColor(NON_TRIGGERED_COLOR);
        colliderCircle.setDelayTriggeredColorFrame(frame.getDELAY_TRIGGERED_COLOR_FRAME());
    }

    @Override
    public void updateGameObjectStatus(GameObject gameObject) {
        frame.removeGraphic(this);
        super.updateGameObjectStatus(gameObject);
        centerCircle.setTransform(gameObject.getTransform());
        frame.addCircle(centerCircle);
        RoundCollider collider = gameObject.getComponent(RoundCollider.class);
        if (collider != null) {
            colliderCircle.setCollider(collider);
            frame.addCircle(colliderCircle);
        }

        List<DistanceConstraint> constraints = gameObject.getComponents(DistanceConstraint.class);

        lines.clear();
        for (DistanceConstraint constraint : constraints) {
            ConstraintLine line = new ConstraintLine();
            line.setLine(constraint.getTransformA(), constraint.getTransformB());
            if (!lines.contains(line)) {
                lines.add(line);
            }
        }

        for (Line line : lines) {
            frame.addLine(line);
        }
    }

    @Override
    public void update(long currentTime, long delta) {
        colliderCircle.updateColliderState();
    }
}
