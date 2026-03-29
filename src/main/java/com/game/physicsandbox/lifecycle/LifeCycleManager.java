package com.game.physicsandbox.lifecycle;

import com.game.physicsandbox.event.EventBus;
import com.game.physicsandbox.mechanism.ComponentExecutor;
import com.game.physicsandbox.mechanism.UpdateLayer;
import com.game.physicsandbox.mechanism.UpdateStage;
import com.game.physicsandbox.object.Component;
import com.game.physicsandbox.physics.*;
import com.game.physicsandbox.render.Renderer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@org.springframework.stereotype.Component
public class LifeCycleManager extends ComponentExecutor {

    private final Map<Component, ComponentExecutor> stageOfComponents = new ConcurrentHashMap<>();
    private final List<Component> managementList = new ArrayList<>();

    private final EventBus eventBus;
    private final ComponentUpdater componentUpdater;
    private final PhysicAnalyzer physicAnalyzer;
    private final PhysicUpdater physicUpdater;
    private final ColliderAnalyzer colliderAnalyzer;
    private final ConstraintSolver constraintSolver;
    private final Renderer renderer;

    @Autowired
    public LifeCycleManager(EventBus eventBus,
                            ComponentUpdater componentUpdater,
                            PhysicAnalyzer physicAnalyzer,
                            PhysicUpdater physicUpdater,
                            ColliderAnalyzer colliderAnalyzer,
                            ConstraintSolver constraintSolver,
                            Renderer renderer) {
        this.eventBus = eventBus;
        this.componentUpdater = componentUpdater;
        this.physicAnalyzer = physicAnalyzer;
        this.physicUpdater = physicUpdater;
        this.colliderAnalyzer = colliderAnalyzer;
        this.constraintSolver = constraintSolver;
        this.renderer = renderer;
    }

    public void registerToExecutor(Component component) {
        ComponentExecutor executor = parseExecutor(component);
        this.stageOfComponents.put(component, executor);
        this.managementList.add(component);
    }

    public void unregisterFromExecutor(Component component) {
        this.stageOfComponents.remove(component);
        this.managementList.remove(component);
    }

    private ComponentExecutor parseExecutor(Component component) {
        UpdateLayer updateLayer = component.getClass().getAnnotation(UpdateLayer.class);
        if (updateLayer != null) {
            UpdateStage stage = updateLayer.value();
            return switch (stage) {
                case CLEAN -> this;
                case EVENT -> this.eventBus;
                case Components -> this.componentUpdater;
                case PHYSICS -> this.physicAnalyzer;
                case UPDATE -> this.physicUpdater;
                case COLLISION -> this.colliderAnalyzer;
                case CONSTRAINT -> this.constraintSolver;
                case RENDER -> this.renderer;
                default -> throw new IllegalArgumentException("Unknown update stage: " + stage);
            };
        }
        return this.componentUpdater;
    }

    @Override
    public void update(long currentTime, long deltaTime) {
        super.update(currentTime, deltaTime);
    }
}
