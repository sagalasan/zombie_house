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
  private LinkedList<Tile> movementQueue;
  private Random rand = new Random();
  private boolean lineZombie;
  private boolean smellPlayer = false;
  private int directionDegree = rand.nextInt(8);

  private boolean moveZombieUp;
  private boolean moveZombieDown;
  private boolean moveZombieRight;
  private boolean moveZombieLeft;

  private boolean lastMoveLeft = false;
  private boolean lastMoveRight = false;
  private boolean lastMoveDown = false;
  private boolean lastMoveUp = false;

  private int startX, startY;

  private boolean initialCheckFinished = false;
  private String zombieSpriteSheet = "character_images/zombie_sprite_sheet.png";

  String zombieStepsFileName = "sound_files/zombie_footsteps.wav";
  String zombieWallBump= "sound_files/wall_hit_zombie.wav";
  //String
  //for the sound, may have to start the timer in the gamecontrol
  //that Way I could calculate each pan/gain value and add it
  Timer zombieWalkSound = new Timer(1000, new ActionListener()
  {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      double multiplier = 1;
      if (getX() < GameControl.userPlayer.getX())
      {
        multiplier = -1;
      }
      double euclid = calculateEuclidDistance();
      double panValue = 1.0 / euclid * multiplier;
      double gainValue = 4.0 * euclid;
      if (hitwall())
      {
        playSound(zombieWallBump, panValue, 12f);
      }
      else
      {
        playSound(zombieStepsFileName, panValue, (float) gainValue);
      }
    }
  });

  public Zombie(int x, int y)
  {
    super("Zombie", x, y);
    setSpeed(ZOMBIE_DEFAULT_SPEED);
    if (rand.nextDouble() > ZOMBIE_RANDOM_OR_LINE_RATE)
    {
     // System.out.println("this is a line zombie1");
      lineZombie = true;
    }
    else
    {
     // System.out.println("this is a random zombie1");
      lineZombie= false;
    }
    setHeading(directionDegree);
    setSpriteSheet(zombieSpriteSheet);
    resetCurrentFrame();
    startAnimation();
    //set zombie intial direction

    setStartX(x);
    setStartY(y);


  }

  public void setZombieSmell(boolean b)
  {
    smellPlayer = b;
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
      gainControl.setValue(-1 * gainValue);
      //System.out.println("gainvalue "+gainValue);
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
      setAnimationDirection(ANIMATION_RIGHT_WALKING);
      moveZombieRight = true;
    }
    else if (directionDegree == 1)
    {
      //north east
      setAnimationDirection(ANIMATION_RIGHT_WALKING);
      moveZombieRight = true;
      moveZombieUp = true;
    }
    else if (directionDegree == 2)
    {
      //north
      setAnimationDirection(ANIMATION_TOP_WALKING);
      moveZombieUp = true;
    }
    else if (directionDegree == 3)
    {
      //north west
      setAnimationDirection(ANIMATION_LEFT_WALKING);
      moveZombieUp = true;
      moveZombieLeft = true;
    }
    else if (directionDegree == 4)
    {
      //west
      setAnimationDirection(ANIMATION_LEFT_WALKING);
      moveZombieLeft = true;
    }
    else if (directionDegree == 5)
    {
      //southwest
      setAnimationDirection(ANIMATION_LEFT_WALKING);
      moveZombieLeft = true;
      moveZombieDown = true;
    }
    else if (directionDegree == 6)
    {
      //south
      setAnimationDirection(ANIMATION_DOWN_WALKING);
      moveZombieDown = true;
    }
    else if (directionDegree == 7)
    {
      //south east
      setAnimationDirection(ANIMATION_RIGHT_WALKING);
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


    if (smellPlayer) {
     /** if (!initialCheckFinished)
      {
        initialCheckFinished = true;
        chasePlayer();
      }
      else if (hitwall())
      {
        //check if
        //just try and follow the next coords
      }
      else {

        chasePlayer();
      }
      **/
    //System.out.println("smells player");
      if (hitwall())
      {
        setHitWall(false);
        System.out.println("chasing cause hit wall");
        chasePlayer(4);
      }
      else
      {
        chasePlayer(TOTAL_DIRECTIONS);
      }


      //if hitwall move to the next tile up in the movementqueue
      if (!movementQueue.isEmpty()) {
        //System.out.println("looking for next coords using "+movementQueue.getLast().x+", "+movementQueue.getLast().y);
        //if hitwall, find way to avoid wall
        if (getX() > movementQueue.getLast().x) {
          //System.out.println("zombie left");
          setAnimationDirection(ANIMATION_LEFT_WALKING);
          moveZombieLeft = true;
        }
        if (getX() < movementQueue.getLast().x) {
          //System.out.println("zombie right");
          setAnimationDirection(ANIMATION_RIGHT_WALKING);
          moveZombieRight = true;
        }
        if (getY() < movementQueue.getLast().y) {
          //System.out.println("zombie down");
          setAnimationDirection(ANIMATION_DOWN_WALKING);
          moveZombieDown = true;
        }
        if (getY() > movementQueue.getLast().y) {
          //System.out.println("zombie up");
          setAnimationDirection(ANIMATION_TOP_WALKING);
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
        //setHitWall(false);
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
        //setHitWall(false);
        stopAnimation();
        //stopanimationtimer
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
  private void chasePlayer(int numberOfFrontierDirections)
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
        map[i][j].setChosen(false);
        map[i][j].parent = null;
        //System.out.println("coords for map x, y " + map[i][j].x +", "+map[i][j].y);
      }
    }

    movementQueue = new LinkedList<>();
    Tile next = findBestPath(start, end, map, numberOfFrontierDirections);
    //System.out.println(next.x+", "+ next.y);

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
      if (one.getfCost() == two.getfCost())
      {
        //this could be screwy since it involves casting double values to int
        if (one.getHeuristicCost() < two.getHeuristicCost())
        {
          return -1;
        }
        else if (one.getHeuristicCost() > two.getHeuristicCost())
        {
          return 1;
        }
        else
        {
          return 0;
        }
      }
      else if (one.getfCost() < two.getfCost())
      {
        return -1;
      }
      else if (one.getfCost() > two.getfCost())
      {
        return 1;
      }
//      GameControl.testCount +=1;
      System.out.println("returned 0");
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
  //todo, fix never ending loop to search for tile
  //try making master zombie and getting that working first
  private Tile findBestPath(Tile start, Tile end, Tile[][] map, int numberOfFrontierDirections) {
    //if distance is only one tile away, just skip the whole pathfinding algo.

    int xDiff = Math.abs(start.x - end.x);
    int yDiff = Math.abs(start.y - end.y);
    if ((xDiff == 1 || xDiff == 0 )&& (yDiff == 1 || yDiff == 0))
    {
      end.parent = start;
      return end;
    }

    start.setChosen(true);
    Tile currentNode;
    PQsort sorter = new PQsort();
    start.setCostSoFar(start.getTileTravelCost());
    PriorityQueue<Tile> queue = new PriorityQueue<>(1, sorter);
    queue.clear();
    queue.add(start);
    //System.out.println("start (" + start.x + ", " + start.y + "), end (" + end.x +", "+end.y+")");
    while (!queue.isEmpty()) {
      currentNode = queue.remove();
      currentNode.setChosen(true);
      if (currentNode.x == end.x && currentNode.y == end.y) {
        break;
      }
      currentNode.setFrontier(queue, end, map, numberOfFrontierDirections);
    }
    return end;
  }




  public void setStartX(int x)
  {
    startX = x;
  }
  public int getStartX()
  {
    return startX;
  }

  public void setStartY(int y)
  {
    startY = y;
  }
  public int getStartY()
  {
    return startY;
  }


}