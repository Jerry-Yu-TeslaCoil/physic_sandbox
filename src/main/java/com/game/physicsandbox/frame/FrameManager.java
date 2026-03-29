package com.game.physicsandbox.frame;

import com.game.physicsandbox.lifecycle.LifeCycleManager;
import com.game.physicsandbox.mechanism.UpdateStage;
import com.game.physicsandbox.render.Renderer;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${settings.physics.calculate_frames_per_second}")
    private int calculateFramesPerSecond;

    private volatile boolean running = true;

    private CalculateFrame calculateFrame;

    private final LifeCycleManager lifeCycleManager;
    private final Renderer renderer;


    @Autowired
    public FrameManager(CalculateFrame calculateFrame, LifeCycleManager lifeCycleManager, Renderer renderer) {
        this.calculateFrame = calculateFrame;
        this.lifeCycleManager = lifeCycleManager;
        this.renderer = renderer;
    }

    public void run() {
        long currentNanoTime;
        long lastNanoTime = -1;
        while (running) {
            currentNanoTime = System.nanoTime();

            if (lastNanoTime < 0) {
                lastNanoTime = currentNanoTime;
                log.trace("First Frame Continued");
                continue;
            }

            long NANO_SECOND_PER_CALCULATE_FRAMES = (long) (1e9 / calculateFramesPerSecond);
            long firstCalculateFrames = lastNanoTime % NANO_SECOND_PER_CALCULATE_FRAMES;
            for (long i = 0, t = lastNanoTime + (NANO_SECOND_PER_CALCULATE_FRAMES - firstCalculateFrames);
                 t <= currentNanoTime;
                 i++, t += NANO_SECOND_PER_CALCULATE_FRAMES) {
                calculateFrame.update(t, NANO_SECOND_PER_CALCULATE_FRAMES);
            }

            lifeCycleManager.stageSwift(UpdateStage.RENDER);

            log.trace("Render frame time: {}ns", currentNanoTime);
            renderer.update(currentNanoTime, NANO_SECOND_PER_CALCULATE_FRAMES);

            lastNanoTime = currentNanoTime;
        }
    }
}
