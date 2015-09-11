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
  public void move(String direction)
  {
    if (direction.toLowerCase() == "left")
    {
      x -= 1;
    }
    else if (direction.toLowerCase() == "right")
    {
      x+= 1;
    }
    else if (direction.toLowerCase() == "down")
    {
      y+=1;
    }
    else if (direction.toLowerCase() == "up")
    {
      y-=1;
    }

    //moves east, north, south, west depending on string.
  }



}
