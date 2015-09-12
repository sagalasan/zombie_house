/**
 * Created by Jalen on 9/9/2015.
 * @Entity
 * has methods for movement
 * intersection, bounding rectangle
 * player, zombie, and master zombie extend this
 */
public abstract class Entity
{
  //posibly make these double?
  private int x,y;
  private int width;
  private String type;
  //speed is in tiles per second
  private double speed;
  public Entity(String type, int x, int y)
  {
    this.type = type;
    this.x = x;
    this.y = y;
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
  public void setSpeed(double speed)
  {
    this.speed = speed;
  }

  public void move(String direction)
  {
    if (direction.toLowerCase() == "left")
    {
      x -= speed;
    }
    else if (direction.toLowerCase() == "right")
    {
      x+= speed;
    }
    else if (direction.toLowerCase() == "down")
    {
      y+=speed;
    }
    else if (direction.toLowerCase() == "up")
    {
      y-=speed;
    }

    //moves east, north, south, west depending on string.
  }



}
