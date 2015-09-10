import javax.swing.*;
import java.awt.*;

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
    panel.setPreferredSize(new Dimension(400, 400));
    this.setVisible(true);
    this.pack();

  }

}
