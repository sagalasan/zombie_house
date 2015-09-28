import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by kasac on 9/27/2015.
 */
public class Visibility extends Constants
{
  private ArrayList<Segment> segments;
  private ArrayList<Point> vertices;
  private ArrayList<Point> finalPoints;

  private int offsetX, offsetY;
  private Point origin;
  private int xStart, yStart, xEnd, yEnd;
  private int tileSize, sightTileRadius;
  private int boundingRadius;

  public Visibility()
  {
    segments = new ArrayList<Segment>();
    vertices = new ArrayList<Point>();
    finalPoints = new ArrayList<Point>();
  }

  public void setOffset(int offsetX, int offsetY)
  {
    this.offsetX = offsetX;
    this.offsetY = offsetY;
  }

  public void setOrigin(int originX, int originY)
  {
    Point p = new Point(originX, originY);
    this.origin = p;
  }

  public void setBoundingTiles(int xStart, int yStart, int xEnd, int yEnd)
  {
    this.xStart = xStart;
    this.yStart = yStart;
    this.xEnd = xEnd;
    this.yEnd = yEnd;
  }

  public void setTileSize(int tileSize)
  {
    this.tileSize = tileSize;
  }

  public void setSightTileRadius(int sightTileRadius)
  {
    this.sightTileRadius = sightTileRadius;
  }

  public ArrayList<Polygon> returnVisibilityPolygon()
  {
    //System.out.println(origin);
    vertices.clear();
    finalPoints.clear();
    segments.clear();
    ArrayList<Polygon> visibilityPolygons = new ArrayList<Polygon>();
    boundingRadius = sightTileRadius * tileSize + tileSize;

    createGeometry(visibilityPolygons);
    Collections.sort(vertices, Point.PointAngleComparator);
    Point.cleanList(vertices);

    extendRaysToBounds();
    //System.out.println(finalPoints.size());

    Polygon shadow = createPointPolygon(finalPoints);
    visibilityPolygons.add(shadow);

    return visibilityPolygons;
  }

  private void extendRaysToBounds()
  {
    ArrayList<Point> possiblePoints = new ArrayList<Point>();
    for(Point point : vertices)
    {
      double angle = point.getAngle();
      double distance = point.getDistance();
      int dx = (int) (boundingRadius * Math.cos(angle));
      int dy = (int) (boundingRadius * Math.sin(angle));
      int x = dx + origin.getX();
      int y = dy + origin.getY();
      Point extension = new Point(x, y, origin);

      Point p = shortenExtension(extension);


      finalPoints.add(p);
      finalPoints.add(origin);
    }
  }

  private Point shortenExtension(Point extension)
  {
    double eAngle = extension.getAngle();
    ArrayList<Point> possiblePoints = getPossiblePoints(extension, eAngle);

    //System.out.println("\n\nOrigin: " + origin);
    //System.out.println("Extension: " + extension);
    //for(Point p : possiblePoints)
    //{
      //System.out.println(p);
    //}

    Point shortest = getShortest(possiblePoints);

    if(shortest == null)
    {
      shortest = extension;
    }
    //System.out.println("Shortest: " + shortest);
    //System.out.println("\n\n");

    return shortest;
  }

  private ArrayList<Point> getPossiblePoints(Point boundary, double angle)
  {
    ArrayList<Point> possible = new ArrayList<Point>();
    for(Segment s : segments)
    {
      Point p1 = s.getPointOne();
      Point p2 = s.getPointTwo();
      double a1 = p1.getAngle();
      double a2 = p2.getAngle();
      if(Math.abs(a2 - a1) > Math.PI / 2)
      {
        if(a2 < a1) a1 -= 2 * Math.PI;
        else a2 -= 2 * Math.PI;
      }
      boolean case1 = (a1 < angle && a2 > angle);
      boolean case2 = (a1 > angle && a2 < angle);
      if(case1 || case2)
      {
        Point p = getIntersection(s, origin, boundary);
        possible.add(p);
      }
    }
    return possible;
  }

