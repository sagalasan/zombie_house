import javafx.application.Application;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.util.Random;

/**
 * Created by Jalen on 9/9/2015.
 */
public class ZombiePanel extends JPanel implements KeyListener, Constants{
  //int pixelSize = 40;
  //Level level = new Level();
  GameControl gameController = new GameControl(this);
  //paints buffered image of map and characters on top

  private BufferedImage[] floorImages;
  private BufferedImage  blacknessImage;
  private BufferedImage wallImage;
  private BufferedImage firetrapImage;
  private BufferedImage scorchedMask;
  private BufferedImage playerVisibleMask;

  private BufferedImage mapBufferedImage;
  private BufferedImage mapScorchedMaskImage;
  private BufferedImage[][] mapImages;
  private int[][] scorchedLocations;

  private RadialGradientPaint playerGradient;
  private Color colorBlackOpaque;
  private Color colorBlackPartial;
  private Color colorBlackTransparent;
  private int playerSight;
  private float[] fractions = {0.0f, 0.7f, 1.0f};


  public ZombiePanel()
  {
    addKeyListener(this);
    initializeImages();
    constructArrayImages();
    constructBufferedImage();

    playerSight = PLAYER_SIGHT * SIZE;
    colorBlackOpaque = new Color(0, 0, 0, 255);
    colorBlackPartial = new Color(0, 0, 0, 220);
    colorBlackTransparent = new Color(0, 0, 0, 0);
    Color[] colors = {colorBlackTransparent, colorBlackPartial, colorBlackOpaque};

    playerGradient = new RadialGradientPaint(1920 / 2, 1080 / 2, playerSight, fractions, colors);
  }

  private void initializeImages()
  {
    floorImages = new BufferedImage[12];
    try
    {
      floorImages[0] = ImageIO.read(new File("tile_images/zombie_house_tile_floor_0_0.png"));
      floorImages[1] = ImageIO.read(new File("tile_images/zombie_house_tile_floor_0_90.png"));
      floorImages[2] = ImageIO.read(new File("tile_images/zombie_house_tile_floor_0_180.png"));
      floorImages[3] = ImageIO.read(new File("tile_images/zombie_house_tile_floor_0_270.png"));

      floorImages[4] = ImageIO.read(new File("tile_images/zombie_house_tile_floor_1_0.png"));
      floorImages[5] = ImageIO.read(new File("tile_images/zombie_house_tile_floor_1_90.png"));
      floorImages[6] = ImageIO.read(new File("tile_images/zombie_house_tile_floor_1_180.png"));
      floorImages[7] = ImageIO.read(new File("tile_images/zombie_house_tile_floor_1_270.png"));

      floorImages[8] = ImageIO.read(new File("tile_images/zombie_house_tile_floor_2_0.png"));
      floorImages[9] = ImageIO.read(new File("tile_images/zombie_house_tile_floor_2_90.png"));
      floorImages[10] = ImageIO.read(new File("tile_images/zombie_house_tile_floor_2_180.png"));
      floorImages[11] = ImageIO.read(new File("tile_images/zombie_house_tile_floor_2_270.png"));

      wallImage = ImageIO.read(new File("tile_images/zombie_house_tile_wall_test.png"));
      firetrapImage = ImageIO.read(new File("tile_images/zombie_house_tile_firetrap.png"));
      blacknessImage = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_ARGB);

      scorchedMask = ImageIO.read(new File("tile_images/scorched_mask.png"));
      playerVisibleMask = ImageIO.read(new File("opacity_masks/zombie_house_player_mask.png"));
    }
    catch (IOException e)
    {
      System.out.println("Image loading failed");
    }

