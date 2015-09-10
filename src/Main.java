import javax.swing.*;
import java.lang.System;

/**
 * Created by Jalen on 9/9/2015.
 */
public class Main {
  //create frame, invoke runnable

  private static void createAndShowGUI()
  {
    ZombieFrame frame = new ZombieFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
  public static void main(String[] args)
  {
    javax.swing.SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        createAndShowGUI();
      }
    });
  }

}
