package com.game.physicsandbox.physics;

import com.game.physicsandbox.event.EventBus;
import com.game.physicsandbox.mechanism.ComponentExecutor;
import com.game.physicsandbox.physics.component.ColliderConstraint;
import com.game.physicsandbox.physics.component.ColliderConstraintIndex;
import com.game.physicsandbox.util.Vector2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * 碰撞分析器。分析即将发生碰撞的碰撞体组件，并生成临时的碰撞约束。
 * 需要将约束直接挂载到约束求解器的权限。因此需要生命周期管理器的引用注入。
 * 同时生命周期管理器应支持尽早挂载方法。
 * 虽然求解碰撞约束本身不需要碰撞体组件的更新方法，但依然会在结尾更新一遍碰撞体组件，以支持可能的变形碰撞体或者其它参数变化。
 *
 * @author Jerry-Yu-TeslaCoil
 * @version 1.0
 */
@Slf4j
@org.springframework.stereotype.Component
public class ColliderAnalyzer extends ComponentExecutor {

    private final List<ColliderConstraint> constraintRecord  = new ArrayList<>();

    private final EventBus eventBus;

    @Autowired
    public ColliderAnalyzer(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public List<ColliderConstraint> getConstraintRecord() {
        return new ArrayList<>(constraintRecord);
    }

    public void cleanConstraintRecord() {
        components.stream().filter(component -> component instanceof Collider)
                .forEach(component -> {
                    ((Collider) component).setTriggered(false);
                });
        constraintRecord.clear();
    }

    @Override
    public void update(long currentTime, long deltaTime) {
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxDetectionRadius = Double.POSITIVE_INFINITY;

        List<Map.Entry<Vector2, Collider>> positionList = components.stream().filter(c -> c instanceof Collider)
                .map(c -> (Collider) c).map(collider ->
                        (Map.Entry<Vector2, Collider>) new AbstractMap.SimpleImmutableEntry<>(
                collider.getWorldPosition(),
                collider)).toList();

        for (Map.Entry<Vector2, Collider> entry : positionList) {
            maxDetectionRadius = Math.max(maxDetectionRadius, entry.getValue().getDetectionRadius());
        }

        Boundary boundary = getMaxBoundary(positionList, minX, minY, maxX, maxY);

        double DETECT_ARG = 4.0;
        double delta = maxDetectionRadius * DETECT_ARG;

        ExtractedMap extractedMap = getExtractedMap(positionList, boundary, delta);

        extractedMap.valueMap.forEach((entry, coordinate) -> {
            for (int x = -1; x < 2; x++) {
                for (int y = -1; y < 2; y++) {
                    Vector2 newCoordinate = coordinate.add(new Vector2(x, y));
                    List<Map.Entry<Vector2, Collider>> colliders =
                            extractedMap.floorMap.getOrDefault(newCoordinate, new ArrayList<>());

                    colliders.forEach(other -> {
                        if (!entry.getValue().equals(other.getValue())) {
                            ColliderConstraintIndex pair = new ColliderConstraintIndex();
                            pair.setAutoDispose(true);
                            ColliderConstraint constraint =
                                    new ColliderConstraint(entry.getValue(), other.getValue(), pair, eventBus);
                            pair.setPair(constraint);
                            constraint.setAutoDispose(true);
                            if (!constraintRecord.contains(constraint)) {
                                entry.getValue().getGameObject().addComponentInstantly(constraint);
                                other.getValue().getGameObject().addComponentInstantly(pair);
                                constraintRecord.add(constraint);
                            }
                        }
                    });
                }
            }
        });

        super.update(currentTime, deltaTime);
    }

    private static ExtractedMap getExtractedMap(List<Map.Entry<Vector2, Collider>> positionList, Boundary boundary,
                                                double delta) {
        double minX = boundary.minX();
        double minY = boundary.minY();

        //位置Map到稀疏表
        Map<Vector2, List<Map.Entry<Vector2, Collider>>> floorMap = new HashMap<>();
        Map<Map.Entry<Vector2, Collider>, Vector2> valueMap = new HashMap<>();
        for (Map.Entry<Vector2, Collider> entry : positionList) {
            Vector2 position = entry.getKey();
            int x = (int) ((position.x() - minX) / delta);
            int y = (int) ((position.y() - minY) / delta);
            Vector2 coordinate = new Vector2(x, y);
            floorMap.computeIfAbsent(coordinate, k -> new ArrayList<>()).add(entry);
            valueMap.put(entry, coordinate);
        }
        return new ExtractedMap(floorMap, valueMap);
    }

    private record ExtractedMap(Map<Vector2, List<Map.Entry<Vector2, Collider>>> floorMap,
                                Map<Map.Entry<Vector2, Collider>, Vector2> valueMap) {
    }

    private static Boundary getMaxBoundary(List<Map.Entry<Vector2, Collider>> positionList,
                                           double minX, double minY, double maxX, double maxY) {
        for (Map.Entry<Vector2, Collider> position : positionList) {
            Vector2 pos = position.getKey();
            minX = Math.min(pos.x(), minX);
            minY = Math.min(pos.y(), minY);
            maxX = Math.max(pos.x(), maxX);
            maxY = Math.max(pos.y(), maxY);
        }
        return new Boundary(minX, minY, maxX, maxY);
    }

    private record Boundary(double minX, double minY, double maxX, double maxY) {
    }
}

