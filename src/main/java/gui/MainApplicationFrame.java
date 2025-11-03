package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

import javax.swing.*;

import log.Logger;
import localization.LocalizationManager;
import state.Save;
import state.SaveState;

/**
 * Главное окно приложения, содержащее все внутренние окна и элементы управления.
 * <p>
 * Реализует интерфейс {@link Save} для поддержки сохранения и восстановления состояния окна.
 * Содержит игровое поле, окно координат и окно лога.
 */
public class MainApplicationFrame extends JFrame implements Save {
    /** Панель для размещения внутренних окон */
    private final JDesktopPane desktopPane = new JDesktopPane();

    /** Класс для управления сохранением и загрузкой состояния окон */
    private final SaveState saveState = new SaveState(new HashMap<>(), new HashSet<>());

    /** Менеджер локализации */
    private final LocalizationManager localization = LocalizationManager.getInstance();

    private LogWindow logWindow;
    private GameWindow gameWindow;
    private CoordinateWindow coordinateWindow;

    /**
     * Конструктор главного окна. Инициализирует и отображает все внутренние окна, меню,
     * загружает сохраненное состояние и обрабатывает закрытие окна.
     */
    public MainApplicationFrame() {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset, screenSize.width - inset * 2, screenSize.height - inset * 2);

        setContentPane(desktopPane);

        try {
            saveState.loadFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Загрузка сохраненной локали
        Locale savedLocale = saveState.loadLocale();
        localization.setLocale(savedLocale);

        setTitle(localization.getString("window.title"));

        gameWindow = new GameWindow();
        addWindow(gameWindow);
        saveState.registerWindow(gameWindow.getNameOfWindow());

        coordinateWindow = new CoordinateWindow(gameWindow.model);
        addWindow(coordinateWindow);
        saveState.registerWindow(coordinateWindow.getNameOfWindow());

        logWindow = createLogWindow();
        addWindow(logWindow);
        saveState.registerWindow(logWindow.getNameOfWindow());

        saveState.registerWindow(this.getNameOfWindow());

        saveState.setWindowParams(this);
        saveState.setWindowParams(logWindow);
        saveState.setWindowParams(gameWindow);
        saveState.setWindowParams(coordinateWindow);

        setJMenuBar(createMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
    }

    /**
     * Сохраняет текущие параметры (позиция, размер, состояние) главного окна.
     *
     * @return карта с параметрами состояния окна
     */
    @Override
    public Map<String, Integer> saveWindowState() {
        Map<String, Integer> state = new HashMap<>();
        state.put("x", getLocation().x);
        state.put("y", getLocation().y);
        state.put("width", getWidth());
        state.put("height", getHeight());
        state.put("state", getExtendedState());
        return state;
    }

    /**
     * Загружает параметры состояния окна из сохранённой карты.
     *
     * @param params карта параметров состояния окна
     */
    @Override
    public void loadWindowState(Map<String, Integer> params) {
        if (params != null) {
            Integer x = params.get("x");
            Integer y = params.get("y");
            Integer width = params.get("width");
            Integer height = params.get("height");
            Integer state = params.get("state");

            if (x != null && y != null) {
                setLocation(x, y);
            }
            if (width != null && height != null) {
                setSize(width, height);
            }
            if (state != null) {
                setExtendedState(state);
            }
        }
    }

    /**
     * Возвращает уникальное имя главного окна для системы сохранения.
     * @return имя окна
     */
    @Override
    public String getNameOfWindow() {
        return "MainApplicationFrame";
    }

    /**
     * Сохраняет состояние всех внутренних окон перед завершением работы приложения.
     */
    private void saveWindowStateBeforeExit() {
        saveState.saveWindowParams(this);
        saveState.saveWindowParams(logWindow);
        saveState.saveWindowParams(gameWindow);
        saveState.saveWindowParams(coordinateWindow);
        saveState.saveLocale(localization.getCurrentLocale()); // Сохраняем локаль
    }

    /**
     * Создаёт окно лога и инициализирует его параметры.
     *
     * @return объект {@link LogWindow}
     */
    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug(localization.getString("log.working"));
        return logWindow;
    }

