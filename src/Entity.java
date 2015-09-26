import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Jalen on 9/9/2015.
 * @Entity
 * has methods for movement
 * intersection, bounding rectangle
 * player, zombie, and master zombie extend this
 */
public abstract class Entity implements Constants
{
  public static int LEFT = 0;
  public static int RIGHT = 1;
  public static int UP = 2;
  public static int DOWN = 3;

  private long previousTime;

  //posibly make these double?
  private int x,y;
  private double xPixel, yPixel;


  private boolean isAlive = true;
  private String type;
  private boolean hitWall;
  //speed is in tiles per second
  private double speed;

  private int animationDirection = ANIMATION_DOWN_WALKING;
  private BufferedImage currentFrame;
  private BufferedImage spriteSheet;
  private int indexForImage = 0;

  public Entity(String type, int x, int y)
  {
    this.type = type;
    this.x = x;
    this.y = y;
    this.xPixel = this.x * SIZE;
    this.yPixel = this.y * SIZE;
    previousTime = System.currentTimeMillis();

    speed = 0;
  }

  Timer animationStart = new Timer(200, new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      indexForImage+=1;
      int totalImages = 9;
      setPlayerFrame(indexForImage % totalImages);
    }
  });
  public void startAnimation()
  {
    animationStart.setInitialDelay(0);
    animationStart.start();
  }
  public void stopAnimation()
  {
    animationStart.stop();
  }

  public void setSpriteSheet(String spriteSheet)
  {
    try
    {
      this.spriteSheet = ImageIO.read(new File(spriteSheet));
    }
    catch (IOException e)
    {
      System.out.println("Player Image did not load");
    }
  }

  private void setPlayerFrame(int indexForPicture)
  {
    currentFrame = spriteSheet.getSubimage(15+(indexForPicture*SPRITE_SPREAD_DISTANCE),
        animationDirection, ANIMATION_WIDTH, ANIMATION_HEIGHT);
  }

  public void setAnimationDirection(int direction)
  {
    animationDirection = direction;
  }

  public void resetCurrentFrame()
  {
    indexForImage = 0;
    animationDirection = ANIMATION_DOWN_WALKING;
    currentFrame = getPlayerSprite().getSubimage(15+(0*SPRITE_SPREAD_DISTANCE), animationDirection, ANIMATION_WIDTH, ANIMATION_HEIGHT);
  }
  public Image getCurrentFrame()
  {
    return currentFrame.getScaledInstance(PLAYER_SPRITE_WIDTH, PLAYER_SPRITE_HEIGHT, Image.SCALE_DEFAULT);
  }

  public BufferedImage getPlayerSprite()
  {
    return spriteSheet;
  }

  public int getXPixel()
  {
    return (int) xPixel;
  }
  public int getYPixel()
  {
    return (int) yPixel;
  }
  public void setXPixel(int x){ xPixel = x;}
  public void setYPixel(int y){ yPixel = y;}
  public int getX()
  {
    return x;
  }
  public int getY()
  {
    return y;
  }
  public void setX(int x)
  {
    this.x = x;
  }
  public void setY(int y)
  {
    this.y = y;
  }

  public void setAlive(boolean aliveStatus)
  {
    isAlive = aliveStatus;

  }

  public boolean isAlive()
  {
    return isAlive;
  }
  public boolean hitwall()
  {
    return hitWall;
  }
  public void setSpeed(double speed)
  {
    this.speed = speed;
  }

  public boolean legalMove(int x, int y) {
    //System.out.println(Level.map[x][y].type);
    //if wall, black boid, or pillar
    //TODO zombies also cant be in the same space as one another
    if (Level.map[x][y].getType() == WALL || Level.map[x][y].getType() == BLACKNESS
            || Level.map[x][y].getType() == PILLAR || Level.map[x][y+1].getType() == EXIT)
    //if wall, black, or pillar
    if (x >= 0 && x < Level.width && y >= 0 && y < Level.height)
    {
      if (Level.map[x][y].getType() == WALL || Level.map[x][y].getType() == BLACKNESS
          || Level.map[x][y].getType() == PILLAR || Level.map[x][y].getType() == EXIT)
      {
        return false;
      }
    }
    if(Level.map[x][y].getType() == EXIT)
    {
      //TODO add constraints for making contact with the exit
      System.out.println("Player has found the exit!");
      //load the second level and clear the playerStatus
    }
      //TODO add constraints for making contact with the exit
    return true;
  }

  //returns true rectangle made with possible x and y pixelse intersect with a nearby wall
  //false if does not intersect with wall
  private boolean intersectsWithWall(double possibleXPixel, double possibleYPixel)
  {
    java.awt.Rectangle legalRectangle = new java.awt.Rectangle((int)possibleXPixel, (int)possibleYPixel, PLAYER_SPRITE_WIDTH, PLAYER_SPRITE_HEIGHT);
    ArrayList<Tile> surroundingWalls = new ArrayList<>();
    //check in 9 directions if legalrect intersects with a wall.
    for (int i = -1; i < 2;i++)
    {
      for (int j = -1; j < 2; j++)
      {
        if (Level.map[i+x][j+y].getType() == WALL || Level.map[i+x][j+y].getType() == PILLAR )
        {
          surroundingWalls.add(Level.map[i+x][j+y]);
        }
      }
    }
    for (Tile wall : surroundingWalls)
    {
      if (legalRectangle.intersects(wall.getBoundingRectangle()))
      {
        return true;
      }
    }
    return false;
  }
  public void setHitWall(boolean b)
  {
    hitWall = b;
  }

  public void move(boolean up, boolean down, boolean right, boolean left)
  {
    double xVector = 0;
    double yVector = 0;

    if (up) yVector -= speed;
    if (down) yVector += speed;
    if (right) xVector += speed;
    if (left) xVector -= speed;

    double xMove, yMove;
    if (Math.abs(xVector) >= .05 && Math.abs(yVector) >= .05)
    {
      xMove = speed * SIN_OF_PI_4 * GUI_TIMER_SPEED / 1000 * 80;
      yMove = speed * SIN_OF_PI_4 * GUI_TIMER_SPEED / 1000 * 80;
      if(xVector < 0) xMove *= -1;
      if(yVector < 0) yMove *= -1;
    }
    else
    {
      xMove = xVector * GUI_TIMER_SPEED / 1000 * SIZE;
      yMove = yVector * GUI_TIMER_SPEED / 1000 * SIZE;
    }
    double possibleXPixel = xPixel + xMove;
    double possibleYPixel = yPixel + yMove;
    int possibleX = (int)possibleXPixel / SIZE;
    int possibleY = (int)possibleYPixel / SIZE;
    //added check to see if move will be legal or not.
    //this will not allow ability to move through walls/blackvoid/pillars
    if (legalMove(possibleX, possibleY))
    {
      //could try testing to see if the pixels are valid instead
      //hitWall = false;
      x = possibleX;
      y = possibleY;
      if (!intersectsWithWall(possibleXPixel, possibleYPixel))
      {
        xPixel = possibleXPixel;
        yPixel = possibleYPixel;
      }
      else
      {
        hitWall = true;
      }
    }
    else
    {
      //if hit right wall or bottom wall, subtract
      //System.out.println("hitwall zombies at " + getX()+", "+getY());
      hitWall = true;
      //if map[x][possibleY] == wall
      //if possible y > y hit south
      //else hit north
      //
    }

  }
}
