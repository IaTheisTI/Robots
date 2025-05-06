package game;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Контроллер игры, реализующий шаблон MVC.
 * Обрабатывает события мыши от пользователя и передаёт координаты цели в модель.
 * Также инициализирует таймер, который периодически обновляет положение робота в модели.
 */
public class GameController extends MouseAdapter {

    /** Ссылка на модель, содержащую логику движения робота. */
    private final GameModel model;

    /**
     * Создаёт контроллер, привязывает его к модели и представлению.
     * Регистрирует слушатель мыши во view и запускает таймер, обновляющий положение робота.
     * @param model модель игры, управляющая логикой перемещения робота
     * @param view визуальный компонент, на который добавляется слушатель мыши
     */
    public GameController(GameModel model, GameVisualizer view) {
        this.model = model;
        view.addMouseListener(this);

        // Таймер вызывает обновление положения робота каждые 50 миллисекунд
        Timer timer = new Timer(50, e -> {
            model.updateRobotPosition();
        });
        timer.start();
    }

    /**
     * Обрабатывает щелчок мыши по игровому полю.
     * Устанавливает новую цель для робота в координаты щелчка.
     * @param e объект события мыши, содержащий координаты щелчка
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        model.setTarget(e.getX(), e.getY());
    }
}
