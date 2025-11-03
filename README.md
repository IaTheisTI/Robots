# Robots 
package gui;

import game.GameModel;
import state.Save;
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

  public class RobotInfoWindow extends AbstractWindow implements Save, PropertyChangeListener {
  private final JLabel distanceLabel;
  private final JLabel speedLabel;
  private final GameModel model;

  public RobotInfoWindow(GameModel model) {
  super("Информация о роботе", 300, 150, 400, 100);
  this.model = model;

       // Подписываемся на изменения модели
       model.addPropertyChangeListener(this);
       
       // Настраиваем интерфейс
       distanceLabel = new JLabel("Дистанция: вычисляется...");
       speedLabel = new JLabel("Скорость: 0");
       
       JPanel panel = new JPanel(new GridLayout(2, 1));
       panel.add(distanceLabel);
       panel.add(speedLabel);
       
       add(panel, BorderLayout.CENTER);
       pack();
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
  if ("position".equals(evt.getPropertyName())) {
  updateInfo();
  }
  }

  private void updateInfo() {
  // Вычисляем дистанцию
  double distance = Math.sqrt(
  Math.pow(model.getTargetX() - model.getX(), 2) +
  Math.pow(model.getTargetY() - model.getY(), 2)
  );

       // Вычисляем скорость (просто пример - можно усложнить логику)
       double speed = distance < 5 ? 0 : 2; // 0 если близко к цели, иначе 2
       
       distanceLabel.setText(String.format("Дистанция: %.2f px", distance));
       speedLabel.setText(String.format("Скорость: %.1f px/сек", speed));
  }

  @Override
  public String getNameOfWindow() {
  return "RobotInfoWindow";
  }
  }