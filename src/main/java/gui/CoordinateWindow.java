package gui;

import game.GameModel;
import state.Save;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *Окно с текущей позицией робота(координаты)
 * Реагирует на изменения позиции робота через механизм PropertyChangeListener
 */

public class CoordinateWindow extends AbstractWindow implements Save, PropertyChangeListener {
    private final JTextArea textArea;

    public CoordinateWindow(GameModel model) {
        super("Координаты", 300, 200, 100, 100);
        textArea = new JTextArea();
        textArea.setEditable(false);
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        pack();
        model.addPropertyChangeListener(this);
        updateText(model.getX(), model.getY());
    }

    /**
     * Показывает координаты
     * @param x
     * @param y
     */
    private void updateText(double x, double y) {
        textArea.setText(String.format("Координаты робота:\nX: %.2f\nY: %.2f", x, y));
    }

    /**
     *Обрабатывает событие изменения координат робота,
     *при получении события с именем "position" обновляет отображение координат
     * @param evt Объект PropertyChangeEvent, описывающий источник события
     *и свойство, которое изменилось.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("position".equals(evt.getPropertyName())) {
            double[] newPos = (double[]) evt.getNewValue();
            updateText(newPos[0], newPos[1]);
        }
    }

    @Override
    public String getNameOfWindow() {
        return "CoordinateWindow";
    }
}