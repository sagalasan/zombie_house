import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.Rectangle;
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
  private long previousTime;

  private int x,y;
  private double xPixel, yPixel;

  private boolean isAlive = true;
  private String type;
  private boolean hitWall;
  double speed;

  private int animationDirection = ANIMATION_DOWN_WALKING;
  private BufferedImage currentFrame;
  private BufferedImage spriteSheet;
  private int indexForImage = 0;
  private int totalImages = 9;

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
  public BufferedImage getPlayerSprite()
  {
    return spriteSheet;
  }
  /**
   * @param indexForPicture
   * sets which frame the animation will be set to
   */
  private void setPlayerFrame(int indexForPicture)
  {
    currentFrame = spriteSheet.getSubimage(15+(indexForPicture*SPRITE_SPREAD_DISTANCE),
        animationDirection, ANIMATION_WIDTH, ANIMATION_HEIGHT);
  }
  Timer animationStart = new Timer(180, new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      indexForImage+=1;
      //total images will be either 9 or 6 depending on which animation
      setPlayerFrame(indexForImage % totalImages);
      if (totalImages == 6 && indexForImage == 5)
      {
        stopAnimation();
      }
    }
  });

  public void startAnimation()
  {
    totalImages = 9;
    animationStart.setInitialDelay(0);
    animationStart.start();
  }
  public void stopAnimation()
  {
    animationStart.stop();
  }

  public void startDeathAnimation()
  {
    indexForImage = 0;
    animationDirection = ANIMATION_DEATH;
    totalImages = 6;
    animationStart.start();
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
  public Rectangle getFeetBoundingRectangle()
  {
    Rectangle feet = new Rectangle((int)xPixel + PLAYER_FEET_PIXEL_X,
        (int)yPixel + PLAYER_FEET_PIXEL_Y, PLAYER_SPRITE_WIDTH - PLAYER_FEET_PIXEL_X, PLAYER_SPRITE_HEIGHT - PLAYER_FEET_PIXEL_Y );
    return feet;
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
  public void setHitWall(boolean b)
  {
    hitWall = b;
  }

  public void setSpeed(double speed)
  {
    this.speed = speed;
  }

  public boolean legalMove(int x, int y) {
    //if (Level.map[x][y].getType() == WALL || Level.map[x][y].getType() == BLACKNESS
    //        || Level.map[x][y].getType() == PILLAR || Level.map[x][y+1].getType() == EXIT)
    //if wall, black, or pillar
    if (x >= 0 && x < Level.width && y >= 0 && y < Level.height)
    {
      if (Level.map[x][y].getType() == WALL || Level.map[x][y].getType() == BLACKNESS
          || Level.map[x][y].getType() == PILLAR)
      {
        return false;
      }
    }
    return true;
  }

  //returns true rectangle made with possible x and y pixelse intersect with a nearby wall
  //false if does not intersect with wall

  /**
   *
   * @param possibleXPixel
   * @param possibleYPixel
   * @return returns true if the rectangle made with the possible x and y pixels intersects with a nearby wall
   *   If it does not intersect with a wall, returns false
   */
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
          if (x+i >= 0 && x+i < Level.width && y+j >= 0 && y+j < Level.height)
          {
            surroundingWalls.add(Level.map[i + x][j + y]);
          }
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


  /**
   *
   * @param up
   * @param down
   * @param right
   * @param left
   *  using the booleans provided as paramaters, adjusts the position
   *  of whatever object is moving(players or zombies)
   *
   */
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

    if (legalMove(possibleX, possibleY))
    {
      x = possibleX;
      y = possibleY;
      if (!intersectsWithWall(possibleXPixel, possibleYPixel))
      {
        xPixel = possibleXPixel;
        yPixel = possibleYPixel;
      }
      else
      {
        stopAnimation();
        hitWall = true;
      }
    }
    else
    {
      stopAnimation();
      hitWall = true;
    }
  }
}
