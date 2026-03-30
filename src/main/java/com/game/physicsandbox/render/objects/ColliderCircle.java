package com.game.physicsandbox.render.objects;

import com.game.physicsandbox.physics.component.RoundCollider;
import com.game.physicsandbox.render.Circle;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;


public class ColliderCircle extends Circle {

    @Setter
    @Getter
    private RoundCollider collider;
    @Setter
    @Getter
    private Color color = Color.BLACK;
    @Setter
    private Color untriggeredColor = Color.BLACK;
    @Setter
    private Color triggeredColor = Color.BLUE;

    @Setter
    private int delayTriggeredColorFrame = 500000;

    private int delayCounter;

    public void updateColliderState() {
        if (collider != null) {

            // 根据 trigger 状态设置碰撞器圆圈颜色
            if (collider.isTriggered()) {
                delayCounter = delayTriggeredColorFrame;
                color = triggeredColor;
            } else {
                delayCounter--;
                if (delayCounter <= 0) {
                    color = untriggeredColor;
                }
            }
        }
    }

    @Override
    public int getCenterX(double scaleFactor, double xOffset) {
        return (int) (collider.getWorldPosition().x() * scaleFactor + xOffset);
    }

    @Override
    public int getCenterY(double scaleFactor, double yOffset) {
        return (int) (collider.getWorldPosition().y() * scaleFactor + yOffset);
    }

    @Override
    public boolean isFilled() {
        return false;
    }

    @Override
    public int getRadius(double scaledFactor) {
        return (int) (collider.getRadius() * scaledFactor);
    }
}
