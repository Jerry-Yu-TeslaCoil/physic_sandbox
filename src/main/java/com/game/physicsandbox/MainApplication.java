package com.game.physicsandbox;

import com.game.physicsandbox.frame.FrameManager;
import com.game.physicsandbox.lifecycle.LifeCycleManager;
import com.game.physicsandbox.object.Component;
import com.game.physicsandbox.object.GameObject;
import com.game.physicsandbox.object.GameObjectFactory;
import com.game.physicsandbox.object.component.Transform;
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

        Component component1 = new Component() {
            @Override
            public void update(long currentTime, long delta) {
                Transform transform = gameObject.getTransform();
                transform.addAcceleration(new Vector2(-3, 0));
                log.trace("{} position = {}", gameObject.getName(), transform.getPosition());
            }
        };

        Component component2 = new Component() {
            @Override
            public void update(long currentTime, long delta) {
                Transform transform = gameObject.getTransform();
                transform.addAcceleration(new Vector2(3, 0));
                GameObject object = gameObject.getLifeCycleManager().findGameObjects("MainGameObject1").get(0);
                log.trace("{} position = {}, differ = {}", gameObject.getName(), transform.getPosition(),
                        object.getTransform().getPosition().sub(transform.getPosition()));
            }
        };

        GraphicFrame frame = context.getBean(GraphicFrame.class);

        GameObjectFactory gameObjectFactory = context.getBean(GameObjectFactory.class);
        GameObject object1 = gameObjectFactory.create("MainGameObject1");
        object1.getTransform().setPosition(new Vector2(3, 0));
        object1.addComponent(new RoundCollider(2));
        object1.addComponent(new RigidBody());
        object1.addComponent(component1);
        object1.addComponent(frame.createPanelRenderer());

        GameObject object2 = gameObjectFactory.create("MainGameObject2");
        object2.getTransform().setPosition(new Vector2(-3, 0));
        object2.addComponent(new RoundCollider(2));
        RigidBody rigidBody = new RigidBody();
        rigidBody.setMass(3.0);
        rigidBody.addForce(new Vector2(3, 0));
        object2.addComponent(new RigidBody());
        object2.addComponent(component2);
        object2.addComponent(frame.createPanelRenderer());


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