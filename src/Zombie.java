import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;

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

  String zombieStepsFileName = "sound_files/player_footsteps.wav";
  //todo set zombie footsteps sound to someother file
  //todo have zombies move in 360 degrees
  //String
  Timer zombieWalkSound = new Timer(1000, new ActionListener()
  {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      double multiplier =1;
      if (getX() < GameControl.userPlayer.getX())
      {
        multiplier = -1;
      }
      double euclid = calculateEuclidDistance();
      double panValue = 1.0/euclid * multiplier;
      double gainValue = 5.0 * euclid;
      playSound(zombieStepsFileName, panValue, (float)gainValue);
    }

  });

  public Zombie(int x, int y)
  {
    super("Zombie", x, y);
    setSpeed(ZOMBIE_DEFAULT_SPEED);
    if (rand.nextDouble() > ZOMBIE_RANDOM_OR_LINE_RATE)
    {
      System.out.println("this is a line zombie1");
      lineZombie = true;
    }
    else
    {
      System.out.println("this is a random zombie1");
      lineZombie= false;
    }
    setHeading(directionDegree);
    //set zombie intial direction

  }

  public double calculateEuclidDistance()
  {
    double euclidDist;
    double xDifference = Math.abs(GameControl.userPlayer.getX() - getX());
    double yDifference = Math.abs(GameControl.userPlayer.getY() - getY());
    euclidDist = Math.sqrt(xDifference*xDifference + yDifference*yDifference);
    return euclidDist;
  }

  public void playSound(String fileName, double panValue, float gainValue)
  {
    try {
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(fileName).getAbsoluteFile());
      Clip clip = AudioSystem.getClip();
      clip.open(audioInputStream);
      FloatControl panControl = (FloatControl)clip.getControl(FloatControl.Type.PAN);
      panControl.setValue((float) panValue);
      FloatControl gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
      gainControl.setValue(-1*gainValue);

      System.out.println("gainvalue "+gainValue);
      clip.start();
    } catch(Exception ex) {
      System.out.println("Error with playing sound.");
      ex.printStackTrace();
    }
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
      //System.out.println("player can hear me");
      // todo need to combine each zombies footsteps
      zombieWalkSound.start();
    }
    else
    {
      zombieWalkSound.stop();
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