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


//gaurav.munjal.us/universal-LPC-Spritesheet-Character-Generator/#
public class Level extends Constants {

  static Tile[][] map;
  static int height = 50, width = 50; //may change these with the level progression
  private int startX = 1, startY = 1;
  private int exitX = 1, exitY = 1;
  private int levelNumber = 0;

  ArrayList<Zombie> zombieList;
  Zombie masterZombie;
  Rectangle rt;
  private boolean masterZombieCreated = false;
  private Random rnd = new Random();

  ArrayList<Rectangle> rectangles = new ArrayList<Rectangle>();

  ArrayList<Tile> possiblePlayerTiles = new ArrayList<>();


  public Level(int n)
  {
    //creates map, use helper methods later
    levelNumber = n;

    //System.out.println("Using NEW map");

    map = new Tile[width][height];

    for (int i = 0; i < width; i++)
    {
      for (int j = 0; j < height; j++)
      {
        map[i][j] = new Tile(BLACKNESS, i, j);
      }
    }

    //TODO if level is complete reset this, maybe.
    mapGen();

  }

  //this constructor is used if the player dies
  public Level(int n, Tile[][] t)
  {
    levelNumber = n;
    //System.out.println("Using old map");
    map = t;
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

    //starting at 1,1,height-1,width-1 will give us a buffer of 1 space around
    //the entire map, which will always be wall
    rt = new Rectangle(1, 1, height-1, width-1);
    rectangles.add(rt); //populate rectangle store with root area

    while(rectangles.size() < 13) // for now this will give us 7 rooms, 14 total elements
    {
      int splitIdx = rnd.nextInt(rectangles.size()); // choose a random element
      Rectangle toSplit = rectangles.get(splitIdx);
      if(toSplit.split())
      { //attempt to split
        rectangles.add(toSplit.leftChild);
        rectangles.add(toSplit.rightChild);
      }
    }

    //create the rooms
    rt.createRoom();
    //after rooms are created set everything
    setGrid(rectangles);

  }


  /**
  * @params r - takes a rectangle ArrayList and sets the grid into tile array
  * @params rooms - counts the number of rooms, may not be needed
  * */
  public void setGrid(ArrayList<Rectangle> r) {
    //rooms counts the nuber of rooms
    zombieList = new ArrayList<Zombie>();
    int rooms = 0;
    double masterZombieSpawnRate = 0;
    for (Rectangle rec : r)
    {
      if (rec.room != null)
      {
        for (int i = 0; i < rec.room.width; i++)
        {
          for (int j = 0; j < rec.room.height; j++)
          {
            map[rec.room.x + j][rec.room.y + i] = new Tile(FLOOR, rec.room.x + j, rec.room.y + i);
            //for floor tiles,
            //if the int is in the top 1 percent, create a zombie
            if (j > 0 && j < rec.room.height - 1 && i > 0 && i < rec.room.width - 1)
            {
              if (rnd.nextDouble() < FIRETRAP_SPAWN_RATE)
              {
                //System.out.println("firetrap spawned at " +(rec.room.x + j)+", "+ (rec.room.y + i));
                map[rec.room.x + j][rec.room.y + i].setType(FIRETRAP);
              }
              if (rnd.nextDouble() < PILLAR_SPAWN_RATE && map[rec.room.x + j][rec.room.y + i].getType() != FIRETRAP)
              {
                map[rec.room.x + j][rec.room.y + i].setType(PILLAR);
              }
              if (map[rec.room.x + j][rec.room.y + i].getType() != PILLAR)
              {
                if (rnd.nextDouble() < ZOMBIE_SPAWN_RATE)
                // && map[rec.room.x + j][rec.room.y + i].getType() != WALL)
                {
                  zombieList.add(new Zombie(rec.room.x + j, rec.room.y + i));
                }
                if (!masterZombieCreated)
                {
                  if (rnd.nextDouble() < masterZombieSpawnRate)// ||
                  //(rec.room.x + j == Level.width-1 && rec.room.y + i == Level.height-1))//if last possible tile to spawn
                  {
                    masterZombieCreated = true;
                    // System.out.println("masterZombie spawnned at " + rec.room.x + j + ", " + rec.room.y + i);
                    masterZombie = new Zombie(rec.room.x + j, rec.room.y + i);
                    masterZombie.setMaster(true);
                  }
                  else
                  {
                    masterZombieSpawnRate += .02;
                  }
                }
              }



            }

            //this line must be [j][i] or the map wont work!!
            if(rooms == 0)
            {
              setStartX(startX = rec.room.x + j);
              setStartY(startY = rec.room.y + i);
              System.out.println("Starting room" + startX + "x" + startY);
            }

            rooms++;
          }
        }
      }
    }
    zombieList.add(masterZombie);



    //this loop created the halls and is not working correctly, currently is
    //allows the hall to be created out of bounds and sometimes does not complete the room
    int currentRoom = 0;
    for (Rectangle rec : r)
    {
      if (rec.room != null)
      {
        Rectangle c = findClosest(rec.room, currentRoom);
        int firstX = rnd.nextInt((rec.room.height+rec.room.x) - rec.room.x)+rec.room.x;
        int firstY = rnd.nextInt((rec.room.width+rec.room.y) - rec.room.y)+rec.room.y;
        int secondX = rnd.nextInt((c.room.height+c.room.x) - c.room.x)+c.room.x;
        int secondY = rnd.nextInt((c.room.width+c.room.y) - c.room.y)+c.room.y;

        //walk to the nearest x then to the nearest y
        while(secondX != firstX || secondY != firstY)
        {
          int offsetX = 0, offsetY = 0;
          if(secondX != firstX)
          {
            if(secondX > firstX)
            {
              secondX--;
            }
            else secondX++;

            offsetY = secondY + 1;
            offsetX = secondX;
          }
          else if(secondY != firstY)
          {
            if(secondY > firstY)
            {
              secondY--;
            }
            else
              secondY++;

            offsetX = secondX + 1;
            offsetY = secondY;
          }
          map[secondX][secondY].setType(FLOOR);//new Tile(FLOOR, secondX, secondY);
          map[offsetX][offsetY].setType(FLOOR);//new Tile(FLOOR, offsetX, offsetY);
          if (rnd.nextDouble() < FIRETRAP_SPAWN_RATE)
          {
            //choose random tile of these
            //spawns firetrap
            if (rnd.nextDouble()<.5)
            {
              map[secondX][secondY].setType(FIRETRAP);
            }
            else
            {
              map[offsetX][offsetY].setType(FIRETRAP);
            }
          }
        }
        currentRoom++;
      }
    }

    //set the outside tiles to walls and inside to floor or firetraps
    for (int i = 0; i < width; i++)
    {
      for (int j = 0; j < height; j++)
      {
        if (map[i][j].getType() == FLOOR || map[i][j].getType() == FIRETRAP)
        {
          for (int ii = i-1; ii <= i+1; ii++)
          {
            for (int jj = j-1; jj <= j+1; jj++)
            {
              if (map[ii][jj].getType() != FLOOR)//|| map[ii][jj].type != FIRETRAP)
              {
                if (map[ii][jj].getType() == FIRETRAP)
                {
                  //some reason firetraps are turned into walls
                  //skip it becoming a wall if firetrap
                }
                else
                {
                  map[ii][jj] = new Tile(WALL, ii,jj);
                }

              }
            }
          }
        }
      }
    }

    //we have to set the exit tiles only after the walls have been made
    //otherwise they need to be created somewhere else
    map[getExitRoomX()][getStartExitY() - 1].setType(EXIT);
    //map[getExitRoomX()+1][getStartExitY()-1].setType(EXIT);
    setGameCopy(map);
    setZombieList(zombieList);


  }



