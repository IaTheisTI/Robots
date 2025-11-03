package localization;

import java.util.Locale;
import java.util.ResourceBundle;

public class LocalizationManager {
    private static LocalizationManager instance;
    private ResourceBundle resourceBundle;
    private Locale currentLocale;

    private LocalizationManager() {
        // По умолчанию русский язык
        currentLocale = new Locale("ru");
        resourceBundle = ResourceBundle.getBundle("messages", currentLocale);
    }

    public static LocalizationManager getInstance() {
        if (instance == null) {
            instance = new LocalizationManager();
        }
        return instance;
    }

    public String getString(String key) {
        return resourceBundle.getString(key);
    }

    public void setLocale(Locale locale) {
        currentLocale = locale;
        resourceBundle = ResourceBundle.getBundle("messages", currentLocale);
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }

    public String getFormattedString(String key, Object... args) {
        String pattern = getString(key);
        return java.text.MessageFormat.format(pattern, args);
    }
}