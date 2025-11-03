package gui;

import game.GameController;
import game.GameVisualizer;
import game.GameModel;
import localization.LocalizationManager;
import state.Save;

import javax.swing.*;
import java.awt.*;

/**
 * Окно игрового поля, отображающее движение робота и его цель.
 * Наследует базовое поведение от {@link AbstractWindow} и реализует интерфейс {@link Save}
 * для поддержки сохранения/восстановления состояния окна.
 * Создает и связывает между собой модель, визуализатор и контроллер.
 */
public class GameWindow extends AbstractWindow implements Save {
    /** Компонент, отвечающий за визуализацию игрового поля и робота */
    private final GameVisualizer m_visualizer;

    /** Модель, содержащая логику движения робота и хранения его состояния */
    public final GameModel model;

    /**
     * Конструктор окна. Инициализирует модель, визуализатор и контроллер.
     * Настраивает размещение компонентов внутри окна.
     */
    public GameWindow() {
        super(LocalizationManager.getInstance().getString("game.window.title"), 400, 400, 50, 50);
        model = new GameModel();
        m_visualizer = new GameVisualizer(model);
        new GameController(model, m_visualizer);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack(); // подгоняет размеры окна под содержимое
    }

    /**
     * Возвращает уникальное имя окна, используется при сохранении состояния.
     * @return строковое имя окна
     */
    @Override
    public String getNameOfWindow() {
        return "GameWindow";
    }

    /**
     * Обновляет визуальное отображение игрового поля.
     */
    public void updateGame() {
        m_visualizer.repaint();
    }
}