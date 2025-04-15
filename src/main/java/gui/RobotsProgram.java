package gui;

import javax.swing.*;
import java.awt.*;
import log.Logger;
import java.beans.PropertyVetoException;

public class RobotsProgram
{
  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
//        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
//        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    } catch (Exception e) {
      Logger.error(e.getMessage());
    }
    SwingUtilities.invokeLater(() -> {
      MainApplicationFrame frame = null;
        try {
            frame = new MainApplicationFrame();
        } catch (PropertyVetoException e) {
            throw new RuntimeException(e);
        }
        frame.pack();
      frame.setVisible(true);
      frame.setExtendedState(Frame.MAXIMIZED_BOTH);
    });
  }}