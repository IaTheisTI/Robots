package gui;

import localization.LocalizationManager;
import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;
import log.Logger;
import state.Save;

import javax.swing.*;
import java.awt.*;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Окно для отображения логов работы приложения
 * Поддерживает локализацию и сохранение состояния
 */
public class LogWindow extends AbstractWindow implements LogChangeListener, Save, PropertyChangeListener {
    private final LogWindowSource logSource;
    private final JTextArea logContent;

    public LogWindow(LogWindowSource logSource) {
        super(LocalizationManager.getInstance().getString("log.window.title"), 300, 800, 10, 10);
        m_logSource = logSource;
        m_logSource.registerListener(this);


        // Настройка текстовой области
        logContent = new JTextArea();
        logContent.setEditable(false);
        logContent.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(logContent);
        scrollPane.setPreferredSize(new Dimension(300, 800));

        getContentPane().add(scrollPane, BorderLayout.CENTER);
        pack();

        // Регистрация слушателя изменений локализации
        LocalizationManager.addLocaleChangeListener(this);

        updateLogContent();
        Logger.debug(LocalizationManager.getInstance().getString("log.working"));
    }

    private void updateLogContent() {
        StringBuilder content = new StringBuilder();
        for (LogEntry entry : logSource.all()) {
            content.append(entry.getMessage()).append("\n");
        }
        logContent.setText(content.toString());
        logContent.setCaretPosition(logContent.getDocument().getLength());
    }

    @Override
    public void onLogChanged() {
        EventQueue.invokeLater(this::updateLogContent);
    }

    @Override
    public String getNameOfWindow() {
        return "LogWindow";
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("locale".equals(evt.getPropertyName())) {
            setTitle(LocalizationManager.getString("log.title"));
        }
    }
}