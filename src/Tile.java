import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.PriorityQueue;

/**
 * Created by Jalen on 9/9/2015.
 * @description - used to also help for astar algorithm
 */
public class Tile implements Constants {




  int x, y;

  //used for aStar pathfinding
  Tile parent;
  private Tile[] directions;
  private boolean chosen = false;
  private double tileTravelCost;
  private double heuristicCost;
  private double fCost;
  private double costSoFar;

  //used for combustion
  private boolean combusting = false;
  private boolean combusted = false;
  private BufferedImage currentFrame;
  private BufferedImage spriteSheet ;

  private int type;
  private int indexForImage = 0;
  private int totalRunTime = 0;

  /**
   *
   * @param type they type of tile this tile will be
   * @param x the x coordinate in the map
   * @param y the y coordinate in the map
   *
   */
  public Tile(int type, int x, int y)
  {
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
    else if (type == BLACKNESS)
    {
      this.type = BLACKNESS;
    }
    else if (type == FIRETRAP)
    {
      this.type = FIRETRAP;
    }
    setTileCost();
  }

  /**
   * @description sets the tile travel cost for the tiles used in pathfinding.
   */
  public void setTileCost()
  {
    if (type == FLOOR || type == SCORCHED_FLOOR || type == FIRETRAP)
    {
      tileTravelCost = 1;
    }
    else
    {
      //never crosses through a object or wall
      tileTravelCost = 500;
    }
  }
  public double getTileTravelCost()
  {
    return tileTravelCost;
  }

  public void setType(int newType)
  {
    type = newType;
  }
  public int getType()
  {
    return type;
  }
  public double getXPixel()
  {
    return (double)(x * SIZE);
  }
  public double getYPixel()
  {
    return (double)(y * SIZE);
  }

  /**
   * @description sets the fire sprite sheet to be used for animation
   */
  public void setSpriteSheet()
  {
    try
    {
      this.spriteSheet = ImageIO.read(new File("tile_images/fire.png"));
    }
    catch (IOException e)
    {
      System.out.println("Player Image did not load");
    }
  }

  public boolean isCombusting()
  {
    return combusting;
  }
  public boolean hasCombusted()
  {
    return combusted;
  }
  public void setCombusted(boolean b)
  {
    combusted = b;
  }

  public java.awt.Rectangle getBoundingRectangle()
  {
    return new Rectangle((int)getXPixel(), (int)getYPixel(), SIZE, SIZE);
  }
  public java.awt.Rectangle getRectangleForFiretrap()
  {
    return new Rectangle((int)getXPixel(), (int)getYPixel() + SIZE /2, SIZE , SIZE );
  }

  /**
   *
   * @param indexForPicture used to determine which part in the animation sprite sheet needs to be drawn
   */
  private void setFireFrame(int indexForPicture)
  {
    currentFrame = spriteSheet.getSubimage(15 + (indexForPicture * SPRITE_SPREAD_DISTANCE),
        0, FIRE_ANIMATION_WIDTH, FIRE_ANIMATION_HEIGHT);
  }
  public Image getCurrentFrame()
  {
    return currentFrame.getScaledInstance(SIZE, SIZE, Image.SCALE_DEFAULT);
  }
  Timer CombustTimer = new Timer(150, new ActionListener()
  {
    int totalFireImages = 5;
    @Override
    public void actionPerformed(ActionEvent e)
    {
      totalRunTime += 150;
      System.out.println(totalRunTime);
      if (totalRunTime >= 15000)
      {
        combusting = false;
        //combusted = true;
        System.out.println("finsihed combusting");
        stopCombust();
      }
      combusting = true;
      setFireFrame(indexForImage % totalFireImages);
      indexForImage += 1;
    }
  });

  private void combust()
  {
    totalRunTime = 0;
    setSpriteSheet();
    CombustTimer.start();
  }
  private void stopCombust()
  {
    CombustTimer.stop();
  }

