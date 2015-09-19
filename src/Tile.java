import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * Created by Jalen on 9/9/2015.
 * @description - used to also help for astar algorithm
 */
public class Tile implements Constants {


  //used for Astar
  Tile parent;
  int x, y;
  Tile[] directions;// = new Tile[8];
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
    if (type == FLOOR)
    {
      this.type = FLOOR;
    }
    else if (type == WALL)
    {
      this.type = WALL;
    }
    else if (type == PILLAR)
    {
      this.type = PILLAR;
    }
    else if (type == EXIT)
    {
      this.type = EXIT;
    }
    else if (type == SCORCHED_FLOOR)
    {
      this.type = SCORCHED_FLOOR;
    }
    else
    {
      //black space
      this.type = BLACKNESS;
    }
    setTileCost();
  }

  public void setTileCost()
  {


    if (type == FLOOR || type == SCORCHED_FLOOR)
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
  public void setFrontier(PriorityQueue<Tile> frontier, Tile end, Tile[][] map)
  {
    directions = new Tile[8];
    for (int i = 0; i< TOTAL_DIRECTIONS; i++)
    {
      //if in bounds
      if (x+X_DIRECTIONS[i] >= 0 && x+X_DIRECTIONS[i] < Level.width
          && y+Y_DIRECTIONS[i] >= 0 && y+Y_DIRECTIONS[i] < Level.height)
      {
        //
        if (!map[x + X_DIRECTIONS[i]][y + Y_DIRECTIONS[i]].chosen
            && map[x + X_DIRECTIONS[i]][y + Y_DIRECTIONS[i]].type != WALL)
        {
          //i set this to be a tile in the grid
          directions[i] = map[x + X_DIRECTIONS[i]][y + Y_DIRECTIONS[i]];
          //if it hasnt been visited before
          if (directions[i].parent == null)
          {
           // visited = true;
            directions[i].parent = this;
            directions[i].costSoFar = this.costSoFar + directions[i].tileTravelCost;
          }
          else
          {
            if (costSoFar < directions[i].parent.costSoFar)
            {
              directions[i].parent = this;
              directions[i].costSoFar = this.costSoFar + directions[i].tileTravelCost;
            }

          }
          directions[i].heuristicCost = calculateEuclidDistance(directions[i], end);
          directions[i].fCost = directions[i].heuristicCost + directions[i].costSoFar;

          frontier.add(directions[i]);

        }
      }
    }
  }
}
