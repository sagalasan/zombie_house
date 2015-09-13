import javax.swing.*;
import java.awt.*;

/**
 * Created by Jalen on 9/9/2015.
 */
public class Main {
  //create frame, invoke runnable

  private static void createAndShowGUI()
  {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    ZombieFrame frame = new ZombieFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(screenSize);
    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
  }
  public static void main(String[] args)
  {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run()
      {
        createAndShowGUI();
      }
    });
  }

}
