package com.game.physicsandbox.physics;

import com.game.physicsandbox.object.Component;
import com.game.physicsandbox.util.Vector2;
import lombok.Getter;
import lombok.Setter;

/**
 * 碰撞体组件。
 * 提供碰撞体中心与物体的相对坐标，以及在某个方向的物理边界距离。
 * 只对满足: 边界线与任意过中心的直线的交点小于等于2 的简单图形有效，并且不会太精细。
 *
 * @author Jerry-Yu-TeslaCoil
 * @version 1.0
 */
@Setter
@Getter
public abstract class Collider extends Component {

    protected Vector2 centerRelativePosition;

    /**
     * 获得碰撞体在某个方向的边界与中心点距离。
     * @param vector 从中心点出发的射线方向
     * @return 此方向下边界与中心的距离
     */
    public abstract double boundaryDistance(Vector2 vector);

    @Override
    public void update(long currentTime, long delta) {

    }
}
