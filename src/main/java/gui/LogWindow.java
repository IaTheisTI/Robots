package gui;

import log.*;
import model.RobotCoordinate;
import state.*;
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.util.*;

public class LogWindow extends JInternalFrame implements LogChangeListener, Save, Observer
{
    private final FrameState stateFormer = new FrameState(this);
    private final LogWindowSource m_logSource;
    private final TextArea m_logContent;

    private final RobotCoordinate robotCoordinate;

    public LogWindow(LogWindowSource logSource, RobotCoordinate robotCoordinate)
    {
        super("Протокол работы", true, true, true, true);
        this.robotCoordinate = robotCoordinate;
        this.robotCoordinate.addObserver(this);
        m_logSource = logSource;
        m_logSource.registerListener(this);
        m_logContent = new TextArea("");
        m_logContent.setSize(200, 500);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateLogContent();
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
        Logger.debug("x: " + robotCoordinate.getM_targetPositionX()
                + " y: " + robotCoordinate.getM_targetPositionY());
    }
}