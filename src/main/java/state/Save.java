package state;

import java.beans.PropertyVetoException;
import java.util.Map;

/**
 * Интерфейс сохранения и восстановления состояния окна графического приложения
 */
public interface Save {
    /**
     * сохранение состояния окна в Map
     * @return - Map: ключ - название параметра состояния, значение - значение параметра состояния
     */
    Map<String, String> saveState();

    /**
     * восстановление состояния окна из Map
     * @param data - Map: ключ - название параметра состояния, значение - значение параметра состояния
     * @throws PropertyVetoException - возникает, когда значение параметра состояния некорректное
     */
    void restoreState(Map<String, String> data) throws PropertyVetoException;
}