    Graphics2D g2d = blacknessImage.createGraphics();
    g2d.setPaint(Color.black);
    g2d.fillRect(0, 0, blacknessImage.getWidth(), blacknessImage.getHeight());

  }
  int fiveSeconds = 5000;
  Timer pickUpFiretrap = new Timer(fiveSeconds, new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      GameControl.userPlayer.takeFiretrap();
      Level.map[GameControl.userPlayer.getX()][GameControl.userPlayer.getY()].type = FLOOR;
      GameControl.userPlayer.setCanMove(true);
    }
  });

  Timer setFiretrap = new Timer(fiveSeconds, new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      GameControl.userPlayer.useFiretrap();
      Level.map[GameControl.userPlayer.getX()][GameControl.userPlayer.getY()].type = FIRETRAP;
      GameControl.userPlayer.setCanMove(true);
    }
  });

  @Override
  public void keyPressed(KeyEvent e)
  {
    if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A)
    {
     // GameControl.userPlayer.moving = true;
      gameController.setPlayerMoveLeft(true);
    }
    if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D)
    {
      //GameControl.userPlayer.moving = true;
      gameController.setPlayerMoveRight(true);
    }
    if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S)
    {
      //GameControl.userPlayer.moving = true;
      gameController.setPlayerMoveDown(true);
    }
    if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W)
    {
      //GameControl.userPlayer.moving = true;
      gameController.setPlayerMoveUp(true);
    }

    if(gameController.checkIfPlayerMoving() && GameControl.userPlayer.canMove())
    {
      GameControl.userPlayer.moving = true;
      GameControl.userPlayer.startWalkingSound();
    }
    if (e.getKeyCode() == KeyEvent.VK_R && !GameControl.userPlayer.running && GameControl.userPlayer.canMove())
    {
      GameControl.userPlayer.addSpeed();
    }
    if (e.getKeyCode() == KeyEvent.VK_P && !GameControl.userPlayer.running)
    {
      Tile playerTile = Level.map[GameControl.userPlayer.getX()][GameControl.userPlayer.getY()];
      if (playerTile.type == FIRETRAP)
      {
        //disable moving
        GameControl.userPlayer.setCanMove(false);
        pickUpFiretrap.setRepeats(false);
        pickUpFiretrap.start();
        //start waiting timer,
      }
      else if (GameControl.userPlayer.hasFiretraps()&& playerTile.type == FLOOR)
      {
        GameControl.userPlayer.setCanMove(false);
        setFiretrap.setRepeats(false);
        setFiretrap.start();

      }
      //if on firetrap tile
      //cant move,
      //wait 5 seconds
      //at the end of 5 seconds, add firetrap to inventory and tile is no longer firetrap

      //else if on reg tile and firetrap inventory is not 0
      //cant move, wait 5 sec
      //at the end add firetrap to tile;
    }
  }

  @Override
  public void keyReleased(KeyEvent e)
  {
    if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A)
    {
      //GameControl.userPlayer.moving = false;
      gameController.setPlayerMoveLeft(false);
    }
    if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D)
    {
      //GameControl.userPlayer.moving = false;
      gameController.setPlayerMoveRight(false);
    }
    if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S)
    {
      //GameControl.userPlayer.moving = false;
      gameController.setPlayerMoveDown(false);
    }
    if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W)
    {
      //GameControl.userPlayer.moving = false;
      gameController.setPlayerMoveUp(false);
    }
    //check all movement bools
    //if all done set movement to false
    if (!gameController.checkIfPlayerMoving())
    {
      GameControl.userPlayer.moving = false;
      GameControl.userPlayer.stopWalkingSound();
      GameControl.userPlayer.stopRunningSound();
    }
    if (e.getKeyCode() == KeyEvent.VK_R)
    {
      GameControl.userPlayer.stopRunningSound();
      if (GameControl.userPlayer.moving)
      {
        GameControl.userPlayer.startWalkingSound();
      }
      GameControl.userPlayer.regenStamina();
    }

    if(e.getKeyCode() == KeyEvent.VK_H)
    {
      debugPrintLocations();
    }
  }

  @Override
  public void keyTyped(KeyEvent e)
  {

  }

  private void debugPrintLocations()
  {
    for(Zombie zombie : gameController.zombieList)
    {
      if (zombie.isAlive())
      {
        System.out.println("Zombie: (" + zombie.getXPixel() + ", " + zombie.getYPixel() + ")");
        System.out.println("Zombie x, y: (" + zombie.getX() + ", " + zombie.getY() + ")");
      }
    }
    System.out.println("Player: (" + gameController.userPlayer.getXPixel() + ", " + gameController.userPlayer.getYPixel() + ")");
    System.out.println("Player: x, y(" + gameController.userPlayer.getX() + ", " + gameController.userPlayer.getY() + ")");
  }



  @Override
  public void paintComponent(Graphics g)
  {
    Graphics2D g2d = (Graphics2D) g;
    Dimension d = this.getSize();

    int playerX = gameController.userPlayer.getXPixel();
    int playerY = gameController.userPlayer.getYPixel();
    int imgOriginX = playerX - d.width / 2;
    int imgOriginY = playerY - d.height / 2;
    int subImgWidth = d.width;
    int subImgHeight = d.height;
    int drawX = 0;
    int drawY = 0;
    int imgWidth = mapBufferedImage.getWidth();
    int imgHeight = mapBufferedImage.getHeight();
    int offsetX = -1 * imgOriginX;
    int offsetY = -1 * imgOriginY;
    BufferedImage tempImg;

    if(imgOriginX < 0)
    {
      drawX = -1 * imgOriginX;
      imgOriginX = 0;
    }
    if(imgOriginY < 0)
    {
      drawY = -1 * imgOriginY;
      imgOriginY = 0;
    }
    if(imgOriginX + d.width >= imgWidth)
    {
      subImgWidth = imgWidth - imgOriginX;
    }
    if(imgOriginY + d.height >= imgHeight)
    {
      subImgHeight = imgHeight - imgOriginY;
    }

    tempImg = mapBufferedImage.getSubimage(imgOriginX, imgOriginY, subImgWidth, subImgHeight);

    g.drawImage(blacknessImage, 0, 0, null);
    g.drawImage(tempImg, drawX, drawY, null);





    g.setColor(Color.red);
    //System.out.println(gameController.userPlayer.x);
    //g.fillOval(gameController.userPlayer.getXPixel(), gameController.userPlayer.getYPixel(), 30, 30);
    g.fillOval(d.width / 2, d.height / 2, 30, 30);

    g.setColor(Color.BLUE);
    //g.fillOval(gameController.zombie1.getXPixel(), gameController.zombie1.getYPixel(), 30, 30);


     /** for (Zombie zombie: gameController.zombieList)
      {
        g.fillOval(zombie.getXPixel(), zombie.getYPixel(), 30, 30);
        //g.fillOval(zombie.getX() * SIZE, zombie.getY() * SIZE, 30, 30);
      }
      **/



    for (Zombie zombie: gameController.zombieList)
    {
      if (zombie.isAlive())
      {
        int zombieXPixel = zombie.getXPixel();
        int zombieYPixel = zombie.getYPixel();
        int zombieDrawX = offsetX + zombieXPixel;
        int zombieDrawY = offsetY + zombieYPixel;

        g.fillOval(zombieDrawX, zombieDrawY, 30, 30);
      }
    }

    // Will finish this later, I need to subtract this from a black image to get the proper visibility.
    //drawCenteredImg(g, playerVisibleMask, d.width / 2, d.height / 2);
    g2d.setPaint(playerGradient);
    //g2d.fillRect(d.width / 2, d.height / 2, playerSight, playerSight);
    g2d.fillRect(0, 0, getWidth(), getHeight());

    //Point2D center = new Point2D.Float(d.width / 2, d.height / 2);
    //float radius = 150;
    //float[] dist = {0.0f, 0.2f, 1.0f};
    //Color[] colors = {Color.RED, Color.WHITE, Color.BLUE};
    //RadialGradientPaint p =
    //        new RadialGradientPaint(center, radius, dist, colors);
    //g2d.setPaint(p);
    //g2d.fillRect(d.width / 2, d.height / 2, (int) radius, (int) radius);
  }

  private void drawCenteredImg(Graphics g, BufferedImage img, int x, int y)
  {
    int drawX = img.getWidth() / 2;
    int drawY = img.getHeight() / 2;

    g.drawImage(img, x - drawX, y - drawY, null);
  }

  private void constructArrayImages()
  {
    mapImages = new BufferedImage[Level.width][Level.height];
    scorchedLocations = new int[Level.width][Level.height];
    Random random = new Random();

    for(int i = 0; i < Level.width; i++)
    {
      for(int j = 0; j < Level.height; j++)
      {
        if(Level.map[i][j].type == WALL)
        {
          mapImages[i][j] = wallImage;
        }
        else if(Level.map[i][j].type == FLOOR)
        {
          int index = random.nextInt(floorImages.length);
          mapImages[i][j] = floorImages[index];
        }
        else if (Level.map[i][j].type == FIRETRAP)
        {
          mapImages[i][j] = firetrapImage;
        }
        else
        {
          mapImages[i][j] = blacknessImage;
        }
        if(Level.map[i][j].type == SCORCHED_FLOOR)
        {
          scorchedLocations[i][j] = 1;
        }
        else
        {
          scorchedLocations[i][j] = 0;
        }
      }
    }
  }

  private void constructBufferedImage()
  {
    mapBufferedImage = new BufferedImage(SIZE * Level.width, SIZE * Level.height, BufferedImage.TYPE_INT_ARGB);
    mapScorchedMaskImage = new BufferedImage(SIZE * Level.width, SIZE * Level.height, BufferedImage.TYPE_INT_ARGB);

    for (int i = 0; i < Level.width; i++)
    {
      for (int j = 0; j < Level.height; j++)
      {
        mapBufferedImage.createGraphics().drawImage(mapImages[i][j], SIZE * i, SIZE * j, null);
        if (scorchedLocations[i][j] == 1)
        {
          mapBufferedImage.createGraphics().drawImage(scorchedMask, SIZE * i, SIZE * j, null);
        }
      }
    }
  }

  private boolean areArrayMapEqaul()
  {
    for(int i = 0; i < Level.width; i++)
    {
      for(int j = 0; j < Level.height; j++)
      {

      }
    }
    return true;
  }

  private class RayTracingThread implements Runnable
  {

    @Override
    public void run()
    {

    }
  }

}
