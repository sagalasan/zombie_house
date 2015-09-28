import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Created by Jalen on 9/9/2015.
 */
public class ZombiePanel extends JPanel implements KeyListener, Constants{
  //Level level = new Level();

  //paints buffered image of map and characters on top
  GameControl gameController;
  private BufferedImage[] floorImages;
  private BufferedImage  blacknessImage;
  private BufferedImage wallImage;
  private BufferedImage pillarImage;
  private Image firetrapImage;
  private BufferedImage exitImage;
  private BufferedImage scorchedMask;
  private BufferedImage playerVisibleMask;
  private BufferedImage playerSprite;

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

  private BufferedImage lightingMask;

  private Visibility visibility;
  private int tileSize = SIZE;


  private int startX, startY, exitX, exitY;
  private Tile[][] mapCopy;
  ArrayList<Zombie> zombieListCopy;
  Zombie masterZombieCopy;
  public boolean gameState = false;
  private int levelNumber = 1;

  public ZombiePanel()
  {
    gameController = new GameControl(this);
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

    visibility = new Visibility();
  }

  public void levelComplete()
  {

    JOptionPane.showMessageDialog(null, "Level " + levelNumber +
            " Complete!\nLevel " + (levelNumber+1) +
            " will begin loading after you press 'OK'.",
            null,JOptionPane.PLAIN_MESSAGE);


    gameState = false;
    gameController.zombieReactionTimer.stop();
    gameController.guiTimer.stop();

    levelNumber++;
    gameController.userPlayer.setTotalFiretraps(0);
    setLevelNumber(levelNumber);
    gameController.zombieHitWallSound.stop();
    gameController.zombieWalkSound.stop();
    gameController.userPlayer.stopWalkingSound();
    gameController.userPlayer.stopRunningSound();
    gameController.userPlayer.speed = 0;
    //****************************************************************************************
    //this line is associated with causing the clone() method to
    //cuause a concurrent exception.
    gameController.zombieList.clear();

    gameController.userPlayer.setMovePlayerLeft(false);
    gameController.userPlayer.setMovePlayerRight(false);
    gameController.userPlayer.setMovePlayerUp(false);
    gameController.userPlayer.setMovePlayerDown(false);


    gameController.userPlayer.setCanMove(false);
    gameController.masterZombie = null;
    gameController.userPlayer = null;

    gameController = new GameControl(this);
    constructArrayImages();
    constructBufferedImage();
  }

  public void resetGame()
  {
    ImageIcon icon = null;
    try {
      icon = new ImageIcon(new URL("http://cdn.makeagif.com/media/9-16-2015/YVHhGp.gif"));
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }

    //maybe play a death animation then show dialog
    int reply = JOptionPane.showConfirmDialog(null, "Your brain has been eaten.\n"
                    + "Would you like to play again?", "You're dead bro.",
            JOptionPane.YES_NO_OPTION,JOptionPane.PLAIN_MESSAGE, icon);//icon can be any saved image

    //If user chooses to reset then reset everythong and create a new gameController
    if (reply == JOptionPane.YES_OPTION)
    {
      gameState = true;
      gameController.zombieReactionTimer.stop();
      gameController.guiTimer.stop();
      gameController.zombieHitWallSound.stop();
      gameController.zombieWalkSound.stop();
      gameController.userPlayer.stopWalkingSound();
      gameController.userPlayer.stopRunningSound();

      //****************************************************************************************
      //this line is associated with causing the clone() method to
      //cuause a concurrent exception.
      gameController.zombieList.clear();


      gameController.masterZombie = null;
      gameController = new GameControl(this);

    }
    else {
      //optional goodbye message
      //JOptionPane.showMessageDialog(null, "GOODBYE");
      //should return to the main menu
      System.exit(0);
    }


  }

  public void setLevelNumber(int n)
  {
   levelNumber = n;
  }
  public int getLevelNumber()
  {
    return levelNumber;
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
      firetrapImage = firetrapImage.getScaledInstance(SIZE, SIZE, Image.SCALE_DEFAULT);
      exitImage = ImageIO.read(new File("tile_images/zombie_house_tile_exit.png"));
      blacknessImage = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_ARGB);
      pillarImage = wallImage;
      playerSprite = ImageIO.read(new File("character_images/player_sprite_sheet.png"));

