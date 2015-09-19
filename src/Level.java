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

public class Level implements Constants {

  static Tile[][] map;
  static int height = 40, width = 40;
  ArrayList<Zombie> zombieList;
  Rectangle rt;

  private Random rnd = new Random();

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
          map[i][j] = new Tile(WALL, i, j);
        }
        else
        {
          map[i][j] = new Tile(FLOOR, i, j);
        }
      }
    }

    mapGen();

  }



/**
 * @params rt - rectangle object that creates rooms and stores rectangle x position
 *         y position, width and height
 * This method splits the area into smaller areas. Areas will be rooms * 2 and will
 * then be merged into a single random room size > 6.
 *
 * */
  public void mapGen()
  {
    rt = new Rectangle( 0, 0, height, width);
    int totalRooms = 10;
    rectangles.add(rt); //populate rectangle store with root area

    //we will make a minimum of 6 and a max of 10, it will be random
    while(rectangles.size() < rnd.nextInt(((totalRooms*2-1)-12))+12)
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
  public ArrayList<Zombie> getZombieList()
  {
    return zombieList;
  }
  /**
  * @params r - takes a rectangle ArrayList and sets the grid into tile array
  * @params rooms - counts the number of rooms, may not be needed
  * */
  public void setGrid(ArrayList<Rectangle> r) {
    //rooms counts the nuber of rooms
    int rooms = 1;
    zombieList = new ArrayList<Zombie>();
    for (Rectangle rec : r)
    {
      if (rec.room != null)
      {

        for (int i = 0; i < rec.room.height; i++)
        {
          for (int j = 0; j < rec.room.width; j++)
          {
            //for floor tiles,
            //if the int is in the top 1 percent, create a zombie
            if(rnd.nextDouble() < ZOMBIE_SPAWN_RATE)
            {
              zombieList.add(new Zombie(rec.room.x + i, rec.room.y + j));
            }
            System.out.println("room coords at "+(rec.room.x + i) +", "+(rec.room.y + j));
            map[rec.room.x + i][rec.room.y + j] = new Tile(SCORCHED_FLOOR, rec.room.x + i, rec.room.y + j);
          }
        }
        rooms++;
      }


    }



    //send the rectangle array to the panel
    //this may not be necessary depending on if we use
    //the objects or just an int array or tile array
    //mp.setRectangle(r);


    //this loop created the halls and is not working correctly, currently is
    //allows the hall to be created out of bounds and sometimes does not complete the room
    for (Rectangle rec : r)
    {
      if (rec.room != null)
      {
        Rectangle c = findClosest(rec.room);

        int firstX = rnd.nextInt((rec.room.width+rec.room.x) - rec.room.x)+rec.room.x;
        int firstY = rnd.nextInt((rec.room.height+rec.room.y) - rec.room.y)+rec.room.y;

        int secondX = rnd.nextInt((c.room.width+c.room.x) - c.room.x)+c.room.x;
        int secondY = rnd.nextInt((c.room.height+c.room.y) - c.room.y)+c.room.y;

        //walk to the nearest x then to the nearest y
        //this method needs to be fixed to work better
        while(secondX != firstX || secondY != firstY)
        {
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
//            map[secondX][secondY] = new Tile(SCORCHED_FLOOR, secondX, secondY);

        }
      }
    }


  }

  /**
   * @aramss currentMidX (Y) - current rectangles mid point
   * @params midX (Y) - mid point of rectangle list being parsed and checked
   * @params closest - finds closest rectangle object
   * @return closest - returns the closest rectangle
   * This class takes a rectangle argument and parses against
   * the other rectangles to find the closest neighbor
   * */
  //this method needs to be corrected a bit as well
  public Rectangle findClosest(Rectangle r)
  {
    int midX = (r.width/2)+r.x;
    int midY = (r.height/2)+r.y;
    int closestDistance = 100;
    Rectangle closest = null;

    for (Rectangle rec : rectangles)
    {
      if (rec.room != null)
      {
        if(r != rec.room)
        {
          int currentMidX = (rec.room.width/2)+rec.room.x;
          int currentMidY = (rec.room.height/2)+rec.room.y;
          int distance = Math.min(Math.abs(currentMidX-midX) - (r.width/2) - (rec.room.width/2),
                  Math.abs(currentMidY-midY) - (r.height/2) - (rec.room.height/2));
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
