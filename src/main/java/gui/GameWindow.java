package gui;

import model.RobotModel;
import state.Save;
import state.FrameState;

import java.awt.BorderLayout;
import java.beans.PropertyVetoException;
import java.util.Map;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class GameWindow extends JInternalFrame implements Save {
    private final FrameState stateFormer = new FrameState(this);

    public GameWindow(RobotModel robotModel) {
        super("Игровое поле", true, true, true, true);
        GameVisualizer m_visualizer = new GameVisualizer(robotModel);
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
