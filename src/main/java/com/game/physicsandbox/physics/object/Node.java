package com.game.physicsandbox.physics.object;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Node {

    private double x = 0f;
    private double y = 0f;
    private double dx = 0f;
    private double dy = 0f;
    private double mass = 1f;
}
