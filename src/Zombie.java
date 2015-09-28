import java.util.*;

/**
 * Created by Jalen on 9/9/2015.
 */
public class Zombie extends Entity
{
  private LinkedList<Tile> movementQueue;

  private Random rand = new Random();

  private boolean lineZombie;
  private boolean smellPlayer = false;
  private int directionDegree = rand.nextInt(8);

  private boolean isMasterZombie = false;

  private boolean moveZombieUp;
  private boolean moveZombieDown;
  private boolean moveZombieRight;
  private boolean moveZombieLeft;

  private int startX, startY;

  private String lineZombieSpriteSheet = "character_images/line_zombie_sprite_sheet.png";
  private String randomZombieSpriteSheet = "character_images/random_zombie_sprite_sheet.png";
  private String masterZombieSpriteSheet = "character_images/masterZombie_sprite_sheet.png";

  public Zombie(int x, int y) {
    super("Zombie", x, y);
    setSpeed(ZOMBIE_DEFAULT_SPEED);
    if (rand.nextDouble() > ZOMBIE_RANDOM_OR_LINE_RATE)
    {
      setSpriteSheet(lineZombieSpriteSheet);
      lineZombie = true;
    }
    else
    {
      setSpriteSheet(randomZombieSpriteSheet);
      lineZombie= false;
    }
    setHeading(directionDegree);
    resetCurrentFrame();
    startAnimation();
    setStartX(x);
    setStartY(y);
  }

  /**
   *
   * @return calculated the euclidian distance from the player to this zombie
   */
  public double calculateEuclidDistance()
  {
    double euclidDist;
    double xDifference = Math.abs(GameControl.userPlayer.getX() - getX());
    double yDifference = Math.abs(GameControl.userPlayer.getY() - getY());
    euclidDist = Math.sqrt(xDifference*xDifference + yDifference*yDifference);
    return euclidDist;
  }

  /**
   *
   * @param b boolean used to set this zombie to a master
   *          also sets the sprite sheet to be used in drawing
   */
  public void setMaster(boolean b)
  {
    setSpeed(MASTER_ZOMBIE_SPEED);
    isMasterZombie = true;
    setSpriteSheet(masterZombieSpriteSheet);
  }

  public boolean getSmellPlayer()
  {
    return smellPlayer;
  }
  public void setSmellPlayer(boolean b)
  {
    smellPlayer = b;
  }

  /**
   *  @description sets whether or not the zombie can
   *  smell the player if in ZOMBIE_SMELL DISTANCE
   */
  public void sniffForPlayer()
  {

    double distToPlayer = calculateEuclidDistance();
    if (distToPlayer <= ZOMBIE_SMELL)
    {
      smellPlayer = true;
    }
    else
    {
      smellPlayer = false;
    }
  }

  public boolean playerCanHearZombie()
  {
    double distToPlayer = calculateEuclidDistance();
    if (distToPlayer <= PLAYER_HEARING_DISTANCE)
    {
      return true;
    }
    else
    {
      return false;
    }
  }

  /**
   *
   * @return calculates the pan value(right and left ear values) for where
   * the player will hear in headphones
   */
  public double calculatePanValue()
  {
    double multiplier = 1;
    if (getX() < GameControl.userPlayer.getX())
    {
      multiplier = -1;
    }
    double euclid = calculateEuclidDistance();//calculate distance from zombie to player

    double panValue = PANVALUE_PADDING + 1.0/ euclid;
    if (panValue > 1.0)
    {
      panValue = 1.0;
    }
    return multiplier * panValue;
  }

  /**
   *
   * @param directionDegree
   * depending on the direction degree,
   * determines which cardinal direction the zombie will face in
   */
  private void setHeading(int directionDegree)
  {
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


  /**
   * called in game control.  Updates which direction the zombie should be facing
   * dependent on smell or whether or not its a random/line zombie
   */
  public void updateDirection()
  {
    moveZombieUp = false;
    moveZombieDown = false;
    moveZombieRight = false;
    moveZombieLeft = false;

    //checks specific to the masterZombie
    if (isMasterZombie && smellPlayer == false)
    {
      sniffForPlayer();
    }
    else if (!isMasterZombie)
    {
      sniffForPlayer();
    }

    //since animation stops on wall hit,
    //this restarts animation for zombies
    if (hitwall())
    {
      startAnimation();
    }
    if (smellPlayer)
    {
      if (hitwall())
      {
       // setHitWall(false);
        chasePlayer(FOUR_CARDINAL_DIRECTIONS);
      }
      else
      {
        chasePlayer(TOTAL_DIRECTIONS);
      }
      //movement queue is created when chaseplayer is called
      //the movement queue is a list of shortest path tiles to player
      //this if statment figures out what direction the zombie needs to face and animate in
      if (!movementQueue.isEmpty())
      {
        if (getX() > movementQueue.getLast().x)
        {
          setAnimationDirection(ANIMATION_LEFT_WALKING);
          moveZombieLeft = true;
        }
        if (getX() < movementQueue.getLast().x)
        {
          setAnimationDirection(ANIMATION_RIGHT_WALKING);
          moveZombieRight = true;
        }
        if (getY() < movementQueue.getLast().y)
        {
          setAnimationDirection(ANIMATION_DOWN_WALKING);
          moveZombieDown = true;
        }
        if (getY() > movementQueue.getLast().y)
        {
          setAnimationDirection(ANIMATION_TOP_WALKING);
          moveZombieUp = true;
        }
        movementQueue.removeLast();
      }
    }
    else if (lineZombie)
    {
      //choose random heading, move until hit wall
      //if hitwall chooses another direction
      if (hitwall())
      {
        setHitWall(false);
        int newDirectionDegree = rand.nextInt(8);
        while(newDirectionDegree == directionDegree)
        {
          newDirectionDegree = rand.nextInt(8);
        }
        directionDegree = newDirectionDegree;
      }
      setHeading(directionDegree);
    }
    else
    {
      //the zombie is a randomzombie
      int newDirectionDegree = rand.nextInt(8);
      if (hitwall())
      {
        setHitWall(false);
        while (newDirectionDegree == directionDegree)
        {
          newDirectionDegree = rand.nextInt(8);
        }
      }
      setHeading(newDirectionDegree);
    }
    startAnimation();
  }

  public void move()
  {

    super.move(moveZombieUp, moveZombieDown, moveZombieRight, moveZombieLeft);
  }

  /**
   * finds the shortest path to player
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
        map[i][j].setChosen(false);
        map[i][j].parent = null;
      }
    }
    movementQueue = new LinkedList<>();
    Tile next = findBestPath(start, end, map, numberOfFrontierDirections);
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
  private Tile findBestPath(Tile start, Tile end, Tile[][] map, int numberOfFrontierDirections) {
    //quick check to see if zombie is next to player
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
    while (!queue.isEmpty())
    {
      currentNode = queue.remove();
      currentNode.setChosen(true);
      if (currentNode.x == end.x && currentNode.y == end.y)
      {
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