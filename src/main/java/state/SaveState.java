package state;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Сохраняет и управляет параметрами окон
 */
public class SaveState {


    /**
     * Параметры всех сохраненных окон приложения
     * Формат имени - "windowName.parameterName"
     */
    private final Map<String, Integer> windowParams;
    private final Set<String> windowsNames;
    private final String configFilePath;

    public SaveState(Map<String, Integer> windowParams, Set<String> windowsNames) {
        this.windowParams = windowParams;
        this.windowsNames = windowsNames;
        this.configFilePath = System.getProperty("user.home") + "/bleskin/save.txt";
    }

    public void registerWindow(String windowName) {
        windowsNames.add(windowName);
    }

    /**
     * Получаем Мапу с параметрами окна по имени этого окна
     *
     * @param windowName название окна
     * @return Мапа с параметрами окна, ключ - название параметра, значение - его значение
     * @throws IllegalArgumentException если окно не найдено
     */
    public Map<String, Integer> getWindowParams(String windowName) {
        if (windowsNames.isEmpty() || !windowsNames.contains(windowName)) {
            throw new IllegalArgumentException("Окно '" + windowName + "' не найдено");
        }
        Map<String, Integer> result = new HashMap<>();
        String prefix = windowName + ".";
        for (Map.Entry<String, Integer> entry : windowParams.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(prefix)) {
                String paramName = key.substring(prefix.length());
                result.put(paramName, entry.getValue());
            }
        }
        return result;
    }


    /**
     * Сохраняет параметры окон в файл
     * @throws IOException если файл не существует - метод ничего
     * не делает
     */
    public void saveToFile() throws IOException {
        File configDir = new File(System.getProperty("user.home") + "/bleskin/");
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFilePath))) {
            for (Map.Entry<String, Integer> entry : windowParams.entrySet()) {
                writer.write(entry.getKey() + "=" + entry.getValue());
                writer.newLine();
            }
        }
    }

    /**
     * Загружает параметры окон из файла
     * @throws IOException если файл не существует - метод ничего
     * не делает
     */
    public void loadFromFile() throws IOException {
        File configFile = new File(configFilePath);
        if (configFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(configFilePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("=");
                    if (parts.length == 2) {
                        windowParams.put(parts[0], Integer.parseInt(parts[1]));
                    }
                }
            }
        }
    }


    /**
     * Сохраняет параметры окна при закрытии
     *
     * @param window окно, параметры которого нужно сохранить
     */
    public void saveWindowParams(Save window) {
        String windowName = window.getNameOfWindow();
        Map<String, Integer> params = window.saveWindowState();
        for (Map.Entry<String, Integer> entry : params.entrySet()) {
            windowParams.put(windowName + "." + entry.getKey(),
                    entry.getValue());
        }
    }


    /**
     * Геттер для параметров всех окон
     */
    public Map<String, Integer> getWindowParams() {
        return windowParams;
    }


    /**
     * Ставит параметры окна
     *
     * @param window
     */
    public void setWindowParams(Save window) {
        window.loadWindowState(getWindowParams(window.getNameOfWindow()));
    }
}