import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * Created by Jalen on 9/9/2015.
 */
public class ZombiePanel extends JPanel  {
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
    BufferedImage floor = new BufferedImage(pixelSize, pixelSize, BufferedImage.TYPE_INT_ARGB);
    Graphics2D floorDraw = floor.createGraphics();
    floorDraw.setColor(Color.BLACK);
    floorDraw.fillRect(0, 0, pixelSize, pixelSize);
    floorDraw.setColor(Color.ORANGE);
    floorDraw.fillRect(1, 1, pixelSize - 1, pixelSize - 1);

    BufferedImage wallBlock = new BufferedImage(pixelSize, pixelSize, BufferedImage.TYPE_INT_ARGB);
    Graphics2D wallDraw = wallBlock.createGraphics();
    wallDraw.setColor(Color.BLACK);
    wallDraw.fillRect(0, 0, pixelSize, pixelSize);
    wallDraw.setColor(Color.gray);
    wallDraw.fillRect(1, 1, pixelSize, pixelSize);

    for (int i = 0; i < Level.width; i++)
    {
      for (int j = 0; j < Level.height; j++)
      {
        if (Level.map[i][j].type == Constants.FLOOR)
        {
          g.drawImage(floor, i * pixelSize, j * pixelSize, null);
        }
        else if (Level.map[i][j].type == Constants.WALL)
        {
          g.drawImage(wallBlock, i * pixelSize, j * pixelSize, null);
        }
      }
    }
  }

}
