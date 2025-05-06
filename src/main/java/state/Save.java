package state;

import java.util.Map;

/**
 * Сохраняет и восстанавливает состояние окна
 */
public interface Save {

    /**
     * Сохраняет состояние окна
     * @return Возвращает мапу, в которой в ключе лежит название
     * параметра, а в значение - состояние
     */
    Map<String, Integer> saveWindowState();

    /**
     * Загружает состояние окна
     */
    void loadWindowState(Map<String, Integer> params);

    /**
     *
     * @return Возвращает имя окна
     */
    String getNameOfWindow();
}