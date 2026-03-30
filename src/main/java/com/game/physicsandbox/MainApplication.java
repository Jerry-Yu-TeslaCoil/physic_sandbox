package com.game.physicsandbox;

import com.game.physicsandbox.frame.FrameManager;
import com.game.physicsandbox.object.GameObject;
import com.game.physicsandbox.object.GameObjectFactory;
import com.game.physicsandbox.physics.component.DistanceConstraint;
import com.game.physicsandbox.physics.component.RigidBody;
import com.game.physicsandbox.physics.component.RoundCollider;
import com.game.physicsandbox.swing.GraphicFrame;
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

        GraphicFrame frame = context.getBean(GraphicFrame.class);

        GameObjectFactory gameObjectFactory = context.getBean(GameObjectFactory.class);

        GameObject object1 = gameObjectFactory.create("MainGameObject1");
        GameObject object2 = gameObjectFactory.create("MainGameObject2");
        GameObject object3 = gameObjectFactory.create("MainGameObject3");
        GameObject object4 = gameObjectFactory.create("MainGameObject4");

        double mass = 5.0;

        object1.getTransform().setPosition(new Vector2(30, 0));
        object1.addComponent(new RoundCollider(1));
        RigidBody rigidBody1 = new RigidBody();
        rigidBody1.setGravity(true);
        rigidBody1.setMass(mass);
        object1.addComponent(rigidBody1);
        object1.addComponent(frame.createPanelRenderer());

        object2.getTransform().setPosition(new Vector2(-30, 0));
        object2.addComponent(new RoundCollider(2));
        RigidBody rigidBody2 = new RigidBody();
        rigidBody2.setKinematic(true);
        rigidBody2.setMass(mass);
        object2.addComponent(rigidBody2);
        object2.addComponent(frame.createPanelRenderer());

        DistanceConstraint.create(object1, object2, 60);

        object3.getTransform().setPosition(new Vector2(0, 30 * 1.732));
        object3.addComponent(new RoundCollider(3));
        RigidBody rigidBody3 = new RigidBody();
        rigidBody3.setGravity(true);
        rigidBody3.setMass(mass);
        //rigidBody3.setKinematic(true);
        object3.addComponent(rigidBody3);
        object3.addComponent(frame.createPanelRenderer());

        DistanceConstraint.create(object2, object3, 30);

        object4.getTransform().setPosition(new Vector2(0, 30 * 1.732));
        object4.addComponent(new RoundCollider(3));
        RigidBody rigidBody4 = new RigidBody();
        rigidBody4.setGravity(true);
        rigidBody4.setMass(mass);
        object4.addComponent(rigidBody4);
        object4.addComponent(frame.createPanelRenderer());

        DistanceConstraint.create(object3, object4, 30);

        FrameManager manager = context.getBean(FrameManager.class);
        manager.run();
    }
}
