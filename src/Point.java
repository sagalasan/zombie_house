import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by kasac on 9/25/2015.
 */
public class Point implements Comparator<Point>
{
  private static final double PI = Math.PI;
  private static final double TWO_PI = Math.PI * 2;
  private double x;
  private double y;
  private double dx;
  private double dy;
  private double angle;
  private boolean noAngle;
  private double distance;
  private boolean outerPoint = false;
  private boolean rightSide;

  public Point(double x, double y, Point origin)
  {
    this.x = x;
    this.y = y;
    if(origin == null) this.noAngle = true;
    if(!this.noAngle) calculateAngle(origin);
  }

  public Point(double x, double y)
  {
    this(x, y, null);
  }

  public void setOuterPoint(boolean outerPoint)
  {
    this.outerPoint = outerPoint;
  }

  public void setRight(boolean rightSide)
  {
    this.rightSide = rightSide;
  }

  public boolean getOuterPoint()
  {
    return outerPoint;
  }

  public boolean getRight()
  {
    return rightSide;
  }

  public double getX()
  {
    return x;
  }

  public double getY()
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

  public void translate(double dx, double dy)
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
    result += "(" + x + ", " + y + ") ";
    result += (angle) + " " + distance;
    return result;
  }

  private void calculateAngle(Point origin)
  {
    double ox = origin.getX();
    double oy = origin.getY();
    dx = this.x - ox;
    dy = this.y - oy;

    distance = Math.sqrt(dx * dx + dy * dy);

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
          return 1;
        }
        else if(d > 0)
        {
          return -1;
        }
      }
      return 0;
    }
  };

  public static void removeDuplicateAngles(ArrayList<Point> points)
  {
    for(int i = 0; i < points.size() - 1; i++)
    {
      Point current = points.get(i);
      Point next = points.get(i + 1);
      double angleDiff = current.getAngle() - next.getAngle();
      double distDiff = current.getDistance() - next.getDistance();
      if(Math.abs(angleDiff) < .001)// && Math.abs(distDiff) < .1)
      {
        points.remove(i + 1);
        if(Math.abs(distDiff) < .1) points.get(i).setOuterPoint(false);
        //points.get(i+1).setOuterPoint(false);
        i--;
      }
    }
  }
}
