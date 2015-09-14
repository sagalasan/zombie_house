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
  private String type;
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

  public int getXPixel() { return (int) xPixel; }
  public int getyPixel() { return (int) yPixel; }
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

  public void setSpeed(double speed)
  {
    this.speed = speed;
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
    xPixel += xMove;
    yPixel += yMove;
    x = (int) xPixel / SIZE;
    y = (int) yPixel / SIZE;

    if()

    System.out.println(type + ": " + "(" + xMove + ", " + yMove + ")");
    //moves east, north, south, west depending on string.
  }



}