    /**
     * Добавляет внутреннее окно на панель.
     *
     * @param frame внутреннее окно {@link JInternalFrame}
     */
    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    /**
     * Создаёт меню приложения.
     *
     * @return строка меню
     */
    protected JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(generateLookAndFeelMenu());
        menuBar.add(generateTestMenu());
        menuBar.add(generateDocumentMenu());
        menuBar.add(generateLanguageMenu());
        return menuBar;
    }

    private JMenu generateLookAndFeelMenu() {
        JMenu lookAndFeelMenu = new JMenu(localization.getString("menu.display"));
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                localization.getString("menu.display.description"));
        lookAndFeelMenu.add(createSystemLookAndFeelMenuButton());
        lookAndFeelMenu.add(createCrossPlatformLookAndFeelMenuButton());
        return lookAndFeelMenu;
    }

    private JMenuItem createSystemLookAndFeelMenuButton() {
        JMenuItem item = new JMenuItem(localization.getString("menu.item.system"), KeyEvent.VK_S);
        item.addActionListener(event -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });
        return item;
    }

    private JMenuItem createCrossPlatformLookAndFeelMenuButton() {
        JMenuItem item = new JMenuItem(localization.getString("menu.item.cross"), KeyEvent.VK_U);
        item.addActionListener(event -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });
        return item;
    }

    private JMenu generateTestMenu() {
        JMenu testMenu = new JMenu(localization.getString("menu.tests"));
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                localization.getString("menu.tests.description"));
        testMenu.add(createAddLogMessageButton());
        return testMenu;
    }

    private JMenuItem createAddLogMessageButton() {
        JMenuItem item = new JMenuItem(localization.getString("menu.item.log"), KeyEvent.VK_L);
        item.addActionListener(event -> Logger.debug(localization.getString("log.new.message")));
        return item;
    }

    private JMenu generateDocumentMenu() {
        JMenu menu = new JMenu(localization.getString("menu.application"));
        menu.setMnemonic(KeyEvent.VK_D);
        menu.add(createQuitButton());
        return menu;
    }

    private JMenuItem createQuitButton() {
        JMenuItem item = new JMenuItem(localization.getString("menu.item.exit"));
        item.setMnemonic(KeyEvent.VK_Q);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.ALT_MASK));
        item.setActionCommand("quit");
        item.addActionListener(event -> exit());
        return item;
    }

    private JMenu generateLanguageMenu() {
        JMenu languageMenu = new JMenu(localization.getString("menu.language"));
        languageMenu.setMnemonic(KeyEvent.VK_L);
        languageMenu.getAccessibleContext().setAccessibleDescription(
                localization.getString("menu.language.description"));

        JMenuItem russianItem = new JMenuItem("Русский");
        russianItem.addActionListener(e -> changeLocale(new Locale("ru")));

        JMenuItem englishItem = new JMenuItem("English");
        englishItem.addActionListener(e -> changeLocale(new Locale("en")));

        languageMenu.add(russianItem);
        languageMenu.add(englishItem);

        return languageMenu;
    }

    /**
     * Изменяет локаль приложения и обновляет интерфейс.
     * @param locale новая локаль
     */
    private void changeLocale(Locale locale) {
        localization.setLocale(locale);
        updateUIForLocalization();
        saveState.saveLocale(locale);
    }

    /**
     * Обновляет весь интерфейс для текущей локализации.
     */
    private void updateUIForLocalization() {
        // Обновляем заголовки окон
        setTitle(localization.getString("window.title"));
        if (gameWindow != null) {
            gameWindow.setTitle(localization.getString("game.window.title"));
        }
        if (logWindow != null) {
            logWindow.setTitle(localization.getString("log.window.title"));
        }
        if (coordinateWindow != null) {
            coordinateWindow.setTitle(localization.getString("coordinates.window.title"));
        }

        // Обновляем меню
        setJMenuBar(createMenuBar());

        // Перерисовываем интерфейс
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            // Игнорировать ошибки при установке L&F
        }
    }

    /**
     * Метод, вызываемый при завершении работы приложения. Сохраняет состояние и завершает программу.
     */
    private void exit() {
        int response = JOptionPane.showConfirmDialog(
                this,
                localization.getString("confirm.exit"),
                localization.getString("confirm.title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (response == JOptionPane.YES_OPTION) {
            saveWindowStateBeforeExit();
            try {
                saveState.saveToFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }
    }
}