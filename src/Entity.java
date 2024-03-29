import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Jalen on 9/9/2015.
 * @Entity
 * has methods for movement
 * intersection, bounding rectangle
 * player, zombie, and master zombie extend this
 */
public abstract class Entity extends Constants
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
      this.spriteSheet = ImageIO.read(getClass().getResource(spriteSheet));
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

  /**
   *
   * @return returns the rectangle around the feet of the sprite
   */
  public Rectangle getFeetBoundingRectangle()
  {
    Rectangle feet = new Rectangle((int)xPixel + PLAYER_FEET_PIXEL_X,
        (int)yPixel + PLAYER_FEET_PIXEL_Y, PLAYER_SPRITE_WIDTH - PLAYER_FEET_PIXEL_X, PLAYER_SPRITE_HEIGHT - PLAYER_FEET_PIXEL_Y );
    return feet;
  }

  /**
   *
   * @return returns an altered rectangle that roughly covers the area of the
   * legs of a sprite
   */
  public Rectangle getBoundingRectangleForFire()
  {
    java.awt.Rectangle legalRectangle = new java.awt.Rectangle((int)xPixel - 5, (int)yPixel + 40, PLAYER_SPRITE_WIDTH-5, PLAYER_SPRITE_HEIGHT - 50);
    return legalRectangle;
  }

  public Rectangle getBoundingRectangle()
  {
    return new java.awt.Rectangle((int)xPixel + 5, (int)yPixel +5 , PLAYER_SPRITE_WIDTH-5, PLAYER_SPRITE_HEIGHT -5 );
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

  private boolean legalXMove(int possibleX)
  {
    if (possibleX >= 0 && possibleX < Level.width)
    {
      if (Level.map[possibleX][y].getType() == WALL || Level.map[possibleX][y].getType() == BLACKNESS
          || Level.map[possibleX][y].getType() == PILLAR)
      {
        return false;
      }
    }
    return true;
  }
  private boolean legalYMove(int possibleY)
  {
    if (possibleY >= 0 && possibleY < Level.height)
    {
      if (Level.map[x][possibleY].getType() == WALL || Level.map[x][possibleY].getType() == BLACKNESS
          || Level.map[x][possibleY].getType() == PILLAR)
      {
        return false;
      }
    }
    return true;
  }

  /**
   *
   * @param possibleXPixel
   * @return returns true if the rectangle made with the possible x and y pixels intersects with a nearby wall
   *   If it does not intersect with a wall, returns false
   */
  private boolean intersectsWithZombieXPixel(double possibleXPixel)
  {
    java.awt.Rectangle legalRectangle = new java.awt.Rectangle((int)possibleXPixel +5, (int)yPixel+5, PLAYER_SPRITE_WIDTH-5, PLAYER_SPRITE_HEIGHT-5);
    //check in 9 directions if legalrect intersects with a wall.
    for (Zombie zombie : GameControl.zombieList)
    {
      if (zombie.getX() == x && zombie.getY() == y);//skip;
      else if (legalRectangle.intersects(zombie.getBoundingRectangle()))
      {
        return true;
      }
    }
    return false;
  }
  /**
   *
   * @param possibleYPixel
   * @return returns true if the rectangle made with the possible x and y pixels intersects with a nearby wall
   *   If it does not intersect with a wall, returns false
   */
  private boolean intersectsWithZombieYPixel(double possibleYPixel)
  {
    java.awt.Rectangle legalRectangle = new java.awt.Rectangle((int)xPixel +5, (int)possibleYPixel+5, PLAYER_SPRITE_WIDTH-5, PLAYER_SPRITE_HEIGHT-5);
    //check in 9 directions if legalrect intersects with a wall.
    for (Zombie zombie : GameControl.zombieList)
    {
      if (zombie.getX() == x && zombie.getY() == y);//skip;
      else if (legalRectangle.intersects(zombie.getBoundingRectangle()))
      {
        return true;
      }
    }
    return false;
  }

  /**
   *
   * @param possibleXPixel
   * @return used to check if the possible x pixel will intersect with a wall
   */
  private boolean intersectsWithWallXPixel(double possibleXPixel)
  {
    java.awt.Rectangle legalRectangle = new java.awt.Rectangle((int)possibleXPixel +5, (int)yPixel+5, PLAYER_SPRITE_WIDTH-5, PLAYER_SPRITE_HEIGHT-5);
    ArrayList<Tile> surroundingWalls = new ArrayList<>();
    //check in 9 directions if legalrect intersects with a wall.
    for (int i = -1; i < 2;i++)
    {
      for (int j = -1; j < 2; j++)
      {
        if (x+i >= 0 && x+i < Level.width && y+j >= 0 && y+j < Level.height)
        {
          if (Level.map[i+x][j+y].getType() == WALL || Level.map[i+x][j+y].getType() == PILLAR )
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
   * @param possibleYPixel
   * @return used to check if the possible y pixle will intersect
   * with a wall above or below the zombies position
   */
  private boolean intersectsWithWallYPixel(double possibleYPixel)
  {
    java.awt.Rectangle legalRectangle = new java.awt.Rectangle((int)xPixel+5, (int)possibleYPixel+5 , PLAYER_SPRITE_WIDTH-5, PLAYER_SPRITE_HEIGHT - 5);
    ArrayList<Tile> surroundingWalls = new ArrayList<>();
    //check in 9 directions if legalrect intersects with a wall.
    for (int i = -1; i < 2;i++)
    {
      for (int j = -1; j < 2; j++)
      {
        if (x+i >= 0 && x+i < Level.width && y+j >= 0 && y+j < Level.height)
        {
          if (Level.map[i+x][j+y].getType() == WALL || Level.map[i+x][j+y].getType() == PILLAR )
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

    if(legalXMove(possibleX))
    {
      x = possibleX;
    }
    if(legalYMove(possibleY))
    {
      y = possibleY;
    }
    if (!intersectsWithWallXPixel(possibleXPixel) && !intersectsWithZombieXPixel(possibleXPixel))
    {
      xPixel = possibleXPixel;
    }
    else
    {
      hitWall = true;
    }
    if (!intersectsWithWallYPixel(possibleYPixel) && !intersectsWithZombieYPixel(possibleYPixel))
    {
      yPixel = possibleYPixel;
    }
    else
    {
      hitWall = true;
    }
  }
}




