package com.game.physicsandbox.physics;

import com.game.physicsandbox.mechanism.ComponentExecutor;
import org.springframework.stereotype.Component;

/**
 * 碰撞分析器。分析即将发生碰撞的碰撞体组件，并生成临时的碰撞约束。
 * 需要将约束直接挂载到约束求解器的权限。因此需要约束求解器的引用注入。
 * 虽然求解碰撞约束本身不需要碰撞体组件的更新方法，但依然会在结尾更新一遍碰撞体组件，以支持可能的变形碰撞体或者其它参数变化。
 *
 * @author Jerry-Yu-TeslaCoil
 * @version 1.0
 */
@Component
public class ColliderAnalyzer extends ComponentExecutor {
}
