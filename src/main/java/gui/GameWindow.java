package gui;

import game.GameController;
import game.GameVisualizer;
import game.GameModel;
import localization.LocalizationManager;
import state.Save;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Окно игрового поля с поддержкой локализации
 */
public class GameWindow extends AbstractWindow implements Save, PropertyChangeListener {
    private final GameVisualizer visualizer;
    public final GameModel model;

    public GameWindow() {
        super(LocalizationManager.getInstance().getString("game.window.title"), 400, 400, 50, 50);
        model = new GameModel();
        m_visualizer = new GameVisualizer(model);
        new GameController(model, m_visualizer);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();

        // Регистрация слушателя изменений локализации
        LocalizationManager.addLocaleChangeListener(this);
    }

    @Override
    public String getNameOfWindow() {
        return "GameWindow";
    }

    public void updateGame() {
        visualizer.repaint();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("locale".equals(evt.getPropertyName())) {
            setTitle(LocalizationManager.getString("game.title"));
            updateGame();
        }
    }
}