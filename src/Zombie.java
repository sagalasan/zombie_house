import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * Created by Jalen on 9/9/2015.
 */
public class Zombie extends Entity
{
  //used for the movements that need to be painted for zombie
  LinkedList<Tile> movementQueue;
  Random rand = new Random();
  boolean lineZombie;
  boolean smellPlayer = false;
  int directionDegree = rand.nextInt(8);

  private boolean moveZombieUp;
  private boolean moveZombieDown;
  private boolean moveZombieRight;
  private boolean moveZombieLeft;


  public Zombie(int x, int y)
  {
    super("Zombie", x, y);
    setSpeed(ZOMBIE_DEFAULT_SPEED);
    if (rand.nextDouble() > ZOMBIE_RANDOM_OR_LINE_RATE)
    {
      //System.out.println("this is a line zombie1");
      lineZombie = true;
    }
    else
    {
      //System.out.println("this is a random zombie1");
      lineZombie= false;
    }
    setHeading(directionDegree);
    //set zombie intial direction

  }

  public double calculateEuclidDistance()
  {
    double euclidDist = 0;
    double xDifference = Math.abs(GameControl.userPlayer.getX() - getX());
    double yDifference = Math.abs(GameControl.userPlayer.getY() - getY());
    euclidDist = Math.sqrt(xDifference*xDifference + yDifference*yDifference);
    return euclidDist;
  }


  public void sniffForPlayer()
  {
    //need to check if player is nearby
    double distToPlayer = calculateEuclidDistance();
    if (distToPlayer <= ZOMBIE_SMELL)
    {
      //System.out.println("detected Player");
      smellPlayer = true;
    }
    else
    {
      smellPlayer = false;
    }


  }
  public void seeIfPlayerCanHear()
  {
    double distToPlayer = calculateEuclidDistance();
    //this prob has to be seperate.  perhaps in move checker?
    if (distToPlayer <= PLAYER_HEARING_DISTANCE)
    {
      //player can hear
      //run sound method
      //if to the right of player, play right sound
      //if to left of player, play left sound, etc
    }
  }



  private void setHeading(int directionDegree)
  {
    //if it hit a wall,
    //get a new heading
    if (directionDegree == 0)
    {
      //east
      moveZombieRight = true;
    }
    else if (directionDegree == 1)
    {
      //north east
      moveZombieRight = true;
      moveZombieUp = true;
    }
    else if (directionDegree == 2)
    {
      //north
      moveZombieUp = true;
    }
    else if (directionDegree == 3)
    {
      //north west
      moveZombieUp = true;
      moveZombieLeft = true;
    }
    else if (directionDegree == 4)
    {
      //west
      moveZombieLeft = true;
    }
    else if (directionDegree == 5)
    {
      //southwest
      moveZombieLeft = true;
      moveZombieDown = true;
    }
    else if (directionDegree == 6)
    {
      //south
      moveZombieDown = true;
    }
    else if (directionDegree == 7)
    {
      //south east
      moveZombieRight = true;
      moveZombieDown = true;
    }
  }



  public void updateDirection()
  {
    moveZombieUp = false;
    moveZombieDown = false;
    moveZombieRight = false;
    moveZombieLeft = false;
    //sniffs for if player is within 7 euclid distance blocks

    sniffForPlayer();


    if (smellPlayer)
    {
      chasePlayer();
      if (!movementQueue.isEmpty())
      {
        if (getX() > movementQueue.getLast().x)
        {
          //System.out.println("zombie left");
          moveZombieLeft = true;
        }
        if (getX() < movementQueue.getLast().x)
        {
          //System.out.println("zombie right");
          moveZombieRight = true;
        }
        if (getY() < movementQueue.getLast().y)
        {
          //System.out.println("zombie down");
          moveZombieDown = true;
        }
        if (getY() > movementQueue.getLast().y)
        {
          //System.out.println("zombie up");
          moveZombieUp = true;
        }
        movementQueue.removeLast();
      }
    }
    else if (lineZombie)
    {
      //choose random heading, move until hit wall, then recalc

      if (hitwall())
      {
        int newDirectionDegree = rand.nextInt(8);
        while(newDirectionDegree == directionDegree)
        {
          newDirectionDegree = rand.nextInt(8);
        }
        directionDegree = newDirectionDegree;
      }
      setHeading(directionDegree);

      //else
      //keep old heading
    }
    else
    {
      //randomZombies
      int newDirectionDegree = rand.nextInt(8);
      if (hitwall())
      {
        while(newDirectionDegree == directionDegree)
        {
          newDirectionDegree = rand.nextInt(8);
        }
      }
      setHeading(newDirectionDegree);
    }
  }