      scorchedMask = ImageIO.read(new File("tile_images/scorched_mask.png"));
      //playerVisibleMask = ImageIO.read(new File("opacity_masks/zombie_house_player_mask.png"));
    }
    catch (IOException e)
    {
      System.out.println("Image loading failed");
    }

    Graphics2D g2d = blacknessImage.createGraphics();
    g2d.setPaint(Color.black);
    g2d.fillRect(0, 0, blacknessImage.getWidth(), blacknessImage.getHeight());

  }
  public BufferedImage getPlayerImage()
  {
    return playerSprite;
  }
  private int fiveSeconds = 5000;
  private Timer pickUpFiretrap = new Timer(fiveSeconds, new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      gameController.userPlayer.takeFiretrap();
      System.out.println("Picking up firetrap " + gameController.userPlayer.getTotalFiretraps());
      Level.map[gameController.userPlayer.getX()][gameController.userPlayer.getY()].setType(FLOOR);
      gameController.userPlayer.setCanMove(true);
    }
  });


  private Timer setFiretrap = new Timer(fiveSeconds, new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      gameController.userPlayer.useFiretrap();
      Level.map[gameController.userPlayer.getX()][gameController.userPlayer.getY()].setType(FIRETRAP);
      gameController.userPlayer.setCanMove(true);
    }
  });


  //todo fix player walking so it looks more natural
  @Override
  public void keyPressed(KeyEvent e)
  {
    if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A)
    {
      gameController.userPlayer.setMovePlayerLeft(true);
    }
    if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D)
    {
      gameController.userPlayer.setMovePlayerRight(true);
    }
    if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S)
    {
      gameController.userPlayer.setMovePlayerDown(true);
    }
    if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W)
    {
      gameController.userPlayer.setMovePlayerUp(true);
    }
    if(gameController.checkIfPlayerMoving() && gameController.userPlayer.canMove())
    {
      gameController.userPlayer.startAnimation();
      gameController.userPlayer.setMoving(true);
      gameController.userPlayer.startWalkingSound();

      if (gameController.userPlayer.isRunning() && gameController.userPlayer.getStamina() > 0)
      {
        gameController.userPlayer.stopWalkingSound();
      }
    }
    if (e.getKeyCode() == KeyEvent.VK_R && gameController.userPlayer.canMove()
        && !gameController.userPlayer.isRunning() && gameController.userPlayer.getStamina() > 0)
    {
      gameController.userPlayer.addSpeed();

    }
    if (e.getKeyCode() == KeyEvent.VK_P && !gameController.userPlayer.isRunning())
    {
      Tile playerTile = Level.map[gameController.userPlayer.getX()][gameController.userPlayer.getY()];
      if (playerTile.getType() == FIRETRAP)
      {
        //disable moving
        gameController.userPlayer.setCanMove(false);
        pickUpFiretrap.setRepeats(false);
        pickUpFiretrap.start();
        //start waiting timer,
      }
      else if (gameController.userPlayer.hasFiretraps()&& playerTile.getType() == FLOOR)
      {
        gameController.userPlayer.setCanMove(false);
        setFiretrap.setRepeats(false);
        setFiretrap.start();
      }
    }
  }

  @Override
  public void keyReleased(KeyEvent e)
  {
    if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A)
    {
      gameController.userPlayer.setMovePlayerLeft(false);
    }
    if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D)
    {
      gameController.userPlayer.setMovePlayerRight(false);
    }
    if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S)
    {
      gameController.userPlayer.setMovePlayerDown(false);
    }
    if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W)
    {
      gameController.userPlayer.setMovePlayerUp(false);
    }
    //check all movement bools
    //if all done set movement to false
    if (!gameController.checkIfPlayerMoving())
    {
      gameController.userPlayer.stopAnimation();
      gameController.userPlayer.setMoving(false);
      gameController.userPlayer.stopWalkingSound();
      gameController.userPlayer.stopRunningSound();
    }
    if (e.getKeyCode() == KeyEvent.VK_R)
    {
      gameController.userPlayer.stopRunningSound();
      if (gameController.userPlayer.isMoving())
      {
        gameController.userPlayer.startWalkingSound();
      }
      gameController.userPlayer.regenStamina();
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

    /**
     * if player bounding rectangle does not intersect with a wall pixel,
     * then draw player
     */
    //can move this down a little bit
    for (int i =0; i < Level.width;i++)
    {
      for (int j = 0; j < Level.height; j++)
      {
        int tileDrawX = offsetX + i * SIZE;
        int tileDrawY = offsetY + j*SIZE;
        Tile checkTile = Level.map[i][j];
        if (checkTile.getType() == FIRETRAP) {
          //draw firetrap instead
          g.drawImage(firetrapImage, tileDrawX, tileDrawY, null);
        }
      }
    }
    for (Zombie zombie: gameController.zombieList)
    {
      //uncomment these is alive lines if zombies are supposed to vanish after dying
      // if (zombie.isAlive())
      //  {
      int zombieXPixel = zombie.getXPixel();
      int zombieYPixel = zombie.getYPixel();
      int zombieDrawX = offsetX + zombieXPixel;
      int zombieDrawY = offsetY + zombieYPixel;
      g.drawImage(zombie.getCurrentFrame() ,zombieDrawX, zombieDrawY ,null );
      //    }
    }

    //combusting a tile slightly causes some lag

    for (int i =0; i < Level.width;i++)
    {
      for (int j = 0; j < Level.height; j++)
      {
        int tileDrawX = offsetX + i * SIZE;
        int tileDrawY = offsetY + j*SIZE;
        Tile checkTile = Level.map[i][j];
        if (checkTile.hasCombusted())
        {
          g.drawImage(scorchedMask , tileDrawX, tileDrawY ,null );
        }
        if (checkTile.isCombusting())
        {
          g.drawImage(checkTile.getCurrentFrame() , tileDrawX, tileDrawY ,null );
        }
      }
    }

    g.drawImage(gameController.userPlayer.getCurrentFrame() ,d.width / 2, d.height / 2,null );


    /**
    int zombieXPixel = GameControl.zombie1.getXPixel();
    int zombieYPixel = GameControl.zombie1.getYPixel();
    int zombieDrawX = offsetX + zombieXPixel;
    int zombieDrawY = offsetY + zombieYPixel;
    g.drawImage(GameControl.zombie1.getCurrentFrame() ,zombieDrawX, zombieDrawY ,null );

     **/
    // Will finish this later, I need to subtract this from a black image to get the proper visibility.
    //drawCenteredImg(g, playerVisibleMask, d.width / 2, d.height / 2);
    g2d.setPaint(playerGradient);
    //g2d.fillRect(d.width / 2, d.height / 2, playerSight, playerSight);\
    //g2d.fillRect(0, 0, getWidth(), getHeight());

    //Point2D center = new Point2D.Float(d.width / 2, d.height / 2);
    //float radius = 150;
    //float[] dist = {0.0f, 0.2f, 1.0f};
    //Color[] colors = {Color.RED, Color.WHITE, Color.BLUE};
    //RadialGradientPaint p =
    //        new RadialGradientPaint(center, radius, dist, colors);
    //g2d.setPaint(p);
    //g2d.fillRect(d.width / 2, d.height / 2, (int) radius, (int) radius);
    //createRayTracingPolygons(offsetX, offsetY);
    createVisibilityMask(offsetX, offsetY);

    playerSight = PLAYER_SIGHT * SIZE;
    colorBlackOpaque = new Color(0, 0, 0, 255);
    colorBlackPartial = new Color(0, 0, 0, 220);
    colorBlackTransparent = new Color(0, 0, 0, 0);
    Color[] colors = {colorBlackTransparent, colorBlackPartial, colorBlackOpaque};

    //playerGradient = new RadialGradientPaint(getWidth() / 2, getHeight() / 2, playerSight, fractions, colors);
    //g2d.setPaint(playerGradient);
    //g2d.fillRect(0, 0, getWidth(), getHeight());
    g2d.drawImage(lightingMask, 0, 0, null);
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
        if(Level.map[i][j].getType() == WALL)
        {
          mapImages[i][j] = wallImage;
        }
        else if(Level.map[i][j].getType() == PILLAR)
        {
          mapImages[i][j] = pillarImage;
        }
        else if(Level.map[i][j].getType() == FLOOR || Level.map[i][j].getType() == FIRETRAP )
        {
          int index = random.nextInt(floorImages.length);
          mapImages[i][j] = floorImages[index];
        }
        else if (Level.map[i][j].getType() == EXIT)
        {
          mapImages[i][j] = exitImage;
        }
        else
        {
          mapImages[i][j] = blacknessImage;
        }
        if(Level.map[i][j].getType() == SCORCHED_FLOOR)
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

  private void createVisibilityMask(int offsetX, int offsetY)
  {
    ArrayList<Polygon> visibilityPolygons;

    int x = gameController.getUserPlayer().getX();
    int y = gameController.getUserPlayer().getY();
    int xp = gameController.getUserPlayer().getXPixel();
    int yp = gameController.getUserPlayer().getYPixel();
    //System.out.println("originx: " + (xp + offsetX));

    int xStart = x - 6;
    int xEnd = x + 6;
    int yStart = y - 6;
    int yEnd = y + 6;
    if(xStart < 0) xStart = 0;
    if(xEnd >= Level.width) xEnd = Level.width;
    if(yStart < 0) yStart = 0;
    if (yEnd >= Level.height) yEnd = Level.height;

    visibility.setOffset(offsetX, offsetY);
    visibility.setOrigin(xp + offsetX, yp + offsetY);
    visibility.setBoundingTiles(xStart, yStart, xEnd, yEnd);
    visibility.setTileSize(tileSize);
    visibility.setSightTileRadius(PLAYER_SIGHT);
    visibilityPolygons = visibility.returnVisibilityPolygon();




    lightingMask = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = lightingMask.createGraphics();
    //g2.setPaint(Color.black);
    g2.setPaint(new Color(0, 0, 0, 0));
    g2.fillRect(0, 0, lightingMask.getWidth(), lightingMask.getHeight());
    g2.dispose();

    Graphics2D g2d = (Graphics2D) lightingMask.getGraphics();
    g2d.drawImage(lightingMask, 0, 0, null);

    int type = AlphaComposite.DST_OUT;
    AlphaComposite alphaComposite = AlphaComposite.getInstance(type, 1f);
    //g2d.setComposite(alphaComposite);
    g2d.setPaint(Color.BLUE);

    for(int i = 0; i < visibilityPolygons.size(); i++)
    {
      g2d.drawPolygon(visibilityPolygons.get(i));
    }


    g2d.dispose();
  }

  private void createRayTracingPolygons(int offsetX, int offsetY)
  {
    lightingMask = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
    ArrayList<Polygon> lightPolygons = new ArrayList<Polygon>();
    //ArrayList<Integer> xVertices = new ArrayList<Integer>();
    //ArrayList<Integer> yVertices = new ArrayList<Integer>();

    ArrayList<Point> vertices = new ArrayList<Point>();

    int x = gameController.getUserPlayer().getX();
    int y = gameController.getUserPlayer().getY();
    int xp = gameController.getUserPlayer().getXPixel();
    int yp = gameController.getUserPlayer().getYPixel();

    Point origin = new Point(xp + offsetX, yp + offsetY);

    int xStart = x - 6;
    int xEnd = x + 6;
    int yStart = y - 6;
    int yEnd = y + 6;
    if(xStart < 0) xStart = 0;
    if(xEnd >= Level.width) xEnd = Level.width;
    if(yStart < 0) yStart = 0;
    if(yEnd >= Level.height) yEnd = Level.height;


    for(int i = xStart; i < xEnd; i++)
    {
      for(int j = yStart; j < yEnd; j++)
      {
        Tile tile = Level.map[i][j];
        int nPoints = 4;
        int[] xCoord = new int[nPoints];
        int[] yCoord = new int[nPoints];

        if(tile.getType() == WALL)
        {
          int xTilePixel = i * SIZE + offsetX;
          int yTilePixel = j * SIZE + offsetY;
          int xTilePixelOne = xTilePixel + SIZE;
          int yTilePixelOne = yTilePixel + SIZE;

          vertices.add(new Point(xTilePixel, yTilePixel, origin));
          xCoord[0] = xTilePixel;
          yCoord[0] = yTilePixel;

          vertices.add(new Point(xTilePixelOne, yTilePixel, origin));
          xCoord[1] = xTilePixelOne;
          yCoord[1] = yTilePixel;

          vertices.add(new Point(xTilePixelOne, yTilePixelOne, origin));
          xCoord[2] = xTilePixelOne;
          yCoord[2] = yTilePixelOne;

          vertices.add(new Point(xTilePixel, yTilePixelOne, origin));
          xCoord[3] = xTilePixel;
          yCoord[3] = yTilePixelOne;

          lightPolygons.add(new Polygon(xCoord, yCoord, nPoints));
        }
      }
    }
    Graphics2D g2 = lightingMask.createGraphics();
    g2.setPaint(Color.black);
    g2.fillRect(0, 0, lightingMask.getWidth(), lightingMask.getHeight());
    g2.dispose();

    Graphics2D g2d = (Graphics2D) lightingMask.getGraphics();
    g2d.drawImage(lightingMask, 0, 0, null);

    int type = AlphaComposite.DST_OUT;
    AlphaComposite alphaComposite = AlphaComposite.getInstance(type, 1f);
    g2d.setComposite(alphaComposite);

    for(int i = 0; i < lightPolygons.size(); i++)
    {
      g2d.fillPolygon(lightPolygons.get(i));
    }

    Collections.sort(vertices, Point.PointAngleComparator);
  /**
    System.out.println("Sorted List");
    for(int i = 0; i < vertices.size(); i++)
    {
      System.out.println(i + " " + vertices.get(i));
    }
    System.out.println("Clean List");
    Point.cleanList(vertices);
    for(int i = 0; i < vertices.size(); i++)
    {
      System.out.println(i + " " + vertices.get(i));
    }
   **/
    Polygon p = createPointPolygon(vertices);
    g2d.fillPolygon(p);
  }

  private Polygon createPointPolygon(ArrayList<Point> points)
  {
    int l = points.size();
    int xPoints[] = new int[l];
    int yPoints[] = new int[l];
    for(int i = 0; i < l; i++)
    {
      Point p = points.get(i);
      int x = p.getX();
      int y = p.getY();
      xPoints[i] = x;
      yPoints[i] = y;
    }
    Polygon polygon = new Polygon(xPoints, yPoints, l);
    return polygon;
  }

  private class RayTracingRunnable implements Runnable
  {
    ArrayList<Point2D> vertices;
    @Override
    public void run()
    {
      int x = gameController.getUserPlayer().getX();
      int y = gameController.getUserPlayer().getY();
      int xp = gameController.getUserPlayer().getXPixel();
      int yp = gameController.getUserPlayer().getYPixel();

    }
  }


  /**
   * @param mc the current map of tiles that needs to be copied over to the next game
   *           type.
   * This method will copy all tiles in the file and copy them into the new map file.
   * This method of copying the map avoids have a "reference" of the map.
   * */
  public void setMapCopy(Tile[][] mc)
  {
    mapCopy = new Tile[50][50];

    for (int i = 0; i < 50; i++)
    {
      for (int j = 0; j < 50; j++)
      {
        mapCopy[j][i] = new Tile(mc[j][i].getType(), mc[j][i].x, mc[j][i].y);
      }
    }
    //TODO if the game ends reset the firetraps to stop combusting


  }

  public Tile[][] getMapCopy()
  {
    return mapCopy;
  }

  public void setZombieListCopy(ArrayList<Zombie> zl)
  {
    //This is the second part causing a concurrent exception**********************************************
    zombieListCopy = (ArrayList<Zombie>)zl.clone();

    //copy each zombie to a new zombie list for use later on
    //and remove the clone method and also the clear() method.


  }
  public void setMasterZombieCopy(Zombie masterZombie)
  {
    masterZombieCopy = masterZombie;
  }

  public ArrayList<Zombie> getZombieListCopy()
  {
    return zombieListCopy;
  }
  public Zombie getMasterZombieCopy()
  {
    return masterZombieCopy;
  }
  public void setStartX(int x)
  {
    startX = x;
  }

  public int getStartX()
  {
    return startX;
  }

  public void setStartY(int y)
  {
    startY = y;
  }

  public int getStartY()
  {
    return startY;
  }

  public void setExitX(int x)
  {
    exitX = x;
  }

  public int getExitX()
  {
    return exitX;
  }

  public void setExitY(int y)
  {
    exitY = y;
  }

  public int getExitY()
  {
    return exitY;
  }

}