  private Point getIntersection(Segment s, Point p1, Point p2)
  {
    double x = 0;
    double y = 0;

    double x1 = p1.getX();
    double y1 = p1.getY();
    double x2 = p2.getX();
    double y2 = p2.getY();
    double diffX1 = x2 - x1;

    double x3 = s.getPointOne().getX();
    double y3 = s.getPointOne().getY();
    double x4 = s.getPointTwo().getX();
    double y4 = s.getPointTwo().getY();
    double diffX2 = x4 - x3;

    if(diffX1 == 0 || diffX2 == 0)
    {
      if(diffX1 == 0)
      {
        x = x1;
        double slope2 = (y4 - y3) / (diffX2);
        y = slope2 * (x - x3) + y3;
      }
      else
      {
        x = x3;
        double slope1 = (y2 - y1) / (diffX1);
        y = slope1 * (x - x1) + y1;
      }
    }
    else
    {
      double slope1 = (y2 - y1) / (diffX1);
      double slope2 = (y4 - y3) / (diffX2);

      double slopeDiff = slope1 - slope2;
      double top = (slope1 * x1) - (slope2 * x3) - y1 + y3;
      x = top / slopeDiff;
      y = slope1 * (x - x1) + y1;
    }


    //System.out.println(x);
    //System.out.println(y);

    Point result = new Point((int) x, (int) y, origin);
    //System.out.println(result);

    //System.out.println("point1: " + p1);
    //System.out.println("point2: " + p2);
    //System.out.println("slope1: " + slope1);
    //System.out.println("s1: " + s.getPointOne());
    //System.out.println("s2: " + s.getPointTwo());
    //System.out.println("slope2: " + slope2);

    return result;
  }

  private Point getShortest(ArrayList<Point> points)
  {
    int size = points.size();
    if(size == 0)
    {
      return null;
    }

    double minimum = points.get(0).getDistance();
    int index = 0;
    for(int i = 0; i < points.size(); i++)
    {
      double d = points.get(i).getDistance();
      if(d <= minimum)
      {
        minimum = d;
        index = i;
      }
    }
    return points.get(index);
  }

  private void createGeometry(ArrayList<Polygon> polygons)
  {
    vertices.add(new Point(xStart * tileSize + offsetX, yStart * tileSize + offsetY, origin));
    vertices.add(new Point(xStart * tileSize + offsetX, yEnd * tileSize + offsetY, origin));
    vertices.add(new Point(xEnd * tileSize + offsetX, yStart * tileSize + offsetY, origin));
    vertices.add(new Point(xEnd * tileSize + offsetX, yEnd * tileSize + offsetY, origin));
    for(int i = xStart; i < xEnd; i++)
    {
      for(int j = yStart; j < yEnd; j++)
      {
        Tile tile = Level.map[i][j];
        int nPoints = 4;
        int[] xCoord = new int[nPoints];
        int[] yCoord = new int[nPoints];

        if(tile.getType() == WALL || tile.getType() == PILLAR)
        {
          int xTilePixel = i * tileSize + offsetX;
          int yTilePixel = j * tileSize + offsetY;
          int xTilePixelOne = xTilePixel + tileSize;
          int yTilePixelOne = yTilePixel + tileSize;

          Point zero = new Point(xTilePixel, yTilePixel, origin);
          vertices.add(zero);
          xCoord[0] = xTilePixel;
          yCoord[0] = yTilePixel;

          Point one = new Point(xTilePixelOne, yTilePixel, origin);
          vertices.add(one);
          xCoord[1] = xTilePixelOne;
          yCoord[1] = yTilePixel;

          Point two = new Point(xTilePixelOne, yTilePixelOne, origin);
          vertices.add(two);
          xCoord[2] = xTilePixelOne;
          yCoord[2] = yTilePixelOne;

          Point three = new Point(xTilePixel, yTilePixelOne, origin);
          vertices.add(three);
          xCoord[3] = xTilePixel;
          yCoord[3] = yTilePixelOne;

          segments.add(new Segment(zero, one));
          segments.add(new Segment(zero, three));
          segments.add(new Segment(two, three));
          segments.add(new Segment(two, one));

          polygons.add(new Polygon(xCoord, yCoord, nPoints));
        }
      }
    }
  }

  private Polygon createPointPolygon(ArrayList<Point> points)
  {
    int l = points.size();
    int xPoints[] = new int[l];
    int yPoints[] = new int[l];
    for(int i = 0; i < l; i++)
    {
      Point p = points.get(i);
      int x = p.getX();
      int y = p.getY();
      xPoints[i] = x;
      yPoints[i] = y;
      //System.out.println(i + "(" + x + ", " + y + ")");
    }
    Polygon polygon = new Polygon(xPoints, yPoints, l);
    return polygon;
  }

  private class Segment
  {
    private Point p1, p2;

    public Segment(Point p1, Point p2)
    {
      this.p1 = p1;
      this.p2 = p2;
    }

    public Point getPointOne()
    {
      return p1;
    }

    public Point getPointTwo()
    {
      return p2;
    }
  }
}
