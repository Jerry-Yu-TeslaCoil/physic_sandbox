package com.game.physicsandbox.object.component;

import com.game.physicsandbox.mechanism.UpdateLayer;
import com.game.physicsandbox.mechanism.UpdateStage;
import com.game.physicsandbox.object.Component;
import com.game.physicsandbox.util.Vector2;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 物体基本运动组件。存储位置并更新物体位移。
 * 使用Verlet积分执行:
 * x2 = 2x1 - x0 + at^2
 */
@Slf4j
@UpdateLayer(UpdateStage.UPDATE)
public class Transform extends Component {
    @Getter
    private Vector2 position = Vector2.zero();

    /**
     * -- SETTER --
     * 设置物体的位置历史记录。
     * 物体的位置记录由Transform本身维护，因此通常情况下不需要此设置。
     * 用于自定义情况。
     */
    @Setter
    private Vector2 positionRecord = Vector2.zero();

    @Setter
    @Getter
    private Vector2 acceleration = Vector2.zero();

    @Getter
    private Vector2 velocity = Vector2.zero();

    /**
     * 为物体此帧添加加速度。用于组件位置更新。
     * 在帧位置更新结束后加速度将清零。
     * @param acceleration 此帧的加速度
     */
    public void addAcceleration(Vector2 acceleration) {
        this.acceleration = this.acceleration.add(acceleration);
    }

    /**
     * 清空物体的加速度。
     */
    public void clearAcceleration() {
        this.acceleration = Vector2.zero();
    }

    /**
     * 设置物体位置。此方法将清空物体速度。
     * @param position 新的物体位置坐标
     */
    public void setPosition(Vector2 position) {
        setPosition(position, PositionSetStrategy.CLEAN_SPEED);
    }

    /**
     * 设置物体位置。根据位置更新策略枚举改变物体位置历史记录。
     * @param position 新的物体位置坐标
     * @param strategy 位置更新策略枚举
     */
    public void setPosition(Vector2 position, PositionSetStrategy strategy) {
        switch (strategy) {
            case CLEAN_SPEED -> this.positionRecord = position;
            case HOLD_SPEED -> this.positionRecord = this.positionRecord.add(position.sub(this.position));
            default -> {}
        }
        this.position = position;
    }

    /**
     * 对物体位置进行更新。
     * 在更新结尾清空本计算帧加速度。
     * @param currentTime 当前时间纳秒
     * @param deltaTime 时间增量纳秒
     */
    @Override
    public void update(long currentTime, long deltaTime) {
        double deltaTimeSeconds = deltaTime * 1e-9;

        Vector2 newPosition = position.mul(2)
                .sub(positionRecord)
                .add(acceleration.mul(deltaTimeSeconds * deltaTimeSeconds));
        positionRecord = position;
        position = newPosition;
        velocity = position.sub(positionRecord).mul(1 / deltaTimeSeconds);
        acceleration = Vector2.zero();
    }

    /**
     * 由于Verlet积分使用位置和位置历史记录来计算下一帧位置，而物体速度隐含在位置变化量中，
     * 因此更新物体新位置时，需要指定速度的处理方式。
     * 若仍要只更新物体新位置请选择POSITION_ONLY，但注意如果位置变化量过大，速度将会失控。
     */
    public enum PositionSetStrategy {
        /**
         * 设置物体位置，并清除物体原本的速度。
         * 此策略下，物体位置历史将直接被新位置覆盖。
         */
        CLEAN_SPEED,
        /**
         * 设置物体位置，并保持物体原本的速度。
         * 此策略下，物体位置历史将加上物体位置的变化量。
         */
        HOLD_SPEED,
        /**
         * 只设置物体位置，不更新位置历史。
         * <br>
         * <b>注意！</b>使用此策略可能导致速度失控。请确保仅在需要同时操作当前位置和历史位置时使用此策略。
         */
        POSITION_ONLY
    }
}
