package state;

import java.beans.PropertyVetoException;
import java.util.Map;

/**
 * Интерфейс сохранения и восстановления состояния окна графического приложения
 */
public interface Save {
    /**
     * сохранение состояния окна в Map
     */
    Map<String, String> saveState();

    /**
     * восстановление состояния окна из Map
     */
    void restoreState(Map<String, String> data) throws PropertyVetoException;
}