  public void explode()
  {
    type = FLOOR;
    for (int i = -1; i < 2; i++)
    {
      for (int j = -1; j < 2; j++)
      {
        if (x+i >= 0 && x+i < Level.width && y+j >= 0 && y+j < Level.height)
        {
          Tile checkingTile = Level.map[x+i][y+j];
          if (checkingTile.type == FLOOR)
          {
            checkingTile.setCombusted(true);
            checkingTile.type = SCORCHED_FLOOR;
          }
          if (checkingTile.type == WALL || checkingTile.type == PILLAR)
          {
            if (checkingTile.checkIfInsideTile())
            {
              checkingTile.setCombusted(true);
              checkingTile.type = SCORCHED_WALL;
            }
          }
          if (checkingTile.type == FIRETRAP)
          {
            checkingTile.explode();
          }
          //checkingTile.combusting = true;
          checkingTile.combust();
        }
      }
    }
  }

  /**
   * @return returns true if valid inside tile to be scorched!
   * else false, it is out of bounds, or has blackness around it so it will not be scorched
   */
  private boolean checkIfInsideTile()
  {
    int checkXLeft = x - 1;
    int checkXRight = x + 1;
    int checkYUp = y - 1;
    int checkYDown = y + 1;
    if (checkXLeft < 0 || checkXRight > Level.width)
    {
      return false;
    }
    if (checkYDown > Level.height || checkYUp < 0)
    {
      return false;
    }
    if (Level.map[x-1][y].type == BLACKNESS)
    {
      return false;
    }
    if (Level.map[x+1][y].type == BLACKNESS){
      return false;
    }
    if (Level.map[x][y-1].type == BLACKNESS){
      return false;
    }
    if (Level.map[x][y+1].type == BLACKNESS)
    {
      return false;
    }
    return true;
  }


  /**
   *
   * @param start the start tile
   * @param end the end tile
   * @return calculates the euclidian distance from the start to end tile
   */
  public double calculateEuclidDistance(Tile start, Tile end)
  {
    double xDifference = Math.abs(start.x - end.x);
    double yDifference = Math.abs(start.y - end.y);
    double euclidDist = Math.sqrt(xDifference*xDifference + yDifference*yDifference);
    return euclidDist;
  }
  public void setChosen(boolean b)
  {
    chosen = b;
  }
  public double getHeuristicCost()
  {
    return heuristicCost;
  }
  public double getfCost()
  {
    return fCost;
  }
  public void setCostSoFar(double newValue)
  {
    costSoFar = newValue;
  }

  /**
   *
   * @param frontier - the priority queue that all the nodes get added to
   * @param end The player or end destination
   *            This will find the nodes around the tile this is called for
   *            Changes the parent node for if there is a better pathway
   */
  public void setFrontier(PriorityQueue<Tile> frontier, Tile end, Tile[][] map,int numberOfFrontierDirections)
  {
    //change directions to 4 if reg astar stuff
    directions = new Tile[numberOfFrontierDirections];
    for (int i = 0; i< numberOfFrontierDirections; i++)
    {
      //if in bounds
      if (x+X_DIRECTIONS[i] >= 0 && x+X_DIRECTIONS[i] < Level.width
          && y+Y_DIRECTIONS[i] >= 0 && y+Y_DIRECTIONS[i] < Level.height)
      {
        if (!map[x + X_DIRECTIONS[i]][y + Y_DIRECTIONS[i]].chosen
            && map[x + X_DIRECTIONS[i]][y + Y_DIRECTIONS[i]].type != WALL)
        {
          directions[i] = map[x + X_DIRECTIONS[i]][y + Y_DIRECTIONS[i]];
          //if it hasnt been visited before
          if (directions[i].parent == null)
          {
           // visited = true;
            directions[i].parent = this;
            directions[i].costSoFar = this.costSoFar + directions[i].tileTravelCost;
            frontier.add(directions[i]);
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
        }
      }
    }
  }
}
