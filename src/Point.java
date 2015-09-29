import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by christiaan on 9/25/2015.
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

  /**
   * Public constructor used for non origin points
   * @param x
   * @param y
   * @param origin
   */
  public Point(double x, double y, Point origin)
  {
    this.x = x;
    this.y = y;
    if(origin == null) this.noAngle = true;
    if(!this.noAngle) calculateAngle(origin);
  }

  /**
   * Public constructor used for the origin
   * @param x
   * @param y
   */
  public Point(double x, double y)
  {
    this(x, y, null);
  }

  /**
   * Sets whether the point is an outer point
   * @param outerPoint
   */
  public void setOuterPoint(boolean outerPoint)
  {
    this.outerPoint = outerPoint;
  }

  /**
   * Sets whether the point is on the right
   * @param rightSide
   */
  public void setRight(boolean rightSide)
  {
    this.rightSide = rightSide;
  }

  /**
   * True when the point does not stop light
   * @return
   */
  public boolean getOuterPoint()
  {
    return outerPoint;
  }

  /**
   * True when the visible portion is on the right
   * @return
   */
  public boolean getRight()
  {
    return rightSide;
  }

  /**
   * Gets the x coordinate
   * @return
   */
  public double getX()
  {
    return x;
  }

  /**
   * Gets the Y coordinate
   * @return
   */
  public double getY()
  {
    return y;
  }

  /**
   * Gets the angle of the point from +x axis
   * @return
   */
  public double getAngle()
  {
    return angle;
  }

  /**
   * Gets the distance of the point from the origin
   * @return
   */
  public double getDistance()
  {
    return distance;
  }

  /**
   * Used to see points on the commandline
   * @return
   */
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

  /**
   * Static comparator for sorting points by angle
   */
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

  /**
   * Static method that removes duplicate angles from a sorted arraylist
   * @param points
   */
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
