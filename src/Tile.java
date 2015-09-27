import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.BorderUIResource;
import java.awt.*;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
  private Tile[] directions;// = new Tile[8];
  private boolean chosen = false;
  private double tileTravelCost;
  private double heuristicCost;
  private double fCost;
  private double costSoFar;
  private boolean combusting = false;
  private boolean combusted = false;
  private int type;
  private int indexForImage = 0;
  private int totalRunTime = 0;
  private BufferedImage currentFrame;
  private BufferedImage spriteSheet ;


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
    else if (type == BLACKNESS)
    {
      //black space
      this.type = BLACKNESS;
    }
    else if (type == FIRETRAP)
    {
      this.type = FIRETRAP;
    }
    setTileCost();
  }

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
  public boolean isCombusting()
  {
    return combusting;
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
    //System.out.println("xpixel for wall block at " + x + ", " + y+" is "+ (x*SIZE)+", "+y*SIZE);
   // System.out.println("userPlayers x y is" + GameControl.userPlayer.getX()+", " + GameControl.userPlayer.getY()+" xpixel is "+ GameControl.userPlayer.getXPixel());
    return (double)(x * SIZE);
  }
  public double getYPixel()
  {
    return (double)(y * SIZE);
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



  private void setFireFrame(int indexForPicture)
  {
    currentFrame = spriteSheet.getSubimage(15 + (indexForPicture * SPRITE_SPREAD_DISTANCE),
        0, FIRE_ANIMATION_WIDTH, FIRE_ANIMATION_HEIGHT);

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

      setFireFrame(indexForImage % totalFireImages);
      indexForImage += 1;


    }
  });
  public Image getCurrentFrame()
  {
    return currentFrame.getScaledInstance(SIZE, SIZE, Image.SCALE_DEFAULT);
  }
  private void combust()
  {
    totalRunTime = 0;
    setSpriteSheet();
    CombustTimer.start();
    //if this tile is a wall or tile
    //and is inside the level with no black around
    //then turn into a floor
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
          //check for if the type is a wall or object as well
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
          checkingTile.combusting = true;
          checkingTile.combust();
          System.out.println("started combusting");

          //wait 5 seconds and then set back to false
          //probably done in the fire animation loop
        }
      }
    }
    //combusts tile and eight blocks around it

  }

  /**
   *
   * @return returns true if valid inside tile to be scorched!
   * else false, it is out of bounds, or has blackness around it
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


  public double calculateEuclidDistance(Tile start, Tile end)
  {
    double xDifference = Math.abs(start.x - end.x);
    double yDifference = Math.abs(start.y - end.y);
    double euclidDist = Math.sqrt(xDifference*xDifference + yDifference*yDifference);
    return euclidDist;
  }


  public boolean isChosen()
  {
    return chosen;
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
          //i set this to be a tile in the grid
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
