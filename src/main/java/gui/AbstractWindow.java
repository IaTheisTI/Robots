package gui;

import state.Save;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

import static java.awt.Frame.ICONIFIED;
import static java.awt.Frame.NORMAL;

/**
 * Абстрактное окно, реализующее базовую логику отображения и сохранения состояния.
 * Наследуется от {@link JInternalFrame} и реализует интерфейс {@link Save} для сохранения и загрузки параметров окна:
 * позиция, размеры и состояние (свернуто/нормально).
 */
public abstract class AbstractWindow extends JInternalFrame implements Save {

    /**
     * Создает внутреннее окно с заданными параметрами.
     * @param title     заголовок окна
     * @param width     ширина окна
     * @param height    высота окна
     * @param defaultX  координата X (по умолчанию)
     * @param defaultY  координата Y (по умолчанию)
     */
    public AbstractWindow(String title, int width, int height, int defaultX, int defaultY) {
        super(title, true, true, true, true); // можно перемещать, изменять размер, иконфицировать, закрывать
        setSize(width, height);
        setLocation(defaultX, defaultY);
        setVisible(true);
    }

    /**
     * Сохраняет текущее состояние окна в виде словаря с параметрами.
     * @return карта параметров, содержащая координаты, размеры и состояние окна
     */
    @Override
    public Map<String, Integer> saveWindowState() {
        Map<String, Integer> state = new HashMap<>();
        state.put("x", getX());
        state.put("y", getY());
        state.put("width", getWidth());
        state.put("height", getHeight());
        state.put("state", isIcon() ? ICONIFIED : NORMAL);
        return state;
    }

    /**
     * Загружает состояние окна из переданной карты параметров.
     * Если карта не содержит нужных ключей, используются значения по умолчанию.
     * @param params карта параметров окна
     */
    @Override
    public void loadWindowState(Map<String, Integer> params) {
        if (params != null) {
            int x = params.getOrDefault("x", 50);
            int y = params.getOrDefault("y", 50);
            int width = params.getOrDefault("width", 200);
            int height = params.getOrDefault("height", 300);
            setBounds(x, y, width, height);

            if (params.getOrDefault("state", NORMAL) == ICONIFIED) {
                try {
                    setIcon(true); // сворачиваем окно, если оно было иконфицировано
                } catch (java.beans.PropertyVetoException e) {
                    e.printStackTrace(); // логируем исключение при невозможности сворачивания
                }
            }
        }
    }
}
