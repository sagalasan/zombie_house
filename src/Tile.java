import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * Created by Jalen on 9/9/2015.
 * @description - used to also help for astar algorithm
 */
public class Tile {


  //used for Astar
  Tile parent;
  int x, y;
  Tile[] directions = new Tile[8];
  boolean visited = false;
  boolean chosen = false;
  double tileTravelCost;
  double heuristicCost;
  double fCost;
  double costSoFar;


  int type;
  public Tile(int type, int x, int y){
    //could change this to for loop,
    //whatever is more readable
    //panel will read the number and draw block
    this.x = x;
    this.y = y;
    if (type == Constants.FLOOR)
    {
      this.type = Constants.FLOOR;
    }
    else if (type == Constants.WALL)
    {
      this.type = Constants.WALL;
    }
    else if (type == Constants.PILLAR)
    {
      this.type = Constants.PILLAR;
    }
    else if (type == Constants.EXIT)
    {
      this.type = Constants.EXIT;
    }
    else if (type == Constants.SCORCHED_FLOOR)
    {
      this.type = Constants.SCORCHED_FLOOR;
    }
    else
    {
      //black space
      this.type = Constants.BLACKNESS;
    }
    setTileCost();
  }

  public void setTileCost()
  {
    if (type == Constants.FLOOR || type == Constants.SCORCHED_FLOOR)
    {
      tileTravelCost = 1;
    }
    else
    {
      //never crosses through a object or wall
      tileTravelCost = 50;
    }
  }

  public double calculateEuclidDistance(Tile start, Tile end)
  {
    double euclidDist = 0;
    double xDifference = Math.abs(start.x - end.x);
    double yDifference = Math.abs(start.y - end.y);
    euclidDist = Math.sqrt(xDifference*xDifference + yDifference*yDifference);
    return euclidDist;
  }

  /**
   *
   * @param frontier - the priority queue that all the nodes get added to
   * @param end The player or end destination
   *            This will find the nodes around the tile this is called for
   *            Changes the parent node for if there is a better pathway
   */
  public void setFrontier(PriorityQueue<Tile> frontier, Tile end)
  {

    for (int i = 0; i< Constants.TOTAL_DIRECTIONS; i++)
    {
      if (x+Constants.X_DIRECTIONS[i] >= 0 && x+Constants.X_DIRECTIONS[i] < Level.width
          && y+Constants.Y_DIRECTIONS[i] >= 0 && y+Constants.Y_DIRECTIONS[i] < Level.height)
      {
        if (!Level.map[x + Constants.X_DIRECTIONS[i]][y + Constants.Y_DIRECTIONS[i]].chosen
            && Level.map[x + Constants.X_DIRECTIONS[i]][y + Constants.Y_DIRECTIONS[i]].type != Constants.WALL)
        {
          directions[i] = Level.map[x + Constants.X_DIRECTIONS[i]][y + Constants.Y_DIRECTIONS[i]];
          if (!directions[i].visited) {
            visited = true;
            directions[i].parent = this;
            directions[i].costSoFar = this.costSoFar + directions[i].tileTravelCost;
          } else if (directions[i].visited)
          {
            if (costSoFar < directions[i].parent.costSoFar)
            {
              directions[i].parent = this;
              directions[i].costSoFar = this.costSoFar + directions[i].tileTravelCost;
            }

          }
          directions[i].heuristicCost = calculateEuclidDistance(directions[i], end);
          directions[i].fCost = directions[i].heuristicCost + directions[i].costSoFar;

          //System.out.println("adding "+directions[i].x+", "+directions[i].y);
          frontier.add(directions[i]);
          //frontier.add(directions[i]);

        }
      }
    }
  }
}
