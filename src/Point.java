import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by kasac on 9/25/2015.
 */
public class Point implements Comparator<Point>
{
  private static final double PI = Math.PI;
  private static final double TWO_PI = Math.PI * 2;
  private int x;
  private int y;
  private double dx;
  private double dy;
  private double angle;
  private boolean noAngle;
  private double distance;

  public Point(int x, int y, Point origin)
  {
    this.x = x;
    this.y = y;
    if(origin == null) this.noAngle = true;
    if(!this.noAngle) calculateAngle(origin);
  }

  public Point(int x, int y)
  {
    this(x, y, null);
  }

  public int getX()
  {
    return x;
  }

  public int getY()
  {
    return y;
  }

  public double getAngle()
  {
    return angle;
  }

  public double getDistance()
  {
    return distance;
  }

  public void translate(int dx, int dy)
  {
    this.x += dx;
    this.y += dy;
  }

  public int compareAngle(Point origin, Point p1, Point p2)
  {
    return 0;
  }

  @Override
  public String toString()
  {
    String result = "";
    result += "(" + dx + ", " + dy + ") ";
    result += (angle) + " " + distance;
    return result;
  }

  private void calculateAngle(Point origin)
  {
    int ox = origin.getX();
    int oy = origin.getY();
    dx = this.x - ox;
    dy = this.y - oy;

    distance = dx * dx + dy * dy;

    if(dx == 0)
    {
      if(dy > 0) this.angle = PI / 2;
      else if(dy == 0) this.angle = 0;
      else this.angle = 3 * PI / 2;
    }
    else if(dy == 0)
    {
      if(dx > 0) this.angle = 0;
      else this.angle = PI;
    }
    else if(dx > 0)
    {
      if(dy > 0) this.angle = Math.atan(dy / dx);
      else this.angle = Math.atan(dy / dx) + TWO_PI;
    }
    else
    {
      this.angle = Math.atan(dy / dx) + PI;
    }
  }

  @Override
  public int compare(Point p1, Point p2)
  {
    return (int) (p1.getAngle() - p2.getAngle());
  }

  public static Comparator<Point> PointAngleComparator = new Comparator<Point>()
  {
    @Override
    public int compare(Point p1, Point p2)
    {
      double difference = p1.getAngle() - p2.getAngle();
      if(difference < 0)
      {
        return -1;
      }
      else if(difference > 0)
      {
        return 1;
      }
      else
      {
        double d = p1.getDistance() - p2.getDistance();
        if(d < 0)
        {
          return -1;
        }
        else if(d > 0)
        {
          return 1;
        }
      }
      return 0;
    }
  };

  public static void cleanList(ArrayList<Point> points)
  {
    for(int i = 0; i < points.size() - 1; i++)
    {
      Point current = points.get(i);
      Point next = points.get(i + 1);
      double angleDiff = current.getAngle() - next.getAngle();
      if(Math.abs(angleDiff) < .001)
      {
        int dx = current.getX() - next.getX();
        int dy = current.getY() - next.getY();
        if(dx == 0 && dy == 0)
        {
          points.remove(i);
          points.remove(i);
          i -= 2;
        }
        else
        {
          points.remove(i);
          i--;
        }
        if(i < 0) i = 0;
      }
    }
  }
}
