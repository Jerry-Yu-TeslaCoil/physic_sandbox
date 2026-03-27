package com.game.physicsandbox;

import com.game.physicsandbox.physics.frame.FrameManager;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class MainApplication {
    public static void main(String[] args) {
        ApplicationContext context = new SpringApplicationBuilder(MainApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);

        FrameManager manager = context.getBean(FrameManager.class);
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                manager.setRunning(false);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
        manager.run();
    }
}