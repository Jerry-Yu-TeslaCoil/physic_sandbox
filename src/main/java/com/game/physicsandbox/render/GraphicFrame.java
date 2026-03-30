package com.game.physicsandbox.render;

import com.game.physicsandbox.mechanism.UpdateLayer;
import com.game.physicsandbox.mechanism.UpdateStage;
import com.game.physicsandbox.physics.component.RoundCollider;
import com.game.physicsandbox.util.Vector2;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class GraphicFrame extends JFrame {

    private final List<Circle> circles = new ArrayList<>();
    private final DrawingPanel drawingPanel;

    public GraphicFrame() {
        drawingPanel = new DrawingPanel();
        initUI();
    }

    private void initUI() {
        setTitle("GraphicFrame");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // 添加绘图面板
        add(drawingPanel);

        setVisible(true);
    }

    public void addCircle(Circle circle) {
        circles.add(circle);
        drawingPanel.repaint(); // 触发重绘
    }

    public void removeCircle(Circle circle) {
        circles.remove(circle);
        drawingPanel.repaint(); // 触发重绘
    }

    public void clearCircles() {
        circles.clear();
        drawingPanel.repaint(); // 触发重绘
    }

    public void refresh() {
        drawingPanel.repaint(); // 手动刷新
    }

    public JPanelRenderer createPanelRenderer() {
        return new JPanelRenderer(this);
    }

    /**
     * 绘图面板内部类
     */
    private class DrawingPanel extends JPanel {

        public DrawingPanel() {
            setBackground(Color.WHITE); // 设置白色背景
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // 开启抗锯齿，使圆边缘更平滑
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // 绘制所有圆
            for (Circle circle : circles) {
                drawCircle(g2d, circle);
            }
        }

        /**
         * 绘制单个圆
         * @param g2d Graphics2D对象
         * @param circle 圆对象
         */
        private void drawCircle(Graphics2D g2d, Circle circle) {
            if (circle == null) return;

            // 计算外接矩形左上角坐标
            int x = circle.getCenterX() - circle.getRadius();
            int y = circle.getCenterY() - circle.getRadius();
            int diameter = circle.getRadius() * 2;

            // 设置颜色（这里可以添加颜色属性，目前使用默认黑色）
            g2d.setColor(Color.BLACK);

            if (circle.isFilled()) {
                // 绘制实心圆（圆点）
                g2d.fillOval(x, y, diameter, diameter);
            } else {
                // 绘制空心圆
                g2d.drawOval(x, y, diameter, diameter);
            }
        }
    }

    /**
     * 渲染器组件类
     */
    @UpdateLayer(UpdateStage.RENDER)
    public static class JPanelRenderer extends com.game.physicsandbox.object.Component {
        private final Circle centerCircle;
        private final Circle colliderCircle;
        private final GraphicFrame frame;

        private final int CENTER_RADIUS = 3;
        private final int SCALE_FACTOR = 20; // 缩放因子
        private final int xOffset = 400;
        private final int yOffset = 300;

        protected JPanelRenderer(GraphicFrame frame) {
            this.frame = frame;
            centerCircle = new Circle();
            colliderCircle = new Circle();
            centerCircle.setFilled(true);
            colliderCircle.setFilled(false);
            frame.addCircle(centerCircle);
            frame.addCircle(colliderCircle);
        }

        @Override
        public void update(long currentTime, long delta) {
            if (gameObject != null) {
                // 更新中心圆（实心圆点）
                Vector2 center = gameObject.getTransform().getPosition();
                centerCircle.setCenterX((int) (center.x() * SCALE_FACTOR) + xOffset);
                centerCircle.setCenterY((int) (center.y() * SCALE_FACTOR) + yOffset);
                centerCircle.setRadius(CENTER_RADIUS);

                // 更新碰撞器圆（空心圆）
                RoundCollider collider = gameObject.getComponent(RoundCollider.class);
                if (collider != null) {
                    Vector2 colliderPosition = collider.getWorldPosition();
                    colliderCircle.setCenterX((int) (colliderPosition.x() * SCALE_FACTOR) + xOffset);
                    colliderCircle.setCenterY((int) (colliderPosition.y() * SCALE_FACTOR) + yOffset);
                    colliderCircle.setRadius((int) (collider.getRadius() * SCALE_FACTOR));
                }

                // 触发重绘
                if (frame != null) {
                    frame.refresh();
                }
            }
        }

        /**
         * 设置缩放因子
         */
        public void setScaleFactor(int scaleFactor) {
            // 可以动态调整缩放因子
        }

        /**
         * 移除渲染的圆
         */
        public void remove() {
            if (frame != null) {
                frame.removeCircle(centerCircle);
                frame.removeCircle(colliderCircle);
            }
        }
    }
}