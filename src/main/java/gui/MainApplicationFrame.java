package gui;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;
import model.RobotCoordinate;

import javax.swing.*;
import state.*;
import log.Logger;

/**
 * Класс MainApplicationFrame представляет основное окно приложения.
 * Он содержит методы для создания и управления окнами, меню и обработки событий.
 */
public class MainApplicationFrame extends JFrame {

    // Константы для элементов меню и сообщений диалогов
    private static final String CLOSING_MENU_ITEM_TITLE = "Выход";
    private static final String LOOK_AND_FEEL_MENU_TITLE = "Режим отображения";
    private static final String LOOK_AND_FEEL_MENU_DESCRIPTION = "Управление режимом отображения приложения";
    private static final String LOOK_AND_FEEL_MENU_ITEM_SYSTEM_SCHEME = "Системная схема";
    private static final String LOOK_AND_FEEL_MENU_ITEM_UNI_SCHEME = "Универсальная схема";
    private static final String TEST_MENU_TITLE = "Тесты";
    private static final String TEST_MENU_DESCRIPTION = "Команды тестов";
    private static final String TEST_MENU_ITEM_TITLE = "Сообщение в журнал";
    private static final String TEST_DEBUG_MESSAGE = "Новая строка";
    private static final String PROTOCOL_WORKING_DEBUG_MESSAGE = "Протокол работает";
    private static final String CLOSE_CONFIRMATION_DIALOG_TITLE = "Подтверждение выхода";
    private static final String CLOSE_CONFIRMATION_DIALOG_QUESTION = "Вы, действительно, что хотите выйти?";
    private static final String CLOSE_CONFIRMATION_DIALOG_YES_OPTION = "Да";
    private static final String CLOSE_CONFIRMATION_DIALOG_NO_OPTION = "Нет";

    private final JDesktopPane desktopPane = new JDesktopPane();
    private final State stateFileManager = new State();


    /**
     * Конструктор класса MainApplicationFrame создает главное окно приложения.
     * Устанавливает размеры и расположение окна, добавляет рабочую область,
     * создает и добавляет окно для логирования и окно для игры,
     * устанавливает строку меню и операцию по закрытию окна приложения.
     */
    public MainApplicationFrame() throws PropertyVetoException {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        setContentPane(desktopPane);

        RobotCoordinate robotCoordinate = new RobotCoordinate();

        // Создание окон
        LogWindow logWindow = createLogWindow(robotCoordinate);
        addWindow(logWindow);

        GameWindow gameWindow = createGameWindow(robotCoordinate);
        addWindow(gameWindow);

        // Установка меню и операции закрытия
        setJMenuBar(createMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                // Сохранение состояния и отображение диалога подтверждения выхода
                Map<String, String> state = new HashMap<>();
                StateTransformer
                        .addSubMapToGeneralMapByPrefix("log", logWindow.saveState(), state);
                StateTransformer
                        .addSubMapToGeneralMapByPrefix("game", gameWindow.saveState(), state);
                stateFileManager.writeStateInFile(state);
                exitWindow(event);
            }
        });
    }


    /**
     * Создает окно журнала.
     */
    protected LogWindow createLogWindow(RobotCoordinate robotCoordinate) throws PropertyVetoException {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource(), robotCoordinate);
        Map<String, String> state = stateFileManager.readStateFromFile();
        if (state != null) {
            logWindow.restoreState(StateTransformer.getSubMap(state, "log"));
        } else {
            logWindow.setLocation(10, 10);
            logWindow.setSize(300, 800);
            setMinimumSize(logWindow.getSize());
            logWindow.pack();
        }
        Logger.debug(PROTOCOL_WORKING_DEBUG_MESSAGE);
        return logWindow;
    }
    protected GameWindow createGameWindow(RobotCoordinate robotCoordinate) throws PropertyVetoException {
        GameWindow gameWindow = new GameWindow(robotCoordinate);
        Map<String, String> state = stateFileManager.readStateFromFile();
        if (state != null) {
            gameWindow.restoreState(StateTransformer.getSubMap(state,"game"));
        } else {
            gameWindow.setSize(400, 400);
        }
        return gameWindow;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }


    /**
     * Метод createMenuBar создает и возвращает меню для раздела "Меню" в строке меню приложения.
     * Включает в себя пункты меню для создания нового игрового поля, открытия окна логов.
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu lookAndFeelMenu = createLookAndFeelMenu();
        JMenu testMenu = createTestMenu();
        JMenuItem closingMenuItem = createMenuItem(CLOSING_MENU_ITEM_TITLE, KeyEvent.VK_X, null,
                (event) -> Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(
                        new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        menuBar.add(closingMenuItem);
        return menuBar;
    }

    
    /**
     * Создает элемент меню.
     */
    private JMenuItem createMenuItem(String text, int mnemonic, KeyStroke accelerator, ActionListener action) {
        JMenuItem item = new JMenuItem(text);
        item.setMnemonic(mnemonic);
        item.setAccelerator(accelerator);
        item.addActionListener(action);
        return item;
    }


    /**
     * Метод createLookAndFeelMenu создает и возвращает меню для раздела "Режим отображения" в строке меню приложения.
     * Включает в себя пункты меню для установки системной и универсальной схем отображения приложения.
     */
    private JMenu createLookAndFeelMenu() {
        JMenu lookAndFeelMenu = new JMenu(LOOK_AND_FEEL_MENU_TITLE);
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(LOOK_AND_FEEL_MENU_DESCRIPTION);

        JMenuItem systemLookAndFeel = createMenuItem(LOOK_AND_FEEL_MENU_ITEM_SYSTEM_SCHEME, KeyEvent.VK_S, null,
                (event) -> {
                    setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    this.invalidate();
                });
        lookAndFeelMenu.add(systemLookAndFeel);

        JMenuItem crossplatformLookAndFeel = createMenuItem(LOOK_AND_FEEL_MENU_ITEM_UNI_SCHEME, KeyEvent.VK_S, null,
                (event) -> {
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
        JMenu testMenu = new JMenu(TEST_MENU_TITLE);
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(TEST_MENU_DESCRIPTION);

        JMenuItem addLogMessageItem = createMenuItem(TEST_MENU_ITEM_TITLE, KeyEvent.VK_S, null,
                (event) -> Logger.debug(TEST_DEBUG_MESSAGE));
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
            // just ignore
        }
    }


    /**
     * Метод exitWindow() отвечает за выход из текущего окна с подтверждением.
     * Устанавливает текст для кнопок подтверждения на русском языке.
     * Если пользователь подтверждает выход, окно будет закрыто.
     */
    private void exitWindow(WindowEvent event) {
        Object[] options = {CLOSE_CONFIRMATION_DIALOG_YES_OPTION, CLOSE_CONFIRMATION_DIALOG_NO_OPTION};
        int n = JOptionPane.showOptionDialog(event.getWindow(), CLOSE_CONFIRMATION_DIALOG_QUESTION,
                CLOSE_CONFIRMATION_DIALOG_TITLE, JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (n == 0) {
            event.getWindow().setVisible(false);
            System.exit(0);
        }
    }

}
