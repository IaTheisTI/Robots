package game;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Визуализатор игрового поля и состояния робота.
 * Отображает текущую позицию и направление робота, а также цель.
 * Реализует {@link PropertyChangeListener}, чтобы реагировать на изменения модели и обновлять графику.
 * Является частью архитектуры MVC как "View".
 */
public class GameVisualizer extends JPanel implements PropertyChangeListener {
    /** Модель, содержащая данные о положении и цели робота */
    private final GameModel model;

    /**
     * Создает панель визуализации, подписываясь на обновления модели.
     * @param model модель игры, которую необходимо визуализировать
     */
    public GameVisualizer(GameModel model) {
        this.model = model;
        model.addPropertyChangeListener(this);
        setDoubleBuffered(true); // улучшает производительность отрисовки
    }

    /**
     * Отрисовывает текущее состояние игры: робота и цель.
     * @param g графический контекст
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawRobot(g, model.getX(), model.getY(), model.getDirection());
        drawTarget(g, model.getTargetX(), model.getTargetY());
    }

    /**
     * Отрисовывает робота на экране в заданной позиции и направлении.
     * @param g         графический контекст
     * @param x         координата X робота
     * @param y         координата Y робота
     * @param direction направление движения робота (в радианах)
     */
    private void drawRobot(Graphics g, double x, double y, double direction) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.translate(x, y);
        g2d.rotate(direction);
        g2d.setColor(Color.BLACK);
        g2d.fillOval(-20, -5, 40, 10); // тело робота
        g2d.setColor(Color.WHITE);
        g2d.fillOval(20 - 2, -2, 4, 4); // "глаз" для направления
        g2d.dispose();
    }

    /**
     * Отрисовывает цель на экране.
     * @param g        графический контекст
     * @param targetX  координата X цели
     * @param targetY  координата Y цели
     */
    private void drawTarget(Graphics g, double targetX, double targetY) {
        g.setColor(Color.GREEN);
        int targetCenterX = (int) targetX;
        int targetCenterY = (int) targetY;

        g.fillOval(targetCenterX - 5, targetCenterY - 5, 10, 10);
    }

    /**
     * Обработчик изменения свойств модели. Перерисовывает панель при изменении позиции или цели.
     * @param evt объект события, содержащий информацию об изменении
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        repaint();
    }
}
