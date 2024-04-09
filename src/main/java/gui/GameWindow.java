package gui;

import javax.swing.*;
import java.awt.*;
import state.*;
import java.beans.PropertyVetoException;
import java.util.Map;



public class GameWindow extends JInternalFrame implements Save {
    private final FrameState stateFormer = new FrameState(this);

    public GameWindow() {
        super("Игровое поле", true, true, true, true);
        GameVisualizer m_visualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    @Override
    public Map<String, String> saveState() {
        return stateFormer.saveState();
    }

    @Override
    public void restoreState(Map<String, String> data) throws PropertyVetoException {
        stateFormer.restoreState(data);
    }

}