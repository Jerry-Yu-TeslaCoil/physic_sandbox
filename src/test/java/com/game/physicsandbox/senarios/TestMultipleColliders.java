package com.game.physicsandbox.senarios;

import com.game.physicsandbox.ApplicationConfig;
import com.game.physicsandbox.MainApplication;
import com.game.physicsandbox.frame.FrameManager;
import com.game.physicsandbox.object.Component;
import com.game.physicsandbox.object.GameObject;
import com.game.physicsandbox.object.GameObjectFactory;
import com.game.physicsandbox.object.component.Transform;
import com.game.physicsandbox.physics.component.RigidBody;
import com.game.physicsandbox.physics.component.RoundCollider;
import com.game.physicsandbox.swing.GraphicFrame;
import com.game.physicsandbox.util.Vector2;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TestMultipleColliders {

    @Test
    public void testMultipleColliders() {
        ApplicationContext context = new SpringApplicationBuilder(MainApplication.class)
                .web(WebApplicationType.NONE)
                .headless(false)
                .run();

        GameObjectFactory factory = context.getBean(GameObjectFactory.class);
        GraphicFrame frame = context.getBean(GraphicFrame.class);
        FrameManager frameManager = context.getBean(FrameManager.class);

        for (int i = 0; i < 25; i++) {
            GameObject object = factory.create("Object" + i);
            object.getTransform().setPosition(new Vector2(((double) (i / 5) - 2.5) * 8, (i % 5 - 2.5) * 8));
            object.addComponent(new RoundCollider(2));
            object.addComponent(new RigidBody());
            object.addComponent(frame.createPanelRenderer());
            object.addComponent(new ForceToCenterComponent());
        }

        frameManager.run();
    }
}

class ForceToCenterComponent extends Component {

    private Transform transform;
    private RigidBody rigidBody;

    @Override
    public void updateGameObjectStatus(GameObject gameObject) {
        super.updateGameObjectStatus(gameObject);
        transform = gameObject.getTransform();
        rigidBody = gameObject.getComponent(RigidBody.class);
    }

    @Override
    public void update(long currentTime, long delta) {
        if (gameObject == null || transform == null || rigidBody == null) {
            return;
        }
        rigidBody.addForce(transform.getPosition().negate());
    }
}