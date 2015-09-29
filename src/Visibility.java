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
  private ArrayList<Box> boxes;

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
    boxes = new ArrayList<Box>();
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
    boundingRadius = sightTileRadius * tileSize + tileSize * 2;

    createGeometry(visibilityPolygons);
    Collections.sort(vertices, Point.PointAngleComparator);
    Point.removeDuplicateAngles(vertices);

    extendRaysToBounds(visibilityPolygons);
    //System.out.println(finalPoints.size());

    //Polygon shadow = createPointPolygon(finalPoints);
    //visibilityPolygons.add(shadow);

    return visibilityPolygons;
  }

  private void extendRaysToBounds(ArrayList<Polygon> visibilityPolygons)
  {
    ArrayList<Point> possiblePoints = new ArrayList<Point>();
    ArrayList<Point> actualPoints = new ArrayList<Point>();
    Point previousPoint = null;
    for(Point point : vertices)
    {
      possiblePoints.clear();
      double angle = point.getAngle();
      double distance = point.getDistance();
      double dx = (boundingRadius * Math.cos(angle));
      double dy = (boundingRadius * Math.sin(angle));
      double x = dx + origin.getX();
      double y = dy + origin.getY();
      Point extension = new Point(x, y, origin);

      possiblePoints = getPossiblePoints(point, angle);

      if(possiblePoints.size() == 0)
      {

        //possiblePoints.add(origin);
        //Polygon t = createPointPolygon(possiblePoints);
        //visibilityPolygons.add(t);
        if(point.getOuterPoint())
        {
          //possiblePoints.clear();
          //possiblePoints = getPossiblePoints(extension, extension.getAngle());
          Point edgePoint = extension;//shortenExtension(extension, point);

          if(point.getRight())
          {
            actualPoints.add(edgePoint);
            actualPoints.add(point);
          }
          else
          {
            actualPoints.add(point);
            actualPoints.add(edgePoint);
          }

          //possiblePoints.clear();

          //possiblePoints.add(origin);
          //Polygon o = createPointPolygon(possiblePoints);
          //visibilityPolygons.add(o);
        }
        else
        {
          actualPoints.add(point);
        }
      }


      //System.out.println("Point" + point);

      //Point p = shortenExtension(point);

      //System.out.println("\n\n");
    }

    //Collections.sort(actualPoints, Point.PointAngleComparator);
    //Polygon finalPolygon = createPointPolygon(actualPoints);
    //visibilityPolygons.add(finalPolygon);

    ArrayList<Point> currentPointList = new ArrayList<Point>();
    for(int i = 0; i < actualPoints.size(); i++)
    {
      currentPointList.clear();
      int nextIndex = i + 1;
      Point current = actualPoints.get(i);

      if(nextIndex == actualPoints.size())
      {
        nextIndex = 0;
      }
      Point next = actualPoints.get(nextIndex);

      currentPointList.add(origin);
      currentPointList.add(current);
      currentPointList.add(next);
      Polygon triangle = createPointPolygon(currentPointList);
      visibilityPolygons.add(triangle);
    }
  }

  private Point shortenExtension(Point extension, Point original)
  {
    double eAngle = extension.getAngle();
    ArrayList<Point> possiblePoints = getPossiblePoints(extension, eAngle);

    if(possiblePoints.size() > 0)
    {
      System.out.println("hi");
      return extension;
    }

    Point shortest = getShortest(possiblePoints, original);

    //System.out.println("Extension: " + extension);
    ////for(Point p : possiblePoints)
    //{
      //System.out.println("Possible: " + p);
    //}
    //if(shortest != null) System.out.println("Shortest: " + shortest);


    if(shortest == null)
    {
      return extension;
    }
    else
    {
      return shortest;
    }

    //return shortest;
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
      boolean case3 = (p1.getDistance() < boundary.getDistance());
      boolean case4 = (p2.getDistance() < boundary.getDistance());
      if((case1 || case2) && (case3 && case4))
      {
        Point p = getIntersection(s, origin, boundary);
        possible.add(p);
      }
    }
    return possible;
  }

  private boolean doesIntersect()
  {

    return false;
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

  private Point getShortest(ArrayList<Point> points, Point vertex)
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
      double vd = vertex.getDistance();
      if(d <= minimum && d > vd + 2)
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

    ArrayList<Segment> tempSegment = new ArrayList<Segment>();
    ArrayList<Point> tempVertices = new ArrayList<Point>();

    for(int i = xStart; i < xEnd; i++)
    {
      for(int j = yStart; j < yEnd; j++)
      {
        tempSegment.clear();
        tempVertices.clear();
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
          xCoord[0] = xTilePixel;
          yCoord[0] = yTilePixel;

          Point one = new Point(xTilePixelOne, yTilePixel, origin);
          xCoord[1] = xTilePixelOne;
          yCoord[1] = yTilePixel;

          Point two = new Point(xTilePixelOne, yTilePixelOne, origin);
          xCoord[2] = xTilePixelOne;
          yCoord[2] = yTilePixelOne;

          Point three = new Point(xTilePixel, yTilePixelOne, origin);
          xCoord[3] = xTilePixel;
          yCoord[3] = yTilePixelOne;

          tempSegment.add(new Segment(zero, one));
          tempSegment.add(new Segment(zero, three));
          tempSegment.add(new Segment(two, three));
          tempSegment.add(new Segment(two, one));

          tempVertices.add(zero);
          tempVertices.add(one);
          tempVertices.add(two);
          tempVertices.add(three);

          Collections.sort(tempVertices, Point.PointAngleComparator);
          //Point.removeDuplicateAngles(tempVertices);

          int size = tempVertices.size();
          tempVertices.get(0).setOuterPoint(true);
          tempVertices.get(0).setRight(true);
          tempVertices.get(size - 1).setOuterPoint(true);
          tempVertices.get(size - 1).setRight(false);

          Point v0 = tempVertices.get(0);
          Point v1 = tempVertices.get(1);
          Point v2 = tempVertices.get(2);
          Point v3 = tempVertices.get(3);

          //if(Math.abs(v0.getAngle() - v1.getAngle()) < .01)
          //{
          //  tempVertices.get(1).setOuterPoint(true);
          //  tempVertices.get(1).setRight(true);
          //}
          //if(Math.abs(v2.getAngle() - v3.getAngle()) < .01)
          //{
          //  tempVertices.get(2).setOuterPoint(true);
          //  tempVertices.get(2).setRight(false);
          //}

          boxes.add(new Box(zero, tileSize));
          vertices.addAll(tempVertices);
          segments.addAll(tempSegment);
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
      double x = p.getX();
      double y = p.getY();
      xPoints[i] = (int) x;
      yPoints[i] = (int) y;
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

  private class Box
  {
    private Point p;
    private double dimension;
    private double distanceAdd;

    public Box(Point p, double dimension)
    {
      this.p = p;
      this.dimension = dimension;
      this.distanceAdd = Math.sqrt(dimension * dimension + dimension * dimension) / 4;
    }

    public boolean extendsInside(Point ray, Point origin)
    {
      double boxX = p.getX();
      double boxY = p.getY();

      double angle = ray.getAngle();
      double distance = ray.getDistance();

      double x = (distance + distanceAdd) * Math.cos(angle);
      double y = (distance + distanceAdd) * Math.sin(angle);

      if((boxX < x && x < (boxX + dimension)))
      {
        if(boxY < y && y < (boxY + dimension))
        {
          return true;
        }
      }
      return false;
    }
  }
}
