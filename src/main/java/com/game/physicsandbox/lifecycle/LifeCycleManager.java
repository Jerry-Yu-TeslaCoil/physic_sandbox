package com.game.physicsandbox.lifecycle;

import com.game.physicsandbox.event.EventBus;
import com.game.physicsandbox.mechanism.ComponentExecutor;
import com.game.physicsandbox.mechanism.UpdateLayer;
import com.game.physicsandbox.mechanism.UpdateStage;
import com.game.physicsandbox.object.Component;
import com.game.physicsandbox.object.GameObject;
import com.game.physicsandbox.object.component.Transform;
import com.game.physicsandbox.physics.*;
import com.game.physicsandbox.render.Renderer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * 生命周期管理器。对元素和组件进行管理。组件自动挂载到执行器。
 *
 * @author Jerry-Yu-TeslaCoil
 * @version 1.0
 */
@org.springframework.stereotype.Component
public class LifeCycleManager extends ComponentExecutor {

    private final List<GameObjectProxy> gameObjects = new ArrayList<>();
    private final List<GameObjectProxy> slowDeleteList = new ArrayList<>();

    private final Map<Component, ComponentExecutor> stageOfComponents = new HashMap<>();
    private final List<Component> managementList = new ArrayList<>();

    private final List<Component> slowRegisterList = new ArrayList<>();
    private final List<Component> quickRegisterList = new ArrayList<>();

    private final List<Component> slowUnRegisterList = new ArrayList<>();
    private final List<Component> quickUnRegisterList = new ArrayList<>();

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

    /**
     * 将组件添加到延时注册列表，在CLEAN阶段注册到对应执行器。
     * @param component 要注册的组件
     */
    public void slowRegisterToExecutor(Component component) {
        if (!slowRegisterList.contains(component)) {
            slowRegisterList.add(component);
        }
    }

    /**
     * 将组件添加到快速注册列表，在阶段切换时注册到对应执行器。
     * @param component 要注册的组件
     */
    public void quickRegisterToExecutor(Component component) {
        if (!quickRegisterList.contains(component)) {
            quickRegisterList.add(component);
        }
    }

    /**
     * 将组件添加到延时注销列表。在CLEAN阶段从执行器和管理器取消引用。
     * @param component 要注销的组件
     */
    public void slowUnregisterFromExecutor(Component component) {
        if (!slowUnRegisterList.contains(component)) {
            slowUnRegisterList.add(component);
        }
    }

    /**
     * 将组件添加到快速注销列表。在阶段切换时从执行器和管理器取消引用。
     * @param component 要注销的组件
     */
    public void quickUnregisterFromExecutor(Component component) {
        if (!quickUnRegisterList.contains(component)) {
            quickUnRegisterList.add(component);
        }
    }

    /**
     * 阶段给改变时调用。将执行组件快速注销和注册。
     * 快速注销将会取消同一个组件的快速注册以及延时注册注销。
     * 快速注册将会取消同一个组件的延时注册。
     * @param newStage 新的阶段枚举
     */
    public void stageSwift(UpdateStage newStage) {
        this.quickUnRegisterList.forEach(component -> {
            unregisterFromExecutor(component);
            this.quickRegisterList.remove(component);
            this.slowRegisterList.remove(component);
            this.slowUnRegisterList.remove(component);
        });
        this.quickUnRegisterList.clear();
        this.quickRegisterList.forEach(component -> {
            registerToExecutor(component);
            this.slowRegisterList.remove(component);
        });
        this.quickRegisterList.clear();
    }

    /**
     * 对元素进行初始化，如注册Transform组件。
     * @param gameObject 初始化的元素
     */
    public GameObject initializeGameObject(GameObject gameObject) {
        GameObjectProxy gameObjectProxy = new GameObjectProxy(this, gameObject);
        this.gameObjects.add(gameObjectProxy);
        Transform transform = gameObject.getTransform();
        registerToExecutor(transform);
        transform.updateGameObjectStatus(gameObjectProxy);
        return gameObjectProxy;
    }