  /**
   * @params currentRec is the selected rectangle to check
   * @paramss currentMidX (Y) - current rectangles mid point
   * @params midX (Y) - mid point of rectangle list being parsed and checked
   * @params closest - finds closest rectangle object
   * @return closest - returns the closest rectangle
   * This class takes a rectangle argument and parses against
   * the other rectangles to find the closest neighbor
   * */
  //this method needs to be corrected a bit as well
  public Rectangle findClosest(Rectangle r, int currentRec)
  {
    int midX = (r.width/2)+r.x;
    int midY = (r.height/2)+r.y;
    int closestDistance = 100;
    int farthestDistance = 0;

    Rectangle closest = null;
    Rectangle farthest = null;

    for (Rectangle rec : rectangles)
    {
      if (rec.room != null)
      {
        if(r != rec.room)
        {
          int currentMidX = (rec.room.width/2)+rec.room.x;
          int currentMidY = (rec.room.height/2)+rec.room.y;
          int distance = Math.min(Math.abs((currentMidX-midX) - (r.width/2) - (rec.room.width/2)),
                  Math.abs((currentMidY-midY) - (r.height/2) - (rec.room.height/2)));

          if(distance < closestDistance)
          {
            closestDistance = distance;
            closest = rec;
          }
          //this is only for the first room where we spawn
          if(distance > farthestDistance && currentRec == 0)
          {
            farthestDistance = distance;
            farthest = rec;
          }

        }
      }
    }
    if (currentRec == 0)
    {
      //System.out.println("Farthest room" + farthest.room.x + "x" + farthest.room.y);
      setExitX(farthest.room.x);
      setExitY(farthest.room.y);
    }

    return closest;
  }


  public void setGameCopy(Tile[][] cm)
  {
    map = cm;
  }

  public Tile[][] getMapCopy()
  {
    return map;
  }

  public void setZombieList(ArrayList<Zombie> zl)
  {
    zombieList = zl;
  }

  public ArrayList<Zombie> getZombieList()
  {
    return zombieList;
  }

  public void setMasterZombie(Zombie masterZombie)
  {
    this.masterZombie = masterZombie;
  }
  public Zombie getMasterZombie()
  {
    return masterZombie;
  }

  public void setStartX(int x)
  {
    startX = x;
  }
  public void setStartY(int y)
  {
    startY = y;
  }

  public int getStartRoomX()
  {
    return startX;
  }
  public int getStartRoomY()
  {
    return startY;
  }

  public void setExitX(int x)
  {
    exitX = x;
  }
  public void setExitY(int y)
  {
    exitY = y;
  }

  public int getExitRoomX()
  {
    return exitX;
  }
  public int getStartExitY()
  {
    return exitY;
  }


}
