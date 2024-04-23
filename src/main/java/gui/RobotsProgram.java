package gui;

import log.Logger;
import java.awt.Frame;
import java.beans.PropertyVetoException;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class RobotsProgram {
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
        Logger.error(e.getMessage());
      }
      frame.pack();
      frame.setVisible(true);
      frame.setExtendedState(Frame.MAXIMIZED_BOTH);
    });
  }
}