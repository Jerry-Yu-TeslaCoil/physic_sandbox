package com.game.physicsandbox.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class TestEvent extends Event {
    private String eventString;

}
