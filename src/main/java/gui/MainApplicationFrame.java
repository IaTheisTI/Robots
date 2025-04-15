package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import log.Logger;

public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();

    /**
     * Конструктор класса MainApplicationFrame создает главное окно приложения.
     * Устанавливает размеры и расположение окна, добавляет рабочую область,
     * создает и добавляет окно для логирования и окно для игры,
     * устанавливает строку меню и операцию по закрытию окна приложения.
     */
    public MainApplicationFrame() {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width  - inset*2,
                screenSize.height - inset*2);

        // Установка содержимого окна в виде рабочей области
        setContentPane(desktopPane);

        // Создание и добавление окна для логирования
        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        // Создание и добавление окна для игры
        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400, 400);
        addWindow(gameWindow);

        setJMenuBar(generateMenuBar());

        // Установка операции по закрытию окна приложения
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        // Добавление обработки для события закрытия окна
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exitWindow();
            }
        });
    }

    /**
     * Метод createLogWindow создает и настраивает окно логирования.
     * Окно получает логгер по умолчанию и устанавливает его расположение, размер,
     * минимальный размер окна, и затем упаковывает содержимое.
     * Также, выводится отладочное сообщение в лог о успешной инициализации логгера.
     */
    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);

        // Установка минимального размера окна
        setMinimumSize(logWindow.getSize());
        logWindow.pack();

        // Вывод отладочного сообщения о успешной инициализации логгера
        Logger.debug("Протокол работает");
        return logWindow;
    }

    /**
     * Метод addWindow добавляет внутреннее окно типа JInternalFrame к рабочей области desktopPane.
     * Устанавливает видимость добавленного окна.
     */
    protected void addWindow(JInternalFrame frame)
    {
        // Добавление внутреннего окна к рабочей области
        desktopPane.add(frame);

        // Установка видимости добавленного окна
        frame.setVisible(true);
    }

    /**
     * Метод generateMenuBar создает и возвращает строку меню (JMenuBar) для главного окна приложения.
     * Включает в себя различные подменю, такие как меню файла, меню внешнего вида и тестовые команды.
     */
    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();

        // Добавление меню файла
        JMenu menu = createMenuBar();
        menuBar.add(menu);

        // Добавление меню внешнего вида
        JMenu lookAndFeelMenu = createLookAndFeelMenu();
        menuBar.add(lookAndFeelMenu);

        // Добавление меню тестовых команд
        JMenu testMenu = createTestMenu();
        menuBar.add(testMenu);

        return menuBar;
    }

    /**
     * Метод createMenuBar создает и возвращает меню для раздела "Меню" в строке меню приложения.
     * Включает в себя пункты меню для создания нового игрового поля, открытия окна логов
     * и выхода из приложения.
     */
    private JMenu createMenuBar() {
        JMenu menu = new JMenu("Меню");
        menu.setMnemonic(KeyEvent.VK_D);

        // Пункт меню для создания нового игрового поля
        JMenuItem menuItem = new JMenuItem("Новое игровое поле");
        menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.ALT_MASK));
        menuItem.addActionListener((event) -> {
            GameWindow newGameWindow = new GameWindow();
            newGameWindow.setSize(400, 400);
            addWindow(newGameWindow);
        });
        menu.add(menuItem);

        // Пункт меню для открытия окна логов
        JMenuItem LogItem = new JMenuItem("Окно логов");
        LogItem.setMnemonic(KeyEvent.VK_L);
        LogItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.ALT_MASK));
        LogItem.addActionListener((event) -> {
            LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
            addWindow(logWindow);
        });
        menu.add(LogItem);

        // Пункт меню для выхода из приложения
        JMenuItem exitMenuItem = new JMenuItem("Выход");
        exitMenuItem.setMnemonic(KeyEvent.VK_Q);
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.ALT_MASK));
        exitMenuItem.addActionListener((event) -> {
            exitWindow();
        });
        menu.add(exitMenuItem);

        return menu;
    }

    /**
     * Метод createLookAndFeelMenu создает и возвращает меню для раздела "Режим отображения" в строке меню приложения.
     * Включает в себя пункты меню для установки системной и универсальной схем отображения приложения.
     */
    private JMenu createLookAndFeelMenu() {
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription("Управление режимом отображения приложения");
        // Пункт меню для установки системной схемы
        JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
        systemLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });
        lookAndFeelMenu.add(systemLookAndFeel);

        // Пункт меню для установки универсальной схемы
        JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_U);
        crossplatformLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });
        lookAndFeelMenu.add(crossplatformLookAndFeel);

        return lookAndFeelMenu;
    }

    /**
     * Метод createTestMenu создает и возвращает меню для раздела "Тесты" в строке меню приложения.
     * Включает в себя пункт меню для добавления сообщения в лог.
     */
    private JMenu createTestMenu() {
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription("Тестовые команды");

        // Пункт меню для добавления сообщения в лог
        JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug("Новая строка");
        });
        testMenu.add(addLogMessageItem);

        return testMenu;
    }

    /**
     * Метод setLookAndFeel устанавливает внешний вид приложения на основе переданного класса внешнего вида.
     * Обновляет компоненты пользовательского интерфейса для применения изменений.
     */
    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException
                 | IllegalAccessException | UnsupportedLookAndFeelException e) {
            // Просто игнорируем возможные ошибки установки внешнего вида
        }
    }

    /**
     * Метод exitWindow() отвечает за выход из текущего окна с подтверждением.
     * Устанавливает текст для кнопок подтверждения на русском языке.
     * Если пользователь подтверждает выход, окно будет закрыто.
     */
    private void exitWindow() {
        // Установка текста для кнопок подтверждения
        UIManager.put("OptionPane.yesButtonText", "Да");
        UIManager.put("OptionPane.noButtonText", "Нет");

        // Отображение диалогового окна с подтверждением выхода
        int confirmation = JOptionPane.showConfirmDialog(this, "Вы, действительно, хотите выйти?", "Подтверждение выхода", JOptionPane.YES_NO_OPTION);

        // Закрытие окна при подтверждении выхода
        if (confirmation == JOptionPane.YES_OPTION) {
            this.dispose();
            System.exit(0);
        }
    }
}