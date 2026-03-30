package com.game.physicsandbox.swing;

import com.game.physicsandbox.render.Circle;
import com.game.physicsandbox.render.Line;
import com.game.physicsandbox.render.Refreshable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class GraphicFrame extends JFrame implements Refreshable {

    private final List<Circle> circles = new ArrayList<>();
    private final List<Line> lines = new ArrayList<>();
    private final DrawingPanel drawingPanel;

    @Getter
    private double scaleFactor = 15; // 缩放因子
    private int xOffset = 400;
    private int yOffset = 300;

    private Point lastDragPoint; // 记录上一次拖拽的点

    // 缩放限制
    private static final int MIN_SCALE_FACTOR = 1;
    private static final int MAX_SCALE_FACTOR = 150;
    private static final float ZOOM_STEP = 1.2f; // 每次缩放的比例因子

    @Getter
    private final int DELAY_TRIGGERED_COLOR_FRAME;

    @Autowired
    public GraphicFrame(@Value("${settings.physics.calculate_frames_per_second}") int calculateFramesPerSecond) {
        DELAY_TRIGGERED_COLOR_FRAME = (int) (2e8 / calculateFramesPerSecond);
        drawingPanel = new DrawingPanel();
        initUI();
        initDragListener();
        initZoomListener();
    }

    /**
     * 刷新绘制
     */
    @Override
    public void refreshFrame() {
        this.refresh();
    }

    private void initUI() {
        setTitle("GraphicFrame - 鼠标拖拽平移 | 滚轮缩放");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // 添加绘图面板
        add(drawingPanel);

        setVisible(true);
    }

    private void initDragListener() {
        // 鼠标按下时记录起始点
        drawingPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastDragPoint = e.getPoint();
                drawingPanel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                lastDragPoint = null;
                drawingPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });

        // 鼠标拖拽时更新偏移量
        drawingPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastDragPoint != null) {
                    int deltaX = e.getX() - lastDragPoint.x;
                    int deltaY = e.getY() - lastDragPoint.y;

                    xOffset += deltaX;
                    yOffset += deltaY;

                    lastDragPoint = e.getPoint();
                }
            }
        });
    }

    private void initZoomListener() {
        // 添加滚轮缩放监听
        drawingPanel.addMouseWheelListener(e -> {
            int rotation = e.getWheelRotation();
            double zoomFactor = rotation < 0 ? ZOOM_STEP : 1.0 / ZOOM_STEP;

            // 获取鼠标位置作为缩放中心
            Point mousePos = e.getPoint();

            // 计算缩放前的世界坐标位置（鼠标指向的实际位置）
            double worldXBefore = (mousePos.x - xOffset) / scaleFactor;
            double worldYBefore = (mousePos.y - yOffset) / scaleFactor;

            // 更新缩放因子
            double newScaleFactor = scaleFactor * zoomFactor;
            if (newScaleFactor >= MIN_SCALE_FACTOR && newScaleFactor <= MAX_SCALE_FACTOR) {
                scaleFactor = newScaleFactor;

                // 调整偏移量，使鼠标指向的世界坐标位置保持不变
                xOffset = (int) (mousePos.x - worldXBefore * scaleFactor);
                yOffset = (int) (mousePos.y - worldYBefore * scaleFactor);
            }
        });
    }

    public void removeGraphic(com.game.physicsandbox.object.Component component) {
        List<Circle> removedCircle = circles.stream().filter(circle -> circle.getCreator() == component).toList();
        List<Line> removedLine = lines.stream().filter(line -> line.getCreator() == component).toList();
        circles.removeAll(removedCircle);
        lines.removeAll(removedLine);
    }

    public void addCircle(Circle circle) {
        if (!circles.contains(circle)) {
            circles.add(circle);
        }
    }

    public void addLine(Line line) {
        if (!lines.contains(line)) {
            lines.add(line);
        }
    }

    public void refresh() {
        drawingPanel.repaint();
    }

    public JPanelRenderer createPanelRenderer() {
        return new JPanelRenderer(this);
    }

    // Getters and Setters for offsets and scale
    public int getXOffset() {
        return xOffset;
    }

    public void setXOffset(int xOffset) {
        this.xOffset = xOffset;
    }

    public int getYOffset() {
        return yOffset;
    }

    public void setYOffset(int yOffset) {
        this.yOffset = yOffset;
    }

    public void setScaleFactor(int scaleFactor) {
        // 限制缩放范围
        if (scaleFactor >= MIN_SCALE_FACTOR && scaleFactor <= MAX_SCALE_FACTOR) {
            this.scaleFactor = scaleFactor;
        }
    }

    /**
     * 重置视图到默认位置和缩放
     */
    public void resetView() {
        scaleFactor = 15;
        xOffset = 400;
        yOffset = 300;
    }

    /**
     * 获取当前缩放比例（用于显示）
     */
    public double getZoomLevel() {
        return scaleFactor / 15.0f;
    }

    /**
     * 绘图面板内部类
     */
    private class DrawingPanel extends JPanel {

        public DrawingPanel() {
            setBackground(Color.WHITE); // 设置白色背景
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

            // 设置焦点，使滚轮事件可以被接收
            setFocusable(true);
            requestFocusInWindow();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // 开启抗锯齿，使圆边缘更平滑
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // 绘制坐标轴辅助线
            drawAxes(g2d);

            // 绘制缩放比例提示
            drawZoomInfo(g2d);

            List<Circle> snapshot = new ArrayList<>(circles);

            // 绘制所有圆
            for (Circle circle : snapshot) {
                drawCircle(g2d, circle);
            }

            for (Line line : lines) {
                drawLine(g2d, line);
            }
        }

        /**
         * 绘制坐标轴辅助线
         */
        private void drawAxes(Graphics2D g2d) {
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.setStroke(new BasicStroke(0.5f));
            // 绘制X轴
            g2d.drawLine(0, yOffset, getWidth(), yOffset);
            // 绘制Y轴
            g2d.drawLine(xOffset, 0, xOffset, getHeight());

            // 绘制原点标记
            g2d.setColor(Color.RED);
            g2d.fillOval(xOffset - 2, yOffset - 2, 4, 4);

            // 绘制刻度标记（可选）
            drawTickMarks(g2d);
        }

        /**
         * 绘制刻度标记
         */
        private void drawTickMarks(Graphics2D g2d) {
            g2d.setColor(Color.GRAY);
            g2d.setStroke(new BasicStroke(0.5f));

            // 计算刻度间隔（根据缩放因子动态调整）
            int tickInterval = scaleFactor >= 20 ? 50 : (scaleFactor >= 10 ? 20 : 10);

            // X轴刻度
            int startX = ((-xOffset) / tickInterval) * tickInterval;
            for (int x = startX; x <= getWidth(); x += tickInterval) {
                int screenX = xOffset + x;
                if (screenX >= 0 && screenX <= getWidth()) {
                    g2d.drawLine(screenX, yOffset - 3, screenX, yOffset + 3);
                }
            }

            // Y轴刻度
            int startY = ((-yOffset) / tickInterval) * tickInterval;
            for (int y = startY; y <= getHeight(); y += tickInterval) {
                int screenY = yOffset + y;
                if (screenY >= 0 && screenY <= getHeight()) {
                    g2d.drawLine(xOffset - 3, screenY, xOffset + 3, screenY);
                }
            }
        }

        /**
         * 绘制缩放比例提示信息
         */
        private void drawZoomInfo(Graphics2D g2d) {
            g2d.setColor(Color.BLUE);
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            String zoomText = String.format("Scale: %.2fx  Scroll | Drag", getZoomLevel());
            g2d.drawString(zoomText, 10, 20);
        }

        /**
         * 绘制单个圆
         * @param g2d Graphics2D对象
         * @param circle 圆对象
         */
        private void drawCircle(Graphics2D g2d, Circle circle) {
            if (circle == null) return;

            // 计算外接矩形左上角坐标
            int x = circle.getCenterX(scaleFactor, xOffset) - circle.getRadius(scaleFactor);
            int y = circle.getCenterY(scaleFactor, yOffset) - circle.getRadius(scaleFactor);
            int diameter = circle.getRadius(scaleFactor) * 2;

            Color oldColor = g2d.getColor();
            // 设置颜色
            g2d.setColor(circle.getColor());

            if (circle.isFilled()) {
                // 绘制实心圆（圆点）
                g2d.fillOval(x, y, diameter, diameter);
            } else {
                // 绘制空心圆
                g2d.drawOval(x, y, diameter, diameter);
            }
            g2d.setColor(oldColor);
        }

        private void drawLine(Graphics2D g2d, Line line) {
            int startX = line.getStartX(scaleFactor, xOffset);
            int startY = line.getStartY(scaleFactor, yOffset);
            int endX = line.getEndX(scaleFactor, xOffset);
            int endY = line.getEndY(scaleFactor, yOffset);
            g2d.setColor(line.getColor());
            g2d.drawLine(startX, startY, endX, endY);
        }
    }

}