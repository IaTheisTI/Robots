package gui;

import game.GameModel;
import state.Save;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;

/**
 * Окно отображения текущих координат робота
 * Реализует автоматическое обновление при изменении позиции и смене языка
 */
public class CoordinateWindow extends AbstractWindow implements Save, PropertyChangeListener {
    private final JTextArea textArea;
    private final GameModel model;
    private double currentX;
    private double currentY;

    public CoordinateWindow(GameModel model) {
        super(LocalizationManager.getString("coordinates.title"), 300, 200, 100, 100);
        this.model = model;

        // Настройка текстовой области
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        // Инициализация
        model.addPropertyChangeListener(this);
        updateCoordinates(model.getX(), model.getY());

        // Регистрация слушателя изменения локализации
        LocalizationManager.addLocaleChangeListener(e -> updateText());

        pack();
    }

    /**
     * Обновляет координаты и текст
     */
    private void updateCoordinates(double x, double y) {
        this.currentX = x;
        this.currentY = y;
        updateText();
    }

    /**
     * Обновляет текст с учетом текущей локализации
     */
    private void updateText() {
        String formattedText = String.format(Locale.US, "%s:\n%s: %.2f\n%s: %.2f",
                LocalizationManager.getString("coordinates.robot"),
                LocalizationManager.getString("coordinates.x"),
                currentX,
                LocalizationManager.getString("coordinates.y"),
                currentY);

        textArea.setText(formattedText);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("position".equals(evt.getPropertyName())) {
            double[] newPos = (double[]) evt.getNewValue();
            updateCoordinates(newPos[0], newPos[1]);
        }
    }

    @Override
    public String getNameOfWindow() {
        return "CoordinateWindow";
    }


}