package com.game.physicsandbox.physics.object;

import com.game.physicsandbox.physics.mechanism.FrameUpdatable;
import lombok.*;

import java.awt.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Node implements FrameUpdatable {
    @Override
    public void addForceToFrame(Frame frame) {

    }

    @Override
    public void clearFrame() {

    }

    @Override
    public void onFrameUpdate(long time) {

    }

    private double x = 0f;
    private double y = 0f;
    private double dx = 0f;
    private double dy = 0f;
    private double mass = 1f;
}
