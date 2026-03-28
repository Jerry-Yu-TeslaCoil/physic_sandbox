package com.game.physicsandbox.physics.frame;

import com.game.physicsandbox.MainApplication;
import com.game.physicsandbox.frame.FrameManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MainApplication.class)
public class CalculateFrameManagerTest {

    @Autowired
    private FrameManager frameManager;

    @Test
    public void testFrameManagerFixedUpdate() {
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            frameManager.setRunning(false);
        }).start();
        frameManager.run();
    }
}
