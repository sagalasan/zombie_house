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

}
