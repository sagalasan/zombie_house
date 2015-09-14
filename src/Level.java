import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Jalen on 9/9/2015.
 * @Level
 * prcedurally generate map
 * 2d array of tile objects
 * floor, pillar, exit, walls, fire
 *
 */

public class Level {

  final int HEIGHT = 40;
  final int WIDTH = 35;

  static Tile[][] map;
  static int height = 30, width = 40;

  Rectangle rt;

  private Random rnd = new Random();

  int[][] mapRooms;

  ArrayList<Rectangle> rectangles = new ArrayList<Rectangle>();


  public Level(){



    //creates map, use helper methods later
    map = new Tile[width][height];
    for (int i = 0; i < width; i++)
    {
      for (int j = 0; j < height; j++)
      {
        if (i == 0 || j == 0 || i == width - 1 || j == height - 1)
        {
          map[i][j] = new Tile(Constants.WALL, i, j);
        }
        else
        {
          map[i][j] = new Tile(Constants.FLOOR, i, j);
        }
      }
    }


  }




  public void mapGen()
  {
    rt = new Rectangle( 0, 0, HEIGHT, WIDTH); //
    rectangles.add(rt); //populate rectangle store with root area
    //number of leaves to create
    while( rectangles.size() < rnd.nextInt(18)+14)
    {
      int splitIdx = rnd.nextInt( rectangles.size() ); // choose a random element
      Rectangle toSplit = rectangles.get(splitIdx);
      if( toSplit.split() )
      { //attempt to split
        rectangles.add( toSplit.leftChild );
        rectangles.add( toSplit.rightChild );
      }
    }

    //create the rooms
    rt.createRoom();
    //after rooms are created set everything
    setGrid(rectangles);

  }

  public void setGrid(ArrayList<Rectangle> r) {
    mapRooms = new int[HEIGHT][WIDTH];
    int rooms = 2;

    for (int i = 0; i < WIDTH; i++) {
      for (int j = 0; j < HEIGHT; j++) {
        mapRooms[j][i] = 0;
      }
    }

    for (Rectangle rec : r)
    {
      if (rec.room != null)
      {
        for (int i = 0; i < rec.room.width; i++)
        {
          for (int j = 0; j < rec.room.height; j++)
          {
            mapRooms[rec.room.x + j][rec.room.y + i] = rooms;
          }
        }

        rooms++;
      }
    }

    //overload the panel with the rectangles
    //mp.setRectangle(r);

    for (Rectangle rec : r)
    {
      if (rec.room != null)
      {
        Rectangle c = findClosest(rec.room);
        //System.out.println("closest = " + rec.room.x +" " + rec.room.y + " " + rec.room.height + "x" + rec.room.width);
        //System.out.println("closest = " + c.room.x +" " + c.room.y + " " + c.room.height + "x" + c.room.width);

        int firstX = rnd.nextInt((rec.room.width+rec.room.x) - rec.room.x)+rec.room.x;
        int firstY = rnd.nextInt((rec.room.height+rec.room.y) - rec.room.y)+rec.room.y;

        int secondX = rnd.nextInt((c.room.width+c.room.x) - c.room.x)+c.room.x;
        int secondY = rnd.nextInt((c.room.height+c.room.y) - c.room.y)+c.room.y;

        while(secondX != firstX || secondY != firstY)
        {
          System.out.println("firstX Y" + firstX + "," + firstY);
          System.out.println("secondX Y" + secondX + "," + secondY);

          if(secondX != firstX)
          {
            if(secondX > firstX)
            {
              secondX--;
            }
            else secondX++;
          }
          else if(secondY != firstY)
          {
            if(secondY > firstY)
            {
              secondY--;
            }
            else
              secondY++;
          }
          mapRooms[secondX][secondY] = 1;
        }
      }

    }

    for (int i = 0; i < WIDTH; i++)
    {
      for (int j = 0; j < HEIGHT; j++)
      {
        System.out.print(map[j][i]);
      }
      System.out.println();
    }

    //overload the panel with the 2d array
    //mp.setMap(map);

  }

  public Rectangle findClosest(Rectangle r)
  {
    int currentMidX = (r.width/2)+r.x;
    int currentMidY = (r.height/2)+r.y;
    int closestDistance = 100;
    Rectangle closest = null;

    for (Rectangle rec : rectangles)
    {
      if (rec.room != null)
      {
        if(r != rec.room)
        {
          int midX = (rec.room.width/2)+rec.room.x;
          int midY = (rec.room.height/2)+rec.room.y;
          //int distance = Math.abs(currentMidX - midX) + Math.abs(currentMidY - midY);
          double distance = Math.sqrt(Math.pow((currentMidX - midX),2) + Math.pow((currentMidY - midY),2));
          if(distance < closestDistance)
          {
            closestDistance = (int)distance;
            closest = rec;
          }
        }
      }
    }
    return closest;
  }

















}
