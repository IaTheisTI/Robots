package gui;

import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;
import log.Logger;
import model.RobotModel;
import state.FrameState;
import state.Save;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.util.*;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;

public class LogWindow extends JInternalFrame implements LogChangeListener, Save, Observer
{
    private final FrameState stateFormer = new FrameState(this);
    private final LogWindowSource m_logSource;
    private final TextArea m_logContent;
    private final RobotModel robotModel;

    public LogWindow(LogWindowSource logSource, RobotModel robotModel)
    {
        super("Протокол работы", true, true, true, true);
        this.robotModel = robotModel;
        this.robotModel.addObserver(this);
        m_logSource = logSource;
        m_logSource.registerListener(this);
        m_logContent = new TextArea("");
        m_logContent.setSize(200, 500);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateLogContent();

        //отписка от источника данных при закрытии окна
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                m_logSource.unregisterListener(LogWindow.this);
            }
        });
    }

    private void updateLogContent()
    {
        StringBuilder content = new StringBuilder();
        for (LogEntry entry : m_logSource.all())
        {
            content.append(entry.getMessage()).append("\n");
        }
        m_logContent.setText(content.toString());
        m_logContent.invalidate();
    }

    @Override
    public void onLogChanged()
    {
        EventQueue.invokeLater(this::updateLogContent);
    }

    @Override
    public Map<String, String> saveState(){
        return stateFormer.saveState();
    }

    @Override
    public void restoreState(Map<String, String> data) throws PropertyVetoException {
        stateFormer.restoreState(data);
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}