    /**
     * 寻找符合名字的元素。
     * @param name 元素名
     * @return 名字一致的元素
     */
    public List<GameObject> findGameObjects(String name) {
        return gameObjects.stream().filter(gameObject -> gameObject.getName().equals(name))
                .map(gameObjectProxy -> (GameObject) gameObjectProxy).toList();
    }

    /**
     * 删除元素。将所有组件延时注销，并移除游戏元素引用。
     * @param gameObject 要删除的元素
     */
    public void destroyGameObject(GameObject gameObject) {
        if (!(gameObject instanceof GameObjectProxy)) {
            throw new IllegalArgumentException("GameObject outside must be an instance of GameObjectProxy");
        }
        Arrays.stream(gameObject.getComponents()).toList().forEach(component -> {
            if (component.getGameObject() != null) {
                component.getGameObject().removeComponent(component);
            }
        });
        this.slowDeleteList.add((GameObjectProxy) gameObject);
    }

    /**
     * 将组件注册到执行器，并更新stageOfComponents和managementList。
     * <b>请手动维护快慢注册以及注销列表。</b>
     * @param component 要注册的组件
     */
    private void registerToExecutor(Component component) {
        ComponentExecutor executor = parseExecutor(component);
        if (this.stageOfComponents.containsKey(component)) {
            return;
        }
        this.stageOfComponents.put(component, executor);
        this.managementList.add(component);
        executor.register(component);
    }

    /**
     * 将组件从执行器注销，并更新stageOfComponents和managementList。
     * <b>请手动维护快慢注册以及注销列表。</b>
     * @param component 要注销的组件
     */
    private void unregisterFromExecutor(Component component) {
        ComponentExecutor executor = stageOfComponents.remove(component);
        if (executor != null) {
            executor.unregister(component);
        }
        this.managementList.remove(component);
    }

    private ComponentExecutor parseExecutor(Component component) {
        UpdateLayer updateLayer = component.getClass().getAnnotation(UpdateLayer.class);
        if (updateLayer != null) {
            UpdateStage stage = updateLayer.value();
            return switch (stage) {
                case CLEAN -> this;
                case EVENT -> this.eventBus;
                case COMPONENTS -> this.componentUpdater;
                case PHYSICS -> this.physicAnalyzer;
                case UPDATE -> this.physicUpdater;
                case COLLISION -> this.colliderAnalyzer;
                case CONSTRAINT -> this.constraintSolver;
                case RENDER -> this.renderer;
            };
        }
        return this.componentUpdater;
    }

    /**
     * 整理组件。对延时删除元素解除引用，对自销毁组件解除引用，并处理延时注销和延时注册。
     * 处理慢注销组件时组件将从所有列表中删除。
     * 处理慢注册组件时组件将从快注册列表中删除。
     * @param currentTime 当前时间纳秒
     * @param deltaTime 时间增量纳秒
     */
    @Override
    public void update(long currentTime, long deltaTime) {
        for (GameObjectProxy object : this.slowDeleteList) {
            this.gameObjects.remove(object);
            object.destroy();
        }
        this.slowDeleteList.clear();

        //取消引用自销毁组件
        this.managementList.forEach(component -> {
            if (component.isAutoDispose()) {
                if (component.getGameObject() != null) {
                    component.getGameObject().removeComponent(component);
                } else {
                    slowUnregisterFromExecutor(component);
                }
            }
        });
        //延时注销
        this.slowUnRegisterList.forEach(component -> {
            this.quickRegisterList.remove(component);
            this.quickUnRegisterList.remove(component);
            this.slowRegisterList.remove(component);
            unregisterFromExecutor(component);
        });
        this.slowUnRegisterList.clear();
        //延时注册
        this.slowRegisterList.forEach(component -> {
            this.quickRegisterList.remove(component);
            registerToExecutor(component);
        });
        this.slowRegisterList.clear();
        super.update(currentTime, deltaTime);
    }
}
