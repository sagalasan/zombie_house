import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * Created by Jalen on 9/9/2015.
 */
public class ZombiePanel extends JPanel {
  int pixelSize = 8;
  Level level = new Level();
  //paints buffered image of map and characters on top
  int speed = 17;
  Timer timer = new Timer(speed, new ActionListener()
  {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      repaint();
    }
  });

  public ZombiePanel()
  {
    timer.start();
  }

  @Override
  public void paintComponent(Graphics g)
  {
    //this is just copy pasted code from conways GoL
    BufferedImage liveBlock = new BufferedImage(pixelSize, pixelSize, BufferedImage.TYPE_INT_ARGB);
    Graphics2D liveDraw = liveBlock.createGraphics();
    liveDraw.setColor(Color.BLACK);
    liveDraw.fillRect(0, 0, pixelSize, pixelSize);
    liveDraw.setColor(Color.ORANGE);
    liveDraw.fillRect(1, 1, pixelSize - 1, pixelSize - 1);

    BufferedImage deadBlock = new BufferedImage(pixelSize, pixelSize, BufferedImage.TYPE_INT_ARGB);
    Graphics2D deadDraw = deadBlock.createGraphics();
    deadDraw.setColor(Color.BLACK);
    deadDraw.fillRect(0, 0, pixelSize, pixelSize);
    deadDraw.setColor(Color.gray);
    deadDraw.fillRect(1, 1, pixelSize, pixelSize);

    for (int i = 0; i < Level.width; i++)
    {
      for (int j = 0; j < Level.height; j++)
      {
        if (Level.map[i][j].type == Constants.FLOOR)
        {
          g.drawImage(liveBlock, i * pixelSize, j * pixelSize, null);
        }
        else if (Level.map[i][j].type == Constants.WALL)
        {
          g.drawImage(deadBlock, i * pixelSize, j * pixelSize, null);
        }
      }
    }
  }
}
