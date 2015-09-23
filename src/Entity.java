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
  private int width;

  private boolean isAlive = true;

  private String type;
  private boolean hitWall;
  //speed is in tiles per second
  private double speed;

  public Entity(String type, int x, int y)
  {
    this.type = type;
    this.x = x;
    this.y = y;
    this.xPixel = this.x * SIZE;
    this.yPixel = this.y * SIZE;
    previousTime = System.currentTimeMillis();
  }

  public int getXPixel()
  {
    return (int) xPixel;
  }
  public int getYPixel()
  {
    return (int) yPixel;
  }
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

  public boolean legalMove(int x, int y)
  {
    //System.out.println(Level.map[x][y].type);
    //if wall, black boid, or pillar
    if (Level.map[x][y].getType() == WALL || Level.map[x][y].getType() == BLACKNESS
            || Level.map[x][y].getType() == PILLAR || Level.map[x][y].getType() == EXIT)
    {
      return false;
    }

    //TODO add constraints for making contact with the exit

    /**
     * this is a bounds check.  shouldnt need since there will be walls all around the level
    if (x == 0 && x > Level.width && y == 0 && y > Level.height)
    {
        if (Level.map[GameControl.userPlayer.getX()][GameControl.userPlayer.getY()] == Level.map[x][y])
        {
          return false;
        }
    }
     **/
    return true;
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
      hitWall = false;
      x = possibleX;
      y = possibleY;
      xPixel = possibleXPixel;
      yPixel = possibleYPixel;
    }
    else
    {
     // System.out.println("hitwall is true");
      hitWall = true;
      //if map[x][possibleY] == wall
      //if possible y > y hit south
      //else hit north
      //
    }

  }



}
