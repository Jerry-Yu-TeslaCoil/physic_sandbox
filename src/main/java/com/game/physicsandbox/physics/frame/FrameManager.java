package com.game.physicsandbox.physics.frame;

import com.game.physicsandbox.physics.event.EventBus;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 帧调度管理器，负责计算帧和渲染帧的协同、帧长计算以实现定长更新，以及调用各执行器
 *
 * @author Jerry-Yu-TeslaCoil
 * @version 1.0
 */
@Setter
@Slf4j
@Component
public class FrameManager {

    public static final long NANO_SECOND_PER_CALCULATE_FRAMES = (long)(1e9 / 480.0);

    private volatile boolean running = true;

    private final EventBus eventBus;

    @Autowired
    public FrameManager(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void run() {
        long lastCalculateFrameTime = 0;
        long currentNanoTime;
        long lastNanoTime = -1;
        while (running) {
            currentNanoTime = System.nanoTime();

            if (lastNanoTime < 0) {
                lastNanoTime = currentNanoTime;
                log.info("Continued");
                continue;
            }

            long firstCalculateFrames = lastNanoTime % NANO_SECOND_PER_CALCULATE_FRAMES;
            for (long i = 0, t = lastNanoTime + (NANO_SECOND_PER_CALCULATE_FRAMES - firstCalculateFrames);
                 t <= currentNanoTime;
                 i++, t += NANO_SECOND_PER_CALCULATE_FRAMES) {

                log.info("Calculate frame time: from {} to {}", t - NANO_SECOND_PER_CALCULATE_FRAMES, t);
                log.info("Fixed time gap: {}ns", t - lastCalculateFrameTime);
                log.info("Set fixed time gap: {}ns", NANO_SECOND_PER_CALCULATE_FRAMES);

                lastCalculateFrameTime = t;
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("Render frame time: {}ns", currentNanoTime);

            lastNanoTime = currentNanoTime;
        }
    }
}

