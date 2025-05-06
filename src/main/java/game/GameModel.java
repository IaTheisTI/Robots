package game;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Модель игры, представляющая робота и его поведение.
 * Содержит текущие координаты, направление движения и координаты цели.
 * Реализует механику движения робота к цели и уведомляет слушателей об изменении состояния.
 * Является частью архитектуры MVC как "Model".
 */
public class GameModel {
    /** Текущая координата X робота */
    private double x = 100;

    /** Текущая координата Y робота */
    private double y = 100;

    /** Текущее направление движения робота в радианах */
    private double direction = 0;

    /** Координата X цели */
    private double targetX = 100;

    /** Координата Y цели */
    private double targetY = 100;

    /** Механизм поддержки слушателей изменения свойств */
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     * Регистрирует слушателя, который будет уведомляться об изменениях свойств модели.
     * @param listener слушатель, реализующий {@link PropertyChangeListener}
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    /**
     * Обновляет положение робота, приближая его к текущей цели.
     * Если робот близко к цели, он останавливается. Иначе либо поворачивается в нужную сторону,
     * либо двигается вперёд по направлению.
     * После обновления координат оповещает слушателей об изменении позиции.
     */
    public void updateRobotPosition() {
        double distance = distanceToTarget();
        if (distance < 0.5) {
            return; // слишком близко к цели, движение не требуется
        }

        double oldX = x;
        double oldY = y;

        double angleToTarget = Math.atan2(targetY - y, targetX - x);
        double angleDiff = normalizeAngle(angleToTarget - direction);

        if (Math.abs(angleDiff) > 0.1) {
            // робот поворачивается к цели
            direction += Math.signum(angleDiff) * 0.05;
        } else {
            // робот движется вперёд к цели
            double speed = Math.min(2.0, distance);
            x += speed * Math.cos(direction);
            y += speed * Math.sin(direction);
        }

        pcs.firePropertyChange("position", new double[]{oldX, oldY}, new double[]{x, y});
    }

    /**
     * Устанавливает новые координаты цели для робота.
     * <p>
     * После установки координат уведомляет слушателей об изменении цели.
     *
     * @param x координата X цели
     * @param y координата Y цели
     */
    public void setTarget(int x, int y) {
        double oldTargetX = this.targetX;
        double oldTargetY = this.targetY;
        this.targetX = x;
        this.targetY = y;

        pcs.firePropertyChange("target", new double[]{oldTargetX, oldTargetY}, new double[]{targetX, targetY});
    }

    /**
     * Возвращает расстояние от текущего положения до цели.
     *
     * @return евклидово расстояние до цели
     */
    private double distanceToTarget() {
        double dx = targetX - x;
        double dy = targetY - y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Нормализует угол в диапазон [-π; π].
     *
     * @param angle угол в радианах
     * @return нормализованный угол
     */
    private double normalizeAngle(double angle) {
        while (angle < -Math.PI) angle += 2 * Math.PI;
        while (angle > Math.PI) angle -= 2 * Math.PI;
        return angle;
    }

    /** @return текущая координата X робота */
    public double getX() {
        return x;
    }

    /** @return текущая координата Y робота */
    public double getY() {
        return y;
    }

    /** @return текущее направление движения робота (в радианах) */
    public double getDirection() {
        return direction;
    }

    /** @return координата X текущей цели */
    public double getTargetX() {
        return targetX;
    }

    /** @return координата Y текущей цели */
    public double getTargetY() {
        return targetY;
    }
}
