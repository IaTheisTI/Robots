package gui;

import log.Logger;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.prefs.Preferences;

/**
 * Класс {@code LocalizationManager} отвечает за управление локализацией приложения.
 * Поддерживает смену языка, загрузку строк из ресурсных файлов, а также уведомление
 * слушателей об изменении локали.
 * <p>
 * Использует {@link ResourceBundle} для загрузки строк из файла ресурсов
 * и {@link Preferences} для сохранения выбранной локали между сессиями.
 */
public class LocalizationManager {

    /** Название базового файла с ресурсами (без расширения и локали) */
    private static final String BUNDLE_BASE_NAME = "messages";

    /** Ключ для хранения локали в системных настройках */
    private static final String LOCALE_PREF_KEY = "appLocale";

    /** Список слушателей, реагирующих на изменение локали */
    private static final List<PropertyChangeListener> listeners = new ArrayList<>();

    /** Текущий ресурсный пакет */
    private static ResourceBundle bundle;

    /** Текущая локаль */
    private static Locale currentLocale;

    /** Объект для хранения пользовательских настроек */
    private static final Preferences prefs = Preferences.userNodeForPackage(LocalizationManager.class);

    // Инициализация локали при загрузке класса
    static {
        initializeLocale();
    }

    /**
     * Инициализирует текущую локаль и загружает соответствующий {@link ResourceBundle}.
     * Если локаль была ранее сохранена, используется она, иначе берется локаль по умолчанию.
     */
    private static void initializeLocale() {
        String savedLocale = prefs.get(LOCALE_PREF_KEY, "");

        if (!savedLocale.isEmpty()) {
            String[] parts = savedLocale.split("_");
            if (parts.length == 1) {
                currentLocale = new Locale(parts[0]);
            } else if (parts.length >= 2) {
                currentLocale = new Locale(parts[0], parts[1]);
            }
        }

        bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, currentLocale);
    }

    /**
     * Возвращает локализованную строку по заданному ключу.
     *
     * @param key ключ локализованной строки
     * @return строка из ресурсного файла или !key!, если ключ не найден
     */
    public static String getString(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            Logger.error("Localization key not found: " + key);
            return "!" + key + "!";
        }
    }

    /**
     * Устанавливает новую локаль и уведомляет всех зарегистрированных слушателей.
     * Также сохраняет локаль в пользовательских настройках.
     *
     * @param locale новая локаль
     */
    public static void setLocale(Locale locale) {
        if (!locale.equals(currentLocale)) {
            Locale oldLocale = currentLocale;
            currentLocale = locale;
            bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, currentLocale);
            saveLocalePreference();
            notifyLocaleChanged(oldLocale, currentLocale);
        }
    }

    /**
     * Устанавливает локаль по языковому тегу (например, "en", "ru-RU").
     *
     * @param languageTag языковой тег
     */
    public static void setLocale(String languageTag) {
        setLocale(Locale.forLanguageTag(languageTag));
    }

    /**
     * Возвращает текущую локаль.
     *
     * @return текущая {@link Locale}
     */
    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    /**
     * Возвращает список поддерживаемых локалей.
     *
     * @return массив поддерживаемых локалей
     */
    public static Locale[] getSupportedLocales() {
        return new Locale[] {
                new Locale("ru", "RU"),
                Locale.ENGLISH
        };
    }

    /**
     * Регистрирует слушателя для получения уведомлений об изменении локали.
     *
     * @param listener слушатель изменения локали
     */
    public static void addLocaleChangeListener(PropertyChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Удаляет ранее зарегистрированного слушателя локали.
     *
     * @param listener слушатель, которого нужно удалить
     */
    public static void removeLocaleChangeListener(PropertyChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * Сохраняет текущую локаль в пользовательских настройках.
     */
    private static void saveLocalePreference() {
        prefs.put(LOCALE_PREF_KEY,
                currentLocale.getLanguage() + "_" + currentLocale.getCountry());
    }

    /**
     * Проверяет, поддерживается ли указанная локаль.
     *
     * @param locale локаль для проверки
     * @return {@code true}, если поддерживается; иначе {@code false}
     */
    private static boolean isLocaleSupported(Locale locale) {
        return Arrays.stream(getSupportedLocales())
                .anyMatch(l -> l.getLanguage().equals(locale.getLanguage()));
    }

    /**
     * Уведомляет всех слушателей об изменении локали.
     * @param oldLocale предыдущая локаль
     * @param newLocale новая локаль
     */
    private static void notifyLocaleChanged(Locale oldLocale, Locale newLocale) {
        PropertyChangeEvent event = new PropertyChangeEvent(
                LocalizationManager.class, "locale", oldLocale, newLocale);

        for (PropertyChangeListener listener : listeners) {
            listener.propertyChange(event);
        }
    }

}
