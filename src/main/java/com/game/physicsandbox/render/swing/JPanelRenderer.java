package com.game.physicsandbox.render.swing;

import com.game.physicsandbox.mechanism.UpdateLayer;
import com.game.physicsandbox.mechanism.UpdateStage;
import com.game.physicsandbox.object.GameObject;
import com.game.physicsandbox.physics.component.DistanceConstraint;
import com.game.physicsandbox.physics.component.RoundCollider;
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

    private RoundCollider collider;
    private final List<ConstraintLine> lines = new ArrayList<>();

    private int delayCounter;

    // 定义不同状态的颜色
    private static final Color TRIGGER_COLOR = Color.BLUE;      // trigger为true时的颜色
    private static final Color NON_TRIGGER_COLOR = Color.BLACK;  // trigger为false时的颜色
    private static final Color CENTER_COLOR = Color.BLUE;        // 中心圆颜色

    protected JPanelRenderer(GraphicFrame frame) {
        this.frame = frame;

        centerCircle = new CenterCircle();
        centerCircle.setCreator(this);
        centerCircle.setColor(CENTER_COLOR);

        colliderCircle = new ColliderCircle();
        colliderCircle.setCreator(this);
        colliderCircle.setColor(NON_TRIGGER_COLOR); // 初始颜色为黑色
        colliderCircle.setTriggeredColor(TRIGGER_COLOR);
        colliderCircle.setUntriggeredColor(NON_TRIGGER_COLOR);
        colliderCircle.setDelayTriggeredColorFrame(frame.getDELAY_TRIGGERED_COLOR_FRAME());
    }

    @Override
    public void updateGameObjectStatus(GameObject gameObject) {
        frame.removeGraphic(this);
        super.updateGameObjectStatus(gameObject);
        centerCircle.setTransform(gameObject.getTransform());
        frame.addCircle(centerCircle);
        collider = gameObject.getComponent(RoundCollider.class);
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
    }

    @Override
    public void update(long currentTime, long delta) {
        colliderCircle.updateColliderState();
    }

    /**
     * 设置缩放因子
     */
    public void setScaleFactor(int scaleFactor) {
        frame.setScaleFactor(scaleFactor);
    }

    /**
     * 移除渲染的圆
     */
    public void remove() {
        if (frame != null) {
            frame.removeCircle(centerCircle);
            frame.removeCircle(colliderCircle);
        }
    }
}
