package gui;

import model.RobotModel;
import log.Logger;
import state.FrameState;
import state.Save;
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class CoordinateWindow extends JInternalFrame implements Save, Observer {
    private final FrameState stateFormer = new FrameState(this);
    private final JTextArea coordinatesArea;

    public CoordinateWindow(RobotModel robotModel) {
        super("Координаты робота", true, true, true, true);

        coordinatesArea = new JTextArea();
        coordinatesArea.setFont(new Font("Arial", Font.PLAIN, 16));
        coordinatesArea.setEditable(false); // Чтобы текст нельзя было изменить

        JScrollPane scrollPane = new JScrollPane(coordinatesArea); // Добавляем прокрутку, если текст не помещается
        getContentPane().add(scrollPane);

        setSize(300, 100);
        setLocation(50, 50);

        // Подписываем окно на наблюдение за объектом RobotModel
        robotModel.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof RobotModel) {
            RobotModel robotModel = (RobotModel) o;
            int x = (int) robotModel.getM_robotPositionX();
            int y = (int) robotModel.getM_robotPositionY();

            String coordinatesText = "Текущие координаты робота: X=" + x + ", Y=" + y;
            coordinatesArea.setText(coordinatesText); // Обновляем содержимое JTextArea
        }
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