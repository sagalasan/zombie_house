//import javax.swing.text.html.parser.Entity;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * Created by Jalen on 9/9/2015.
 */
public class Zombie extends Entity
{
  //used for the movements that need to be painted for zombie
  LinkedList<Tile> movementQueue;

  public Zombie(int x, int y)
  {
    super("Zombie", x, y);
    setSpeed(Constants.ZOMBIE_DEFAULT_SPEED);

  }

  /**
   * finds the be astar path to player
   * puts results in a queue that is called later to draw movements of zombie
   */
  public void chasePlayer()
  {
    movementQueue = new LinkedList<Tile>();
    Tile next = findBestPath(Level.map[getX()][getY()], Level.map[GameControl.userPlayer.getX()][GameControl.userPlayer.getY()]);
    while(next != Level.map[getX()][getY()])
    {
      movementQueue.addLast(next);
      next.chosen = false;

     // System.out.println("added tile ("+next.x+", "+next.y+")");
      next = next.parent;
    }
  }

  /**
   * gets the next coordinates for zombie to be drawn at
   *
   */
  public void getNextCoordsToFollowPlayer()
  {
    if (!movementQueue.isEmpty())
    {
      Tile nextMove = movementQueue.getLast();
      setX(nextMove.x);
      setY(nextMove.y);
      movementQueue.removeLast();
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
  private Tile findBestPath(Tile start, Tile end) {
    //todo
    //each zombie needs to know when to chase and not to chase

    start.chosen = true;
    Tile currentNode;
    PQsort sorter = new PQsort();
    start.costSoFar = start.tileTravelCost;
    PriorityQueue<Tile> queue = new PriorityQueue<Tile>(10, sorter);
    queue.add(start);
    while (!queue.isEmpty())
    {
      currentNode = queue.remove();
      currentNode.chosen = true;
      //need to loop through map to reset visited status and parents
      if (currentNode.x == end.x && currentNode.y == end.y)
      {
        break;
      }
      currentNode.setFrontier(queue, end);
     // System.out.println("popped off tile at ("+currentNode.x+", "+currentNode.y+") from frontier queue");
    }
    return end;
  }

}