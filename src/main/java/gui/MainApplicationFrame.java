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

public class MainApplicationFrame extends JFrame implements Save {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final SaveState saveState = new SaveState(new HashMap<>(), new HashSet<>());

    /** Менеджер локализации */
    private final LocalizationManager localization = LocalizationManager.getInstance();

    private LogWindow logWindow;
    private GameWindow gameWindow;
    private CoordinateWindow coordinateWindow;

    // Элементы меню, которые нужно обновлять при смене языка
    private JMenu lookAndFeelMenu;
    private JMenu testMenu;
    private JMenu languageMenu;
    private JMenu documentMenu;
    private JMenuItem exitMenuItem;

    public MainApplicationFrame() {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset, screenSize.width - inset * 2, screenSize.height - inset * 2);
        setContentPane(desktopPane);

        // Загрузка сохраненного состояния
        try {
            saveState.loadFromFile();
        } catch (IOException e) {
            Logger.error("Ошибка загрузки состояния: " + e.getMessage());
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
    }

    protected JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Меню внешнего вида
        lookAndFeelMenu = new JMenu();
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.add(createLookAndFeelMenuItem("System", KeyEvent.VK_S,
                UIManager.getSystemLookAndFeelClassName()));
        lookAndFeelMenu.add(createLookAndFeelMenuItem("Cross-platform", KeyEvent.VK_U,
                UIManager.getCrossPlatformLookAndFeelClassName()));
        menuBar.add(lookAndFeelMenu);

        // Меню тестов
        testMenu = new JMenu();
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.add(createTestMenuItem());
        menuBar.add(testMenu);

        // Меню языка
        languageMenu = new JMenu();
        languageMenu.setMnemonic(KeyEvent.VK_L);
        languageMenu.add(createLanguageMenuItem("Русский", "ru"));
        languageMenu.add(createLanguageMenuItem("English", "en"));
        menuBar.add(languageMenu);

        // Меню приложения
        documentMenu = new JMenu();
        documentMenu.setMnemonic(KeyEvent.VK_A);
        exitMenuItem = createExitMenuItem();
        documentMenu.add(exitMenuItem);
        menuBar.add(documentMenu);

        return menuBar;
    }

    private JMenuItem createLookAndFeelMenuItem(String text, int mnemonic, String lookAndFeel) {
        JMenuItem item = new JMenuItem(text, mnemonic);
        item.addActionListener(e -> {
            setLookAndFeel(lookAndFeel);
            invalidate();
        });
        return item;
    }

    private JMenuItem createTestMenuItem() {
        JMenuItem item = new JMenuItem("Log message", KeyEvent.VK_L);
        item.addActionListener(e -> Logger.debug("Новая строка"));
        return item;
    }

    private JMenuItem createLanguageMenuItem(String text, String languageCode) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(e -> {
            LocalizationManager.setLocale(new Locale(languageCode));
            updateLocalizedUI();
        });
        return item;
    }

    private JMenuItem createExitMenuItem() {
        JMenuItem item = new JMenuItem("Exit", KeyEvent.VK_Q);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.ALT_MASK));
        item.addActionListener(e -> exit());
        return item;
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            Logger.error("Ошибка установки LookAndFeel: " + e.getMessage());
        }
    }

    private void updateLocalizedUI() {
        // Обновление меню
        lookAndFeelMenu.setText(LocalizationManager.getString("lookAndFeel"));
        testMenu.setText(LocalizationManager.getString("tests"));
        languageMenu.setText(LocalizationManager.getString("language"));
        documentMenu.setText(LocalizationManager.getString("application"));
        exitMenuItem.setText(LocalizationManager.getString("exit"));

        // Обновление заголовка окна
        setTitle(LocalizationManager.getString("applicationTitle"));

        // Обновление всех внутренних окон
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void exit() {
        int response = JOptionPane.showConfirmDialog(
                this,
                LocalizationManager.getString("confirmExit"),
                LocalizationManager.getString("confirmExitTitle"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (response == JOptionPane.YES_OPTION) {
            saveWindowStateBeforeExit();
            try {
                saveState.saveToFile();
            } catch (IOException e) {
                Logger.error("Ошибка сохранения состояния: " + e.getMessage());
            }
            System.exit(0);
        }
    }

    // Остальные методы остаются без изменений
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

    @Override
    public void loadWindowState(Map<String, Integer> params) {
        if (params != null) {
            Integer x = params.get("x");
            Integer y = params.get("y");
            Integer width = params.get("width");
            Integer height = params.get("height");
            Integer state = params.get("state");

            if (x != null && y != null) setLocation(x, y);
            if (width != null && height != null) setSize(width, height);
            if (state != null) setExtendedState(state);
        }
    }

    @Override
    public String getNameOfWindow() {
        return "MainApplicationFrame";
    }

    private void saveWindowStateBeforeExit() {
        saveState.saveWindowParams(this);
        saveState.saveWindowParams(logWindow);
        saveState.saveWindowParams(gameWindow);
        saveState.saveWindowParams(coordinateWindow);
        saveState.saveLocale(localization.getCurrentLocale()); // Сохраняем локаль
    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug(localization.getString("log.working"));
        return logWindow;
    }

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