  public void move()
  {
    super.move(moveZombieUp, moveZombieDown, moveZombieRight, moveZombieLeft);
  }
  /**
   * finds the be astar path to player
   * puts results in a queue that is called later to draw movements of zombie
   */
  private void chasePlayer()
  {
    Tile[][] map = Level.map.clone();
    Tile start = map[getX()][getY()];
    Tile end = map[GameControl.userPlayer.getX()][GameControl.userPlayer.getY()];
    for (int i = 0; i< Level.width;i++)
    {
      for (int j = 0; j<Level.height;j++)
      {
        //reset board here
        //I wanted to do it on only the affected nodes but cant seem to get it
        map[i][j].chosen = false;
        map[i][j].parent = null;
      }
    }

    movementQueue = new LinkedList<>();
    Tile next = findBestPath(start, end, map);
    System.out.println(next.x+", "+ next.y);

    while (next != null )
    {
      movementQueue.addLast(next);
      next = next.parent;
      if (next == start)
      {
        break;
      }
    }
  }

  /**
   * used for sorting the priority queue
   */
  class PQsort implements Comparator<Tile>
  {
    @Override
    public int compare(Tile one, Tile two) {
      if (one.fCost == two.fCost)
      {
        //this could be screwy since it involves casting double values to int
        if (one.heuristicCost < two.heuristicCost)
        {
          return -1;
        }
        else if (one.heuristicCost > two.heuristicCost)
        {
          return 1;
        }
        else
        {
          return 0;
        }

      }
      else if (one.fCost < two.fCost)
      {
        return -1;
      }
      else if (one.fCost > two.fCost)
      {
        return 1;
      }
      return 0;
    }

  }

  /**
   *
   * @param start - the start zombie tile
   * @param end - the player tile
   * @return the player tile which should have updated parents that follow up to the start zombie tile
   * @throws IndexOutOfBoundsException
   * Finds the shortest path between a zombie and player
   */
  private Tile findBestPath(Tile start, Tile end, Tile[][] map) {
    //if distance is only one tile away, just skip the whole pathfinding algo.
    int xDiff = Math.abs(start.x - end.x);
    int yDiff = Math.abs(start.y - end.y);
    if ((xDiff == 1 || xDiff == 0 )&& (yDiff == 1 || yDiff == 0))
    {
      end.parent = start;
      return end;
    }

    start.chosen = true;
    Tile currentNode;
    PQsort sorter = new PQsort();
    start.costSoFar = start.tileTravelCost;
    PriorityQueue<Tile> queue = new PriorityQueue<>(1, sorter);
    queue.clear();
    queue.add(start);
    //System.out.println("start (" + start.x + ", " + start.y + "), end (" + end.x +", "+end.y+")");
    while (!queue.isEmpty())
    {
      currentNode = queue.remove();
      currentNode.chosen = true;
      if (currentNode.x == end.x && currentNode.y == end.y)
      {
        break;
      }
      //System.out.println("looking for coords x y "+end.x+", "+end.y+"...currentnodecoords "+currentNode.x +", "+ currentNode.y);
      currentNode.setFrontier(queue, end, map);
    }
    return end;
  }

}