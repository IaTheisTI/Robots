package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.*;


import log.Logger;

public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();

    public HashMap<String, Object> getProperties(JInternalFrame frame) {
        // Создание HashMap для хранения свойств
        HashMap<String, Object> result = new HashMap<String, Object>();

        // Добавление свойства "Местоположение" в HashMap
        result.put("Местоположение", frame.getLocation());

        // Добавление свойства "Размер" в HashMap
        result.put("Размер", frame.getSize());

        // Добавление свойства "Выбран" в HashMap
        result.put("Выбран", frame.isSelected());

        //Добавление свернутого окна
        result.put("Свернут", frame.isIcon());

        // Проверка, является ли frame экземпляром LogWindow
        // Добавление свойства "Тип" как "Журнал"
        if (frame instanceof LogWindow)
            result.put("Тип", "Журнал");
            // Проверка, является ли frame экземпляром GameWindow
            // Добавление свойства "Тип" как "Игра"
        else if (frame instanceof GameWindow)
            result.put("Тип", "Игра");
        // Возврат HashMap, содержащего свойства
        return result;
    }

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
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        setContentPane(desktopPane);
        setJMenuBar(generateMenuBar());

        // Установка действия по умолчанию при закрытии окна
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);


        // Добавляем слушатель оконных событий
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Отображение диалогового окна с подтверждением выхода
                exitWindow();
            }
        });


        start.restoreWindows(this);
    }


    public class start {
        public static void restoreWindows(MainApplicationFrame mainAppFrame) {
            File configFile = new File(System.getProperty("user.home"), "saves.out");

            if (!configFile.exists()) {
                return; // Если файл конфигурации не существует, выходим
            }

            try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(configFile)))) {
                ArrayList<HashMap<String, Object>> restored = (ArrayList<HashMap<String, Object>>) ois.readObject();

                for (HashMap<String, Object> frame : restored) {
                    try {
                        JInternalFrame window = null;
                        if ("Журнал".equals(frame.get("Тип"))) {
                            window = mainAppFrame.createLogWindow();
                        } else if ("Игра".equals(frame.get("Тип"))) {
                            window = new GameWindow();
                        }

                        if (window != null) {
                            window.setLocation((java.awt.Point) frame.get("Местоположение"));
                            window.setSize((java.awt.Dimension) frame.get("Размер"));
                            window.setSelected((boolean) frame.get("Выбран"));
                            mainAppFrame.addWindow(window, window.getSize().width, window.getSize().height);
                            if ((boolean) frame.get("Свернут")) {
                                window.setIcon(true);
                            }
                        }
                    } catch (java.beans.PropertyVetoException e) {
                        System.err.println("Ошибка при восстановлении окна: " + e.getMessage());
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Создает окно журнала.
     */
    protected LogWindow createLogWindow() {
        // Создание экземпляра окна журнала с использованием
        // стандартного источника журнала
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());

        // Вывод отладочного сообщения о начале работы протокола
        Logger.debug(("Протокол Работает"));

        // Возвращение созданного окна журнала
        return logWindow;
    }

    /**
     * Метод addWindow добавляет внутреннее окно типа JInternalFrame к рабочей области desktopPane.
     * Устанавливает видимость добавленного окна.
     */
    protected void addWindow(JInternalFrame frame, int width, int height) {
        desktopPane.add(frame);
        frame.setSize(width, height);
        frame.setVisible(true);
    }

    /**
     * Метод generateMenuBar создает и возвращает строку меню (JMenuBar) для главного окна приложения.
     * Включает в себя различные подменю, такие как меню файла, меню внешнего вида и тестовые команды.
     */
    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        menuBar.add(createMenuBar());

        menuBar.add(createLookAndFeelMenu());

        menuBar.add(createTestMenu());

        return menuBar;
    }

    /**
     * Метод createMenuBar создает и возвращает меню для раздела "Меню" в строке меню приложения.
     * Включает в себя пункты меню для создания нового игрового поля, открытия окна логов.
     */
    private JMenu createMenuBar() {

        JMenu menu = new JMenu(("Меню"));
        menu.setMnemonic(KeyEvent.VK_D);

        menu.add(createMenuItem(("Новое игровое окно"), KeyEvent.VK_N, KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.ALT_MASK), (event) -> {
            GameWindow window = new GameWindow();
            addWindow(window, 400, 400);
        }));


        menu.add(createMenuItem(("Журнальное окно"), KeyEvent.VK_L, KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.ALT_MASK), (event) -> {
            LogWindow window = new LogWindow(Logger.getDefaultLogSource());
            addWindow(window, 150, 350);
        }));

        menu.add(exit());

        return menu;
    }

    /**
     * Создает элемент меню.
     */
    private JMenuItem createMenuItem(String text, int mnemonic, KeyStroke accelerator, ActionListener action) {
        // Создание нового элемента меню с указанным текстом
        JMenuItem item = new JMenuItem(text);

        // Установка мнемоники для быстрого доступа к элементу меню
        item.setMnemonic(mnemonic);
        // Установка сочетания клавиш для быстрого доступа к элементу меню
        item.setAccelerator(accelerator);
        // Установка слушателя действий для элемента меню
        item.addActionListener(action);

        return item;

    }

    /**
     * Метод createLookAndFeelMenu создает и возвращает меню для раздела "Режим отображения" в строке меню приложения.
     * Включает в себя пункты меню для установки системной и универсальной схем отображения приложения.
     */
    private JMenu createLookAndFeelMenu() {
        JMenu lookAndFeelMenu = new JMenu(("Дисплей"));
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(("Управление режимом отображения приложения"));

        lookAndFeelMenu.add(createMenuItem(("Системная схема"), KeyEvent.VK_S, null, (event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        }));

        lookAndFeelMenu.add(createMenuItem(("Универсальная схема"), KeyEvent.VK_U, null, (event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        }));

        return lookAndFeelMenu;
    }

    /**
     * Метод createTestMenu создает и возвращает меню для раздела "Тесты" в строке меню приложения.
     * Включает в себя пункт меню для добавления сообщения в лог.
     */
    private JMenu createTestMenu() {
        JMenu testMenu = new JMenu(("Тесты"));
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(("Команды тестов"));

        JMenuItem addLogMessageItem = new JMenuItem(("Сообщение в журнал"), KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug(("Новая строка"));
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
        }
    }

    /**
     * Создает элемент меню для выхода из приложения.
     */
    private JMenuItem exit()
    {
        // Создание элемента меню для выхода с указанным текстом
        JMenuItem exitMenuItem = new JMenuItem(("Выход"));

        // Установка мнемоники для быстрого доступа к элементу меню
        exitMenuItem.setMnemonic(KeyEvent.VK_Q);
        // Установка сочетания клавиш для быстрого доступа к элементу меню
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.ALT_MASK));
        // Установка слушателя действий для элемента меню
        exitMenuItem.addActionListener((event) -> {
            exitWindow();
        });
        return exitMenuItem;

    }

    /**
     * Метод exitWindow() отвечает за выход из текущего окна с подтверждением.
     * Устанавливает текст для кнопок подтверждения на русском языке.
     * Если пользователь подтверждает выход, окно будет закрыто.
     */
    private void exitWindow() {
        // Установка текста для кнопок подтверждения
        UIManager.put("OptionPane.yesButtonText", ("Да"));
        UIManager.put("OptionPane.noButtonText", ("Нет"));
        // Отображение диалогового окна с подтверждением выхода
        int confirmation = JOptionPane.showConfirmDialog(this, ("Вы, действительно, что хотите выйти?"), ("Подтверждение выхода"), JOptionPane.YES_NO_OPTION);
        // Закрытие окна при подтверждении выхода
        if (confirmation == JOptionPane.YES_OPTION) {
            saveAndExit(); // Вызов метода сохранения и выхода
        }
    }

    /**
     * Сохраняет состояние окон на рабочем столе в файл и завершает работу приложения.
     */
    private void saveAndExit() {
        save(); // Вызов метода сохранения состояния
        this.dispose(); // Закрытие текущего окна
        System.exit(0); // Завершение работы приложения
    }

    /**
     * Сохраняет состояние окон на рабочем столе в файл.
     */
    private void save() {
        ArrayList<HashMap<String, Object>> frames = new ArrayList<>();
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            frames.add(getProperties(frame));
        }

        String userHomeDir = System.getProperty("user.home");
        File configFile = new File(userHomeDir, "saves.out");

        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        try {
            fos = new FileOutputStream(configFile);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(frames);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}