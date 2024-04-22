package gui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import log.Logger;
import model.RobotModel;
import state.StateTransformer;
import state.State;

public class MainApplicationFrame extends JFrame {
    private static final String CLOSING_MENU_ITEM_TITLE = "Закрыть";
    private static final String LOOK_AND_FEEL_MENU_TITLE = "Режим отображения";
    private static final String LOOK_AND_FEEL_MENU_DESCRIPTION = "Управление режимом отображения приложения";
    private static final String LOOK_AND_FEEL_MENU_ITEM_SYSTEM_SCHEME = "Системная схема";
    private static final String LOOK_AND_FEEL_MENU_ITEM_UNI_SCHEME = "Универсальная схема";

    private static final String TEST_MENU_TITLE = "Тесты";
    private static final String TEST_MENU_DESCRIPTION = "Тестовые команды";
    private static final String TEST_MENU_ITEM_TITLE = "Сообщение в лог";
    private static final String TEST_DEBUG_MESSAGE = "Новая строка";

    private static final String PROTOCOL_WORKING_DEBUG_MESSAGE = "Протокол работает";
    private static final String CLOSE_CONFIRMATION_DIALOG_TITLE = "Подтверждение выхода";
    private static final String CLOSE_CONFIRMATION_DIALOG_QUESTION = "Закрыть окно?";
    private static final String CLOSE_CONFIRMATION_DIALOG_YES_OPTION = "Да";
    private static final String CLOSE_CONFIRMATION_DIALOG_NO_OPTION = "Нет";

    private final JDesktopPane desktopPane = new JDesktopPane();
    private final State stateFileManager = new State();

    private CoordinateWindow coordinateWindow;

    public MainApplicationFrame() throws PropertyVetoException {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        setContentPane(desktopPane);

        RobotModel robotModel = new RobotModel();

        LogWindow logWindow = createLogWindow(robotModel);
        addWindow(logWindow);

        GameWindow gameWindow = createGameWindow(robotModel);
        addWindow(gameWindow);

        // Создаем и добавляем CoordinateWindow
        coordinateWindow = createCoordinateWindow(robotModel);

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                Map<String, String> state = new HashMap<>();
                StateTransformer
                        .addSubMapToGeneralMapByPrefix("log",
                                logWindow.saveState(), state);
                StateTransformer
                        .addSubMapToGeneralMapByPrefix("game",
                                gameWindow.saveState(), state);
                stateFileManager.writeStateInFile(state);
                showConfirmationExitDialog(event);
            }
        });
    }

    private void showConfirmationExitDialog(WindowEvent event) {
        Object[] options = {CLOSE_CONFIRMATION_DIALOG_YES_OPTION, CLOSE_CONFIRMATION_DIALOG_NO_OPTION};
        int n = JOptionPane.showOptionDialog(event.getWindow(), CLOSE_CONFIRMATION_DIALOG_QUESTION,
                CLOSE_CONFIRMATION_DIALOG_TITLE, JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (n == 0) {
            event.getWindow().setVisible(false);
            System.exit(0);
        }
    }

    protected LogWindow createLogWindow(RobotModel robotModel) throws PropertyVetoException {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
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

    protected GameWindow createGameWindow(RobotModel robotModel) throws PropertyVetoException {
        GameWindow gameWindow = new GameWindow(robotModel);
        Map<String, String> state = stateFileManager.readStateFromFile();
        if (state != null) {
            gameWindow.restoreState(StateTransformer.getSubMap(state,"game"));
        } else {
            gameWindow.setSize(400, 400);
        }
        return gameWindow;
    }

    protected CoordinateWindow createCoordinateWindow(RobotModel robotModel) {
        CoordinateWindow coordinateWindow = new CoordinateWindow(robotModel);
        addWindow(coordinateWindow);
        return coordinateWindow;
    }


    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu lookAndFeelMenu = generateLookAndFeelMenu();
        JMenu testMenu = generateTestMenu();
        JMenuItem closingMenuItem = createNewJMenuItem(CLOSING_MENU_ITEM_TITLE, KeyEvent.VK_X,
                (event) -> Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(
                        new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        menuBar.add(closingMenuItem);
        return menuBar;
    }

    private JMenuItem createNewJMenuItem(String itemText, int keyEvent, ActionListener listener) {
        JMenuItem newItem = new JMenuItem(itemText, keyEvent);
        newItem.addActionListener(listener);
        return newItem;
    }

    private JMenu createNewJMenu(String title, int keyEvent, String accessibleDescription) {
        JMenu newMenuBar = new JMenu(title);
        newMenuBar.setMnemonic(keyEvent);
        newMenuBar.getAccessibleContext().setAccessibleDescription(
                accessibleDescription);
        return newMenuBar;
    }

    private JMenu generateLookAndFeelMenu() {
        JMenu lookAndFeelMenu = createNewJMenu(LOOK_AND_FEEL_MENU_TITLE, KeyEvent.VK_V,
                LOOK_AND_FEEL_MENU_DESCRIPTION);

        {
            JMenuItem systemLookAndFeel = createNewJMenuItem(LOOK_AND_FEEL_MENU_ITEM_SYSTEM_SCHEME, KeyEvent.VK_S,
                    (event) -> {
                        setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                        this.invalidate();
                    });
            lookAndFeelMenu.add(systemLookAndFeel);
        }

        {
            JMenuItem crossplatformLookAndFeel = createNewJMenuItem(LOOK_AND_FEEL_MENU_ITEM_UNI_SCHEME, KeyEvent.VK_S,
                    (event) -> {
                        setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                        this.invalidate();
                    });
            lookAndFeelMenu.add(crossplatformLookAndFeel);
        }
        return lookAndFeelMenu;
    }

    private JMenu generateTestMenu() {
        JMenu testMenu = createNewJMenu(TEST_MENU_TITLE, KeyEvent.VK_T, TEST_MENU_DESCRIPTION);

        {
            JMenuItem addLogMessageItem = createNewJMenuItem(TEST_MENU_ITEM_TITLE, KeyEvent.VK_S,
                    (event) -> Logger.debug(TEST_DEBUG_MESSAGE));
            testMenu.add(addLogMessageItem);
        }
        return testMenu;
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException
                 | IllegalAccessException | UnsupportedLookAndFeelException e) {
            // just ignore
        }
    }
}
