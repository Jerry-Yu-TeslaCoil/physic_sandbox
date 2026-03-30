package com.game.physicsandbox;

import com.game.physicsandbox.frame.FrameManager;
import com.game.physicsandbox.object.Component;
import com.game.physicsandbox.object.GameObject;
import com.game.physicsandbox.object.GameObjectFactory;
import com.game.physicsandbox.physics.component.DistanceConstraint;
import com.game.physicsandbox.physics.component.RigidBody;
import com.game.physicsandbox.physics.component.RoundCollider;
import com.game.physicsandbox.render.GraphicFrame;
import com.game.physicsandbox.util.Vector2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

@Slf4j
@SpringBootApplication
public class MainApplication {
    public static void main(String[] args) {
        ApplicationContext context = new SpringApplicationBuilder(MainApplication.class)
                .web(WebApplicationType.NONE)
                .headless(false)
                .run(args);

        double force = 20.0;

        Component component12 = new TestComponent(force, "MainGameObject2");

        Component component23 = new TestComponent(force, "MainGameObject3");

        Component component31 = new TestComponent(force, "MainGameObject1");

        Component component13 = new TestComponent(force, "MainGameObject3");

        Component component21 = new TestComponent(force, "MainGameObject1");

        Component component32 = new TestComponent(force, "MainGameObject2");

        GraphicFrame frame = context.getBean(GraphicFrame.class);

        GameObjectFactory gameObjectFactory = context.getBean(GameObjectFactory.class);

        GameObject object1 = gameObjectFactory.create("MainGameObject1");
        GameObject object2 = gameObjectFactory.create("MainGameObject2");
        GameObject object3 = gameObjectFactory.create("MainGameObject3");

        object1.getTransform().setPosition(new Vector2(15, 0));
        object1.addComponent(new RoundCollider(1));
        object1.addComponent(new RigidBody());
        object1.addComponent(component12);
        object1.addComponent(component13);
        object1.addComponent(frame.createPanelRenderer());

        object2.getTransform().setPosition(new Vector2(-30, 0));
        object2.addComponent(new RoundCollider(2));
        object2.addComponent(new RigidBody());
        object2.addComponent(component23);
        object2.addComponent(component21);
        object2.addComponent(frame.createPanelRenderer());

        DistanceConstraint.create(object1, object2, 45);

        object3.getTransform().setPosition(new Vector2(0, 30 * 1.732));
        object3.addComponent(new RoundCollider(3));
        object3.addComponent(new RigidBody());
        object3.addComponent(component31);
        object3.addComponent(component32);
        object3.addComponent(frame.createPanelRenderer());

        DistanceConstraint.create(object2, object3, 45);

        FrameManager manager = context.getBean(FrameManager.class);
        /*
        new Thread(() -> {
            try {
                Thread.sleep(6000);
                manager.setRunning(false);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
        */
        manager.run();
    }
}

class TestComponent extends Component {
    private final double force;
    private RigidBody rigidBody;
    private final String target;
    private GameObject targetGameObject;

    public TestComponent(double force, String target) {
        this.force = force;
        this.target = target;
    }

    @Override
    public void updateGameObjectStatus(GameObject gameObject) {
        super.updateGameObjectStatus(gameObject);
        rigidBody = gameObject.getComponent(RigidBody.class);
        targetGameObject = gameObject.getLifeCycleManager().findGameObjects(target).get(0);
    }

    @Override
    public void update(long currentTime, long delta) {
        if (rigidBody == null || targetGameObject == null) {
            return;
        }
        Vector2 vector = targetGameObject.getTransform().getPosition().sub(gameObject.getTransform().getPosition());
        vector = vector.normalize();
        rigidBody.addForce(vector.mul(force));
    }
}