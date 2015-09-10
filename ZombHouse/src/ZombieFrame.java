import javax.swing.*;

/**
 * Created by Jalen on 9/9/2015.
 */
public class ZombieFrame extends JFrame{
  //have mouse listeners keyboard listeners
  //frame
  ZombiePanel panel;
  public ZombieFrame()
  {
    panel = new ZombiePanel();
    add(panel);
    this.setVisible(true);
    this.pack();

  }

}
