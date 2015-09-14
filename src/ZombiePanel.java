import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;

/**
 * Created by Jalen on 9/9/2015.
 */
public class ZombiePanel extends JPanel implements KeyListener, Constants{
  //int pixelSize = 40;
  Level level = new Level();
  GameControl gameController = new GameControl(this);
  //paints buffered image of map and characters on top

  private BufferedImage[] floorImages;


  public ZombiePanel()
  {
    addKeyListener(this);
    initializeImages();
  }

  private void initializeImages()
  {
    floorImages = new BufferedImage[5];
    try
    {
      floorImages[0] = ImageIO.read(new File("tile_images/zombie_house_tile_floor_0_0.png"));
      floorImages[1] = ImageIO.read(new File("tile_images/zombie_house_tile_floor_0_90.png"));
      floorImages[2] = ImageIO.read(new File("tile_images/zombie_house_tile_floor_0_180.png"));
      floorImages[3] = ImageIO.read(new File("tile_images/zombie_house_tile_floor_0_270.png"));
      floorImages[4] = ImageIO.read(new File("tile_images/zombie_house_tile_floor_2_270.png"));

    }
    catch (IOException e)
    {
      System.out.println("Image loading failed");
    }
  }

  @Override
  public void keyPressed(KeyEvent e)
  {
    if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A)
    {
      //gameController.userPlayer.move("left");
      //System.out.println("left key pressed");
      gameController.setPlayerMoveLeft(true);
    }
    if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D)
    {
      //gameController.userPlayer.move("right");
      //System.out.println("right key pressed");
      gameController.setPlayerMoveRight(true);
    }
    if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S)
    {
      //gameController.userPlayer.move("down");
      //System.out.println("down key pressed");
      gameController.setPlayerMoveDown(true);
    }
    if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W)
    {
      //gameController.userPlayer.move("up");
      //System.out.println("up key pressed");
      gameController.setPlayerMoveUp(true);
    }
  }

  @Override
  public void keyReleased(KeyEvent e)
  {
    if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A)
    {
      //gameController.userPlayer.move("left");
      //System.out.println("left key pressed");
      gameController.setPlayerMoveLeft(false);
    }
    if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D)
    {
      //gameController.userPlayer.move("right");
      //System.out.println("right key pressed");
      gameController.setPlayerMoveRight(false);
    }
    if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S)
    {
      //gameController.userPlayer.move("down");
      //System.out.println("down key pressed");
      gameController.setPlayerMoveDown(false);
    }
    if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W)
    {
      //gameController.userPlayer.move("up");
      //System.out.println("up key pressed");
      gameController.setPlayerMoveUp(false);
    }
  }

  @Override
  public void keyTyped(KeyEvent e) {

  }

  @Override
  public void paintComponent(Graphics g)
  {

    //None of this is actually how its gonna work, this is just to test
    BufferedImage floor = floorImages[0];
    BufferedImage scorchedFloor = floorImages[4];

    BufferedImage wall = null;
    try
    {
      wall = ImageIO.read(new File("tile_images/zombie_house_tile_wall_test.png"));
    } catch (IOException e) {}

    for (int i = 0; i < Level.width; i++)
    {
      for (int j = 0; j < Level.height; j++)
      {
        if (Level.map[i][j].type == FLOOR)
        {
          g.drawImage(floor, i * SIZE, j * SIZE, null);
        }
        else if (Level.map[i][j].type == WALL)
        {
          g.drawImage(wall, i * SIZE, j * SIZE, null);
        }
        else if (Level.map[i][j].type == SCORCHED_FLOOR)
        {
          g.drawImage(scorchedFloor, i * SIZE, j * SIZE, null);
        }
      }
    }


    g.setColor(Color.red);
    //System.out.println(gameController.userPlayer.x);
    g.fillOval(gameController.userPlayer.getXPixel(), gameController.userPlayer.getYPixel(), 30, 30);

    g.setColor(Color.BLUE);
    g.fillOval(gameController.zombie1.getX() * SIZE, gameController.zombie1.getY() * SIZE, 30, 30);

  }

}
